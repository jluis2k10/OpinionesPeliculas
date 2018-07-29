
package es.uned.components;

import es.uned.adapters.ClassifierType;
import es.uned.entities.Opinion;
import es.uned.entities.Polarity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class DatasetsBuilder<T> {

    private Map<String, T> rawDataset = new HashMap<>();
    private ClassifierType classifierType;

    public T get(final String key) {
        return rawDataset.get(key);
    }

    public void put(final String key, T value) {
        rawDataset.put(key, value);
    }

    public void setClassifierType (ClassifierType classifierType) {
        this.classifierType = classifierType;
    }

    public Map<Enum, List<String>> build() {
        if (this.classifierType == ClassifierType.POLARITY)
            return buildPolarityDatasets();
        else
            return buildOpinionDatasets();
    }

    private Map<Enum, List<String>> buildPolarityDatasets() {
        Map<Enum, List<String>> datasets = new EnumMap(Polarity.class);
        datasets.put(Polarity.POSITIVE, getSentences(rawDataset.get("positives")));
        datasets.put(Polarity.NEGATIVE, getSentences(rawDataset.get("negatives")));
        if (rawDataset.containsKey("neutrals"))
            datasets.put(Polarity.NEUTRAL, getSentences(rawDataset.get("neutrals")));
        return datasets;
    }

    private Map<Enum, List<String>> buildOpinionDatasets() {
        Map<Enum, List<String>> datasets = new EnumMap(Opinion.class);
        datasets.put(Opinion.SUBJECTIVE, getSentences(rawDataset.get("subjectives")));
        datasets.put(Opinion.OBJECTIVE, getSentences(rawDataset.get("objectives")));
        return datasets;
    }

    private List<String> getSentences(T rawDataset) {
        if (rawDataset instanceof String)
            return getSentences((String) rawDataset);
        else if (rawDataset instanceof MultipartFile)
            return getSentences((MultipartFile) rawDataset);
        return null; // No acaba de gustarme esto
    }

    /**
     * Lee el contenido de un archivo de texto y devuelve una lista de cadenas a partir del
     * contenido del archivo, añadiendo un elemento a la lista de salida con cada nueva línea
     * de texto en el archivo.
     * @param file Archivo de texto a leer
     * @return Lista con un elemento por cada nueva línea en el archivo de texto
     */
    private List<String> getSentences(MultipartFile file) {
        List<String> sentences = new ArrayList<>();
        try {
            InputStream is = file.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sentences.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sentences;
    }

    /**
     * Devuelve una lista de cadenas de texto a partir de un texto cualquiera. Cada nueva línea
     * del texto de entrada es un elemento más en la lista de salida.
     * @param text Texto de entrada a convertir
     * @return Lista con un elemento por cada nueva línea en el texto de entrada
     */
    private List<String> getSentences(String text) {
        String[] lines = text.split("\\r?\\n");
        return new ArrayList<>(Arrays.asList(lines));
    }
}

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

/**
 * Crear datasets con el formato adecuado para ser utilizados durante los procesos de creación
 * y entrenamiento de un clasificador.
 * <p>
 * @see es.uned.adapters.sentiment.SentimentAdapter#createModel(String, Map, Map)
 * @see es.uned.adapters.sentiment.SentimentAdapter#trainModel(String, Map)
 * @see es.uned.adapters.subjectivity.SubjectivityAdapter#createModel(String, Map, Map)
 * @see es.uned.adapters.subjectivity.SubjectivityAdapter#trainModel(String, Map)
 * @param <T> tipo de elementos a partir de los cuales generar los datasets ({@link String} o
 *           {@link MultipartFile})
 */
@Component
public class DatasetsBuilder<T> {

    /**
     * Dataset "en bruto" proviniente de los formularios de creación o entreno
     * de modelos de lenguaje.
     * <p>
     * @see es.uned.forms.CreateLanguageModelForm
     * @see es.uned.forms.TrainModelForm
     */
    private Map<String, T> rawDataset = new HashMap<>();

    /**
     * Tipo de clasificador donde se utilizará el dataset.
     */
    private ClassifierType classifierType;

    /**
     * Devuelve un elemento del mapa con el dataset "en bruto".
     * @param key clave del mapa del dataset
     * @return valor del dataset "en bruto"
     */
    public T get(final String key) {
        return rawDataset.get(key);
    }

    /**
     * Inserta un nuevo elemento en el dataset "en bruto".
     * @param key   clave para identificar la entrada en el dataset
     * @param value elemento a insertar
     */
    public void put(final String key, T value) {
        rawDataset.put(key, value);
    }

    /**
     * Define el tipo de clasificador que utilizará el dataset que se generará.
     * @param classifierType tipo de clasificador
     */
    public void setClassifierType (ClassifierType classifierType) {
        this.classifierType = classifierType;
    }

    /**
     * El formato de los datasets que acepta un clasificador es básicamente una lista
     * de textos o Strings. Pero como los clasificadores clasifican por categorías,
     * en vez de pasarles como entrada una lista de strings por cada categoría, les
     * damos un mapa con tantos elementos como categorías y cuyos valores sean precisamente
     * las listas de Strings.
     * <p>
     * Es decir, para clasificadores de polaridad, los datasets vendrán en la forma:
     * <p>
     * <table>
     *     <thead>
     *         <tr>
     *             <th>Clave</th>
     *             <th>Valor</th>
     *         </tr>
     *     </thead>
     *     <tbody>
     *         <tr>
     *             <td>positivos</td>
     *             <td>lista de frases</td>
     *         </tr>
     *         <tr>
     *             <td>negativos</td>
     *             <td>lista de frases</td>
     *         </tr>
     *         <tr>
     *             <td>neutrales</td>
     *             <td>lista de frases</td>
     *         </tr>
     *     </tbody>
     * </table>
     * @return mapa clasificado por categorías con los datasets que acepta el clasificador
     */
    public Map<Enum, List<String>> build() {
        if (this.classifierType == ClassifierType.POLARITY)
            return buildPolarityDatasets();
        else
            return buildOpinionDatasets();
    }

    /**
     * Construye el mapa con los datasets para clasificadores de polaridad.
     * @return mapa clasificado por categorías con los datasets
     */
    private Map<Enum, List<String>> buildPolarityDatasets() {
        Map<Enum, List<String>> datasets = new EnumMap(Polarity.class);
        datasets.put(Polarity.POSITIVE, getSentences(rawDataset.get("positives")));
        datasets.put(Polarity.NEGATIVE, getSentences(rawDataset.get("negatives")));
        if (rawDataset.containsKey("neutrals"))
            datasets.put(Polarity.NEUTRAL, getSentences(rawDataset.get("neutrals")));
        return datasets;
    }

    /**
     * Construye el mapa con los datasets para clasificadores de opinión.
     * @return mapa clasificado por categorías con los datasets
     */
    private Map<Enum, List<String>> buildOpinionDatasets() {
        Map<Enum, List<String>> datasets = new EnumMap(Opinion.class);
        datasets.put(Opinion.SUBJECTIVE, getSentences(rawDataset.get("subjectives")));
        datasets.put(Opinion.OBJECTIVE, getSentences(rawDataset.get("objectives")));
        return datasets;
    }

    /**
     * Recupera las frases (1 frase = 1 línea) contenidas en los archivos/textos de los
     * datasets "en bruto", devolviendo una lista con todas las frases contenidas en
     * ellos.
     * @param rawDataset dataset en bruto de donde ir sacando las frases
     * @return lista de frases o strings contenidas en el dataset "en bruto"
     */
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

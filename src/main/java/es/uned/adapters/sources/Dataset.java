package es.uned.adapters.sources;

import es.uned.entities.Comment;
import es.uned.entities.Corpus;
import es.uned.forms.SourceForm;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Fuente de comentarios a partir de un archivo o Dataset.
 * Se recupera un comentario por línea del archivo.
 */
@Component("es.uned.adapters.sources.Dataset")
public class Dataset implements SourceAdapter {

    /**
     * Idioma del dataset
     */
    private String lang;

    /**
     * Archivo con los comentarios
     */
    private MultipartFile file;

    /**
     * {@inheritDoc}
     * @param sourceForm Formulario con los parámetros opcionales
     */
    @Override
    public void setOptions(SourceForm sourceForm) {
        this.lang = sourceForm.getLang();
        this.file = sourceForm.getFile();
    }

    /**
     * {@inheritDoc}
     * @param corpus Corpus sobre el que se está trabajando
     */
    @Override
    public void generateCorpus(Corpus corpus) {
        corpus.setLang(this.lang);
        addComments(corpus);
    }

    /**
     * {@inheritDoc}
     * @param sourceForm Formulario con los parámetros opcionales
     * @param corpus     Corpus sobre el que se está trabajando
     * @return Número de nuevos comentarios añadidos al corpus
     */
    @Override
    public int updateCorpus(SourceForm sourceForm, Corpus corpus) {
        setOptions(sourceForm);
        int oldSize = corpus.getComments().size();
        addComments(corpus);
        corpus.setUpdated(LocalDateTime.now());
        return corpus.getComments().size() - oldSize;
    }

    /**
     * Añadir comentarios al corpus desde el archivo subido.
     * @param corpus Corpus sobre el que se está trabajando
     */
    private void addComments(Corpus corpus) {
        try {
            InputStream is = file.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                // Al crear el comentario lo añadimos al corpus automáticamente evitando duplicados
                Comment comment = new Comment.Builder()
                        .corpus(corpus)
                        .date(new Date())
                        .source("Dataset")
                        .content(line)
                        .build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

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
 *
 */
// Debe estar nombrado para que la fábrica lo localice y pueda inyectarlo.
@Component("es.uned.adapters.sources.Dataset")
public class Dataset implements SourceAdapter {

    private String lang;
    private MultipartFile file;

    @Override
    public void setOptions(SourceForm sourceForm) {
        this.lang = sourceForm.getLang();
        this.file = sourceForm.getFile();
    }

    @Override
    public void generateCorpus(Corpus corpus) {
        corpus.setLang(this.lang);
        addComments(corpus);
    }

    @Override
    public int updateCorpus(SourceForm sourceForm, Corpus corpus) {
        setOptions(sourceForm);
        Map<Integer, Comment> thisSourceComments = corpus.getComments().stream()
                .filter(comment -> comment.getSource().equals("Dataset"))
                .collect(Collectors.toMap(comment -> comment.getHash(), Function.identity(), (oldVal, newVal) -> oldVal, LinkedHashMap::new));

        int oldSize = corpus.getComments().size();
        addComments(corpus);
        corpus.setUpdated(LocalDateTime.now());

        return corpus.getComments().size() - oldSize;
    }

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

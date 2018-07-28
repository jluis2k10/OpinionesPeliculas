package es.uned.adapters.sources;

import es.uned.entities.Comment;
import es.uned.entities.Corpus;
import es.uned.forms.SourceForm;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Fuente de comentarios a partir de una única frase escrita por el usuario.
 */
@Component("es.uned.adapters.sources.SentenceSearch")
public class SentenceSearch implements SourceAdapter {

    /**
     * Frase a añadir
     */
    private String phrase;

    /**
     * Idioma de la frase
     */
    private String lang;

    /**
     * {@inheritDoc}
     * @param sourceForm Formulario con los parámetros opcionales
     */
    @Override
    public void setOptions(SourceForm sourceForm) {
        this.phrase = sourceForm.getTerm();
        this.lang = sourceForm.getLang();
    }

    /**
     * {@inheritDoc}
     * @param corpus Corpus sobre el que se está trabajando
     */
    @Override
    public void generateCorpus(Corpus corpus) {
        corpus.setLang(this.lang);
        Comment comment = new Comment.Builder()
                .source("Frase")
                .url("")
                .date(new Date())
                .content(this.phrase)
                .corpus(corpus)
                .build();
        corpus.addComment(comment);
    }

    /**
     * {@inheritDoc}
     * @param sourceForm Formulario con los parámetros opcionales
     * @param corpus     Corpus sobre el que se está trabajando
     * @return Númmero de nuevos comentarios añadidos al corpus (siempre 1 en este caso)
     */
    @Override
    public int updateCorpus(SourceForm sourceForm, Corpus corpus) {
        corpus.setUpdated(LocalDateTime.now());
        Comment comment = new Comment.Builder()
                .source("Frase")
                .url("")
                .date(new Date())
                .content(sourceForm.getTerm())
                .corpus(corpus)
                .build();
        corpus.addComment(comment);
        return 1;
    }
}

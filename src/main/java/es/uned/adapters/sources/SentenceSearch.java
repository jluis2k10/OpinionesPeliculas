package es.uned.adapters.sources;

import es.uned.entities.Comment;
import es.uned.entities.Corpus;
import es.uned.forms.SourceForm;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 */
@Component("es.uned.adapters.sources.SentenceSearch")
public class SentenceSearch implements SourceAdapter {

    private String phrase;
    private String lang;

    @Override
    public void setOptions(SourceForm sourceForm) {
        this.phrase = sourceForm.getTerm();
        this.lang = sourceForm.getLang();
    }

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

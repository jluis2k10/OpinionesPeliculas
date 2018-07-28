package es.uned.adapters.sources;

import es.uned.entities.Comment;
import es.uned.entities.Corpus;
import es.uned.forms.SourceForm;
import org.springframework.core.env.Environment;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Fuente de comentarios a partir de tweets.
 */
@Component("es.uned.adapters.sources.TwitterSearch")
public class TwitterSearch implements SourceAdapter {

    @Inject private Environment environment;

    /**
     * Idioma de los comentarios a recuperar
     */
    private String lang;

    /**
     * Identificador de esta fuente de comentarios (Twitter)
     */
    private String source;

    /**
     * Número máximo de comentarios a recuperar
     */
    private int limit;

    /**
     * Término de búsqueda
     */
    private String searchTerm;

    /**
     * Fecha límite. Tweets escritos no más tarde que esta fecha
     */
    private Date untilDate;

    /**
     * {@inheritDoc}
     * @param sourceForm Formulario con los parámetros opcionales
     */
    @Override
    public void setOptions(SourceForm sourceForm) {
        this.lang = sourceForm.getLang();
        this.source = sourceForm.getSource();
        this.limit = sourceForm.getLimit();
        this.searchTerm = sourceForm.getTerm();
        this.untilDate = sourceForm.getUntilDate();
    }

    /**
     * {@inheritDoc}
     * @param corpus Corpus sobre el que se está trabajando
     */
    @Override
    public void generateCorpus(Corpus corpus) {
        corpus.setLang(lang);
        addComments(corpus);
    }

    /**
     * {@inheritDoc}
     * @param sourceForm Formulario con los parámetros opcionales
     * @param corpus     Corpus sobre el que se está trabajando
     * @return Número de comentarios añadidos al corpus
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
     * Realiza una búsqueda utilizando el API público de Twitter y añade los resultados
     * al corpus.
     * @param corpus Corpus sobre el que se está trabajando
     */
    private void addComments(Corpus corpus) {
        Twitter twitter = new TwitterTemplate(
                environment.getProperty("twitter.consumerKey"),
                environment.getProperty("twitter.consumerSecret")
        );

        SearchParameters parameters = new SearchParameters(searchTerm)
                .lang(lang)
                .resultType(SearchParameters.ResultType.RECENT)
                .count(100)
                .until((untilDate != null ? untilDate : null))
                .includeEntities(false);

        int totalLimit = corpus.getComments().size() + limit;
        while (corpus.getComments().size() < totalLimit) {
            SearchResults results = twitter.searchOperations().search(parameters);
            if (results.getTweets().size() == 0)
                break;

            for (Tweet tweet : results.getTweets()) {
                if (tweet.getRetweetCount() == 0 && // No retweets
                        tweet.getText().toLowerCase().indexOf("http") == -1 && // Que no contengan enlaces
                        tweet.getText().toLowerCase().indexOf(searchTerm.toLowerCase()) != -1 && // Que contengan el término de búsqueda
                        corpus.getComments().size() < totalLimit)
                {
                    // Al crear el comentario lo añadimos al corpus automáticamente evitando duplicados
                    Comment comment = new Comment.Builder()
                            .source(source)
                            .url("https://twitter.com/" + tweet.getFromUser() + "/status/" + tweet.getId())
                            .date(tweet.getCreatedAt())
                            .content(tweet.getText())
                            .corpus(corpus)
                            .build();
                }
            }

            int last = results.getTweets().size() - 1;
            Long maxID = results.getTweets().get(last).getId();
            parameters.maxId(maxID-1);
        }
    }
}

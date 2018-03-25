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
 *
 */
// Debe estar nombrado para que la fábrica lo localice y pueda inyectarlo.
@Component("es.uned.adapters.sources.TwitterSearch")
public class TwitterSearch implements SourceAdapter {

    @Inject
    private Environment environment;

    /* Opciones de búsqueda */
    private String lang;
    private String source;
    private int limit;
    private String searchTerm;
    private Date untilDate;

    @Override
    public void setOptions(SourceForm sourceForm) {
        this.lang = sourceForm.getLang();
        this.source = sourceForm.getSource();
        this.limit = sourceForm.getLimit();
        this.searchTerm = sourceForm.getTerm();
        this.untilDate = sourceForm.getUntilDate();
    }

    @Override
    public void generateCorpus(Corpus corpus) {
        corpus.setLang(lang);
        addComments(corpus);
    }

    @Override
    public int updateCorpus(SourceForm sourceForm, Corpus corpus) {
        setOptions(sourceForm);
        Map<Integer, Comment> thisSourceComments = corpus.getComments().stream()
                .filter(comment -> comment.getSource().equals(source))
                .collect(Collectors.toMap(comment -> comment.getHash(), Function.identity(), (oldVal, newVal) -> oldVal, LinkedHashMap::new));

        int oldSize = corpus.getComments().size();
        addComments(corpus);
        corpus.setUpdated(LocalDateTime.now());
        return corpus.getComments().size() - oldSize;
    }

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

package es.uned.adapters.sources;

import es.uned.entities.CommentWithSentiment;
import es.uned.entities.Search;
import org.springframework.core.env.Environment;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    @Override
    public void doSearch(Search search) {
        getComments(search, new LinkedHashMap<>());
    }

    @Override
    public int updateSearch(Search search) {
        int sizeBefore = search.getComments().size();
        Map<Integer, CommentWithSentiment> oldComments = search.getComments().stream()
                .collect(Collectors.toMap(c -> c.getSourceURL().hashCode(), Function.identity(), (oldVal, newVal) -> oldVal, LinkedHashMap::new));
        getComments(search, oldComments);
        return search.getComments().size() - sizeBefore;
    }

    private void getComments(Search search, Map<Integer, CommentWithSentiment> comments) {
        Twitter twitter = new TwitterTemplate(
                environment.getProperty("twitter.consumerKey"),
                environment.getProperty("twitter.consumerSecret")
        );

        SearchParameters parameters = new SearchParameters(search.getTerm())
                .lang(search.getLang())
                .resultType(SearchParameters.ResultType.RECENT)
                .count(100)
                .includeEntities(false);
        if (!search.getUntilDate().equals("")) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date untilDate = dateFormatter.parse(search.getUntilDate());
                parameters.until(untilDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        while (comments.size() < search.getLimit()) {
            SearchResults results = twitter.searchOperations().search(parameters);
            if (results.getTweets().size() == 0)
                break;

            for (Tweet tweet : results.getTweets()) {
                if (tweet.getRetweetCount() == 0 && // No retweets
                        tweet.getText().toLowerCase().indexOf("http") == -1 && // Que no contengan enlaces
                        tweet.getText().toLowerCase().indexOf(search.getTerm().toLowerCase()) != -1 && // Que contengan el término de búsqueda
                        comments.size() < search.getLimit()) {
                    CommentWithSentiment comment = new CommentWithSentiment.Builder()
                            .search(search)
                            .sourceUrl("https://twitter.com/" + tweet.getFromUser() + "/status/" + tweet.getId())
                            .date(tweet.getCreatedAt())
                            .comment(tweet.getText())
                            .build();
                    comments.putIfAbsent(comment.getComment().hashCode(), comment);
                }
            }

            int last = results.getTweets().size() - 1;
            Long maxID = results.getTweets().get(last).getId();
            parameters.maxId(maxID-1);
        }
        // Convertimos hashmap a lista y la guardamos
        search.setComments(new LinkedList<>(comments.values()));
    }
}

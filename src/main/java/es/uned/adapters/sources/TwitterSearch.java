package es.uned.adapters.sources;

import es.uned.entities.SearchParams;
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
import java.util.Date;
import java.util.HashMap;

/**
 *
 */
// Debe estar nombrado para que la fábrica lo localice y pueda inyectarlo.
@Component("es.uned.adapters.sources.TwitterSearch")
public class TwitterSearch implements SourceAdapter {

    @Inject
    private Environment environment;

    @Override
    public HashMap<Integer,String> getComments(SearchParams params) {
        HashMap<Integer,String> comments = new HashMap<>();
        Twitter twitter = new TwitterTemplate(
                environment.getProperty("twitter.consumerKey"),
                environment.getProperty("twitter.consumerSecret")
        );

        SearchParameters parameters = new SearchParameters(params.getSearchTerm())
                .lang(params.getLang())
                .resultType(SearchParameters.ResultType.RECENT)
                .count(100)
                .includeEntities(false);
        if (params.getUntilDate() != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date untilDate = formatter.parse(params.getUntilDate());
                parameters.until(untilDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        while (comments.size() < params.getLimit()) {
            SearchResults results = twitter.searchOperations().search(parameters);
            if (results.getTweets().size() == 0)
                break;

            for (Tweet tweet : results.getTweets()) {
                if (tweet.getRetweetCount() == 0 && tweet.getText().toLowerCase().indexOf("http") == -1
                        && tweet.getText().toLowerCase().indexOf(params.getSearchTerm().toLowerCase()) != -1
                        && comments.size() < params.getLimit()) {
                    /*comments.add(new CommentWithSentiment(tweet.getText(), tweet.getIdStr(), tweet.getFromUser(),
                            tweet.getCreatedAt(), tweet.getProfileImageUrl()));*/
                    String clean = this.cleanTweet(tweet.getText());
                    comments.put(clean.hashCode(), clean);
                }
            }

            int last = results.getTweets().size() - 1;
            Long maxID = results.getTweets().get(last).getId();
            parameters.maxId(maxID-1);
        }

        return comments;
    }

    private String cleanTweet(String tweet) {
        String clean = null;
        // Eliminar menciones a usuarios
        clean = tweet.replaceAll("(?:\\s|\\A)[@]+([A-Za-z0-9-_]+)", "");
        return clean;
    }
}

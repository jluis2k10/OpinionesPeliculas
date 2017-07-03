package es.uned.adapters.sources;

import es.uned.entities.CommentWithSentiment;
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
    public HashMap<Integer,CommentWithSentiment> getComments(SearchParams params) {
        HashMap<Integer,CommentWithSentiment> comments = new HashMap<>();
        Twitter twitter = new TwitterTemplate(
                environment.getProperty("twitter.consumerKey"),
                environment.getProperty("twitter.consumerSecret")
        );

        SearchParameters parameters = new SearchParameters(params.getSearchTerm())
                .lang(params.getLang())
                .resultType(SearchParameters.ResultType.RECENT)
                .count(100)
                .includeEntities(false);
        if (!params.getUntilDate().equals("")) {
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
                if (tweet.getRetweetCount() == 0 && // No retweets
                        tweet.getText().toLowerCase().indexOf("http") == -1 && // Que no contengan enlaces
                        tweet.getText().toLowerCase().indexOf(params.getSearchTerm().toLowerCase()) != -1 && // Que contengan el término de búsqueda
                        comments.size() < params.getLimit()) {
                    CommentWithSentiment comment = new CommentWithSentiment.Builder()
                            .searchTerm(params.getSearchTerm())
                            .comment(tweet.getText())
                            .build();
                    comments.put(comment.getComment().hashCode(), comment);
                }
            }

            int last = results.getTweets().size() - 1;
            Long maxID = results.getTweets().get(last).getId();
            parameters.maxId(maxID-1);
        }

        return comments;
    }
}

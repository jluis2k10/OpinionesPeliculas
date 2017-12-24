package es.uned.adapters.sources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.Search;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

/**
 *
 */
@Component("es.uned.adapters.sources.IMDBSearch")
public class IMDBSearch implements SourceAdapter {

    @Inject
    private Environment environment;

    @Override
    public void doSearch(Search search) {
        LinkedList<CommentWithSentiment> comments = new LinkedList<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        URI uri = null;

        try {
            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("app.imdb.com")
                    .setPath("/title/usercomments")
                    .setParameter("api", "v1")
                    .setParameter("appid", "iphone1_1")
                    .setParameter("apiPolicy", "app1_1")
                    .setParameter("locale", search.getLang())
                    .setParameter("apiKey", environment.getProperty("imdb.apiKey"))
                    .setParameter("limit", Integer.toString(search.getLimit()))
                    .setParameter("tconst", search.getTerm())
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpGet request = new HttpGet(uri);

        try {
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Fallo: HTTP error code: " + response.getStatusLine().getStatusCode());
            }
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
            JsonNode userComments = jsonNode.get("data").get("user_comments");
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
            for (JsonNode userComment : userComments) {
                CommentWithSentiment comment = new CommentWithSentiment.Builder()
                        .search(search)
                        .sourceUrl("http://www.imdb.com/title/" + search.getTerm() + "/reviews")
                        .date(dateFormatter.parse(userComment.get("date").asText()))
                        .comment(userComment.get("text").asText())
                        .build();
                comments.add(comment);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        search.setComments(comments);
    }
}

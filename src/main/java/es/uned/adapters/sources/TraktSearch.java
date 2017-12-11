package es.uned.adapters.sources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;
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
import java.util.HashMap;

/**
 *
 */
@Component("es.uned.adapters.sources.TraktSearch")
public class TraktSearch implements SourceAdapter {

    @Inject
    private Environment environment;

    @Override
    public HashMap<Integer, CommentWithSentiment> getComments(SearchParams params) {
        HashMap<Integer,CommentWithSentiment> comments = new HashMap<>();
        HttpClient httpClient = HttpClientBuilder.create().build();
        URI uri = null;

        try {
            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.trakt.tv")
                    .setPath("/movies/" + params.getSearchTerm() + "/comments/newest")
                    .setParameter("page", "1")
                    .setParameter("limit", Integer.toString(params.getLimit()))
                    .build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        HttpGet request = new HttpGet(uri);
        request.addHeader("Content-Type", "application/json");
        request.addHeader("trakt-api-version", "2");
        request.addHeader("trakt-api-key", environment.getProperty("trakt.apiKey"));

        try {
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Fallo: HTTP error code: " + response.getStatusLine().getStatusCode());
            }
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);

            for (JsonNode TraktComment : jsonNode) {
                CommentWithSentiment comment = new CommentWithSentiment.Builder()
                        .searchTerm(params.getSearchTerm())
                        .comment(TraktComment.get("comment").asText())
                        .build();
                comments.put(comment.getComment().hashCode(), comment);
                //comments.add(new CommentWithSentiment(comment.get("comment").asText(),
                //        comment.get("user").get("username").asText(),
                //        dateFormat.parse(comment.get("created_at").asText())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return comments;
    }
}

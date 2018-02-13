package es.uned.adapters.sources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.uned.components.TrakttvLookup;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.Search;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
@Component("es.uned.adapters.sources.TraktSearch")
public class TraktSearch implements SourceAdapter {

    @Inject
    private Environment environment;

    @Autowired
    private TrakttvLookup trakttvLookup;

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
        HttpClient httpClient = HttpClientBuilder.create().build();
        URI uri = null;

        try {
            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.trakt.tv")
                    .setPath("/movies/" + search.getTerm() + "/comments/newest")
                    .setParameter("page", "1")
                    .setParameter("limit", Integer.toString(search.getLimit()))
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
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            for (JsonNode traktComment : jsonNode) {
                CommentWithSentiment comment = new CommentWithSentiment.Builder()
                        .search(search)
                        .sourceUrl("https://trakt.tv/comments/" + traktComment.get("id").asText())
                        .date(dateFormatter.parse(traktComment.get("created_at").asText()))
                        .comment(traktComment.get("comment").asText())
                        .build();
                comments.putIfAbsent(comment.getSourceURL().hashCode(), comment);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        search.setTitle(trakttvLookup.imdbToTitle(search.getTerm()));
        search.setComments(new LinkedList<>(comments.values()));
    }
}

package es.uned.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 */
@Service
public class TrakttvLookup {

    @Inject
    private Environment environment;

    public ObjectNode lookup(String title, String page) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();
        ArrayNode films = mapper.createArrayNode();
        HttpClient httpClient = HttpClientBuilder.create().build();
        URI uri = null;

        try {
            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.trakt.tv")
                    .setPath("/search/movie")
                    .setParameter("query", title)
                    .setParameter("page", page)
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

            int itemCount = Integer.parseInt(response.getFirstHeader("x-pagination-item-count").getValue());
            result.put("total_count", itemCount);
            HttpEntity entity = response.getEntity();
            String jsonResponse = EntityUtils.toString(entity);
            JsonNode jsonNode = mapper.readTree(jsonResponse);
            for (JsonNode node: jsonNode) {
                ObjectNode resultNode = mapper.createObjectNode();
                resultNode.set("title", node.get("movie").get("title"));
                resultNode.set("year", node.get("movie").get("year"));
                resultNode.set("imdbID", node.get("movie").get("ids").get("imdb"));
                films.add(resultNode);
            }
            result.set("films", films);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}

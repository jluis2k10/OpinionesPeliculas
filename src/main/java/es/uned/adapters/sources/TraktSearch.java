package es.uned.adapters.sources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.uned.components.TrakttvLookup;
import es.uned.entities.Comment;
import es.uned.entities.Corpus;
import es.uned.forms.SourceForm;
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
import java.time.LocalDateTime;
import java.util.*;
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

    /* Opciones de búsqueda */
    private String source;
    private int limit;
    private String searchTerm;

    @Override
    public void setOptions(SourceForm sourceForm) {
        this.source = sourceForm.getSource();
        this.limit = sourceForm.getLimit();
        this.searchTerm = sourceForm.getTerm();
    }

    @Override
    public void generateCorpus(Corpus corpus) {
        corpus.setLang("en");
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
        HttpClient httpClient = HttpClientBuilder.create().build();
        URI uri = null;

        int traktvCommentsSize = Math.toIntExact(corpus.getComments().stream()
                .filter(comment -> comment.getSource().equals(source))
                .count());

        try {
            uri = new URIBuilder()
                    .setScheme("https")
                    .setHost("api.trakt.tv")
                    .setPath("/movies/" + searchTerm + "/comments/newest")
                    .setParameter("page", "1")
                    .setParameter("limit", Integer.toString(traktvCommentsSize + limit))
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
                // Al crear el comentario lo añadimos al corpus automáticamente evitando duplicados
                Comment comment = new Comment.Builder()
                        .source(source)
                        .url("https://trakt.tv/comments/" + traktComment.get("id").asText())
                        .date(dateFormatter.parse(traktComment.get("created_at").asText()))
                        .content(traktComment.get("comment").asText())
                        .corpus(corpus)
                        .build();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

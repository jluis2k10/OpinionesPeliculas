package es.uned.adapters.sources;

import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 *
 */
@Component("es.uned.adapters.sources.SentenceSearch")
public class SentenceSearch implements SourceAdapter {

    @Override
    public HashMap<Integer, CommentWithSentiment> getComments(SearchParams params) {
        HashMap<Integer, CommentWithSentiment> results = new HashMap<>();
        results.put(params.getSearchTerm().hashCode(),  new CommentWithSentiment.Builder().comment(params.getSearchTerm()).build());
        return results;
    }

}

package es.uned.adapters.sources;

import es.uned.entities.SearchParams;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 *
 */
@Component("es.uned.adapters.sources.SentenceSearch")
public class SentenceSearch implements SourceAdapter {

    @Override
    public HashMap<Integer, String> getComments(SearchParams params) {
        HashMap<Integer, String> results = new HashMap<>();
        results.put(params.getSearchTerm().hashCode(), params.getSearchTerm());
        return results;
    }

}

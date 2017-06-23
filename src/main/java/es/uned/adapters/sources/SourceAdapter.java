package es.uned.adapters.sources;

import es.uned.entities.SearchParams;

import java.util.HashMap;

/**
 *
 */
public interface SourceAdapter {

    HashMap<Integer, String> getComments(SearchParams params);

}

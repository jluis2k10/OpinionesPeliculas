package es.uned.adapters.sources;

import es.uned.entities.Search;

/**
 *
 */
public interface SourceAdapter {

    void doSearch(Search search);
    int updateSearch(Search search);

}

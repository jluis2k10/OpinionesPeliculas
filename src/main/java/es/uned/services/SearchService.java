package es.uned.services;

import es.uned.entities.Search;

/**
 *
 */
public interface SearchService {

    Search findOne(Long id);
    void save(Search search);
    void delete(Search search);

}

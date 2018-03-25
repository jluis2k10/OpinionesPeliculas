package es.uned.services;

import es.uned.entities.Corpus;

/**
 *
 */
public interface CorpusService {

    Corpus findOne(Long id);
    void save(Corpus corpus);
    void delete(Corpus corpus);

}

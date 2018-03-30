package es.uned.services;

import es.uned.entities.Account;
import es.uned.entities.Corpus;

import java.util.List;

/**
 *
 */
public interface CorpusService {

    Corpus findOne(Long id);
    List<Corpus> findByOwner(Account owner);
    void save(Corpus corpus);
    void delete(Corpus corpus);

}

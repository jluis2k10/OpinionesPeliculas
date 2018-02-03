package es.uned.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.entities.Account;
import es.uned.entities.Search;

import java.util.Set;

/**
 *
 */
public interface SearchService {

    Search findOne(Long id);
    void save(Search search);
    Set<Search> mySearches(Account account);
    Set<Search> usersSearches(Account account);
    ObjectNode JSONsearches(Account account);
    void delete(Search search);

}

package es.uned.services;

import es.uned.entities.Search;
import es.uned.repositories.CommentsRepo;
import es.uned.repositories.SearchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class MySearchService implements SearchService {

    @Autowired
    SearchRepo searchRepo;
    @Autowired
    CommentsRepo commentsRepo;

    public void save(Search search) {
        searchRepo.save(search);
        // Guardamos comentarios
        commentsRepo.save(search.getComments());
    }

}

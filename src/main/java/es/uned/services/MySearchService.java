package es.uned.services;

import es.uned.components.TrakttvLookup;
import es.uned.entities.Search;
import es.uned.repositories.CommentsWithSentimentRepo;
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
    CommentsWithSentimentRepo commentsWithSentimentRepo;
    @Autowired
    TrakttvLookup trakttvLookup;

    @Override
    public Search findOne(Long id) {
        return searchRepo.findOne(id);
    }

    public void save(Search search) {
        searchRepo.save(search);
        // Guardamos comentarios
        commentsWithSentimentRepo.save(search.getComments());
    }

    @Override
    public void delete(Search search) {
        commentsWithSentimentRepo.deleteInBatch(search.getComments());
        searchRepo.delete(search);
    }

}

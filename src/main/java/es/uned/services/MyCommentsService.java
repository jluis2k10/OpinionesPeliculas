package es.uned.services;

import es.uned.entities.Comment;
import es.uned.repositories.CommentsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class MyCommentsService implements CommentsService {

    @Autowired
    CommentsRepo commentsRepo;

    @Autowired
    RecordsService recordsService;

    @Override
    public void save(Comment comment) {
        commentsRepo.save(comment);
    }

    @Override
    public void delete(Comment comment) {
        commentsRepo.delete(comment);
    }
}

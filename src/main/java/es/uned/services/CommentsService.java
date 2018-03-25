package es.uned.services;

import es.uned.entities.Comment;

/**
 *
 */
public interface CommentsService {

    void save(Comment comment);
    void delete(Comment comment);

}

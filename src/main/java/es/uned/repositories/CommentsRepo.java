package es.uned.repositories;

import es.uned.entities.CommentWithSentiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface CommentsRepo extends JpaRepository<CommentWithSentiment, Long> {



}

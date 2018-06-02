package es.uned.repositories;

import es.uned.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface CommentsRepo extends JpaRepository<Comment, Long> {

    void deleteByCorpus_Id(Long corpusID);

}

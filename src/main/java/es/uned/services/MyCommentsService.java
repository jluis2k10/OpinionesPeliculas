package es.uned.services;

import es.uned.entities.Comment;
import es.uned.repositories.CommentsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la interfaz {@link CommentsService} para dar servicio
 * a operaciones sobre cuentas de usuario ({@link Comment}).
 */
@Service
public class MyCommentsService implements CommentsService {

    @Autowired private CommentsRepo commentsRepo;

    /**
     * {@inheritDoc}
     * @param comment comentario a persistir
     */
    @Override
    public void save(Comment comment) {
        commentsRepo.save(comment);
    }

    /**
     * {@inheritDoc}
     * @param comment comentario a eliminar
     */
    @Override
    public void delete(Comment comment) {
        comment.clearRecords();
        commentsRepo.delete(comment);
    }

    /**
     * {@inheritDoc}
     * @param corpusID identificador del corpus cuyos comentarios hay que eliminar
     */
    @Transactional
    @Override
    public void deleteByCorpus(Long corpusID) {
        commentsRepo.deleteByCorpus_Id(corpusID);
    }
}

package es.uned.services;

import es.uned.entities.Comment;

/**
 * Proporciona servicio para manejar comentarios ({@link Comment}).
 * <p>
 * Forma parte de la capa de servicio de la aplicación, es decir se encarga
 * de tratar los datos antes de enviarlos a la capa de persistencia o tras
 * recibirlos de la capa de persistencia.
 */
public interface CommentsService {

    /**
     * Persiste un comentario
     * @param comment comentario a persistir
     */
    void save(Comment comment);

    /**
     * Elimina un comentario de la base de datos
     * @param comment comentario a eliminar
     */
    void delete(Comment comment);

    /**
     * Elimina todos los comentarios pertenecientes a un corpus dado
     * @param corpusID identificador del corpus cuyos comentarios hay que eliminar
     */
    void deleteByCorpus(Long corpusID);

}

package es.uned.repositories;

import es.uned.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Comment}
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre
 * la aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen. Spring Data se
 * encarga de traducirlos a las consultas SQL correspondientes de forma automática.
 */
@Repository
public interface CommentsRepo extends JpaRepository<Comment, Long> {

    /**
     * Eliminar comentarios pertenecientes a un corpus concreto dado.
     * @param corpusID Identificador deñ Corpus
     */
    void deleteByCorpus_Id(Long corpusID);

}

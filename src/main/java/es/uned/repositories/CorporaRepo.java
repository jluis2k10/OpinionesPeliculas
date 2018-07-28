package es.uned.repositories;

import es.uned.entities.Account;
import es.uned.entities.Corpus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad {@link Corpus}
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre
 * la aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen. Spring Data se
 * encarga de traducirlos a las consultas SQL correspondientes de forma automática.
 */
@Repository
public interface CorporaRepo extends JpaRepository<Corpus, Long> {

    /**
     * Devuelve todos los Corpus pertenecientes (creados por) a un usuario dado.
     * @param owner Usuario que ha generado los corpus
     * @return Lista de corpus creados por el usuario
     */
    List<Corpus> findAllByOwner(Account owner);

}

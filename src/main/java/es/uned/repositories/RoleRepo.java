package es.uned.repositories;

import es.uned.entities.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link AccountRole}
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre
 * la aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen. Spring Data se
 * encarga de traducirlos a las consultas SQL correspondientes de forma automática.
 */
@Repository
public interface RoleRepo extends JpaRepository<AccountRole, Long> {

    /**
     * Busca un Rol por su nombre.
     * @param role nombre del rol a buscar
     * @return el Rol encontrado
     */
    AccountRole findByRole(String role);
}

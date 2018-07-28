package es.uned.repositories;

import es.uned.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Account}
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre
 * la aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen. Spring Data se
 * encarga de traducirlos a las consultas SQL correspondientes de forma automática.
 */
@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {

    /**
     * Devuelve la cuenta correspondiente al nombre de usuario indicado.
     * @param userName Nombnre de usuario
     * @return Cuenta correspondiente
     */
    Account findByUserName(String userName);
}

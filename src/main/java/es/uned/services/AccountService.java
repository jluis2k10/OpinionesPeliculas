package es.uned.services;

import es.uned.entities.Account;

/**
 * Proporciona servicio para manejar cuentas de usuario.
 * <p>
 * Forma parte de la capa de servicio de la aplicación, es decir se encarga
 * de tratar los datos antes de enviarlos a la capa de persistencia o tras
 * recibirlos de la capa de persistencia.
 */
public interface AccountService {

    /**
     * Persiste una cuenta de usuario.
     * @param account Cuenta a persistir
     */
    void save(Account account);

    /**
     * Busca una cuenta de usuario por su nombre de usuario.
     * @param userName nombre de usuario a buscar
     * @return Cuenta de usuario coincidente con el nombre de usuario
     */
    Account findByUserName(String userName);

}

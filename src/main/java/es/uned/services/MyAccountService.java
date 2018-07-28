package es.uned.services;

import es.uned.entities.Account;
import es.uned.repositories.AccountRepo;
import es.uned.repositories.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementación de la interfaz {@link AccountService} para dar servicio
 * a operaciones sobre cuentas de usuario ({@link Account}).
 */
@Service
public class MyAccountService implements AccountService {

    @Autowired private AccountRepo accountRepo;
    @Autowired private RoleRepo roleRepo;

    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * {@inheritDoc}
     * @param account Cuenta a persistir
     */
    public void save(Account account) {
        // Si el password está vacío quiere decir que no se ha modificado y debemos mantener el existente
        if (account.getPassword() == null)
            account.setPassword(accountRepo.findOne(account.getId()).getPassword());
        else
            account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
        if (account.getRoles().isEmpty())
            account.getRoles().add(roleRepo.findByRole("USER"));
        accountRepo.save(account);
    }

    /**
     * {@inheritDoc}
     * @param userName nombre de usuario a buscar
     * @return Cuenta de usuario
     */
    public Account findByUserName(String userName) {
        return accountRepo.findByUserName(userName);
    }
}

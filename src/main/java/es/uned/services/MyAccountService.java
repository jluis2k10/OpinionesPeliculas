package es.uned.services;

import es.uned.entities.Account;
import es.uned.repositories.AccountRepo;
import es.uned.repositories.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class MyAccountService implements AccountService {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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

    public Account findByUserName(String userName) {
        return accountRepo.findByUserName(userName);
    }
}

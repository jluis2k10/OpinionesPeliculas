package es.uned.services;

import es.uned.entities.Account;
import es.uned.entities.AccountRole;
import es.uned.repositories.AccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service("myUserDetailsService")
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepo accountRepo;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepo.findByUserName(username);
        if (account == null)
            throw new UsernameNotFoundException("Usuario no encontrado");
        return new User(account.getUserName(), account.getPassword(), account.isActive(),
                true, true, true, getGrantedAuthorities(account));
    }

    private List<GrantedAuthority> getGrantedAuthorities(Account account) {
        List<GrantedAuthority> auths = new ArrayList<>();
        for (AccountRole accountRole : account.getAccountRoles())
            auths.add(new SimpleGrantedAuthority("ROLE_" + accountRole.getRole()));
        return auths;
    }

}
package es.uned.services;

import es.uned.entities.Account;
import es.uned.entities.AdapterModels;
import es.uned.repositories.AccountRepo;
import es.uned.repositories.AdapterModelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Set;

/**
 *
 */
@Service
public class MyAdapterModelService implements AdapterModelService {

    @Autowired
    private AdapterModelRepo adapterModelRepo;
    @Autowired
    private AccountRepo accountRepo;


    @Override
    public void save(AdapterModels adapterModels) {
        adapterModelRepo.save(adapterModels);
    }

    @Override
    public Set<AdapterModels> findByAdapterClass(String adapterClass, Long userID) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Account account = null;
        if (!auth.getPrincipal().equals("anonymousUser")) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            account = accountRepo.findByUserName(userDetails.getUsername());
        }
        if (account != null) {
            if (account.getId() == userID) {
                // sólo los modelos del usuario
                return adapterModelRepo.findAllByAdapterClassAndOwner(adapterClass, account);
            } else if (account.isAdmin()) {
                // todos los modelos disponibles
                return adapterModelRepo.findAllByAdapterClass(adapterClass);
            } else {
                // los modelos del usuario + los que sean públicos
                return adapterModelRepo.findByAdapterClassAndOwner_OrAdapterClassAndIsPublicTrue(adapterClass, account, adapterClass);
            }
        } else {
            // sólo modelos públicos
            return adapterModelRepo.findByAdapterClassAndIsPublicTrue(adapterClass);
        }
    }
}

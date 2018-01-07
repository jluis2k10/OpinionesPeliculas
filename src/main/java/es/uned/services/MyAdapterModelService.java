package es.uned.services;

import es.uned.adapters.AdapterType;
import es.uned.entities.Account;
import es.uned.entities.AdapterModels;
import es.uned.repositories.AdapterModelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 *
 */
@Service
public class MyAdapterModelService implements AdapterModelService {

    @Autowired
    private AdapterModelRepo adapterModelRepo;

    @Override
    public void save(AdapterModels adapterModels) {
        adapterModelRepo.save(adapterModels);
    }

    @Override
    public AdapterModels findOne(Long id) {
        return adapterModelRepo.findOne(id);
    }

    @Override
    public Set<AdapterModels> findUserModels(Account account, AdapterType adapterType) {
        return adapterModelRepo.findByOwnerAndAdapterType(account, adapterType);
    }

    @Override
    public Set<AdapterModels> findFromOthers(Account account, AdapterType adapterType) {
        return adapterModelRepo.findByOwnerNotAndAdapterType(account, adapterType);
    }

    @Override
    public Set<AdapterModels> findByType(AdapterType adapterType) {
        return adapterModelRepo.findByAdapterType(adapterType);
    }

    @Override
    public Set<AdapterModels> findByAdapterClass(String adapterClass, Account account) {
        if (account != null && account.isAdmin())
            return adapterModelRepo.findAllByAdapterClass(adapterClass);
        else
            return adapterModelRepo.findByAdapterClassAndOwner_OrAdapterClassAndOpenTrue(adapterClass, account, adapterClass);
    }
}

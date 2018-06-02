package es.uned.services;

import es.uned.adapters.ClassifierType;
import es.uned.entities.Account;
import es.uned.entities.AdapterModels;

import java.util.Set;

/**
 *
 */
public interface AdapterModelService {

    void save(AdapterModels adapterModels);
    boolean delete(String adapterPath, AdapterModels adapterModels);
    AdapterModels findOne(Long id);
    Set<AdapterModels> findUserModels(Account account, ClassifierType adapterType);
    Set<AdapterModels> findFromOthers(Account account, ClassifierType adapterType);
}

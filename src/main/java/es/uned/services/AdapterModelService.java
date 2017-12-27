package es.uned.services;

import es.uned.entities.AdapterModels;

import java.util.Set;

/**
 *
 */
public interface AdapterModelService {

    void save(AdapterModels adapterModels);
    Set<AdapterModels> findByAdapterClass(String adapterClass);

}

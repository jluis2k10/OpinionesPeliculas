package es.uned.services;

import es.uned.entities.AdapterModel;

import java.util.Set;

/**
 *
 */
public interface AdapterModelService {

    void save(AdapterModel adapterModel);
    Set<AdapterModel> findByAdapterClass(String adapterClass);

}

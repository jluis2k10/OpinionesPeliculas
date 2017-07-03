package es.uned.services;

import es.uned.entities.AdapterModel;

import java.util.Set;

/**
 *
 */
public interface AdapterModelService {

    Set<AdapterModel> findByAdapterClass(String adapterClass);

}

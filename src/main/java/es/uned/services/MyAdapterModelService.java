package es.uned.services;

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

    private final AdapterModelRepo adapterModelRepo;

    @Autowired
    public MyAdapterModelService(AdapterModelRepo adapterModelRepo) {
        this.adapterModelRepo = adapterModelRepo;
    }

    @Override
    public void save(AdapterModels adapterModels) {
        adapterModelRepo.save(adapterModels);
    }

    @Override
    public Set<AdapterModels> findByAdapterClass(String adapterClass) {
        return adapterModelRepo.findAllByAdapterClass(adapterClass);
    }
}

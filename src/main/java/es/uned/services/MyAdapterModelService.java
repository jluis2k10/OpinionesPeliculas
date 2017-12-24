package es.uned.services;

import es.uned.entities.AdapterModel;
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
    public void save(AdapterModel adapterModel) {
        adapterModelRepo.save(adapterModel);
    }

    @Override
    public Set<AdapterModel> findByAdapterClass(String adapterClass) {
        return adapterModelRepo.findAllByAdapterClass(adapterClass);
    }
}

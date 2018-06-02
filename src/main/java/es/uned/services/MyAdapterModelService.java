package es.uned.services;

import es.uned.adapters.ClassifierType;
import es.uned.entities.Account;
import es.uned.entities.AdapterModels;
import es.uned.repositories.AdapterModelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.util.Set;

/**
 *
 */
@Service
public class MyAdapterModelService implements AdapterModelService {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private AdapterModelRepo adapterModelRepo;

    @Override
    public void save(AdapterModels adapterModels) {
        adapterModelRepo.save(adapterModels);
    }

    @Override
    public boolean delete(String adapterPath, AdapterModels adapterModels) {
        Resource dir = resourceLoader.getResource(adapterPath + adapterModels.getLocation());
        try {
            FileSystemUtils.deleteRecursively(dir.getFile());
            adapterModelRepo.delete(adapterModels.getId());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public AdapterModels findOne(Long id) {
        return adapterModelRepo.findOne(id);
    }

    @Override
    public Set<AdapterModels> findUserModels(Account account, ClassifierType adapterType) {
        return adapterModelRepo.findByOwnerAndAdapterType(account, adapterType);
    }

    @Override
    public Set<AdapterModels> findFromOthers(Account account, ClassifierType adapterType) {
        return adapterModelRepo.findByOwnerNotAndAdapterType(account, adapterType);
    }
}

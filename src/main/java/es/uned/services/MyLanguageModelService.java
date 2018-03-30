package es.uned.services;

import es.uned.entities.Account;
import es.uned.entities.LanguageModel;
import es.uned.repositories.LanguageModelsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 *
 */
@Service
public class MyLanguageModelService implements LanguageModelService {

    @Autowired
    LanguageModelsRepo languageModelsRepo;

    @Override
    public LanguageModel findOne(Long id) {
        return languageModelsRepo.findOne(id);
    }

    @Override
    public Set<LanguageModel> findByAdapterClassAndLang(String adapterClass, String lang, Account owner) {
        return languageModelsRepo.findByAdapterClassAndLanguageAndOwner_OrAdapterClassAndLanguageAndIsPublicTrue(adapterClass, lang, owner, adapterClass, lang);
    }
}

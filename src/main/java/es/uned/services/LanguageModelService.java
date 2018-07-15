package es.uned.services;

import es.uned.adapters.ClassifierType;
import es.uned.entities.Account;
import es.uned.entities.LanguageModel;

import java.util.Set;

/**
 *
 */
public interface LanguageModelService {

    LanguageModel findOne(Long id);
    Set<LanguageModel> findByAdapterClassAndLang(String adapterClass, String lang, Account owner);
    Set<LanguageModel> findUserModels(Account account, ClassifierType adapterType);
    Set<LanguageModel> findFromOthers(Account account, ClassifierType adapterType);
    boolean delete(String adapterPath, LanguageModel languageModel);
    void save(LanguageModel languageModel);

}

package es.uned.services;

import es.uned.entities.Account;
import es.uned.entities.LanguageModel;

import java.util.Set;

/**
 *
 */
public interface LanguageModelService {

    LanguageModel findOne(Long id);
    Set<LanguageModel> findByAdapterClassAndLang(String adapterClass, String lang, Account owner);

}

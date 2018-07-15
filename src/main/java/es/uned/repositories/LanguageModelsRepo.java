package es.uned.repositories;

import es.uned.adapters.ClassifierType;
import es.uned.entities.Account;
import es.uned.entities.LanguageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 *
 */
@Repository
public interface LanguageModelsRepo extends JpaRepository<LanguageModel, Long> {

    Set<LanguageModel> findByAdapterClassAndLanguageAndOwner_OrAdapterClassAndLanguageAndIsPublicTrue(String adapterClass, String lang, Account owner, String adapterClass2, String lang2);
    Set<LanguageModel> findByOwnerAndClassifierType(Account account, ClassifierType classifierType);
    Set<LanguageModel> findByOwnerNotAndClassifierType(Account account, ClassifierType classifierType);

}

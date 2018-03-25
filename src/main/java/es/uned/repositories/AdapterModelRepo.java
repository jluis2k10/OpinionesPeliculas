package es.uned.repositories;

import es.uned.adapters.ClassifierType;
import es.uned.entities.Account;
import es.uned.entities.AdapterModels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 *
 */
@Repository
public interface AdapterModelRepo extends JpaRepository<AdapterModels, Long> {

    Set<AdapterModels> findByOwnerAndAdapterType(Account account, ClassifierType adapterType);
    Set<AdapterModels> findByOwnerNotAndAdapterType(Account account, ClassifierType adapterType);
    Set<AdapterModels> findByAdapterType(ClassifierType adapterType);
    Set<AdapterModels> findAllByAdapterClass(String adapterClass);
    Set<AdapterModels> findAllByAdapterClassAndOwner(String adapterClass, Account account);
    Set<AdapterModels> findByAdapterClassAndOwner_OrAdapterClassAndOpenTrue(String adapterClass, Account account, String adapterClass2);
    Set<AdapterModels> findByAdapterClassAndLanguageAndOwner_OrAdapterClassAndLanguageAndOpenTrue(String adapterClass, String lang, Account account, String adapterClass2, String lang2);
    Set<AdapterModels> findByAdapterClassAndOpenTrue(String adapterClass);

}

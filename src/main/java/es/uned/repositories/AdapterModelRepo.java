package es.uned.repositories;

import es.uned.adapters.AdapterType;
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

    Set<AdapterModels> findByOwnerAndAdapterType(Account account, AdapterType adapterType);
    Set<AdapterModels> findByOwnerNotAndAdapterType(Account account, AdapterType adapterType);
    Set<AdapterModels> findByAdapterType(AdapterType adapterType);
    Set<AdapterModels> findAllByAdapterClass(String adapterClass);
    Set<AdapterModels> findAllByAdapterClassAndOwner(String adapterClass, Account account);
    Set<AdapterModels> findByAdapterClassAndOwner_OrAdapterClassAndOpenTrue(String adapterClass, Account account, String adapterClass2);
    Set<AdapterModels> findByAdapterClassAndOpenTrue(String adapterClass);

}

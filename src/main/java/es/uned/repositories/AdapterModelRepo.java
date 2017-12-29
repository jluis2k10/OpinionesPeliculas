package es.uned.repositories;

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

    Set<AdapterModels> findAllByAdapterClass(String adapterClass);
    Set<AdapterModels> findAllByAdapterClassAndOwner(String adapterClass, Account account);
    Set<AdapterModels> findByAdapterClassAndOwner_OrAdapterClassAndIsPublicTrue(String adapterClass, Account account, String adapterClass2);
    Set<AdapterModels> findByAdapterClassAndIsPublicTrue(String adapterClass);

}

package es.uned.repositories;

import es.uned.entities.AdapterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 *
 */
@Repository
public interface AdapterModelRepo extends JpaRepository<AdapterModel, Long> {

    Set<AdapterModel> findAllByAdapterClass(String adapterClass);

}

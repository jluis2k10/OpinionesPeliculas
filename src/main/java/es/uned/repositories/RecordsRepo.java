package es.uned.repositories;

import es.uned.entities.Record;
import es.uned.entities.RecordID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 */
@Repository
public interface RecordsRepo extends JpaRepository<Record, RecordID> {
}

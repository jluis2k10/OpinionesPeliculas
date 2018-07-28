package es.uned.services;

import es.uned.entities.Record;
import es.uned.entities.RecordID;
import es.uned.repositories.RecordsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de la interfaz {@link RecordsService} para dar servicio
 * a operaciones sobre records ({@link Record}).
 */
@Service
public class MyRecordsService implements RecordsService {

    @Autowired private RecordsRepo recordsRepo;

    /**
     * {@inheritDoc}
     * @param record record a persistir
     */
    @Override
    public void save(Record record) {
        recordsRepo.save(record);
    }

    /**
     * {@inheritDoc}
     * @param recordID identificador del record a encontrar
     * @return Record encontrado
     */
    @Override
    public Record findOne(RecordID recordID) {
        return recordsRepo.findOne(recordID);
    }

    /**
     * {@inheritDoc}
     * @param record Record a eliminar
     */
    @Override
    public void delete(Record record) {
        recordsRepo.delete(record);
    }

    /**
     * {@inheritDoc}
     * @param analysisID identificador del análisis
     */
    @Transactional
    @Override
    public void deleteByAnalysis(Long analysisID) {
        recordsRepo.deleteRecordByAnalysis_Id(analysisID);
    }
}

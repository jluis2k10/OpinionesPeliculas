package es.uned.services;

import es.uned.entities.Record;
import es.uned.entities.RecordID;

/**
 *
 */
public interface RecordsService {

    void save(Record record);
    Record findOne(RecordID recordID);
    void delete(Record record);
    void deleteByAnalysis(Long analysisID);

}

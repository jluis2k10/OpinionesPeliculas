package es.uned.services;

import es.uned.entities.Record;
import es.uned.entities.RecordID;
import es.uned.repositories.RecordsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service
public class MyRecordsService implements RecordsService {

    @Autowired
    RecordsRepo recordsRepo;

    @Override
    public void save(Record record) {
        recordsRepo.save(record);
    }

    @Override
    public Record findOne(RecordID recordID) {
        return recordsRepo.findOne(recordID);
    }

    @Override
    public void delete(Record record) {
        recordsRepo.delete(record);
    }

    @Transactional
    @Override
    public void deleteByAnalysis(Long analysisID) {
        recordsRepo.deleteRecordByAnalysis_Id(analysisID);
    }
}

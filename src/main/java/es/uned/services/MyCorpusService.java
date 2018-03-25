package es.uned.services;

import es.uned.entities.Corpus;
import es.uned.entities.Record;
import es.uned.entities.RecordID;
import es.uned.repositories.CorporaRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

/**
 *
 */
@Service
public class MyCorpusService implements CorpusService {

    @Autowired
    CorporaRepo corporaRepo;

    @Autowired
    CommentsService commentsService;

    @Autowired
    AnalysisService analysisService;

    @Autowired
    RecordsService recordsService;

    @Autowired
    EntityManager entityManager;


    @Override
    public Corpus findOne(Long id) {
        return corporaRepo.findOne(id);
    }

    @Transactional
    @Override
    public void save(Corpus corpus) {
        corpus.getAnalyses().forEach(analysis ->{
            if (analysis.getId() == null) {
                analysisService.save(analysis);
                analysis.getRecords().forEach(record -> record.getId().setAnalysis(analysis.getId()));
            }
        });
        corpus.getComments().forEach(comment -> {
            if (comment.getId() == null) {
                commentsService.save(comment);
                comment.getRecords().forEach(record -> record.getId().setComment(comment.getId()));
            }
            else {
                // Puede haber comentarios con nuevos records y hay que guardarlos antes
                comment.getRecords().forEach(record -> saveNewRecord(record));
                commentsService.save(comment);
            }
        });
        corpus.getAnalyses().forEach(analysis ->
            analysis.getRecords().forEach(record -> saveNewRecord(record))
        );
        corporaRepo.save(corpus);
    }

    @Override
    public void delete(Corpus corpus) {
        corpus.getComments().forEach(comment -> {
            comment.getRecords().forEach(record -> recordsService.delete(record));
            comment.clearRecords();
            commentsService.delete(comment);
        });
        corpus.getAnalyses().forEach(analysis -> {
            analysis.clearRecords();
            analysisService.delete(analysis);
        });
        corpus.clearAll();
        corporaRepo.delete(corpus);
    }

    private void saveNewRecord(Record record) {
        Record existingRecord = recordsService.findOne(record.getId());
        if (existingRecord == null) {
            record.setId(new RecordID(null, null));
            recordsService.save(record);
        }
    }
}

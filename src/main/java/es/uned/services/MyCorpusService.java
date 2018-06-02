package es.uned.services;

import es.uned.entities.Account;
import es.uned.entities.Corpus;
import es.uned.entities.Record;
import es.uned.entities.RecordID;
import es.uned.repositories.CorporaRepo;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    public Corpus findOne(Long id) {
        return corporaRepo.findOne(id);
    }

    /**
     * Al actualizar el corpus, es necesario que las diferentes colecciones de elementos que
     * contiene (comentarios, análisis y records) no se recuperen mediante "Lazy Loading",
     * así que obligamos a que hibernate las inicialice todas.
     * @param id
     * @return
     */
    @Override
    public Corpus findOneFetchAll(Long id) {
        Corpus corpus = findOne(id);
        Hibernate.initialize(corpus.getComments());
        Hibernate.initialize(corpus.getAnalyses());
        corpus.getComments().forEach(comment -> Hibernate.initialize(comment.getRecords()));
        corpus.getAnalyses().forEach(analysis -> Hibernate.initialize(analysis.getRecords()));
        return corpus;
    }

    @Override
    public List<Corpus> findByOwner(Account owner) {
        return corporaRepo.findAllByOwner(owner);
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
    public void quickSave(Corpus corpus) {
        corporaRepo.save(corpus);
    }

    @Override
    public void delete(Corpus corpus) {
        corpus.getAnalyses().forEach(analysis -> analysisService.delete(analysis));
        commentsService.deleteByCorpus(corpus.getId());
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

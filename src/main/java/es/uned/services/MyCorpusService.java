package es.uned.services;

import es.uned.entities.Account;
import es.uned.entities.Corpus;
import es.uned.repositories.CorporaRepo;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación de la interfaz {@link CorpusService} para dar servicio
 * a operaciones sobre cuentas de usuario ({@link Corpus}).
 */
@Service
public class MyCorpusService implements CorpusService {

    @Autowired private CorporaRepo corporaRepo;
    @Autowired private CommentsService commentsService;
    @Autowired private AnalysisService analysisService;
    @Autowired private RecordsService recordsService;

    /**
     * {@inheritDoc}
     * @param id identificador del corpus a encontrar
     * @return Corpus encontrado
     */
    @Override
    public Corpus findOne(Long id) {
        return corporaRepo.findOne(id);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Al actualizar el corpus, es necesario que las diferentes colecciones de elementos que
     * contiene (comentarios, análisis y records) no se recuperen mediante "Lazy Loading",
     * así que obligamos a que hibernate las inicialice todas.
     * @param id identificador del corpus a encontrar
     * @return Corpus encontrado (evitanto lazy loading)
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

    /**
     * {@inheritDoc}
     * @param owner Cuenta del usuario que ha creado los corpus
     * @return Lista de corpus creados por el usuario
     */
    @Override
    public List<Corpus> findByOwner(Account owner) {
        return corporaRepo.findAllByOwner(owner);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Antes de guardar el corpus necesitamos guardar todos los cambios que se hayan producido
     * en sus diferentes elementos (análisis ejecutados, comentarios que lo componen y récords
     * con los resultados de los análisis para cada comentario). De este modo evitamos
     * posibles inconsistencias en la base de datos y sus consecuentes errores a la hora de
     * persistir la entidad.
     * @param corpus Corpus a persistir
     */
    @Transactional
    @Override
    public void save(Corpus corpus) {
        // Guardamos los nuevos análisis ejecutados sobre el corpus. De este modo se les asigna
        // automáticamente un nuevo identificador que a su vez utilizaremos para indicar, en cada
        // record, el análisis al cual se corresponde.
        corpus.getAnalyses().forEach(analysis ->{
            if (analysis.getId() == null) {
                analysisService.save(analysis);
                analysis.getRecords().forEach(record -> record.getId().setAnalysis(analysis.getId()));
            }
        });
        // Guardamos los nuevos comentarios que tiene el corpus. De este modo, se les asigna automáticamente
        // un nuevo identificador que a su vez utilizaremos para indicar, en cada record, el comentario
        // con el cual se corresponde.
        corpus.getComments().forEach(comment -> {
            if (comment.getId() == null) {
                commentsService.save(comment);
                comment.getRecords().forEach(record -> record.getId().setComment(comment.getId()));
            }
            else {
                // Puede haber comentarios ya existentes pero con nuevos records y hay que guardarlos antes
                comment.getRecords().forEach(record -> recordsService.save(record));
                commentsService.save(comment);
            }
        });
        // Una vez hemos persistido los análisis y los comentarios y tenemos todos los records
        // actualizados con sus identificadores, podemos persistirlos
        corpus.getAnalyses().forEach(analysis ->
            analysis.getRecords().forEach(record -> recordsService.save(record))
        );
        // Por último persistimos el corpus
        corporaRepo.save(corpus);
    }

    /**
     * {@inheritDoc}
     * @param corpus Corpus a persistir
     */
    @Override
    public void quickSave(Corpus corpus) {
        corporaRepo.save(corpus);
    }

    /**
     * {@inheritDoc}
     * @param corpus Corpus a eliminar
     */
    @Override
    public void delete(Corpus corpus) {
        corpus.getAnalyses().forEach(analysis -> analysisService.delete(analysis));
        commentsService.deleteByCorpus(corpus.getId());
        corpus.clearAll();
        corporaRepo.delete(corpus);
    }
}

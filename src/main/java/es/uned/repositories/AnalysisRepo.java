package es.uned.repositories;

import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.entities.LanguageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Repositorio para la entidad {@link Analysis}
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre
 * la aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen. Spring Data se
 * encarga de traducirlos a las consultas SQL correspondientes de forma automática.
 */
@Repository
public interface AnalysisRepo extends JpaRepository<Analysis, Long> {

    /**
     * Devuelve todos los análisis ejecutados que hayan empleado para ello
     * el modelo de lenguaje indicado.
     * @param languageModel Modelo de lenguaje que ha sido utilizado en el análisis
     * @return Conjunto de análisis
     */
    Set<Analysis> findByLanguageModel(LanguageModel languageModel);

    /**
     * Devuelve cuántos análisis existen en la base de datos que hayan utilizado
     * el modelo de lenguaje indicado para su ejecución.
     * @param languageModel Modelo de lenguaje utilizado en los análisis
     * @return Cantidad total de análisis
     */
    int countByLanguageModel(LanguageModel languageModel);

}

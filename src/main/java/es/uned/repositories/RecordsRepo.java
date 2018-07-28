package es.uned.repositories;

import es.uned.entities.Record;
import es.uned.entities.RecordID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Record}
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre
 * la aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen. Spring Data se
 * encarga de traducirlos a las consultas SQL correspondientes de forma automática.
 */
@Repository
public interface RecordsRepo extends JpaRepository<Record, RecordID> {

    /**
     * Elimina de la base de datos todos los records generados por el análisis dado.
     * @param analysisID Identificador del análisis
     */
    void deleteRecordByAnalysis_Id(Long analysisID);

}

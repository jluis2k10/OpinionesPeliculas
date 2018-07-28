package es.uned.services;

import es.uned.entities.Record;
import es.uned.entities.RecordID;

/**
 * Proporciona servicio para manejar records ({@link Record}).
 * <p>
 * Forma parte de la capa de servicio de la aplicación, es decir se encarga
 * de tratar los datos antes de enviarlos a la capa de persistencia o tras
 * recibirlos de la capa de persistencia.
 */
public interface RecordsService {

    /**
     * Persiste un record
     * @param record record a persistir
     */
    void save(Record record);

    /**
     * Busca un record por su identeficador
     * @param recordID identificador del record a encontrar
     * @return Record encontrado
     */
    Record findOne(RecordID recordID);

    /**
     * Elimina un record de la base de datos
     * @param record Record a eliminar
     */
    void delete(Record record);

    /**
     * Elimina todos los records pertenecientes a un análisis dados (todos
     * los records creados durante la ejecución de un análisis).
     * @param analysisID identificador del análisis
     */
    void deleteByAnalysis(Long analysisID);

}

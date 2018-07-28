package es.uned.services;

import es.uned.entities.Account;
import es.uned.entities.Corpus;

import java.util.List;

/**
 * Proporciona servicio para manejar los corpus ({@link Corpus}).
 * <p>
 * Forma parte de la capa de servicio de la aplicación, es decir se encarga
 * de tratar los datos antes de enviarlos a la capa de persistencia o tras
 * recibirlos de la capa de persistencia.
 */
public interface CorpusService {

    /**
     * Busca un corpus por su identificador
     * @param id identificador del corpus a encontrar
     * @return Corpus encontrado
     */
    Corpus findOne(Long id);

    /**
     * Busca un corpus por su identificador.
     * <p>
     * Asimismo recuperamos todos los elementos que contiene el corpus en esta misma
     * operación, evitando el "lazy loading" por defecto de hibernate.
     * @param id identificador del corpus a encontrar
     * @return Corpus encontrado
     */
    Corpus findOneFetchAll(Long id);

    /**
     * Busca todos los corpus pertenecientes a un usuario (creados por el usuario).
     * @param owner Cuenta del usuario que ha creado los corpus
     * @return Lista de corpus creados por el usuario
     */
    List<Corpus> findByOwner(Account owner);

    /**
     * Persiste un corpus
     * @param corpus Corpus a persistir
     */
    void save(Corpus corpus);

    /**
     * Persiste un corpus sin preocuparse por la consistencia de los elementos
     * que lo componen (comentarios, análisis y récords).
     * <p>
     * Útil para cuando sabemos que sólo hemos realizado cambios en la entidad corpus
     * como tal y no en ninguna de las otras entidades que lo componen (análisis,
     * comentarios y records). Con este método nos ahorramos llamadas a operaciones
     * de actualización en la base de datos.
     * <p>
     * Por ejemplo si sólo cambiamos la propiedad {@link Corpus#isPublic} del corpus
     * no sería necesario tener que recorrer todos los comentarios, análisis y records
     * que componen el corpus actualizándolos en la base de dastos, ya que estos no
     * cambian.
     * @param corpus Corpus a persistir
     */
    void quickSave(Corpus corpus);

    /**
     * Elimina un corpus de la base de datos
     * @param corpus Corpus a eliminar
     */
    void delete(Corpus corpus);

}

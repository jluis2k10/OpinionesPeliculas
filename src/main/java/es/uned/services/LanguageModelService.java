package es.uned.services;

import es.uned.adapters.ClassifierType;
import es.uned.entities.Account;
import es.uned.entities.LanguageModel;

import java.util.Set;

/**
 * Proporciona servicio para manejar modelos de lenguaje ({@link LanguageModel}).
 * <p>
 * Forma parte de la capa de servicio de la aplicación, es decir se encarga
 * de tratar los datos antes de enviarlos a la capa de persistencia o tras
 * recibirlos de la capa de persistencia.
 */
public interface LanguageModelService {

    /**
     * Busca un modelo de lenguaje por su identificador
     * @param id identificador del modelo de lenguaje
     * @return modelo de lenguaje encontrado
     */
    LanguageModel findOne(Long id);

    /**
     * Devuelve un conjunto de modelos de lenguaje según el adaptador que los utilice, su idioma
     * y el usuario que los ha creado.
     * @param adapterClass adaptador (de clasificador) que utiliza los modelos de lenguaje
     * @param lang         idioma de los modelos de lenguaje
     * @param owner        usuario propietario (creador) de los modelos de lenguaje
     * @return conjunto de modelos de lenguaje
     */
    Set<LanguageModel> findByAdapterClassAndLang(String adapterClass, String lang, Account owner);

    /**
     * Devuelve un conjunto de modelos de lenguaje creados por el usuario dado y del tipo de
     * tipo de clasificador (opinión, polaridad) para el cual han sido creados.
     * @param account     usuario propietarios de los modelos de lenguaje
     * @param adapterType tipo de clasificador utilizado
     * @return conjunto de modelos de lenguaje
     */
    Set<LanguageModel> findUserModels(Account account, ClassifierType adapterType);

    /**
     * Devuelve el conjunto de modelos de lenguaje no creados por el usuario dado (útil para
     * que el usuario administrador tenga acceso a ellos) y del tipo de clasificador (opinión,
     * polaridad) para el cual han sido creados.
     * @param account     cuenta de usuario no propietario de los modelos de lenguaje
     * @param adapterType tipo de clasificador utilizado
     * @return conjunto de modelos de lenguaje
     */
    Set<LanguageModel> findFromOthers(Account account, ClassifierType adapterType);

    /**
     * Elimina un modelo de lenguaje de la base de datos y borra su archivo serializado en disco.
     * @param adapterPath   ruta total o parcial donde se encuentra almacenado en disco el archivo
     *                      serializado del modelo
     * @param languageModel modelo de lenguaje a eliminar
     * @return true en caso de que la operación haya tenido éxito, false en caso contrario
     */
    boolean delete(String adapterPath, LanguageModel languageModel);

    /**
     * Persiste un modelo de lenguaje
     * @param languageModel modelo de lenguaje a persistir
     */
    void save(LanguageModel languageModel);

}

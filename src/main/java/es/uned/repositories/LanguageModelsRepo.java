package es.uned.repositories;

import es.uned.adapters.ClassifierType;
import es.uned.entities.Account;
import es.uned.entities.LanguageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Repositorio para la entidad {@link LanguageModel}
 * <p>
 * Se comporta como un DAO (Data Access Object), proporcionando una interfaz entre
 * la aplicación y la base de datos donde persisten las entidades.
 * <p>
 * No es necesario implementar los métodos que aquí se exponen. Spring Data se
 * encarga de traducirlos a las consultas SQL correspondientes de forma automática.
 */
@Repository
public interface LanguageModelsRepo extends JpaRepository<LanguageModel, Long> {

    /**
     * Devuelve el conjunto de modelos de lenguaje utilizados por un adaptador y pertenecientes
     * a un usuario en un idioma concreto más los utilzados por un adaptador y que sean públicos
     * en un idioma concreto. Se utiliza haciendo que sus parámetros adapterClass y lang sean
     * idénticos a adapterClass2 y lang2.
     * <p>
     * Es decir este método encuentra todos los modelos de lenguaje que:
     * <li>Se utilizan en el adaptador dado</li>
     * <li>Sean en el idioma dado</li>
     * <li>Su propietario (creador) sea el usuario dado</li>
     * <li>O sean públicos</li>
     * @param adapterClass  adaptador que utiliza los modelos de lenguaje
     * @param lang          idioma de los modelos de lenguaje
     * @param owner         usuario propietario (creador) de los modelos de lenguaje
     * @param adapterClass2 adaptador que utiliza los modelos de lenguaje
     * @param lang2         idioma de los modelos de lenguaje
     * @return Conjunto de modelos de lenguaje
     */
    Set<LanguageModel> findByAdapterClassAndLanguageAndOwner_OrAdapterClassAndLanguageAndIsPublicTrue(String adapterClass, String lang, Account owner, String adapterClass2, String lang2);

    /**
     * Encuentra todos los modelos de lenguaje pertenecientes a un usuario dado y cuyo tipo de clasificador
     * (opinión o polaridad) sea el indicado.
     * @param account        usuario propietario (creador) de los modelos de lenguaje
     * @param classifierType tipo de clasificador utilizado por el modelo de lenguaje
     * @return Conjunto de modelos de lenguaje
     */
    Set<LanguageModel> findByOwnerAndClassifierType(Account account, ClassifierType classifierType);

    /**
     * Encuentra todos los modelos de lenguaje que no pertenezcan al usuario dado y couyo tipo de clasificador
     * (opinión o polaridad) sea el indicado.
     * <p>
     * Se utiliza para que los usuarios administradores tengan acceso a los modelos de lenguaje creados
     * por otros usuarios.
     * @param account        usuario administrador
     * @param classifierType tipo de clasificador utilizado por el modelo de lenguaje
     * @return Conjunto de modelos de lenguaje
     */
    Set<LanguageModel> findByOwnerNotAndClassifierType(Account account, ClassifierType classifierType);

}

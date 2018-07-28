package es.uned.adapters;

import es.uned.adapters.subjectivity.SubjectivityAdapter;
import es.uned.config.ApplicationContextConfig;

/**
 * Fábrica para adpatadores de análisis de opinión disponibles.
 * {@link ApplicationContextConfig#svcLocSubjectivityAdapterFactory()}
 */
public interface SubjectivityAdapterFactory {

    /**
     * Devuelve instancia del adaptador de análisis de opinión especificado.
     * @param adapter Adaptador solicitado. Debe coincidir con la etqueta @Component de su implementación
     * @return instancia del adaptador
     */
    SubjectivityAdapter get(String adapter);

}

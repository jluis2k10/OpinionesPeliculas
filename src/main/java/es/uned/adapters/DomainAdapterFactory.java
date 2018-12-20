package es.uned.adapters;

import es.uned.adapters.domain.DomainAdapter;
import es.uned.config.ApplicationContextConfig;

/**
 * Fábrica para adaptadores de análisis de dominio disponibles.
 * {@link ApplicationContextConfig#svcLocDomainAdapterFactory()}
 */
public interface DomainAdapterFactory {

    /**
     * Devuelve instancia del adaptador de sentimiento especificado.
     * @param adapter Adaptador solicitado. Debe coincidir con la etiqueta @Component de su implementación
     * @return instancia del adaptador
     */
    DomainAdapter get(String adapter);

}

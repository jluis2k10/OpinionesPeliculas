package es.uned.adapters;

import es.uned.adapters.sources.SourceAdapter;
import es.uned.config.ApplicationContextConfig;

/**
 * Fábrica para adaptadores de fuentes de comentarios disponibles.
 * {@link ApplicationContextConfig#svcLocSourceAdapterFactory()}
 */
public interface SourceAdapterFactory {

    /**
     * Devuelve instancia del adaptador de fuente de comentarios especificado.
     * @param adapter Adaptador solicitado. Debe coincidir con la etiqueta @Component de su implementación
     * @return instancia del adaptador
     */
    SourceAdapter get(String adapter);

}

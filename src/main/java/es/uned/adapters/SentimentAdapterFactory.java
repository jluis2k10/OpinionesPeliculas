package es.uned.adapters;

import es.uned.adapters.sentiment.SentimentAdapter;
import es.uned.config.ApplicationContextConfig;

/**
 * Fábrica para adaptadores de análisis de sentimiento disponibles.
 * {@link ApplicationContextConfig#svcLocSentimentAdapterFactory()}
 */
public interface SentimentAdapterFactory {

    /**
     * Devuelve instancia del adaptador de sentimiento especificado.
     * @param adapter Adaptador solicitado. Debe coincidir con la etiqueta @Component de su implementación
     * @return instancia del adaptador
     */
    SentimentAdapter get(String adapter);

}

package es.uned.adapters;

import es.uned.adapters.sentiment.SentimentAdapter;

/**
 *
 */
public interface SentimentAdapterFactory {

    SentimentAdapter get(String adapter);

}

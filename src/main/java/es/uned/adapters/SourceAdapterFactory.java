package es.uned.adapters;

import es.uned.adapters.sources.SourceAdapter;

/**
 *
 */
public interface SourceAdapterFactory {

    SourceAdapter get(String adapter);

}

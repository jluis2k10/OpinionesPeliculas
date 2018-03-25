package es.uned.adapters.sources;

import es.uned.entities.Corpus;
import es.uned.forms.SourceForm;

/**
 *
 */
public interface SourceAdapter {

    void setOptions(SourceForm sourceForm);
    void generateCorpus(Corpus corpus);
    int updateCorpus(SourceForm sourceForm, Corpus corpus);

}

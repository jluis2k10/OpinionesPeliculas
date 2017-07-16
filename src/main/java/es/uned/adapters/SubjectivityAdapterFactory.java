package es.uned.adapters;

import es.uned.adapters.subjectivity.SubjectivityAdapter;

/**
 *
 */
public interface SubjectivityAdapterFactory {

    SubjectivityAdapter get(String adapter);

}

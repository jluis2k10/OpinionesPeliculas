package es.uned.adapters.subjectivity;

import es.uned.adapters.AdapterType;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface SubjectivityAdapter {

    String MODELS_DIR = "/models/subjectivity";
    AdapterType adapterType = AdapterType.SUBJECTIVITY;

    void analyze(Map<Integer,CommentWithSentiment> comments, SearchParams search, Map<String,String> options);

    void trainModel(String modelLocation, List<String> subjectives, List<String> objectives);

    void createModel(String modelLocation, Map<String,String> options, List<String> subjectives, List<String> objectives);

}

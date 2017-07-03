package es.uned.adapters.sentiment;

import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;

import java.util.Map;

/**
 *
 */
public interface SentimentAdapter {

    void analyze(Map<Integer,CommentWithSentiment> comments, SearchParams search, Map<String,String> options);

    void createModel();

}

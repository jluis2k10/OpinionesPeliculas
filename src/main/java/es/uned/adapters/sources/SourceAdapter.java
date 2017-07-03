package es.uned.adapters.sources;

import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;

import java.util.HashMap;

/**
 *
 */
public interface SourceAdapter {

    HashMap<Integer, CommentWithSentiment> getComments(SearchParams params);

}

package es.uned.adapters.sources;

import es.uned.entities.CommentWithSentiment;
import es.uned.entities.Search;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedList;

/**
 *
 */
@Component("es.uned.adapters.sources.SentenceSearch")
public class SentenceSearch implements SourceAdapter {

    @Override
    public void doSearch(Search search) {
        LinkedList<CommentWithSentiment> comments = new LinkedList<>();
        CommentWithSentiment comment = new CommentWithSentiment.Builder()
                .search(search)
                .sourceUrl("")
                .date(new Date())
                .comment(search.getTerm())
                .build();
        comments.add(comment);
        search.setComments(comments);
    }

    @Override
    public int updateSearch(Search search) {
        return 0;
    }

}

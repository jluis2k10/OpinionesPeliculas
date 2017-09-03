package es.uned.adapters.subjectivity;

import com.datumbox.framework.applications.nlp.TextClassifier;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.core.common.dataobjects.Record;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.storage.inmemory.InMemoryConfiguration;
import es.uned.components.TwitterTokenizer;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@Component("es.uned.adapters.subjectivity.Datumbox")
public class Datumbox implements SubjectivityAdapter {

    @Autowired private ResourceLoader resourceLoader;
    @Autowired private TwitterTokenizer twitterTokenizer;

    /* Debe coincidir con ID del XML */
    private final String myID = "S01";

    @Override
    public void analyze(Map<Integer, CommentWithSentiment> comments, SearchParams search, Map<String, String> options) {
        Configuration configuration = Configuration.getConfiguration();
        InMemoryConfiguration memConfiguration = new InMemoryConfiguration();
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR);
        String modelsDirectory = null;
        try {
            modelsDirectory = resource.getFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        memConfiguration.setDirectory(modelsDirectory);
        configuration.setStorageConfiguration(memConfiguration);
        TextClassifier subjectivityClassifier = MLBuilder.load(TextClassifier.class, search.getSubjectivityModel(), configuration);

        twitterTokenizer.setLanguage(search.getLang());
        twitterTokenizer.setSearchTerm(search.getSearchTerm());
        Set<Integer> commentsToRemove = new HashSet<>();
        comments.forEach((k, comment) -> {
            String commentString = null;
            if (options.get(myID + "-preprocesar").equals("yes")) {
                if (!comment.isTokenized()) {
                    comment.setTokenized(true);
                    comment.setTokenizedComment(twitterTokenizer.cleanUp(comment.getComment()));
                }
                commentString = comment.getTokenizedComment();
            } else {
                commentString = comment.getComment();
            }
            Record subjectivity = subjectivityClassifier.predict(commentString);
            String pred = subjectivity.getYPredicted().toString();
            String prob = subjectivity.getYPredictedProbabilities().get(subjectivity.getYPredicted()).toString();
            comment.setPredictedSubjectivity(pred);
            comment.setSubjectivityScore(Double.parseDouble(prob));
            if (pred.equals("objective") && search.isDiscardNonSubjective())
                commentsToRemove.add(k);
            else
                comments.put(k, comment);
        });
        comments.keySet().removeAll(commentsToRemove);
    }

    @Override
    public void trainModel(String modelLocation, List<String> subjectives, List<String> objectives) {

    }

    @Override
    public void createModel(String modelLocation, Map<String,String> options, List<String> subjectives, List<String> objectives) {

    }
}

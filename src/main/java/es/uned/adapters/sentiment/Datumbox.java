package es.uned.adapters.sentiment;

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
import java.util.Map;

/**
 *
 */
@Component("es.uned.adapters.sentiment.Datumbox")
public class Datumbox implements SentimentAdapter {

    @Autowired private ResourceLoader resourceLoader;
    @Autowired private TwitterTokenizer twitterTokenizer;

    public void analyze(Map<Integer,CommentWithSentiment> comments, SearchParams search, Map<String,String> options) {
        Configuration configuration = Configuration.getConfiguration();
        InMemoryConfiguration memConfiguration = new InMemoryConfiguration();
        Resource resource = resourceLoader.getResource("classpath:/models");
        String modelsDirectory = null;
        try {
            modelsDirectory = resource.getFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        memConfiguration.setDirectory(modelsDirectory);
        configuration.setStorageConfiguration(memConfiguration);
        TextClassifier sentimentClassifier = MLBuilder.load(TextClassifier.class, search.getSentimentModel(), configuration);

        twitterTokenizer.setLanguage(search.getLang());
        twitterTokenizer.setSearchTerm(search.getSearchTerm());
        comments.forEach((k, comment) -> {
            if (options.get("preprocesar").equals("yes")) {
                comment.setTokenized(true);
                comment.setTokenizedComment(twitterTokenizer.cleanUp(comment.getComment()));
            } else {
                comment.setTokenizedComment(comment.getComment());
            }
            Record sentiment = sentimentClassifier.predict(comment.getTokenizedComment());
            String pred = sentiment.getYPredicted().toString();
            String prob = sentiment.getYPredictedProbabilities().get(sentiment.getYPredicted()).toString();
            comment.setPredictedSentiment(pred);
            comment.setSentimentScore(Double.parseDouble(prob));
            comments.put(k, comment);
        });
    }

    public void createModel() {
        return;
    }

}

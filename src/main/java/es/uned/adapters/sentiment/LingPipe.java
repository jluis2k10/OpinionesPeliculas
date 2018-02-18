package es.uned.adapters.sentiment;

import com.aliasi.classify.BaseClassifier;
import com.aliasi.classify.Classification;
import com.aliasi.classify.JointClassification;
import com.aliasi.util.AbstractExternalizable;
import es.uned.adapters.AdapterType;
import es.uned.adapters.common.CommonLingpipe;
import es.uned.components.Tokenizer;
import es.uned.entities.Search;
import es.uned.entities.Sentiment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 *
 */
@Component("es.uned.adapters.sentiment.LingPipe")
public class LingPipe extends CommonLingpipe implements SentimentAdapter {

    @Autowired
    private Tokenizer.TokenizerBuilder tokenizerBuilder;
    @Autowired
    private ResourceLoader resourceLoader;

    /* Debe coincidir con ID del XML */
    private final String myID = "P02";
    private static final String ADAPTER_DIR = "/lingpipe";

    @Override
    public String get_adapter_path() {
        return "classpath:" + MODELS_DIR + ADAPTER_DIR + "/";
    }

    @Override
    public AdapterType get_adapter_type() {
        return adapterType;
    }

    @Override
    public void analyze(Search search) {
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + ADAPTER_DIR + "/"  + search.getSentimentModel().getLocation() + "/classifier.model");
        File modelFile = null;
        BaseClassifier<String> classifier = null;
        try {
            modelFile = resource.getFile();
            classifier = (BaseClassifier<String>) AbstractExternalizable.readObject(modelFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Crear tokenizer
        Tokenizer tokenizer = tokenizerBuilder.searchTerm(search.getTerm())
                .language(search.getLang())
                .removeStopWords(search.isDelStopWords())
                .cleanTweet(search.isCleanTweet())
                .build();

        BaseClassifier<String> finalClassifier = classifier;

        search.getComments().forEach(comment -> {
            if (!comment.isTokenized()) {
                comment.setTokenized(true);
                comment.setTokenizedComment(tokenizer.tokenize(comment.getComment()));
            }
            Classification classification = finalClassifier.classify(comment.getTokenizedComment());
            switch (classification.bestCategory()) {
                case "pos":
                    comment.setSentiment(Sentiment.POSITIVE);
                    break;
                case "neg":
                    comment.setSentiment(Sentiment.NEGATIVE);
                    break;
            }
            comment.setSentimentScore(((JointClassification) classification).conditionalProbability(classification.bestCategory()));
            comment.setPositivityScore(((JointClassification) classification).conditionalProbability("pos"));
            comment.setNegativityScore(((JointClassification) classification).conditionalProbability("neg"));
        });

    }
}

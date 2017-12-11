package es.uned.adapters.subjectivity;

import com.aliasi.classify.BaseClassifier;
import com.aliasi.classify.Classification;
import com.aliasi.classify.JointClassification;
import com.aliasi.util.AbstractExternalizable;
import es.uned.adapters.AdapterType;
import es.uned.adapters.common.CommonLingpipe;
import es.uned.components.Tokenizer;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@Component("es.uned.adapters.subjectivity.Lingpipe")
public class Lingpipe extends CommonLingpipe implements SubjectivityAdapter {

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private Tokenizer.TokenizerBuilder tokenizerBuilder;

    /* Debe coincidir con ID del XML */
    private final String myID = "S02";
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
    public void analyze(Map<Integer, CommentWithSentiment> comments, SearchParams search, Map<String, String> options) {
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + ADAPTER_DIR + "/"  + search.getSubjectivityModel() + "/classifier.model");
        File modelFile = null;

        BaseClassifier<String> bClassifier = null;
        try {
            modelFile = resource.getFile();
            bClassifier = (BaseClassifier<String>) AbstractExternalizable.readObject(modelFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Crear tokenizer
        Tokenizer tokenizer = tokenizerBuilder.searchTerm(search.getSearchTerm())
                .language(search.getLang())
                .removeStopWords(search.isDelStopWords())
                .cleanTweet(search.isCleanTweet())
                .build();

        final BaseClassifier<String> finalClassifier = bClassifier;
        Set<Integer> commentsToRemove = new HashSet<>();
        comments.forEach((k, comment) -> {
            if (!comment.isTokenized()) {
                comment.setTokenized(true);
                comment.setTokenizedComment(tokenizer.tokenize(comment.getComment()));
            }
            Classification classification = finalClassifier.classify(comment.getTokenizedComment());
            comment.setPredictedSubjectivity(classification.bestCategory());
            comment.setSubjectivityScore(((JointClassification) classification).conditionalProbability(classification.bestCategory()));
            if (classification.bestCategory().equals("objective") && search.isDiscardNonSubjective())
                commentsToRemove.add(k);
        });
        comments.keySet().removeAll(commentsToRemove);
    }
}

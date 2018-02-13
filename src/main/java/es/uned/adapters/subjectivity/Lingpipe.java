package es.uned.adapters.subjectivity;

import com.aliasi.classify.BaseClassifier;
import com.aliasi.classify.Classification;
import com.aliasi.classify.JointClassification;
import com.aliasi.util.AbstractExternalizable;
import es.uned.adapters.AdapterType;
import es.uned.adapters.common.CommonLingpipe;
import es.uned.components.Tokenizer;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.Search;
import es.uned.entities.Subjectivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

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
    public void analyze(Search search) {
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + ADAPTER_DIR + "/"  + search.getSubjectivityModel().getLocation() + "/classifier.model");
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
        Tokenizer tokenizer = tokenizerBuilder.searchTerm(search.getTerm())
                .language(search.getLang())
                .removeStopWords(search.isDelStopWords())
                .cleanTweet(search.isCleanTweet())
                .build();

        final BaseClassifier<String> finalClassifier = bClassifier;
        Iterator<CommentWithSentiment> it = search.getComments().iterator();
        while (it.hasNext()) {
            CommentWithSentiment comment = it.next();
            if (!comment.isTokenized()) {
                comment.setTokenized(true);
                comment.setTokenizedComment(tokenizer.tokenize(comment.getComment()));
            }
            Classification classification = finalClassifier.classify(comment.getTokenizedComment());
            comment.setSubjectivityScore(((JointClassification) classification).conditionalProbability(classification.bestCategory()));
            switch (classification.bestCategory()) {
                case "subjective":
                    comment.setSubjectivity(Subjectivity.SUBJECTIVE);
                    break;
                case "objective":
                    comment.setSubjectivity(Subjectivity.OBJECTIVE);
                    if (search.isDiscardNonSubjective())
                        it.remove();
                    break;
            }
        }
    }
}

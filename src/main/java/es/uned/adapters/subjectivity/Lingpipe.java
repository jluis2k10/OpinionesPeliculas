package es.uned.adapters.subjectivity;

import com.aliasi.classify.BaseClassifier;
import com.aliasi.classify.Classification;
import com.aliasi.classify.JointClassification;
import es.uned.adapters.ClassifierType;
import es.uned.adapters.common.CommonLingpipe;
import es.uned.components.Tokenizer;
import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.entities.Opinion;
import es.uned.entities.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component("es.uned.adapters.subjectivity.Lingpipe")
public class Lingpipe extends CommonLingpipe implements SubjectivityAdapter {

    private static final String ADAPTER_DIR = "/lingpipe";

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private Tokenizer tokenizer;

    @Override
    public String get_adapter_path() {
        return "classpath:" + MODELS_DIR + ADAPTER_DIR + "/";
    }

    @Override
    public void analyze(Corpus corpus, Analysis analysis) {
        // Opciones para tokenizer
        tokenizer.setLanguage(analysis.getLang());
        tokenizer.setDeleteStopWords(analysis.isDeleteStopWords());

        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + ADAPTER_DIR + "/"  + analysis.getLanguageModel().getLocation() + "/classifier.model");
        final BaseClassifier<String> baseClassifier = getBaseClassifier(resource);

        corpus.getComments().forEach(comment -> {
            Record commentRecord = comment.findRecord(analysis.getId());
            Classification classification = baseClassifier.classify(tokenizer.tokenize(comment.getContent()));
            double prob = ((JointClassification) classification).conditionalProbability(classification.bestCategory());
            if (classification.bestCategory().equals("subjective")) {
                commentRecord.setOpinion(Opinion.SUBJECTIVE);
                commentRecord.setSubjectiveScore(prob);
            } else {
                commentRecord.setOpinion(Opinion.OBJECTIVE);
                commentRecord.setSubjectiveScore(1L - prob);
            }
            comment.addRecord(commentRecord);
            analysis.addRecord(commentRecord);
        });
        corpus.addAnalysis(analysis);
    }

    @Override
    public ClassifierType get_adapter_type() {
        return adapterType;
    }
}

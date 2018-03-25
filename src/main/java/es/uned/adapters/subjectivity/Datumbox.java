package es.uned.adapters.subjectivity;

import com.datumbox.framework.applications.nlp.TextClassifier;
import com.datumbox.framework.core.common.dataobjects.Record;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import es.uned.adapters.ClassifierType;
import es.uned.adapters.common.CommonDatumbox;
import es.uned.components.Tokenizer;
import es.uned.entities.Analysis;
import es.uned.entities.Corpus;
import es.uned.entities.Opinion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component("es.uned.adapters.subjectivity.Datumbox")
public class Datumbox extends CommonDatumbox implements SubjectivityAdapter {

    private static final String ADAPTER_DIR = "/datumbox";

    @Autowired
    private Tokenizer tokenizer;

    @Override
    public String get_adapter_path() {
        return "classpath:" + MODELS_DIR + ADAPTER_DIR + "/";
    }

    @Override
    public void analyze(Corpus corpus, Analysis analysis) {
        // Cargar modelo desde archivo serializado
        TextClassifier subjectivityClassifier = MLBuilder.load(TextClassifier.class, analysis.getLanguageModelLocation(), defineConfiguration());

        // Opciones para tokenizer
        tokenizer.setLanguage(analysis.getLang());
        tokenizer.setDeleteStopWords(analysis.isDeleteStopWords());

        corpus.getComments().forEach(comment -> {
            es.uned.entities.Record commentRecord = new es.uned.entities.Record();

            Record opinion = subjectivityClassifier.predict(tokenizer.tokenize(comment.getContent()));
            String prob = opinion.getYPredictedProbabilities().get(opinion.getYPredicted()).toString();
            if (opinion.getYPredicted().toString().equals("subjective")) {
                commentRecord.setOpinion(Opinion.SUBJECTIVE);
                commentRecord.setSubjectiveScore(Double.parseDouble(prob));
            }
            else {
                commentRecord.setOpinion(Opinion.OBJECTIVE);
                commentRecord.setSubjectiveScore(1L - Double.parseDouble(prob));
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

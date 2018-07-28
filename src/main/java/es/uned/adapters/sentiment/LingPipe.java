package es.uned.adapters.sentiment;

import com.aliasi.classify.BaseClassifier;
import com.aliasi.classify.Classification;
import com.aliasi.classify.JointClassification;
import es.uned.adapters.ClassifierType;
import es.uned.adapters.common.CommonLingpipe;
import es.uned.components.Tokenizer;
import es.uned.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * Adaptador que utiliza el clasificador/libreria Lingpipe para análisis de polaridad (sentimiento).
 * @see <a href="http://alias-i.com/lingpipe/index.html">Lingpipe</a>
 */
@Component("es.uned.adapters.sentiment.LingPipe")
public class LingPipe extends CommonLingpipe implements SentimentAdapter {

    @Autowired private ResourceLoader resourceLoader;
    @Autowired private Tokenizer tokenizer;

    /**
     * Directorio específico para este adaptador
     */
    private static final String ADAPTER_DIR = "/lingpipe";

    /**
     * {@inheritDoc}
     * @return String con la ruta
     */
    @Override
    public String get_adapter_path() {
        return "classpath:" + MODELS_DIR + ADAPTER_DIR + "/";
    }

    /**
     * {@inheritDoc}
     * @param corpus   Corpus sobre el que se ejecutará el análisis
     * @param analysis Análisis que se ejecutará y sus opciones
     */
    @Override
    public void analyze(Corpus corpus, Analysis analysis) {
        // Opciones para tokenizer
        tokenizer.setLanguage(analysis.getLang());
        tokenizer.setDeleteStopWords(analysis.isDeleteStopWords());

        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + ADAPTER_DIR + "/"  + analysis.getLanguageModel().getLocation() + "/classifier.model");
        final BaseClassifier<String> baseClassifier = getBaseClassifier(resource);

        corpus.getComments().stream()
                .filter(c -> !analysis.isOnlyOpinions() || (analysis.isOnlyOpinions() && c.getOpinion() == Opinion.SUBJECTIVE))
                .forEach(comment -> {
            Record commentRecord = comment.findRecord(analysis.getId());
            Classification classification = baseClassifier.classify(tokenizer.tokenize(comment.getContent()));
            if (classification.bestCategory().equals("pos")) {
                commentRecord.setPolarity(Polarity.POSITIVE);
            }
            else if (classification.bestCategory().equals("neg")) {
                commentRecord.setPolarity(Polarity.NEGATIVE);
            }
            commentRecord.setPolarityScore(((JointClassification) classification).conditionalProbability(classification.bestCategory()));
            commentRecord.setPositiveScore(((JointClassification) classification).conditionalProbability("pos"));
            commentRecord.setNegativeScore(((JointClassification) classification).conditionalProbability("neg"));
            commentRecord.setNeutralScore(0);

            comment.addRecord(commentRecord);
            analysis.addRecord(commentRecord);
        });
        corpus.addAnalysis(analysis);
    }

    /**
     * {@inheritDoc}
     * @return Tipo de adaptador
     */
    @Override
    public ClassifierType get_adapter_type() {
        return adapterType;
    }
}

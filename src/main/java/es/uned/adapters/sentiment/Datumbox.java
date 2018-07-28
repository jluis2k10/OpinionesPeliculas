package es.uned.adapters.sentiment;

import com.datumbox.framework.applications.nlp.TextClassifier;
import com.datumbox.framework.core.common.dataobjects.Record;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import es.uned.adapters.ClassifierType;
import es.uned.adapters.common.CommonDatumbox;
import es.uned.components.Tokenizer;
import es.uned.entities.*;
import es.uned.services.RecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Adaptador que utiliza el clasificador/librería Datumbox para análisis de polaridad (sentimiento).
 * @see <a href="https://github.com/datumbox/datumbox-framework/">Datumbox Framework</a>
 */
@Component("es.uned.adapters.sentiment.Datumbox")
public class Datumbox extends CommonDatumbox implements SentimentAdapter {

    @Autowired private Tokenizer tokenizer;

    /**
     * Directorio específico para este adaptador
     */
    private static final String ADAPTER_DIR = "/datumbox";

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
     *      * @param analysis Análisis que se ejecutará y sus opciones
     */
    @Override
    public void analyze(Corpus corpus, Analysis analysis) {
        // Cargar modelo desde archivo serializado
        TextClassifier sentimentClassifier = MLBuilder.load(TextClassifier.class, analysis.getLanguageModel().getLocation(), defineConfiguration());

        // Opciones para tokenizer
        tokenizer.setLanguage(analysis.getLang());
        tokenizer.setDeleteStopWords(analysis.isDeleteStopWords());

        corpus.getComments().stream()
                .filter(c -> !analysis.isOnlyOpinions() || (analysis.isOnlyOpinions() && c.getOpinion() == Opinion.SUBJECTIVE))
                .forEach(comment -> {
            es.uned.entities.Record commentRecord = comment.findRecord(analysis.getId());

            Record sentiment = sentimentClassifier.predict(tokenizer.tokenize(comment.getContent()));
            String prob = sentiment.getYPredictedProbabilities().get(sentiment.getYPredicted()).toString();
            if (sentiment.getYPredicted().toString().equals("positive")) {
                commentRecord.setPolarity(Polarity.POSITIVE);
            }
            else if (sentiment.getYPredicted().toString().equals("negative")) {
                commentRecord.setPolarity(Polarity.NEGATIVE);
            }
            else {
                commentRecord.setPolarity(Polarity.NEUTRAL);
            }
            commentRecord.setPolarityScore(Double.parseDouble(prob));
            commentRecord.setPositiveScore(sentiment.getYPredictedProbabilities().getDouble("positive"));
            commentRecord.setNegativeScore(sentiment.getYPredictedProbabilities().getDouble("negative"));
            if (sentiment.getYPredictedProbabilities().containsKey("neutral"))
                 commentRecord.setNeutralScore(sentiment.getYPredictedProbabilities().getDouble("neutral"));

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

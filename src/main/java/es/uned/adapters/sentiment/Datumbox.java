package es.uned.adapters.sentiment;

import com.datumbox.framework.applications.nlp.TextClassifier;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.core.common.dataobjects.Record;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import es.uned.adapters.AdapterType;
import es.uned.adapters.common.CommonDatumbox;
import es.uned.components.Tokenizer;
import es.uned.entities.Search;
import es.uned.entities.Sentiment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component("es.uned.adapters.sentiment.Datumbox")
public class Datumbox extends CommonDatumbox implements SentimentAdapter {

    @Autowired
    private Tokenizer.TokenizerBuilder tokenizerBuilder;

    /* Debe coincidir con ID del XML */
    private final String myID = "P01";
    private static final String ADAPTER_DIR = "/datumbox";

    @Override
    public String get_adapter_path() {
        return "classpath:" + MODELS_DIR + ADAPTER_DIR + "/";
    }

    @Override
    public AdapterType get_adapter_type() {
        return adapterType;
    }

    public void analyze(Search search) {
        // Configuración del modelo
        Configuration configuration = defineConfiguration();

        // Cargar modelo desde archivo serializado
        TextClassifier sentimentClassifier = MLBuilder.load(TextClassifier.class, search.getSentimentModel().getLocation(), configuration);

        // Crear tokenizer
        Tokenizer tokenizer = tokenizerBuilder.searchTerm(search.getTerm())
                .language(search.getLang())
                .removeStopWords(search.isDelStopWords())
                .cleanTweet(search.isCleanTweet())
                .build();

        search.getComments().forEach(comment -> {
            if (!comment.isTokenized()) {
                comment.setTokenized(true);
                comment.setTokenizedComment(tokenizer.tokenize(comment.getComment()));
            }
            Record sentiment = sentimentClassifier.predict(comment.getTokenizedComment());
            Sentiment sentimentType = null;
            switch (sentiment.getYPredicted().toString()) {
                case "positive":
                    sentimentType = Sentiment.POSITIVE;
                    break;
                case "negative":
                    sentimentType = Sentiment.NEGATIVE;
                    break;
                case "neutral":
                    sentimentType = Sentiment.NEUTRAL;
                    break;
            }
            String prob = sentiment.getYPredictedProbabilities().get(sentiment.getYPredicted()).toString();
            comment.setSentiment(sentimentType);
            comment.setSentimentScore(Double.parseDouble(prob));
            comment.setPositivityScore(sentiment.getYPredictedProbabilities().getDouble("positive"));
            comment.setNegativityScore(sentiment.getYPredictedProbabilities().getDouble("negative"));
            comment.setNeutralityScore(sentiment.getYPredictedProbabilities().getDouble("neutral"));
        });
    }
}

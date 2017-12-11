package es.uned.adapters.sentiment;

import com.datumbox.framework.applications.nlp.TextClassifier;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.core.common.dataobjects.Record;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import es.uned.adapters.AdapterType;
import es.uned.adapters.common.CommonDatumbox;
import es.uned.components.Tokenizer;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 */
@Component("es.uned.adapters.sentiment.Datumbox")
public class Datumbox extends CommonDatumbox implements SentimentAdapter {

    @Autowired private Tokenizer.TokenizerBuilder tokenizerBuilder;

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

    public void analyze(Map<Integer,CommentWithSentiment> comments, SearchParams search, Map<String,String> options) {
        // Configuración del modelo
        Configuration configuration = defineConfiguration();

        // Cargar modelo desde archivo serializado
        TextClassifier sentimentClassifier = MLBuilder.load(TextClassifier.class, search.getSentimentModel(), configuration);

        // Crear tokenizer
        Tokenizer tokenizer = tokenizerBuilder.searchTerm(search.getSearchTerm())
                .language(search.getLang())
                .removeStopWords(search.isDelStopWords())
                .cleanTweet(search.isCleanTweet())
                .build();

        comments.forEach((k, comment) -> {
            if (!comment.isTokenized()) {
                comment.setTokenized(true);
                comment.setTokenizedComment(tokenizer.tokenize(comment.getComment()));
            }
            Record sentiment = sentimentClassifier.predict(comment.getTokenizedComment());
            String pred = sentiment.getYPredicted().toString();
            String prob = sentiment.getYPredictedProbabilities().get(sentiment.getYPredicted()).toString();
            comment.setPredictedSentiment(pred);
            comment.setSentimentScore(Double.parseDouble(prob));
        });
    }
}

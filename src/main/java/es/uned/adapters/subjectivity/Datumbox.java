package es.uned.adapters.subjectivity;

import com.datumbox.framework.applications.nlp.TextClassifier;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.core.common.dataobjects.Record;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import es.uned.adapters.AdapterType;
import es.uned.adapters.common.CommonDatumbox;
import es.uned.components.Tokenizer;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.Search;
import es.uned.entities.Subjectivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 *
 */
@Component("es.uned.adapters.subjectivity.Datumbox")
public class Datumbox extends CommonDatumbox implements SubjectivityAdapter {

    @Autowired private Tokenizer.TokenizerBuilder tokenizerBuilder;

    /* Debe coincidir con ID del XML */
    private final String myID = "S01";
    private static final String ADAPTER_DIR = "/datumbox";

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
        // Configuración del modelo
        Configuration configuration = defineConfiguration();

        // Cargar modelo desde archivo serializado
        TextClassifier subjectivityClassifier = MLBuilder.load(TextClassifier.class, search.getSubjectivityModel(), configuration);

        // Crear tokenizer
        Tokenizer tokenizer = tokenizerBuilder.searchTerm(search.getTerm())
                .language(search.getLang())
                .removeStopWords(search.isDelStopWords())
                .cleanTweet(search.isCleanTweet())
                .build();

        Iterator<CommentWithSentiment> it = search.getComments().iterator();
        while (it.hasNext()) {
            CommentWithSentiment comment = it.next();
            if (!comment.isTokenized()) {
                comment.setTokenized(true);
                comment.setTokenizedComment(tokenizer.tokenize(comment.getComment()));
            }
            Record subjectivity = subjectivityClassifier.predict(comment.getTokenizedComment());
            String prob = subjectivity.getYPredictedProbabilities().get(subjectivity.getYPredicted()).toString();
            comment.setSubjectivityScore(Double.parseDouble(prob));
            switch (subjectivity.getYPredicted().toString()) {
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

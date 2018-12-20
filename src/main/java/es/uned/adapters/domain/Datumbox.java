package es.uned.adapters.domain;

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
 * Adaptador que utiliza el clasificador/librería Datumbox para análisis de dominio (tema).
 * @see <a href="https://github.com/datumbox/datumbox-framework/">Datumbox Framework</a>
 */
@Component("es.uned.adapters.domain.Datumbox")
public class Datumbox extends CommonDatumbox implements DomainAdapter {

    @Autowired
    private Tokenizer tokenizer;

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
        TextClassifier domainClassifier = MLBuilder.load(TextClassifier.class, analysis.getLanguageModel().getLocation(), defineConfiguration());

        // Opciones para tokenizer
        tokenizer.setLanguage(analysis.getLang());
        tokenizer.setDeleteStopWords(analysis.isDeleteStopWords());

        corpus.getComments().stream()
                .filter(c -> !analysis.isOnlyOpinions() || (analysis.isOnlyOpinions() && c.getOpinion() == Opinion.SUBJECTIVE))
                .forEach(comment -> {
                    Record domain = domainClassifier.predict(tokenizer.tokenize(comment.getContent()));
                    String prob = domain.getYPredictedProbabilities().get(domain.getYPredicted()).toString();
                    comment.setDomain(domain.getYPredicted().toString());
                    comment.setDomainScore(Double.parseDouble(prob));
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

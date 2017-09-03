package es.uned.adapters.sentiment;

import com.aliasi.classify.BaseClassifier;
import com.aliasi.classify.Classification;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.classify.JointClassification;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.io.BitInput;
import com.aliasi.lm.LanguageModel;
import com.aliasi.lm.NGramBoundaryLM;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.AbstractExternalizable;
import es.uned.components.TwitterTokenizer;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Component("es.uned.adapters.sentiment.LingPipe")
public class LingPipe implements SentimentAdapter {

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private TwitterTokenizer twitterTokenizer;

    /* Debe coincidir con ID del XML */
    private final String myID = "P02";

    private static final String ADAPTER_DIR = "/lingpipe";

    @Override
    public void analyze(Map<Integer,CommentWithSentiment> comments, SearchParams search, Map<String,String> options) {
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + ADAPTER_DIR  + search.getSentimentModel() + "/classifier.model");
        File modelFile = null;
        BaseClassifier<String> classifier = null;
        try {
            modelFile = resource.getFile();
            classifier = (BaseClassifier<String>) AbstractExternalizable.readObject(modelFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        twitterTokenizer.setLanguage(search.getLang());
        twitterTokenizer.setSearchTerm(search.getSearchTerm());
        BaseClassifier<String> finalClassifier = classifier;

        comments.forEach((k, comment) -> {
            comment.setTokenized(true);
            //comment.setTokenizedComment(twitterTokenizer.cleanUp(comment.getComment()));
            comment.setTokenizedComment(comment.getComment());
            Classification classification = finalClassifier.classify(comment.getTokenizedComment());
            comment.setPredictedSentiment(classification.bestCategory());
            comment.setSentimentScore(((JointClassification) classification).conditionalProbability(classification.bestCategory()));
        });

    }

    @Override
    public void trainModel(String modelLocation, List<String> positives, List<String> negatives) {
        ObjectHandler<CharSequence>[] lms = null;
        String[] categories = {"positive", "negative"};
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + ADAPTER_DIR + "/" + modelLocation);
        File dir = null;

        try {
            dir = resource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File posFile = new File(dir.toString() + "/lm_positive.model");
        File negFile = new File(dir.toString() + "/lm_negative.model");

        // Determinar si el LM es NGramBoundary o NGramProcess
        boolean isNGramProcess = true;
        try {
            BitInput bitInput = new BitInput(new BufferedInputStream(FileUtils.openInputStream(posFile)));
            char boundaryChar = (char) (bitInput.readDelta()-1L);
            if (boundaryChar == '\uFFFF') // Carácter unicode "prohibido" que se utiliza como frontera en el modelo
                isNGramProcess = false;   // y que sólo debería aparecer en NGramBoundary
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Cargar language models
        try {
            if (isNGramProcess) {
                lms = new NGramProcessLM[2];
                lms[0] = NGramProcessLM.readFrom(new BufferedInputStream(FileUtils.openInputStream(posFile)));
                lms[1] = NGramProcessLM.readFrom(new BufferedInputStream(FileUtils.openInputStream(negFile)));
            } else {
                lms = new NGramBoundaryLM[2];
                lms[0] = NGramBoundaryLM.readFrom(new BufferedInputStream(FileUtils.openInputStream(posFile)));
                lms[1] = NGramBoundaryLM.readFrom(new BufferedInputStream(FileUtils.openInputStream(negFile)));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Entrenamos los language models con los nuevos datasets
        for (String positive: positives) {
            if (!positive.isEmpty() && isNGramProcess) {
                ((NGramProcessLM)lms[0]).train(positive);
            } else if (!positive.isEmpty() && !isNGramProcess) {
                ((NGramBoundaryLM)lms[0]).train(positive);
            }
        }
        for (String negative: negatives) {
            if (!negative.isEmpty() && isNGramProcess) {
                ((NGramProcessLM)lms[0]).train(negative);
            } else if (!negative.isEmpty() && !isNGramProcess) {
                ((NGramBoundaryLM)lms[0]).train(negative);
            }
        }

        // Recrear modelo del clasificador a partir de los language models anteriores
        DynamicLMClassifier<LanguageModel.Dynamic> classifier = new DynamicLMClassifier(categories, (LanguageModel.Dynamic[]) lms);

        // guardar modelos de lenguaje
        for(int j = 0; j < lms.length; ++j) {
            try {
                dir = resource.getFile();
                File saveToFile = new File(dir.toString() + "/lm_" + categories[j] + ".model");
                OutputStream fileOut = FileUtils.openOutputStream(saveToFile);
                BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);
                if (isNGramProcess)
                    ((NGramProcessLM)lms[j]).writeTo(bufOut);
                else
                    ((NGramBoundaryLM)lms[j]).writeTo(bufOut);
                fileOut.close();
                bufOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // guardar también modelo del clasificador
        try {
            dir = resource.getFile();
            File saveToFile = new File(dir.toString() + "/classifier.model");
            FileOutputStream fileOut = FileUtils.openOutputStream(saveToFile);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            classifier.compileTo(objOut);
            fileOut.close();
            objOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Lingpipe no tiene modelos que se puedan re-entrenar. Lo que hay que hacer es serializar los "language models",
     * recrear el modelo con ellos y guardarlo todo.
     */
    public void createModel(String modelLocation, Map<String,String> options, List<String> positives, List<String> negatives) {
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + ADAPTER_DIR);
        File dir = null;
        try {
            dir = resource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] categories = {"positive", "negative"};

        ObjectHandler<CharSequence>[] lms = null;
        // Construir Language Model según las opciones
        switch (options.get("LanguageModel")) {
            case "NGramProcessLM":
                lms = new NGramProcessLM[categories.length];
                int nGram = Integer.parseInt(options.get("NGramProcessLM_nGram"));
                int numChars = Integer.parseInt(options.get("NGramProcessLM_numChars"));
                double lambdaFactor = Double.parseDouble(options.get("NGramProcessLM_lambdaFactor"));
                for (int i = 0; i < categories.length; i++)
                    lms[i] = new NGramProcessLM(nGram, numChars, lambdaFactor);
                break;
            case "NGramBoundaryLM":
                lms = new NGramBoundaryLM[categories.length];
                int nGramB = Integer.parseInt(options.get("NGramBoundaryLM_nGram"));
                int numCharsB = Integer.parseInt(options.get("NGramBoundaryLM_numChars"));
                double lambdaFactorB = Double.parseDouble(options.get("NGramBoundaryLM_lambdaFactor"));
                for (int i = 0; i < categories.length; i++)
                    lms[i] = new NGramBoundaryLM(nGramB, numCharsB, lambdaFactorB, '\uffff');
                break;
            /*case "TokenizedLM":
                lms = new TokenizedLM[categories.length];
                TokenizerFactory tokenizerFactory = null;
                switch (options.get("tokenizerFactory")) {
                    case "CharacterTokenizerFactory":
                        tokenizerFactory = CharacterTokenizerFactory.INSTANCE;
                        break;
                    case "IndoEuropeanTokenizer":
                        tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;
                        break;
                    case "NGramTokenizerFactory":
                        int minNGram = Integer.parseInt(options.get("NGramTokenizer_minNGram"));
                        int maxNGram = Integer.parseInt(options.get("NGramTokenizer_maxNGram"));
                        tokenizerFactory = new NGramTokenizerFactory(minNGram,maxNGram);
                        break;
                }
                int nGramOrder = Integer.parseInt(options.get("TokenizedLM_nGramOrder"));
                for (int i = 0; i < categories.length; i++)
                    lms[i] = new TokenizedLM(tokenizerFactory, nGramOrder);
                break;*/
        }

        // Entrenar Language Models
        // lms[0] es el LM para comentarios positivos y lms[1] el LM para los negativos
        for (String positive: positives)
            lms[0].handle(positive);
        for (String negative: negatives)
            lms[1].handle(negative);

        // Guardar Language Models
        for(int i = 0; i < lms.length; ++i) {
            try {
                File saveToFile = new File(dir.toString() + "/" + modelLocation + "/lm_" + categories[i] + ".model");
                OutputStream fileOut = FileUtils.openOutputStream(saveToFile);
                BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);
                switch (options.get("LanguageModel")) {
                    case "NGramProcessLM":
                        ((NGramProcessLM) lms[i]).writeTo(bufOut);
                        break;
                    case "NGramBoundaryLM":
                        ((NGramBoundaryLM) lms[i]).writeTo(bufOut);
                        break;
                }
                fileOut.close();
                bufOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Construir clasificador a partir de los LM
        DynamicLMClassifier<LanguageModel.Dynamic> classifier = new DynamicLMClassifier(categories, (LanguageModel.Dynamic[]) lms);

        // Guardar modelo del clasificador
        try {
            File saveToFile = new File(dir.toString() + "/" + modelLocation  + "/classifier.model");
            FileOutputStream fileOut = FileUtils.openOutputStream(saveToFile);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            classifier.compileTo(objOut);
            fileOut.close();
            objOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

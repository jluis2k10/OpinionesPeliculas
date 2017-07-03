package es.uned.adapters.sentiment;

import com.aliasi.classify.*;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Files;
import es.uned.components.TwitterTokenizer;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;

/**
 *
 */
@Component("es.uned.adapters.sentiment.LingPipe")
public class LingPipe implements SentimentAdapter {

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired private TwitterTokenizer twitterTokenizer;

    @Override
    public void analyze(Map<Integer,CommentWithSentiment> comments, SearchParams search, Map<String,String> options) {
        Resource resource = resourceLoader.getResource("classpath:/models" + search.getSentimentModel());
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

        BaseClassifier<String> finalClassifier = classifier;
        comments.forEach((k, comment) -> {
            comment.setTokenized(true);
            comment.setTokenizedComment(twitterTokenizer.cleanUp(comment.getComment(), comment.getSearchTerm()));
            Classification classification = finalClassifier.classify(comment.getTokenizedComment());
            comment.setPredictedSentiment(classification.bestCategory());
            comment.setSentimentScore(((JointClassification) classification).conditionalProbability(classification.bestCategory()));
        });

    }

    public void createModel() {
        Resource resource = resourceLoader.getResource("classpath:/datasets/" + "polarityMovieReviews");
        File dir = null;
        try {
            dir = resource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Array con los directorios donde está el dataset ("pos" y "neg")
        String[] categories = dir.list();
        // Analizamos en conjuntos de 8 palabras
        int nGram = 8;
        // Instanciamos el clasificador
        DynamicLMClassifier<NGramProcessLM> classifier = DynamicLMClassifier.createNGramProcess(categories,nGram);

        /*
        Entrenamos el clasificador con el dataset
         */
        for (String category: categories) {
            Classification classification = new Classification(category);
            // Accedemos a los archivos de cada directorio (/pos y /neg)
            File file = new File(dir, category);
            File[] trainFiles = file.listFiles();

            for (File trainFile: trainFiles) {
                String review = null;
                try {
                    review = Files.readFromFile(trainFile, "UTF-8");
                    review = twitterTokenizer.cleanUp(review);
                    Classified<CharSequence> classified = new Classified<CharSequence>(review, classification);
                    classifier.handle(classified);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Guardar el modelo
        resource = resourceLoader.getResource("classpath:/models/lingpipe");
        try {
            dir = resource.getFile();
            FileOutputStream fileOut = new FileOutputStream(dir.toString() + "/polarityMovieReviews.model");
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            classifier.compileTo(objOut);
            objOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }

}

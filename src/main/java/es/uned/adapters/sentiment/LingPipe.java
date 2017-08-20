package es.uned.adapters.sentiment;

import com.aliasi.classify.*;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.Files;
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

    @Override
    public void analyze(Map<Integer,CommentWithSentiment> comments, SearchParams search, Map<String,String> options) {
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + search.getSentimentModel() + "/classifier.model");
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
        NGramProcessLM[] lms = new NGramProcessLM[2];
        String[] categories = {"neg", "pos"};
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + modelLocation);
        File dir = null;

        // Cargar language models
        try {
            dir = resource.getFile();
            File posFile = new File(dir.toString() + "/lm_neg.model");
            File negFile = new File(dir.toString() + "/lm_pos.model");
            lms[0] = NGramProcessLM.readFrom(new BufferedInputStream(FileUtils.openInputStream(negFile)));
            lms[1] = NGramProcessLM.readFrom(new BufferedInputStream(FileUtils.openInputStream(posFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Entrenamos los language models con los nuevos datasets
        for (String negative: negatives) {
            lms[0].train(negative);
            //lms[0].handle(negative);
        }
        for (String positive: positives) {
            lms[1].train(positive);
            //lms[1].handle(positive);
        }

        // Recrear modelo del clasificador a partir de los language models anteriores
        DynamicLMClassifier<NGramProcessLM> classifier = new DynamicLMClassifier<NGramProcessLM>(categories, lms);

        // guardar modelos de lenguaje
        for(int j = 0; j < lms.length; ++j) {
            try {
                dir = resource.getFile();
                File saveToFile = new File(dir.toString() + "/lm_" + categories[j] + ".model");
                OutputStream fileOut = FileUtils.openOutputStream(saveToFile);
                BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);
                lms[j].writeTo(bufOut);
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
    public void createModel() {
        // Cargar datasets
        Resource resource = resourceLoader.getResource("classpath:/datasets/" + "polarityMovieReviews");
        File dir = null;
        try {
            dir = resource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Dos categorías según los directorios del dataset (pos y neg)
        String[] categories = dir.list();
        int nGram = 8;
        NGramProcessLM[] lms = new NGramProcessLM[categories.length];

        // Definir language models (2, uno para pos y otro para neg)
        for(int i = 0; i < lms.length; ++i) {
            lms[i] = new NGramProcessLM(nGram);
        }

        // Entrenar language models
        int i = 0;
        for (String category: categories) {
            // Accedemos a los archivos de cada directorio (/pos y /neg)
            File file = new File(dir, category);
            File[] trainFiles = file.listFiles();

            for (File trainFile: trainFiles) {
                String review = null;
                try {
                    review = Files.readFromFile(trainFile, "UTF-8");
                    twitterTokenizer.setLanguage("en");
                    twitterTokenizer.setSearchTerm("");
                    review = twitterTokenizer.cleanUp(review);
                    lms[i].handle(review);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            i++;
        }
        // Crear clasificador a partir de los language models
        DynamicLMClassifier<NGramProcessLM> classifier = new DynamicLMClassifier<NGramProcessLM>(categories, lms);

        resource = resourceLoader.getResource("classpath:" + MODELS_DIR + "/lingpipe");
        // guardar modelos de lenguaje
        for(int j = 0; j < lms.length; ++j) {
            try {
                dir = resource.getFile();
                File saveToFile = new File(dir.toString() + "/test1/lm_" + categories[j] + ".model");
                OutputStream fileOut = FileUtils.openOutputStream(saveToFile);
                BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);
                //ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
                lms[j].writeTo(bufOut);
                //lms[j].compileTo(objOut);
                fileOut.close();
                bufOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // guardar también modelo del clasificador
        try {
            dir = resource.getFile();
            File saveToFile = new File(dir.toString() + "/test1/classifier.model");
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

        return;
    }

    public void createModel2() {
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
                    twitterTokenizer.setLanguage("en");
                    twitterTokenizer.setSearchTerm("");
                    review = twitterTokenizer.cleanUp(review);
                    Classified<CharSequence> classified = new Classified<CharSequence>(review, classification);
                    classifier.handle(classified);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Guardar el modelo
        resource = resourceLoader.getResource("classpath:" + MODELS_DIR + "/lingpipe");
        try {
            dir = resource.getFile();
            File saveToFile = new File(dir.toString() + "/test2/classifier.model");
            FileOutputStream fileOut = FileUtils.openOutputStream(saveToFile);
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

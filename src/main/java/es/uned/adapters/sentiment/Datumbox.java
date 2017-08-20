package es.uned.adapters.sentiment;

import com.datumbox.framework.applications.nlp.TextClassifier;
import com.datumbox.framework.common.Configuration;
import com.datumbox.framework.common.dataobjects.AssociativeArray;
import com.datumbox.framework.common.utilities.RandomGenerator;
import com.datumbox.framework.core.common.dataobjects.Dataframe;
import com.datumbox.framework.core.common.dataobjects.Record;
import com.datumbox.framework.core.common.text.StringCleaner;
import com.datumbox.framework.core.common.text.extractors.AbstractTextExtractor;
import com.datumbox.framework.core.common.text.extractors.NgramsExtractor;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.core.machinelearning.classification.MultinomialNaiveBayes;
import com.datumbox.framework.core.machinelearning.featureselection.MutualInformation;
import com.datumbox.framework.storage.inmemory.InMemoryConfiguration;
import es.uned.components.TwitterTokenizer;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 *
 */
@Component("es.uned.adapters.sentiment.Datumbox")
public class Datumbox implements SentimentAdapter {

    @Autowired private ResourceLoader resourceLoader;
    @Autowired private TwitterTokenizer twitterTokenizer;

    /* Debe coincidir con ID del XML */
    private final String myID = "P01";

    public void analyze(Map<Integer,CommentWithSentiment> comments, SearchParams search, Map<String,String> options) {
        Configuration configuration = Configuration.getConfiguration();
        InMemoryConfiguration memConfiguration = new InMemoryConfiguration();
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR);
        String modelsDirectory = null;
        try {
            modelsDirectory = resource.getFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        memConfiguration.setDirectory(modelsDirectory);
        configuration.setStorageConfiguration(memConfiguration);
        TextClassifier sentimentClassifier = MLBuilder.load(TextClassifier.class, search.getSentimentModel(), configuration);

        twitterTokenizer.setLanguage(search.getLang());
        twitterTokenizer.setSearchTerm(search.getSearchTerm());
        comments.forEach((k, comment) -> {
            String commentString = null;
            if (options.get(myID + "-preprocesar").equals("yes")) {
                if (!comment.isTokenized()) {
                    comment.setTokenized(true);
                    comment.setTokenizedComment(twitterTokenizer.cleanUp(comment.getComment()));
                }
                commentString = comment.getTokenizedComment();
            } else {
                commentString = comment.getComment();
            }
            Record sentiment = sentimentClassifier.predict(commentString);
            String pred = sentiment.getYPredicted().toString();
            String prob = sentiment.getYPredictedProbabilities().get(sentiment.getYPredicted()).toString();
            comment.setPredictedSentiment(pred);
            comment.setSentimentScore(Double.parseDouble(prob));
            comments.put(k, comment);
        });
    }

    public void trainModel(String modelLocation, List<String> positives, List<String> negatives) {
        RandomGenerator.setGlobalSeed(42L);
        Configuration configuration = Configuration.getConfiguration();

        // Definir configuración del modelo (en memoria, directorio)
        InMemoryConfiguration memConfiguration = new InMemoryConfiguration();
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR);
        String modelsDirectory = null;
        try {
            modelsDirectory = resource.getFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        memConfiguration.setDirectory(modelsDirectory);
        configuration.setStorageConfiguration(memConfiguration);

        // Cargar el clasificador según el modelo indicado
        TextClassifier sentimentClassifier = MLBuilder.load(TextClassifier.class, modelLocation, configuration);
        TextClassifier.TrainingParameters trainingParameters = (TextClassifier.TrainingParameters) sentimentClassifier.getTrainingParameters();
        AbstractTextExtractor textExtractor = AbstractTextExtractor.newInstance(trainingParameters.getTextExtractorParameters());

        // Cargar el dataset con el que se construyó el modelo
        Dataframe dataset = Dataframe.Builder.load(modelLocation + "/dataset", configuration);

        // Crear nuevas entradas para el dataset
        List<Record> records = new ArrayList<>();
        for (String positive: positives) {
            if (!positive.isEmpty()) {
                AssociativeArray xData = new AssociativeArray(textExtractor.extract(StringCleaner.clear(positive)));
                records.add(new Record(xData, "positive"));
            }
        }
        for (String negative: negatives) {
            if (!negative.isEmpty()) {
                AssociativeArray xData = new AssociativeArray(textExtractor.extract(StringCleaner.clear(negative)));
                records.add(new Record(xData, "negative"));
            }
        }

        // Añadir entradas al dataset
        for (Record r: records) {
            dataset.set(dataset.size(), r);
        }

        // Guardar dataset
        dataset.save(modelLocation + "/dataset");
        // Entrenar el clasificador
        sentimentClassifier.fit(dataset);
        // Guardar modelo del clasificador
        sentimentClassifier.save(modelLocation);
    }

    public void createModel() {
        RandomGenerator.setGlobalSeed(42L);
        Configuration configuration = Configuration.getConfiguration();

        // Definir configuración del modelo (en memoria, directorio)
        InMemoryConfiguration memConfiguration = new InMemoryConfiguration();
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR);
        String modelsDirectory = null;
        try {
            modelsDirectory = resource.getFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        memConfiguration.setDirectory(modelsDirectory);
        configuration.setStorageConfiguration(memConfiguration);

        // Configurar training parameters
        TextClassifier.TrainingParameters trainingParameters = new TextClassifier.TrainingParameters();
        trainingParameters.setNumericalScalerTrainingParameters(null);
        trainingParameters.setCategoricalEncoderTrainingParameters(null);
        trainingParameters.setFeatureSelectorTrainingParametersList(Arrays.asList(new MutualInformation.TrainingParameters()));
        trainingParameters.setTextExtractorParameters(new NgramsExtractor.Parameters());
        trainingParameters.setModelerTrainingParameters(new MultinomialNaiveBayes.TrainingParameters());

        // Crear el clasificador
        TextClassifier sentimentClassifier = MLBuilder.create(trainingParameters, configuration);
        AbstractTextExtractor textExtractor = AbstractTextExtractor.newInstance(trainingParameters.getTextExtractorParameters());

        // Cargar dataset desde texto y guardarlo en formato Datumbox
        Map<Object, URI> datasets = new HashMap<>();
        try {
            datasets.put("positive", Datumbox.class.getClassLoader().getResource("datasets/testPolaridad/polaridad.pos").toURI());
            datasets.put("negative", Datumbox.class.getClassLoader().getResource("datasets/testPolaridad/polaridad.neg").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Entrenar el clasificador
        sentimentClassifier.fit(datasets);

        // Guardar dataset
        Dataframe dataset = Dataframe.Builder.parseTextFiles(datasets, textExtractor, configuration);
        dataset.save("/datumbox/testDatumbox/dataset");

        // Guardar clasificador
        sentimentClassifier.save("/datumbox/testDatumbox");

    }

    public void createModel2() {
        RandomGenerator.setGlobalSeed(42L);
        Configuration configuration = Configuration.getConfiguration();

        // Definir configuración del modelo (en memoria, directorio)
        InMemoryConfiguration memConfiguration = new InMemoryConfiguration();
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR);
        String modelsDirectory = null;
        try {
            modelsDirectory = resource.getFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        memConfiguration.setDirectory(modelsDirectory);
        configuration.setStorageConfiguration(memConfiguration);
        // Cargar el clasificador según el modelo indicado
        TextClassifier sentimentClassifier = MLBuilder.load(TextClassifier.class, "/datumbox/TwitterSentimentAnalysis", configuration);

        TextClassifier.TrainingParameters trainingParameters = (TextClassifier.TrainingParameters) sentimentClassifier.getTrainingParameters();
        AbstractTextExtractor textExtractor = AbstractTextExtractor.newInstance(trainingParameters.getTextExtractorParameters());
        /*
        // Cargar dataset desde texto y guardarlo en formato Datumbox
        Map<Object, URI> datasets = new HashMap<>();
        try {
            datasets.put("positive", Datumbox.class.getClassLoader().getResource("datasets/datumboxPolarity/rt-polarity.pos").toURI());
            datasets.put("negative", Datumbox.class.getClassLoader().getResource("datasets/datumboxPolarity/rt-polarity.neg").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Dataframe dataset = Dataframe.Builder.parseTextFiles(datasets, textExtractor, configuration);
        dataset.save("/datumbox/TwitterSentimentAnalysis/dataset");*/

        // Definir nueva entrada para el dataset
        AssociativeArray xData = new AssociativeArray(textExtractor.extract(StringCleaner.clear("probando datumbox")));
        Record r = new Record(xData, "positive");
        // Cargar el dataset con el que se construyó el modelo
        Dataframe dataset = Dataframe.Builder.load("/datumbox/TwitterSentimentAnalysis/dataset", configuration);
        // Añadir entrada al dataset
        dataset.set(dataset.size(), r);
        // Entrenar el clasificador
        sentimentClassifier.fit(dataset);

        // Guardar dataset y modelo del clasificador
        dataset.save("/datumbox/TwitterSentimentAnalysis/dataset");
        sentimentClassifier.save("/datumbox/TwitterSentimentAnalysis");
    }

}

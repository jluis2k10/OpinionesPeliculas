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
import com.datumbox.framework.core.common.text.extractors.UniqueWordSequenceExtractor;
import com.datumbox.framework.core.common.text.extractors.WordSequenceExtractor;
import com.datumbox.framework.core.machinelearning.MLBuilder;
import com.datumbox.framework.core.machinelearning.classification.*;
import com.datumbox.framework.core.machinelearning.featureselection.ChisquareSelect;
import com.datumbox.framework.core.machinelearning.featureselection.MutualInformation;
import com.datumbox.framework.core.machinelearning.preprocessing.*;
import com.datumbox.framework.storage.inmemory.InMemoryConfiguration;
import es.uned.components.TwitterTokenizer;
import es.uned.entities.CommentWithSentiment;
import es.uned.entities.SearchParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Component("es.uned.adapters.sentiment.Datumbox")
public class Datumbox implements SentimentAdapter {

    @Autowired private ResourceLoader resourceLoader;
    @Autowired private TwitterTokenizer twitterTokenizer;

    /* Debe coincidir con ID del XML */
    private final String myID = "P01";

    private static final String ADAPTER_DIR = "/datumbox";

    public void analyze(Map<Integer,CommentWithSentiment> comments, SearchParams search, Map<String,String> options) {
        Configuration configuration = Configuration.getConfiguration();
        InMemoryConfiguration memConfiguration = new InMemoryConfiguration();
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + ADAPTER_DIR);
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
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + ADAPTER_DIR);
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

    public void createModel(String modelLocation, Map<String,String> options, List<String> positives, List<String> negatives) {
        RandomGenerator.setGlobalSeed(42L);
        Configuration configuration = Configuration.getConfiguration();

        // Definir configuración del modelo (en memoria, directorio)
        InMemoryConfiguration memConfiguration = new InMemoryConfiguration();
        Resource resource = resourceLoader.getResource("classpath:" + MODELS_DIR + ADAPTER_DIR);
        String modelsDirectory = null;
        try {
            modelsDirectory = resource.getFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        memConfiguration.setDirectory(modelsDirectory);
        configuration.setStorageConfiguration(memConfiguration);

        // Ajustar parámetros de entrenamiento
        // -----------------------------------
        TextClassifier.TrainingParameters trainingParameters = new TextClassifier.TrainingParameters();

        // 1. Numerical Scaler
        switch (options.get("NumericalScaler")) {
            case "BinaryScaler":
                BinaryScaler.TrainingParameters BStrainingParameters = new BinaryScaler.TrainingParameters();
                BStrainingParameters.setThreshold(Double.parseDouble(options.get("BinaryScaler_Threshold")));
                trainingParameters.setNumericalScalerTrainingParameters(BStrainingParameters);
                break;
            case "MaxAbsScaler":
                trainingParameters.setNumericalScalerTrainingParameters(new MaxAbsScaler.TrainingParameters());
                break;
            case "MinMaxScaler":
                trainingParameters.setNumericalScalerTrainingParameters(new MinMaxScaler.TrainingParameters());
                break;
            case "StandardScaler":
                trainingParameters.setNumericalScalerTrainingParameters(new StandardScaler.TrainingParameters());
                break;
            default:
                break;
        }

        // 2. Feature Selector List
        // TODO: puede tener ambos de forma simultánea.
        switch (options.get("FeatureSelector")) {
            case "ChisquareSelect":
                ChisquareSelect.TrainingParameters CSTrainingParameters = new ChisquareSelect.TrainingParameters();
                CSTrainingParameters.setALevel(Double.parseDouble(options.get("ChisquareSelect_ALevel")));
                trainingParameters.setFeatureSelectorTrainingParametersList(Arrays.asList(CSTrainingParameters));
                break;
            case "MutualInformation":
                trainingParameters.setFeatureSelectorTrainingParametersList(Arrays.asList(new MutualInformation.TrainingParameters()));
                break;
        }

        // 3. Text Extractor
        switch (options.get("TextExtractor")) {
            case "Ngrams":
                NgramsExtractor.Parameters parameters = new NgramsExtractor.Parameters();
                parameters.setExaminationWindowLength(Integer.parseInt(options.get("Ngrams_ExaminationWindowLength")));
                parameters.setMaxCombinations(Integer.parseInt(options.get("Ngrams_MaxCombinations")));
                parameters.setMaxDistanceBetweenKwds(Integer.parseInt(options.get("Ngrams_MaxDistanceBetweenKwds")));
                parameters.setMinWordLength(Integer.parseInt(options.get("Ngrams_MinWordLength")));
                parameters.setMinWordOccurrence(Integer.parseInt(options.get("Ngrams_MinWordOccurrence")));
                trainingParameters.setTextExtractorParameters(parameters);
                break;
            case "UniqueWordSequence":
                trainingParameters.setTextExtractorParameters(new UniqueWordSequenceExtractor.Parameters());
                break;
            case "WordSequence":
                trainingParameters.setTextExtractorParameters(new WordSequenceExtractor.Parameters());
                break;
        }

        // 4. Modeler Training
        switch (options.get("ModelerTraining")) {
            case "BernoulliNaiveBayes":
                trainingParameters.setModelerTrainingParameters(new BernoulliNaiveBayes.TrainingParameters());
                break;
            case "BinarizedNaiveBayes":
                trainingParameters.setModelerTrainingParameters(new BinarizedNaiveBayes.TrainingParameters());
                break;
            case "MaximumEntropy":
                MaximumEntropy.TrainingParameters MEParameters = new MaximumEntropy.TrainingParameters();
                MEParameters.setTotalIterations(Integer.parseInt(options.get("MaximumEntropy_TotalIterations")));
                trainingParameters.setModelerTrainingParameters(MEParameters);
                break;
            case "MultinomialNaiveBayes":
                trainingParameters.setModelerTrainingParameters(new MultinomialNaiveBayes.TrainingParameters());
                break;
            case "OrdinalRegression":
                OrdinalRegression.TrainingParameters ORparameters = new OrdinalRegression.TrainingParameters();
                ORparameters.setTotalIterations(Integer.parseInt(options.get("OrdinalRegression_TotalIterations")));
                ORparameters.setLearningRate(Double.parseDouble(options.get("OrdinalRegression_LearningRate")));
                ORparameters.setL2(Double.parseDouble(options.get("OrdinalRegression_L2")));
                trainingParameters.setModelerTrainingParameters(ORparameters);
                break;
            case "SoftMaxRegression":
                SoftMaxRegression.TrainingParameters SMRparameters = new SoftMaxRegression.TrainingParameters();
                SMRparameters.setTotalIterations(Integer.parseInt(options.get("SoftMaxRegression_TotalIterations")));
                SMRparameters.setLearningRate(Double.parseDouble(options.get("SoftMaxRegression_LearningRate")));
                SMRparameters.setL1(Double.parseDouble(options.get("SoftMaxRegression_L1")));
                SMRparameters.setL2(Double.parseDouble(options.get("SoftMaxRegression_L2")));
                trainingParameters.setModelerTrainingParameters(SMRparameters);
                break;
            case "SupportVectorMachine":
                // No nos liamos con los parámetros para el clasificador y dejamos los que vienen por defecto
                trainingParameters.setModelerTrainingParameters(new SupportVectorMachine.TrainingParameters());
                break;
        }
        // ----- Fin parámetros entrenamiento ----- //

        // Generar el clasificador
        TextClassifier classifier = MLBuilder.create(trainingParameters, configuration);
        AbstractTextExtractor textExtractor = AbstractTextExtractor.newInstance(trainingParameters.getTextExtractorParameters());

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
        Dataframe trainingData = new Dataframe(configuration);
        for (Record r: records) {
            trainingData.set(trainingData.size(), r);
        }

        // Guardar dataset
        trainingData.save("/" + modelLocation + "/dataset");
        // Entrenar el clasificador
        classifier.fit(trainingData);
        // Guardar modelo del clasificador
        classifier.save("/" + modelLocation);
    }
}

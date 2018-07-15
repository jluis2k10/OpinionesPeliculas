package es.uned.adapters.common;

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
import com.datumbox.framework.core.machinelearning.preprocessing.BinaryScaler;
import com.datumbox.framework.core.machinelearning.preprocessing.MaxAbsScaler;
import com.datumbox.framework.core.machinelearning.preprocessing.MinMaxScaler;
import com.datumbox.framework.core.machinelearning.preprocessing.StandardScaler;
import com.datumbox.framework.storage.inmemory.InMemoryConfiguration;
import es.uned.adapters.ClassifierType;
import es.uned.entities.Opinion;
import es.uned.entities.Polarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.*;

/**
 * Clase común para los clasificadores de la librería Datumbox
 */
public abstract class CommonDatumbox  {

    @Autowired private ResourceLoader resourceLoader;

    public abstract String get_adapter_path();

    public abstract ClassifierType get_adapter_type();

    /**
     * Definir configuración del modelo (en memoria, directorio)
     * @return Configuration del modelo
     */
    protected Configuration defineConfiguration() {
        Configuration configuration = Configuration.getConfiguration();
        InMemoryConfiguration memConfiguration = new InMemoryConfiguration();
        Resource resource = resourceLoader.getResource(get_adapter_path().toString());
        String modelsDirectory = null;
        try {
            modelsDirectory = resource.getFile().getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        memConfiguration.setDirectory(modelsDirectory);
        configuration.setStorageConfiguration(memConfiguration);
        return configuration;
    }

    /**
     * Guarda en disco el modelo serializado.
     * @param modelLocation Directorio donde se guardará el modelo
     * @param dataframe     Conjunto de datos o dataset
     * @param classifier    Clasificador
     */
    private void saveModel(String modelLocation, Dataframe dataframe, TextClassifier classifier) {
        dataframe.save(modelLocation + "/dataset");
        classifier.save(modelLocation);
    }

    public void trainModel(String modelLocation, Map<Enum, List<String>> datasets) {
        RandomGenerator.setGlobalSeed(42L);
        Configuration configuration = defineConfiguration();

        // Cargar el clasificador según el modelo indicado
        TextClassifier classifier = MLBuilder.load(TextClassifier.class, modelLocation, configuration);
        TextClassifier.TrainingParameters trainingParameters = (TextClassifier.TrainingParameters) classifier.getTrainingParameters();
        AbstractTextExtractor textExtractor = AbstractTextExtractor.newInstance(trainingParameters.getTextExtractorParameters());

        // Cargar el dataset con el que se construyó el modelo
        Dataframe dataset = Dataframe.Builder.load(modelLocation + "/dataset", configuration);

        // Crear nuevas entradas para el dataset
        List<Record> records = generateRecords(textExtractor, datasets);

        // Añadir entradas al dataset
        for (Record r: records) {
            dataset.set(dataset.size(), r);
        }

        // Entrenar el clasificador
        classifier.fit(dataset);

        saveModel(modelLocation, dataset, classifier);
    }

    public void createModel(String modelLocation, Map<String,String> options, Map<Enum, List<String>> datasets) {
        RandomGenerator.setGlobalSeed(42L);
        Configuration configuration = defineConfiguration();

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
                MutualInformation.TrainingParameters MITrainingParameters = new MutualInformation.TrainingParameters();
                MITrainingParameters.setRareFeatureThreshold(Integer.parseInt(options.get("MutualInformation_RareFeatureThreshold")));
                MITrainingParameters.setMaxFeatures(Integer.parseInt(options.get("MutualInformation_MaxFeatures")));
                trainingParameters.setFeatureSelectorTrainingParametersList(Arrays.asList(MITrainingParameters));
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
        List<Record> records = generateRecords(textExtractor, datasets);

        // Añadir entradas al dataset
        Dataframe trainingData = new Dataframe(configuration);
        for (Record r: records) {
            trainingData.set(trainingData.size(), r);
        }

        // Entrenar el clasificador
        classifier.fit(trainingData);

        saveModel(modelLocation, trainingData, classifier);
    }

    /**
     * Genera lista de records para ser añadidos al dataset.
     * @param textExtractor TextExtractor para procesar el String
     * @param datasets      Datasets de comentarios clasificados por categoría (positivos, negativos, objetivos...)
     * @return Lista de records
     */
    private List<Record> generateRecords(AbstractTextExtractor textExtractor, Map<Enum, List<String>> datasets) {
        List<Record> records = new ArrayList<>();

        if (get_adapter_type() == ClassifierType.POLARITY) {
            datasets.get(Polarity.POSITIVE).stream()
                    .filter(sentence -> null != sentence && !sentence.isEmpty())
                    .forEach(sentence -> {
                        AssociativeArray xData = new AssociativeArray(textExtractor.extract(StringCleaner.clear(sentence)));
                        records.add(new Record(xData, "positive"));
                    });
            datasets.get(Polarity.NEGATIVE).stream()
                    .filter(sentence -> null != sentence && !sentence.isEmpty())
                    .forEach(sentence -> {
                        AssociativeArray xData = new AssociativeArray(textExtractor.extract(StringCleaner.clear(sentence)));
                        records.add(new Record(xData, "negative"));
                    });
            if (datasets.containsKey(Polarity.NEUTRAL)) {
                datasets.get(Polarity.NEUTRAL).stream()
                        .filter(sentence -> null != sentence && !sentence.isEmpty())
                        .forEach(sentence -> {
                            AssociativeArray xData = new AssociativeArray(textExtractor.extract(StringCleaner.clear(sentence)));
                            records.add(new Record(xData, "neutral"));
                        });
            }
        }
        else if (get_adapter_type() == ClassifierType.OPINION) {
            datasets.get(Opinion.SUBJECTIVE).stream()
                    .filter(sentence -> null != sentence && !sentence.isEmpty())
                    .forEach(sentence -> {
                        AssociativeArray xData = new AssociativeArray(textExtractor.extract(StringCleaner.clear(sentence)));
                        records.add(new Record(xData, "subjective"));
                    });
            datasets.get(Opinion.OBJECTIVE).stream()
                    .filter(sentence -> null != sentence && !sentence.isEmpty())
                    .forEach(sentence -> {
                        AssociativeArray xData = new AssociativeArray(textExtractor.extract(StringCleaner.clear(sentence)));
                        records.add(new Record(xData, "objective"));
                    });
        }

        return records;
    }

}

package es.uned.adapters.common;

import com.aliasi.classify.BaseClassifier;
import com.aliasi.classify.DynamicLMClassifier;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.io.BitInput;
import com.aliasi.lm.LanguageModel;
import com.aliasi.lm.NGramBoundaryLM;
import com.aliasi.lm.NGramProcessLM;
import com.aliasi.util.AbstractExternalizable;
import es.uned.adapters.ClassifierType;
import es.uned.entities.Opinion;
import es.uned.entities.Polarity;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public abstract class CommonLingpipe {

    @Autowired
    private ResourceLoader resourceLoader;

    public abstract String get_adapter_path();

    public abstract ClassifierType get_adapter_type();

    public BaseClassifier<String> getBaseClassifier(Resource resource) {
        BaseClassifier<String> baseClassifier = null;
        try {
            File modelFile = resource.getFile();
            baseClassifier = (BaseClassifier<String>) AbstractExternalizable.readObject(modelFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return baseClassifier;
    }

    private Map<Enum, File> getFiles(File dir) {
        Map<Enum, File> files;

        if (get_adapter_type() == ClassifierType.POLARITY) {
            files = new EnumMap(Polarity.class);
            files.put(Polarity.POSITIVE, new File(dir.toString() + "/lm_pos.model"));
            files.put(Polarity.NEGATIVE, new File(dir.toString() + "/lm_neg.model"));
            if (Files.exists(Paths.get(dir.toString() + "/lm_neu.model")))
                files.put(Polarity.NEUTRAL, new File(dir.toString() + "/lm_neu.model"));
        }
        else {
            files = new EnumMap(Opinion.class);
            files.put(Opinion.SUBJECTIVE, new File(dir.toString() + "/lm_subjective.model"));
            files.put(Opinion.OBJECTIVE, new File(dir.toString() + "/lm_objective.model"));
        }

        return files;
    }

    /**
     * Lingpipe no tiene modelos que se puedan re-entrenar. Lo que hay que hacer es serializar los "language models",
     * recrear el modelo con ellos y guardarlo todo.
     */
    public void trainModel(String modelLocation, Map<Enum, List<String>> datasets) {
        Resource resource = resourceLoader.getResource(get_adapter_path().toString() + modelLocation);
        File dir = null;
        try {
            dir = resource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Enum, File> files = getFiles(dir);

        String[] categories = new String[files.size()];
        if (get_adapter_type() == ClassifierType.POLARITY) {
            categories[0] = "pos";
            categories[1] = "neg";
            if (files.containsKey(Polarity.NEUTRAL))
                categories[2] = "neu";
        } else {
            categories[0] = "subjective";
            categories[1] = "objective";
        }

        // Determinar si el LM es NGramBoundary o NGramProcess
        char boundaryChar = '\0';
        try {
            File first = files.get(Polarity.POSITIVE);
            BitInput bitInput = new BitInput(new BufferedInputStream(FileUtils.openInputStream(first)));
            boundaryChar = (char) (bitInput.readDelta()-1L);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // '\uFFFF' carácter unicode "prohibido" que se utiliza como frontera en el modelo
        // y que sólo debería aparecer en NGramBoundary
        final boolean isNGramProcess = boundaryChar != '\uFFFF';

        // Cargar language models
        Map<Enum, ObjectHandler<CharSequence>> lms;
        if (get_adapter_type() == ClassifierType.POLARITY)
            lms = new EnumMap(Polarity.class);
        else
            lms = new EnumMap(Opinion.class);
        files.forEach((key, file) -> {
            try {
                if (isNGramProcess)
                    lms.put(key, NGramProcessLM.readFrom(new BufferedInputStream(FileUtils.openInputStream(file))));
                else
                    lms.put(key, NGramBoundaryLM.readFrom(new BufferedInputStream(FileUtils.openInputStream(file))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Entrenamos los language models con los nuevos datasets
        datasets.forEach((key, dataset) -> {
            dataset.stream()
                    .filter(sentence -> null != sentence && !sentence.isEmpty())
                    .forEach(sentence -> {
                        if (isNGramProcess)
                            ((NGramProcessLM) lms.get(key)).train(sentence);
                        else
                            ((NGramBoundaryLM) lms.get(key)).train(sentence);
                    });
        });

        // Convertir mapa de languame models a array
        ObjectHandler<CharSequence>[] lmsArray;
        if (isNGramProcess)
            lmsArray = new NGramProcessLM[lms.size()];
        else
            lmsArray = new NGramBoundaryLM[lms.size()];
        if (get_adapter_type() == ClassifierType.POLARITY) {
            lmsArray[0] = lms.get(Polarity.POSITIVE);
            lmsArray[1] = lms.get(Polarity.NEGATIVE);
            if (lms.containsKey(Polarity.NEUTRAL))
                lmsArray[2] = lms.get(Polarity.NEUTRAL);
        }
        else {
            lmsArray[0] = lms.get(Opinion.SUBJECTIVE);
            lmsArray[1] = lms.get(Opinion.OBJECTIVE);
        }

        // Recrear modelo del clasificador a partir de los language models anteriores
        DynamicLMClassifier<LanguageModel.Dynamic> classifier = new DynamicLMClassifier(categories, (LanguageModel.Dynamic[]) lmsArray);

        // guardar modelos de lenguaje
        for(int j = 0; j < lmsArray.length; ++j) {
            try {
                dir = resource.getFile();
                File saveToFile = new File(dir.toString() + "/lm_" + categories[j] + ".model");
                OutputStream fileOut = FileUtils.openOutputStream(saveToFile);
                BufferedOutputStream bufOut = new BufferedOutputStream(fileOut);
                if (isNGramProcess)
                    ((NGramProcessLM)lmsArray[j]).writeTo(bufOut);
                else
                    ((NGramBoundaryLM)lmsArray[j]).writeTo(bufOut);
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

    public void createModel(String modelLocation, Map<String,String> options, Map<Enum, List<String>> datasets) {
        Resource resource = resourceLoader.getResource(get_adapter_path().toString());
        File dir = null;
        try {
            dir = resource.getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] categories = new String[datasets.size()];
        if (get_adapter_type() == ClassifierType.POLARITY) {
            categories[0] = "pos";
            categories[1] = "neg";
            if (datasets.containsKey(Polarity.NEUTRAL))
                categories[2] = "neu";
        } else {
            categories[0] = "subjective";
            categories[1] = "objective";
        }

        // Construir Language Model según las opciones
        ObjectHandler<CharSequence>[] lms = null; // lms[0] -> positivos/subjetivos, lms[1] -> negativos/objetivos, lms[2] -> neutrales
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
        if (get_adapter_type() == ClassifierType.POLARITY) {
            for (String sentence : datasets.get(Polarity.POSITIVE)) {
                if (null != sentence && !sentence.isEmpty())
                    lms[0].handle(sentence);
            }
            for (String sentence : datasets.get(Polarity.NEGATIVE)) {
                if (null != sentence && !sentence.isEmpty())
                    lms[1].handle(sentence);
            }
            if (datasets.containsKey(Polarity.NEUTRAL)) {
                for (String sentence : datasets.get(Polarity.NEUTRAL)) {
                    if (null != sentence && !sentence.isEmpty())
                        lms[2].handle(sentence);
                }
            }
        }
        else {
            for (String sentence : datasets.get(Opinion.SUBJECTIVE)) {
                if (null != sentence && !sentence.isEmpty())
                    lms[0].handle(sentence);
            }
            for (String sentence : datasets.get(Opinion.OBJECTIVE)) {
                if (null != sentence && !sentence.isEmpty())
                    lms[1].handle(sentence);
            }
        }

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

package es.uned.entities;

import es.uned.adapters.AdapterType;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class TrainParams {

    private String term;
    private String sourceClass;
    private int limit;
    private String sinceDate;
    private String untilDate;
    private String lang;
    private AdapterType adapterType;
    private String modelLocation;
    private String adapterClass;
    private String psText; // positive or subjective text
    private String noText; // negative or objective text
    private MultipartFile psFile; // positive or subjective file
    private MultipartFile noFile; // negative or objective file

    public TrainParams() {}

    public TrainParams(AdapterModels model) {
        this.lang = model.getLanguage();
        this.adapterType = model.getAdapterType();
        this.modelLocation = model.getLocation();
        this.adapterClass = model.getAdapterClass();
    }

    public List<String> sentenceList(MultipartFile file) {
        List<String> sentences = new ArrayList<>();
        try {
            InputStream is = file.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sentences.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sentences;
    }

    public List<String> sentenceList(String text) {
        String[] lines = text.split("\\r?\\n");
        return new ArrayList<>(Arrays.asList(lines));
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(String sourceClass) {
        this.sourceClass = sourceClass;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getSinceDate() {
        return sinceDate;
    }

    public void setSinceDate(String sinceDate) {
        this.sinceDate = sinceDate;
    }

    public String getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(String untilDate) {
        this.untilDate = untilDate;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public AdapterType getAdapterType() {
        return adapterType;
    }

    public void setAdapterType(AdapterType adapterType) {
        this.adapterType = adapterType;
    }

    public String getModelLocation() {
        return modelLocation;
    }

    public void setModelLocation(String modelLocation) {
        this.modelLocation = modelLocation;
    }

    public String getAdapterClass() {
        return adapterClass;
    }

    public void setAdapterClass(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public String getPsText() {
        return psText;
    }

    public void setPsText(String psText) {
        this.psText = psText;
    }

    public String getNoText() {
        return noText;
    }

    public void setNoText(String noText) {
        this.noText = noText;
    }

    public MultipartFile getPsFile() {
        return psFile;
    }

    public void setPsFile(MultipartFile psFile) {
        this.psFile = psFile;
    }

    public MultipartFile getNoFile() {
        return noFile;
    }

    public void setNoFile(MultipartFile noFile) {
        this.noFile = noFile;
    }
}

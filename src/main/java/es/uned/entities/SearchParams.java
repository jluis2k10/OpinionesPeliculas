package es.uned.entities;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class SearchParams {

    private String searchTerm;
    private String sourceClass;
    private int limit;
    private String sinceDate;
    private String untilDate;
    private String lang;
    private String sentimentAdapter;
    private String sentimentModel;

    private final String PARAMS_KEYS = "(?:searchTerm|sourceClass|limit|sinceDate|untilDate|lang|sentimentAdapter|" +
            "sentimentModel)";

    public Map<String,String> getOptionalParameters(Map<String,String[]> parameters) {

        Map<String,String> optionalParameters = new HashMap<>();
        Pattern pattern = Pattern.compile(PARAMS_KEYS);

        parameters.forEach((key, value) -> {
            Matcher matcher = pattern.matcher(key);
            if (!matcher.matches())
                optionalParameters.put(key, StringUtils.join(value, ""));
        });

        return optionalParameters;

    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
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

    public String getSentimentAdapter() {
        return sentimentAdapter;
    }

    public void setSentimentAdapter(String sentimentAdapter) {
        this.sentimentAdapter = sentimentAdapter;
    }

    public String getSentimentModel() {
        return sentimentModel;
    }

    public void setSentimentModel(String sentimentModel) {
        this.sentimentModel = sentimentModel;
    }
}

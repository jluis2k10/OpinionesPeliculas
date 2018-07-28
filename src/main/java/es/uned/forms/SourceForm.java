package es.uned.forms;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsula el formulario para generar un Corpus desde alguna fuente cualquiera
 * de comentarios.
 */
public class SourceForm {

    private String term;

    private int limit;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date sinceDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date untilDate;

    private String lang;

    private String source;

    private String sourceAdapter;

    private MultipartFile file;

    private Map<String, String> options = new HashMap<>();

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Date getSinceDate() {
        return sinceDate;
    }

    public void setSinceDate(Date sinceDate) {
        this.sinceDate = sinceDate;
    }

    public Date getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(Date untilDate) {
        this.untilDate = untilDate;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceAdapter() {
        return sourceAdapter;
    }

    public void setSourceAdapter(String sourceAdapter) {
        this.sourceAdapter = sourceAdapter;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
}

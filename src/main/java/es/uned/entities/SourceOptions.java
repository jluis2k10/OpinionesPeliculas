package es.uned.entities;

/**
 *
 */
public class SourceOptions {

    private String name;
    private String adapterClass;
    private boolean limitEnabled;
    private boolean sinceDateEnabled;
    private boolean untilDateEnabled;
    private boolean languageEnabled;
    private boolean imdbIDEnabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdapterClass() {
        return adapterClass;
    }

    public void setAdapterClass(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public boolean isLimitEnabled() {
        return limitEnabled;
    }

    public void setLimitEnabled(boolean limitEnabled) {
        this.limitEnabled = limitEnabled;
    }

    public boolean isSinceDateEnabled() {
        return sinceDateEnabled;
    }

    public void setSinceDateEnabled(boolean sinceDateEnabled) {
        this.sinceDateEnabled = sinceDateEnabled;
    }

    public boolean isUntilDateEnabled() {
        return untilDateEnabled;
    }

    public void setUntilDateEnabled(boolean untilDateEnabled) {
        this.untilDateEnabled = untilDateEnabled;
    }

    public boolean isImdbIDEnabled() {
        return imdbIDEnabled;
    }

    public void setImdbIDEnabled(boolean imdbIDEnabled) {
        this.imdbIDEnabled = imdbIDEnabled;
    }

    public boolean isLanguageEnabled() {
        return languageEnabled;
    }

    public void setLanguageEnabled(boolean languageEnabled) {
        this.languageEnabled = languageEnabled;
    }
}

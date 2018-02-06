package es.uned.adapters;

/**
 *
 */
public enum AdapterType {

    SENTIMENT("Sentiment"),
    SUBJECTIVITY("Subjectivity");

    String adapterType;

    AdapterType(String adapterType) {
        this.adapterType = adapterType;
    }

    public String getAdapterType() {
        return this.adapterType;
    }
}

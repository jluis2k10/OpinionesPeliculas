package es.uned.adapters;

/**
 *
 */
public enum ClassifierType {

    POLARITY("Polarity"),
    OPINION("Opinion");

    String classifierType;

    ClassifierType(String classifierType) {
        this.classifierType = classifierType;
    }

    public String getClassifierType() {
        return this.classifierType;
    }
}

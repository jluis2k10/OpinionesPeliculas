package es.uned.entities;

/**
 *
 */
public enum Subjectivity {

    SUBJECTIVE("Subjective"),
    OBJECTIVE("Objective");

    String subjectivity;

    Subjectivity(String subjectivity) {
        this.subjectivity = subjectivity;
    }

    public String getSubjectivity() {
        return this.subjectivity;
    }

}

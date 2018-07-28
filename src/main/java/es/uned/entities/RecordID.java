package es.uned.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Los {@link es.uned.entities.Record} tienen un identificador compuesto.
 * Esta clase modela dicho identificador, el cual comprende por un lado el
 * ID del {@link es.uned.entities.Analysis} que los genera y por otro el ID
 * del {@link es.uned.entities.Comment} sobre el cual ha sido ejecutado el
 * análisis.
 */
@Embeddable
public class RecordID implements Serializable {

    @Column(name = "analysis_id", insertable = false, updatable = false)
    private Long analysis;

    @Column(name = "comment_id", insertable = false, updatable = false)
    private Long comment;

    public RecordID() {}

    public RecordID(Long analysis, Long comment) {
        this.analysis = analysis;
        this.comment = comment;
    }

    public Long getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Long analysis) {
        this.analysis = analysis;
    }

    public Long getComment() {
        return comment;
    }

    public void setComment(Long comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordID recordID = (RecordID) o;
        return Objects.equals(analysis, recordID.analysis) &&
                Objects.equals(comment, recordID.comment);
    }

    @Override
    public int hashCode() {

        return Objects.hash(analysis, comment);
    }
}

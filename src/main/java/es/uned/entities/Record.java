package es.uned.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.adapters.ClassifierType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.*;
import java.util.Objects;

/**
 *
 */
@Entity
@Table(name = "records")
public class Record {

    private static Log logger = LogFactory.getLog(Record.class);

    @EmbeddedId
    private RecordID id = new RecordID();

    @MapsId("analysis")
    @ManyToOne
    @JoinColumn(name = "analysis_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Analysis analysis;

    @MapsId("comment")
    @ManyToOne
    @JoinColumn(name = "comment_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Comment comment;

    @Column(name = "comment_hash", nullable = false)
    private int commentHash;

    @Enumerated
    @Column(name = "polarity", columnDefinition = "smallint")
    private Polarity polarity;

    @Enumerated
    @Column(name = "opinion", columnDefinition = "smallint")
    private Opinion opinion;

    @Column(name = "polarity_score")
    private double polarityScore;

    @Column(name = "pos_score")
    private double positiveScore;

    @Column(name = "neu_score")
    private double neutralScore;

    @Column(name = "neg_score")
    private double negativeScore;

    @Column(name = "subjective_score")
    private double subjectiveScore;

    public Record() {
    }

    public ObjectNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode recordNode = mapper.createObjectNode();
        recordNode.put("comment_hash", getCommentHash());
        if (analysis.getAnalysisType() == ClassifierType.POLARITY) {
            recordNode.put("polarity", getPolarity().getPolarity());
            recordNode.put("score", getPolarityScore());
            recordNode.put("positiveScore", getPositiveScore());
            recordNode.put("neutralScore", getNeutralScore());
            recordNode.put("negativeScore", getNegativeScore());
        }
        else if (analysis.getAnalysisType() == ClassifierType.OPINION) {
            recordNode.put("opinion", getOpinion().getOpinion());
            recordNode.put("subjectiveScore", getSubjectiveScore());
        }
        return recordNode;
    }

    public void update(Record fromRecord) {
        this.polarity = fromRecord.getPolarity();
        this.opinion = fromRecord.getOpinion();
        this.polarityScore = fromRecord.getPolarityScore();
        this.positiveScore = fromRecord.getPositiveScore();
        this.negativeScore = fromRecord.getNegativeScore();
        this.neutralScore = fromRecord.getNeutralScore();
        this.subjectiveScore = fromRecord.getSubjectiveScore();
    }

    public RecordID getId() {
        return id;
    }

    public void setId(RecordID id) {
        this.id = id;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        if (sameAsFormer(analysis))
            return;
        Analysis oldAnalysis = this.analysis;
        this.id.setAnalysis(analysis == null ? null : analysis.getId());
        this.analysis = analysis;
        if (oldAnalysis != null)
            oldAnalysis.removeRecord(this);
        if (analysis != null)
            analysis.addRecord(this);
    }

    public boolean sameAsFormer(Analysis newAnalysis) {
        return analysis == null ? newAnalysis == null : analysis.equals(newAnalysis);
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        if (sameAsFormer(comment))
            return;
        Comment oldComment = this.comment;
        this.comment = comment;
        this.id.setComment(comment == null ? null: comment.getId());
        if (oldComment != null)
            oldComment.removeRecord(this);
        if (comment != null)
            comment.addRecord(this);
        this.commentHash = comment == null ? 0 : comment.getHash();
    }

    public boolean sameAsFormer(Comment newComment) {
        return comment == null ? newComment == null : comment.equals(newComment);
    }

    public int getCommentHash() {
        return commentHash;
    }

    public void setCommentHash(int commentHash) {
        this.commentHash = commentHash;
    }

    public Polarity getPolarity() {
        return polarity;
    }

    public void setPolarity(Polarity polarity) {
        this.polarity = polarity;
    }

    public Opinion getOpinion() {
        return opinion;
    }

    public void setOpinion(Opinion opinion) {
        this.opinion = opinion;
    }

    public double getPolarityScore() {
        return polarityScore;
    }

    public void setPolarityScore(double polarityScore) {
        this.polarityScore = polarityScore;
    }

    public double getPositiveScore() {
        return positiveScore;
    }

    public void setPositiveScore(double positiveScore) {
        this.positiveScore = positiveScore;
    }

    public double getNeutralScore() {
        return neutralScore;
    }

    public void setNeutralScore(double neutralScore) {
        this.neutralScore = neutralScore;
    }

    public double getNegativeScore() {
        return negativeScore;
    }

    public void setNegativeScore(double negativeScore) {
        this.negativeScore = negativeScore;
    }

    public double getSubjectiveScore() {
        return subjectiveScore;
    }

    public void setSubjectiveScore(double subjectiveScore) {
        this.subjectiveScore = subjectiveScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(getAnalysis(), record.getAnalysis()) &&
                Objects.equals(getComment(), record.getComment());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAnalysis(), getComment(), getCommentHash(), getPolarity(), getOpinion(), getPolarityScore(), getPositiveScore(), getNeutralScore(), getNegativeScore(), getSubjectiveScore());
    }
}

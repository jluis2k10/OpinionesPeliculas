package es.uned.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 *
 */
@Entity
@Table(name = "Corpora")
public class Corpus {

    private static Log logger = LogFactory.getLog(Corpus.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", nullable = false)
    private Account owner;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "lang", nullable = false)
    private String lang;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();

    @Column(name = "updated")
    private LocalDateTime updated;

    @Column(name = "public", nullable = false)
    private boolean isPublic = false;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "corpus_id")
    private Collection<Analysis> analyses = new LinkedList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "corpus_id")
    private Collection<Comment> comments = new LinkedList<>();

    public Corpus() {
    }

    public void refreshScores() {
        this.comments.forEach(comment -> comment.refreshScores());
    }

    public ObjectNode toJson(boolean withComments, boolean withAnalyses, boolean withRecords) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode corpusNode = mapper.createObjectNode();

        if (getId() != null)
            corpusNode.put("id", getId());
        else
            corpusNode.putNull("id");
        if (getOwner() != null)
            corpusNode.put("owner", getOwner().getUserName());
        else
            corpusNode.putNull("owner");
        if (getName() != null)
            corpusNode.put("name", getName());
        else
            corpusNode.putNull("name");
        if (getDescription() != null)
            corpusNode.put("description", getDescription());
        corpusNode.put("lang", getLang());
        corpusNode.put("created", getCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if (getUpdated() != null)
            corpusNode.put("updated", getUpdated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        else
            corpusNode.putNull("updated");
        corpusNode.put("is_public", isPublic());
        corpusNode.put("total_comments", getComments().size());
        if (withComments) {
            LinkedList<Comment> commentsList = new LinkedList<>(getComments());
            Collections.sort(commentsList);
            ArrayNode commentsArrayNode = mapper.createArrayNode();
            commentsList.forEach(comment -> commentsArrayNode.add(comment.toJson(withRecords)));
            corpusNode.set("comments", commentsArrayNode);
        }
        if (withAnalyses) {
            ArrayNode analysesArray = mapper.createArrayNode();
            getAnalyses().forEach(analysis -> analysesArray.add(analysis.toJson(true)));
            corpusNode.set("analyses", analysesArray);
        }

        return corpusNode;
    }

    public void clearAll() {
        analyses.clear();
        comments.clear();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public Collection<Comment> getComments() {
        return new LinkedList<Comment>(comments);
    }

    public void addComment(Comment comment) {
        if (comments.contains(comment))
            return;
        comments.add(comment);
        comment.setCorpus(this);
    }

    public void addComments(List<Comment> newComments) {
        newComments.forEach(newComment -> addComment(newComment));
    }

    public void removeComment(Comment comment) {
        if (!comments.contains(comment))
            return;
        comments.remove(comment);
        comment.setCorpus(null);
    }

    public Collection<Analysis> getAnalyses() {
        return new LinkedList<Analysis>(analyses);
    }

    public void addAnalysis(Analysis analysis) {
        if (analyses.contains(analysis))
            return;
        analyses.add(analysis);
        analysis.setCorpus(this);
    }

    public void removeAnalysis(Analysis analysis) {
        if (!analyses.contains(analysis))
            return;
        analyses.remove(analysis);
        analysis.setCorpus(null);
    }

    public void addAnalyses(List<Analysis> analyses) {
        analyses.forEach(analysis -> addAnalysis(analysis));
    }

    /**
     * JSP necesita este método para acceder a la variable
     * @return
     */
    public boolean getIsPublic() {
        return isPublic;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Corpus)) return false;
        Corpus corpus = (Corpus) o;
        return isPublic() == corpus.isPublic() &&
                Objects.equals(getId(), corpus.getId()) &&
                Objects.equals(getOwner(), corpus.getOwner()) &&
                Objects.equals(getName(), corpus.getName()) &&
                Objects.equals(getDescription(), corpus.getDescription()) &&
                Objects.equals(getLang(), corpus.getLang()) &&
                Objects.equals(getCreated(), corpus.getCreated()) &&
                Objects.equals(getUpdated(), corpus.getUpdated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOwner(), getName(), getDescription(), getLang(), getCreated(), getUpdated(), isPublic());
    }
}

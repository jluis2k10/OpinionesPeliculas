package es.uned.entities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.uned.adapters.ClassifierType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Entidad para el Corpus. Un corpus es un almacén de comentarios y de análisis sobre esos comentarios.
 * Tabla CORPORA en la base de datos.
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

    /**
     * Recalcula las puntuaciones medias obtenidas durante la ejecución de los análisis
     * sobre los comentarios del corpus.
     */
    public void refreshScores() {
        this.comments.forEach(comment -> comment.refreshScores());
    }

    /**
     * Convertir la entidad a un objeto en formato JSON
     * @param withComments True si se debe incluir la lista de comentarios ({@link es.uned.entities.Comment})
     *                     del corpus
     * @param withAnalyses True si se debe incluir la lista de análisis ({@link es.uned.entities.Analysis})
     *                     ejecutados sobre los comentarios del corpus
     * @param withRecords  True si debe incluir la lista de records ({@link es.uned.entities.Record}) del corpus
     * @return Entidad con formato JSON
     */
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
        corpusNode.put("domain_analysis", hasDomainAnalysis());
        if (withAnalyses) {
            ArrayNode analysesArray = mapper.createArrayNode();
            getAnalyses().forEach(analysis -> analysesArray.add(analysis.toJson(withRecords)));
            corpusNode.set("analyses", analysesArray);
        }
        return corpusNode;
    }

    /**
     * Devuelve cierto si ya se ha ejecutado algún análisis de dominio sobre el Corpus.
     * @return true si el Corpus tiene algún análisis de dominio, false en caso contrario
     */
    public boolean hasDomainAnalysis() {
        return getAnalyses().stream().anyMatch(analysis -> analysis.getAnalysisType() == ClassifierType.DOMAIN);
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

    /**
     * Añade un nuevo {@link es.uned.entities.Comment} al corpus evitando duplicados. Asimismo añade
     * este corpus al comentario para evitar inconsistencias en la base de datos
     * @param comment Comentario a añadir
     */
    public void addComment(Comment comment) {
        if (comments.contains(comment))
            return;
        comments.add(comment);
        comment.setCorpus(this);
    }

    /**
     * Añade una lista de {@link es.uned.entities.Comment} al corpus evitando duplicados y añadiendo este
     * corpus a cada uno de los comentarios para evitar inconsistencias en la base de datos
     * @param newComments Lista de comentarios a añadir
     */
    public void addComments(List<Comment> newComments) {
        newComments.forEach(newComment -> addComment(newComment));
    }

    /**
     * Elimina un {@link es.uned.entities.Comment} del corpus, eliminaando también la asociación a este
     * corpus en el comentario para evitar inconsistencias en la base de datos
     * @param comment El comentario a eliminar
     */
    public void removeComment(Comment comment) {
        if (!comments.contains(comment))
            return;
        comments.remove(comment);
        comment.setCorpus(null);
    }

    public Collection<Analysis> getAnalyses() {
        return new LinkedList<>(analyses);
    }

    /**
     * Añade un nuevo {@link es.uned.entities.Analysis} al corpus evitando duplicados. Asimismo añade
     * este corpus al análisis para evitar inconsistencias en la base de datos
     * @param analysis Análisis a añadir
     */
    public void addAnalysis(Analysis analysis) {
        if (analyses.contains(analysis))
            return;

        // Si ya existe un análisis de dominio, lo sustituimos
        if (analysis.getAnalysisType() == ClassifierType.DOMAIN && this.hasDomainAnalysis()) {
            Analysis oldAnalysis = getAnalyses().stream()
                    .filter(an -> an.getAnalysisType() == ClassifierType.DOMAIN)
                    .findFirst()
                    .get();
            this.removeAnalysis(oldAnalysis);
        }

        analyses.add(analysis);
        analysis.setCorpus(this);
    }

    /**
     * Añade una lista de {@link es.uned.entities.Analysis} al corpus evitando duplicados y añadiendo este
     * corpus a cada uno de los análisis para evitar inconsistencias en la base de datos
     * @param analyses Lista de análisis a añadir
     */
    public void addAnalyses(List<Analysis> analyses) {
        analyses.forEach(analysis -> addAnalysis(analysis));
    }

    /**
     * Elimina un {@link es.uned.entities.Analysis} del corpus, eliminando también la asociación a este
     * corpus en el análisis para evitar inconsistencias en la base de datos
     * @param analysis El análisis a eliminar
     */
    public void removeAnalysis(Analysis analysis) {
        if (!analyses.contains(analysis))
            return;
        analyses.remove(analysis);
        analysis.setCorpus(null);
    }

    /**
     * JSP necesita este método para acceder a la variable
     * @return true si corpus es público, false en caso contrario
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

package es.uned.entities;

import es.uned.adapters.ClassifierType;

import javax.persistence.*;
import java.util.Objects;

/**
 *
 */
@Entity
@Table(name = "Language_Models")
public class LanguageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, unique = true, nullable = false)
    private String name;

    @Column(name = "classifier_type", length = 13)
    private ClassifierType classifierType;

    @Column(name = "adapter_class", nullable = false)
    private String adapterClass;

    @Column(name = "language", length = 2, nullable = false)
    private String language;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "trainable")
    private boolean trainable = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner")
    private Account owner;

    @Column(name = "public")
    private boolean isPublic;

    @Column(name = "description", nullable = true)
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClassifierType getClassifierType() {
        return classifierType;
    }

    public void setClassifierType(ClassifierType classifierType) {
        this.classifierType = classifierType;
    }

    public String getAdapterClass() {
        return adapterClass;
    }

    public void setAdapterClass(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isTrainable() {
        return trainable;
    }

    public void setTrainable(boolean trainable) {
        this.trainable = trainable;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LanguageModel that = (LanguageModel) o;
        return trainable == that.trainable &&
                isPublic == that.isPublic &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                classifierType == that.classifierType &&
                Objects.equals(adapterClass, that.adapterClass) &&
                Objects.equals(language, that.language) &&
                Objects.equals(location, that.location) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, classifierType, adapterClass, language, location, trainable, isPublic, description);
    }
}
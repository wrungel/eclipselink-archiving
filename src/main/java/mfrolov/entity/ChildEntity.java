package mfrolov.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.eclipse.persistence.annotations.AdditionalCriteria;

@AdditionalCriteria("this.archived in :archived or this.archived is null")
@Entity
public class ChildEntity {
    @Id
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String archived;

    @ManyToOne
    private ParentEntity parent;

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setArchived(String archived) {
        this.archived = archived;
    }

    public String getArchived() {
        return archived;
    }

    public ParentEntity getParent() {
        return parent;
    }

    public void setParent(ParentEntity parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}

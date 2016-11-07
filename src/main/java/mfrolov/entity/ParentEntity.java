package mfrolov.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.eclipse.persistence.annotations.AdditionalCriteria;

@AdditionalCriteria("this.archived in :archived or this.archived is null")
@Entity
public class ParentEntity {
    @Id
    private Long id;

    private String description;

    private String archived;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    private List<ChildEntity> children = new ArrayList<>();

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

    public List<ChildEntity> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return getDescription();
    }
}

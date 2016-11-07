package mfrolov.test;

import java.util.Objects;

public class ChildParentDto {
    private final String childDescription;
    private final String parentDescription;

    public ChildParentDto(String childDescription, String parentDescription) {
        this.childDescription = childDescription;
        this.parentDescription = parentDescription;
    }

    public String getChildDescription() {
        return childDescription;
    }

    public String getParentDescription() {
        return parentDescription;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", childDescription, parentDescription);
    }

    @Override
    public int hashCode() {
        return Objects.hash(childDescription, parentDescription);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof ChildParentDto)) {
            return false;
        }

        ChildParentDto other = (ChildParentDto) obj;

        return Objects.equals(this.childDescription, other.childDescription)
                && Objects.equals(this.parentDescription, other.parentDescription);
    }

    /**
     * @param description e.g <code>C5:P2</code>
     */
    public static ChildParentDto childParentDto(String description) {
        String[] descriptions = description.split(":");
        return new ChildParentDto(descriptions[0], descriptions[1]);
    }
}

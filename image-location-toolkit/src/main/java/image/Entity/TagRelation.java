package image.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Simple POJO class for the Image <-> Tag database relation
 */
@Entity
@Table(name = "image_tag_relation")
public class TagRelation implements image.Entity.Entity{

    // Data Members
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "relation_id", unique = true, nullable = false)
    private int relationId;

    @Column(name = "image_id")
    private int imageId;

    @Column(name = "tag_id")
    private int tagId;

    @Column(name = "tag_confidence")
    private double tagConfidence;

    // Constructors
    /**
     * Constructor - instantiates a generic TagRelation object.
     */
    public TagRelation() {}

    /**
     * Constructor - instantiates a TagRelation object with its fields initialized to the values of passed parameters.
     * @param imageId the image id
     * @param tagId the tag id
     * @param tagConfidence the tag confidence level
     */
    public TagRelation(int imageId, int tagId, double tagConfidence) {
        this.imageId = imageId;
        this.tagId = tagId;
        this.tagConfidence = tagConfidence;
    }

    // Mutators
    /**
     * Sets the image id of the relation.
     *
     * @param imageId the image id as an int
     */
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    /**
     * Sets the tag id of teh relation.
     *
     * @param tagId the tag id as an int
     */
    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    /**
     * Sets the tag-confidence level of the relation.
     *
     * @param tagConfidence a double value between 0 and 100
     */
    public void setTagConfidence(double tagConfidence) {
        this.tagConfidence = tagConfidence;
    }

    /**
     * Sets the unique relation id (unused due to auto generation, but included for future use if needed).
     *
     * @param relationId int value of the unique id
     */
    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }

    // Accessors
    /**
     * Returns the image id from the relation.
     *
     * @return the int value of the image id
     */
    public int getImageId() {
        return imageId;
    }

    /**
     * Returns the tag id from the relation.
     *
     * @return the int value of the tag id
     */
    public int getTagId() {
        return tagId;
    }

    /**
     * Returns the tag confidence value from the relation.
     *
     * @return the tag confidence as a double
     */
    public double getTagConfidence() {
        return tagConfidence;
    }

    /**
     * Returns the id of the entire relation.
     *
     * @return the id of the relation as an int
     */
    public int getRelationId() {
        return relationId;
    }
}

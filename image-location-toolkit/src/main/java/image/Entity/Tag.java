package image.Entity;

import javax.persistence.*;
import javax.persistence.Entity;

/**
 * This class is used to represent an image tag.
 */


@Entity
@Table(name = "tags")
public class Tag implements image.Entity.Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private int id; // Tag id

    @Column(name = "tag")
    private String tag; // Tag name

    /**
     * Constructor - instantiates a generic Tag object.
     */
    public Tag() {}

    /**
     * Constructor - instantiates a Tag object with its fields initialized to the values of parameters.
     * @param id
     * @param tag
     */
    public Tag(int id, String tag) {
        this.id = id;
        this.tag = tag;
    }

    /**
     * Sets tag id.
     *
     * @param id the id of this tag
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets tag name.
     *
     * @param tag the name of this tag
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Returns the tag id.
     *
     * @return the id of this tag
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the tag name.
     *
     * @return the name of this tag
     */
    public String getTag() {
        return tag;
    }

}

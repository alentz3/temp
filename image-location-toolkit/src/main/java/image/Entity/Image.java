package image.Entity;

import javax.persistence.*;
import javax.persistence.Entity;
import java.sql.Blob;

/**
 * POJO class representing an image
 */
@Entity
@Table(name = "image")
public class Image  implements image.Entity.Entity{

    /**
     * Data members of Image class with annotations for database mapping
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "image")
    private Blob image;

    @Column(name = "date")
    private String date;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "location")
    private String location;

    @Column(name = "text")
    private String text;

    // CONSTRUCTORS
    /**
     * Constructor - constructs a generic Image object.
     */
    public Image() {}

    /**
     * Constructor - constructs an Image object with its fields initialized to values passed as parameters.
     *
     * @param image Blob (Binary Large Object) data representing the image
     * @param date date on which the image was taken
     * @param longitude longitude coordinate where the image was taken
     * @param latitude latitude coordinate where the image was taken
     * @param location location where the image was taken
     * @param text text extracted from the image by Tesseract OCR
     */
    public Image(Blob image, String date, String longitude, String latitude, String location, String text) {
        this.image = image;
        this.date = date;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
        this.text = text;
    }

    // ACCESSORS
    /**
     * Returns the image ID.
     *
     * @return the unique ID of an image, as an int
     */
    public int getID() {
        return id;
    }

    /**
     * Returns image data in Blob (Binary Large Object) form.
     *
     * @return a Blob representation of the image
     */
    public Blob getImage() {
        return image;
    }

    /**
     * Returns the date on which the image was taken.
     *
     * @return the date the image was taken, as a String
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the longitude coordinate where the image was taken.
     *
     * @return the longitude coordinate, as a String
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * Returns the latitude coordinate where the image was taken.
     *
     * @return the latitude coordinate, as a String
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * Returns the location where the image was taken.
     *
     * @return the location, as a String
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns any text extracted from the image by Tesseract OCR.
     *
     * @return extracted text, as a String
     */
    public String getText() {
        return text;
    }

    // MUTATORS
    /**
     * Changes the unique ID of the image.
     *
     * @param id a unique ID
     */
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Sets the image data to the Blob specified by the parameter.
     *
     * @param image the image data in Blob (Binary Large Object) form
     */
    public void setImage(Blob image) {
        this.image = image;
    }

    /**
     * Sets the date on which the image was taken.
     *
     * @param date the date the image was taken
     */
    public void setDate(String date) { this.date = date; }

    /**
     * Sets the longitude coordinate where the image was taken.
     *
     * @param longitude the longitude coordinate
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * Sets the latitude coordinate where the image was taken.
     *
     * @param latitude the latitude coordinate
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * Sets the location where the image was taken.
     *
     * @param location the location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the extracted text of the image.
     *
     * @param text the extracted text
     */
    public void setText(String text) {
        this.text = text;
    }
}
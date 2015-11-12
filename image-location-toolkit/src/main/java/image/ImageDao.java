package image;

import image.Entity.Entity;
import image.Entity.Image;
import image.Entity.Tag;
import org.hibernate.HibernateException;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ImageDao {

    /**
     * Returns the Image object.
     *
     * @param id the id of the Image object (auto-generated during instantiation)
     * @return the Image object
     */
    Image getImageById(int id);


    /**
     * Returns the tags from an Image.
     *
     * @param imageId the image id
     * @return a String of comma delimited tags
     */
    String getTagsById(int imageId);

    /**
     * Retrieves all Image objects from database.
     *
     * @return List of Image objects
     */
    List<Image> readImages();

    /**
     * Primary Image search method. Forms a search query based upon given parameters.
     *
     * @param allAnyChoice user's choice on whether to require results to match all or any search terms
     *                     - true:all, false:any
     * @param ThesChoice user's choice on whether to include search values as substrings via wildcard operator
     *                    - true:yes, false:no
     * @param keys the database columns to search
     * @param values the search value for a given column
     * @return a list of Images that match the query
     */
    List<Integer> getImageByParams(boolean allAnyChoice, boolean ThesChoice, String[] keys, String[] values);

    /**
     * Returns a list of List.
     *
     * @param tagIDs list of Integers
     * @return a different list of Integers
     */
    List<Integer> getImageIdByTagId(List<Integer> tagIDs);

    /**
     * Searches database for id's of images from a list of Tags.
     *
     * @param tagList the tags in a String
     * @return a list of Integers that corrospond to an Image id
     */
    List<Integer> SearchImagesForTags(String tagList);






    /**
     * Allows the editing of the location data, used for geo-locating.
     *
     * @param imageId the image id
     * @param latitude the latitude
     * @param longitude the longitude
     * @param location the location
     */
    void editLocationData(int imageId, String latitude, String longitude, String location);

    /**
     * Checks to see if the Tag object has already been added to the database, returns the Tag if it does.
     *
     * @param tagName the tag as a String
     * @return the Tag object if found in the database
     */
    Tag checkIfTagExists(String tagName);

    /*
     * Adds an  object to a database table.
     *
     * @param entity the Entity object to be added (NOTE: Fields need to have already been filled.)
     * @throws FileNotFoundException
     * @throws SQLException
     */
    void addEntity(Entity entity) throws FileNotFoundException, SQLException;

    /**
     * Checks to see if the image already exists in database.
     *
     * @param image the Image object
     * @return the Image Object if it exists
     * @throws SQLException
     */
    Image imageBlobAlreadyExists(Image image) throws SQLException;

    /**
     * Deletes the image from our database by id (not used, but added in case of future development).
     *
     * @param id the id of the image
     * @return the Integer id of the image.
     */
    Integer deleteImageById(int id);
    /**
     * Gets the Image ids for a set of Tags.
     *
     * @param allAnyChoice
     * @param TagIDSets
     * @return
     */
    List<Integer> getImageIdsByTagIDSets(boolean allAnyChoice, ArrayList<ArrayList<Integer>> TagIDSets);
}

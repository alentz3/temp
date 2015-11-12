package image;

import com.mashape.unirest.http.exceptions.UnirestException;
import image.Entity.Image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public interface APIInteraction {
    /**
     * Deletes the image from Imgur.
     *
     * @throws IOException
     * @throws UnirestException
     */
    void deleteImageFromImgur() throws IOException, UnirestException;

    /**
     * Connects to the Imagga API, tags the image via a URL address.
     *
     * @param image the Image object to be tagged
     * @param imageUrlAddress the URL address to the Image hosted online
     */
    void tagImageUrl(Image image, String imageUrlAddress);
    /**
     * Uploads the image to Imgur API.
     *
     * @param imageFile the file to upload
     * @return a String value of the URL link to Imgur
     * @throws IOException
     * @throws UnirestException
     */
    String uploadImage(File imageFile) throws IOException, UnirestException;
    /**
     * Searches the thesaurus API for synonyms.
     *
     * @param word the word to search
     * @return an array list of synonyms
     */
    ArrayList<String> searchThesaurus(String word);

    String[] getThesaurusQuery(boolean ThesChoice, String[] keys, String[] values);

    void reverseGeocode(Image image) throws Exception;
}

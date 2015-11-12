package image.BusinessLogic;

import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by 571743 on 11/5/2015.
 */
public interface SearchLogic {
    //Returns image IDs of images that
    String getImagesByTags(String tags);

    List<String> searchDataBase(String json);

    String searchGeotaggedImages();

    String createPublicURL(String imageId) throws SQLException, IOException, UnirestException;

    void deleteIfromI() throws IOException, UnirestException;
}

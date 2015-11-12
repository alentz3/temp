package image.BusinessLogic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by 571743 on 11/5/2015.
 */
public interface DisplayLogic {
    //Returns an image based on the ID provided
    BufferedImage getImage(String imageID) throws IOException, SQLException;

    //Accesses metadata (in image object) of image with parameter's ID. Returns Latitude Coordinate from that metadata.
    String getLatitudeCoordinate(String imageID);

    //Accesses metadata (in image object) of image with parameter's ID. Returns Longitude Coordinate from that metadata.
    String getLongitudeCoordinate(String imageID);

    //Returns metadata of image with parameter's ID.
    String getMetadata(String imageID);

    /*Accesses metadata (in tag object paired with image object)
         of image with parameter's ID. Returns Longitude Coordinate from that metadata. */
    String getTags(String imageID);
}

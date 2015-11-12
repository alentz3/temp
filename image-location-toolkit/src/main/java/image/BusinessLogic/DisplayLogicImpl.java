package image.BusinessLogic;

import com.cedarsoftware.util.io.JsonWriter;
import image.DatabaseOperations;
import image.Entity.Image;
import image.ImageDao;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

//Responds to Calls from REST class(es) and handles requests
public class DisplayLogicImpl implements DisplayLogic {
    @Autowired
    private ImageDao dBOps;
    @Autowired
    private CryptoHandler cryptoHandler;

    //Returns an image based on the ID provided
    @Override
    public BufferedImage getImage(String imageID) throws IOException, SQLException {
        Blob tempBlob = dBOps.getImageById(cryptoHandler.decryptId(imageID)).getImage();
        byte[] imageData = tempBlob.getBytes(1, (int) tempBlob.length());
        return ImageIO.read(new ByteArrayInputStream(imageData));
    }

    //Accesses metadata (in image object) of image with parameter's ID. Returns Latitude Coordinate from that metadata.
    @Override
    public String getLatitudeCoordinate(String imageID) {
        return dBOps.getImageById(cryptoHandler.decryptId(imageID)).getLatitude();
    }

    //Accesses metadata (in image object) of image with parameter's ID. Returns Longitude Coordinate from that metadata.
    @Override
    public String getLongitudeCoordinate(String imageID) {
        return dBOps.getImageById(cryptoHandler.decryptId(imageID)).getLatitude();
    }

    //Returns metadata of image with parameter's ID.
    @Override
    public String getMetadata(String imageID) {
        Image tempImage = dBOps.getImageById(cryptoHandler.decryptId(imageID));
        return JsonWriter.objectToJson(tempImage);

    }

    /*Accesses metadata (in tag object paired with image object)
     of image with parameter's ID. Returns Longitude Coordinate from that metadata. */
    @Override
    public String getTags(String imageID) {
        return dBOps.getTagsById((cryptoHandler.decryptId(imageID)));
    }
}

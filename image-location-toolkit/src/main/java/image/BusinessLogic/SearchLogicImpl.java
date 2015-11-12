package image.BusinessLogic;

import com.mashape.unirest.http.exceptions.UnirestException;
import image.*;
import image.Entity.Image;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//Responds to Calls from REST class(es) and handles requests
public class SearchLogicImpl implements SearchLogic {
    @Autowired
    private ImageDao dBOps;
    @Autowired
    private APIInteraction aPII;
    @Autowired
    private CryptoHandler cryptoHandler;
    @Autowired
    private QueryInfo qI;

    //Returns image IDs of images that
    @Override
    public String getImagesByTags(String tags) {
        List<Integer> ImageIDs = dBOps.SearchImagesForTags(tags);
        String IDs = "";
        for(Integer i: ImageIDs){
            IDs += cryptoHandler.encryptId(i) + ",";
        }
        if(!IDs.equals("")) {
            IDs = IDs.substring(0, IDs.length() - 1);
        }
        return IDs;
    }

    @Override
    public List<String> searchDataBase(String json){
        List<Integer> IDs;
        List String_IDs = new ArrayList<String>();
        if(json.equals("")) {
            return String_IDs;
        }

        json = json.replace('+', ' ');
        qI.buildQueryInfo(json);

        String[] searchValues = aPII.getThesaurusQuery(qI.getThesChoice(),qI.getKeys(),qI.getValues());
        IDs = dBOps.getImageByParams(qI.getAllAnyChoice(),qI.getThesChoice(),qI.getKeys(), searchValues);
        for(Integer i: IDs){
            String encodedID = cryptoHandler.encryptId(i);
            String_IDs.add(encodedID);
        }
        return String_IDs;
    }

    @Override
    public String searchGeotaggedImages() {
        List<Image> images = new DatabaseOperations().readImages(); // Gets all the images in the database

        // Builds the return string
        String returnStr = "";
        for (int i = 0; i < images.size(); i++) {
            // Add all ids, lats, and lngs that are within the specified distance
            if (images.get(i).getLatitude() != null && images.get(i).getLongitude() != null)
                returnStr += cryptoHandler.encryptId(images.get(i).getID()) + ","
                        + images.get(i).getLatitude() + "," + images.get(i).getLongitude() + ";";
        }
        return returnStr;
    }

    @Override
    public String createPublicURL(String imageId) throws SQLException, IOException, UnirestException {
        Blob imageBlob = dBOps.getImageById(cryptoHandler.decryptId(imageId)).getImage();
        InputStream input = imageBlob.getBinaryStream();
        File tempFile = ImageOperations.streamToFile(input);

        return aPII.uploadImage(tempFile);

    }

    @Override
    public void deleteIfromI() throws IOException, UnirestException {
        aPII.deleteImageFromImgur();
    }
}
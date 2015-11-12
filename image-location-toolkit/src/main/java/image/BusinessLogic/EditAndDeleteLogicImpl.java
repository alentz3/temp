package image.BusinessLogic;

import image.DatabaseOperations;
import image.Entity.Image;
import image.ImageDao;
import image.ImageOperations;
import org.springframework.beans.factory.annotation.Autowired;

//Responds to Calls from REST class(es) and handles requests
public class EditAndDeleteLogicImpl implements EditAndDeleteLogic {
    @Autowired
    private ImageDao dBOps;
    @Autowired
    private CryptoHandler cryptoHandler;


    //Takes geocoordinates input from user and adds them to image metadata
    @Override
    public void edit_coor(String coordinates, String imageId) {
        Image newImg = new Image();
        newImg.setID(cryptoHandler.decryptId(imageId));
        String[] latLng = coordinates.split(",");
        newImg.setLatitude(latLng[0]);
        newImg.setLongitude(latLng[1]);

        ImageOperations.reverseGeocode(latLng[0], latLng[1], newImg);
        dBOps.editLocationData(newImg.getID(), newImg.getLatitude(),
                newImg.getLongitude(), newImg.getLocation());
    }


}

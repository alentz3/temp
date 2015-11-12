package image.BusinessLogic;

import image.*;
import image.Entity.Image;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.Geographic;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.im4java.core.IM4JavaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import java.io.*;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by 571743 on 11/2/2015.
 */
public class UploadAndExtractMDImpl implements UploadAndExtractMD {
    @Autowired
    private ImageDao dBOps;
    @Autowired
    private APIInteraction aPII;
    @Autowired
    private CryptoHandler cryptoHandler;

    /**
     * Takes in an InputStream representing the submitted image file from the user's computer, uploads the image to
     * imgur for auto-tagging, processes and stores the image, and returns a redirect to the image's results page.
     *
     * @param inputStream InputStream representing the image to be processed
     * @return a redirect to the image's results page
     * @throws IOException
     * @throws SQLException
     */
    @Override
    public String processInputStream(InputStream inputStream) throws IOException, SQLException {
        Image image = new Image();
        File tempFile = ImageOperations.streamToFile(inputStream);
        fillFields(image, tempFile);
        String imgurUploadUrl;
        Image tempImg;
        try {
            if((tempImg = dBOps.imageBlobAlreadyExists(image)) == null) {
                dBOps.addEntity(image);
                imgurUploadUrl = aPII.uploadImage(tempFile);
                aPII.tagImageUrl(image, imgurUploadUrl);
                aPII.deleteImageFromImgur();
            }
            else { image.setID(tempImg.getID()); }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String id = cryptoHandler.encryptId(image.getID());
        return id;
    }

    /**
     * Takes in an InputStream representing the submitted image file and a String representing the submitted URL,
     * processes and stores the image, and returns a redirect to the image's results page.
     *
     * @param inputStream InputStream representing the image to be processed
     * @param url String representing the URL of the image
     * @return a redirect to the image's results page
     * @throws IOException
     * @throws SQLException
     */
    @Override
    public String processInputStream(InputStream inputStream, String url) throws IOException, SQLException {
        Image image = new Image();
        File tempFile = ImageOperations.streamToFile(inputStream);
        new ImageOperations().fillFields(image, tempFile); // Sends image file to the DatabaseOperations object
        Image tempImg;

        try {
            if((tempImg = dBOps.imageBlobAlreadyExists(image)) == null) {
                dBOps.addEntity(image);
                aPII.tagImageUrl(image, url);
            }
            else {
                image.setID(tempImg.getID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String id = cryptoHandler.encryptId(image.getID());
        return id;
    }   /* NOTE: Change domain if switching to another server. */



    public void fillFields(Image image, File file) throws FileNotFoundException {
        try {
            parseImage(image, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            image.setImage(ImageOperations.imageToBlob(file));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void parseImage(Image image, File file) throws Exception {
        try {
            // Detects the file type
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            FileInputStream inputStream = new FileInputStream(file);
            ParseContext parseContext = new ParseContext();

            // Parser
            AutoDetectParser parser = new AutoDetectParser();
            parser.parse(inputStream, handler, metadata, parseContext);

            // Image field setting
            String date;
            if (metadata.getDate(metadata.ORIGINAL_DATE) != null) {
                date = metadata.getDate(metadata.ORIGINAL_DATE).toString();
            } else if (metadata.getDate(TikaCoreProperties.CREATED) != null) {
                date = metadata.getDate(TikaCoreProperties.CREATED).toString();
            } else if (metadata.getDate(DublinCore.CREATED) != null) {
                date = metadata.getDate(DublinCore.CREATED).toString();
            } else if (metadata.getDate(TikaCoreProperties.METADATA_DATE) != null) {
                date = metadata.getDate(TikaCoreProperties.METADATA_DATE).toString();
            } else if (metadata.getDate(DublinCore.MODIFIED) != null) {
                date = metadata.getDate(DublinCore.MODIFIED).toString();
            } else {
                // Current date+time
                metadata.set(Metadata.DATE, new Date());
                date = metadata.get(Metadata.DATE);
            }
            image.setLongitude(metadata.get(Geographic.LONGITUDE));
            image.setLatitude(metadata.get(Geographic.LATITUDE));
            ImageOperations.setMetadataParsingFinished();

            if (date != null) {
                image.setDate(date.toString());
            } else {
                image.setDate(null);
            }
            image.setLongitude(image.getLongitude());
            image.setLatitude(image.getLatitude());
            aPII.reverseGeocode(image);
            ImageOperations.setReverseGeocodeFinished();
            ImageOperations iO = new ImageOperations();
            iO.doOCR(image, file);
            ImageOperations.setOcrFinished();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (TikaException te) {
            System.out.println(te.getMessage());
        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (InterruptedException ie) {
            System.out.println(ie.getMessage());
        } catch (IM4JavaException je) {
            je.printStackTrace();
        }
    }




    @Override
    public void waitforGeocode() throws InterruptedException {
        while (!ImageOperations.reverseGeocodeIsFinished()) {
            Thread.sleep(500);
        }
        ImageOperations.resetGeocodeFinished();
    }

    @Override
    public void waitforMetaData() throws InterruptedException {
        while (!ImageOperations.extractingMetadataIsFinished()) {
            Thread.sleep(500);
        }
        ImageOperations.resetMetadataFinished();
    }

    @Override
    public void waitforOCR() throws InterruptedException {
        while (!ImageOperations.ocrIsFinished()) {
            Thread.sleep(500);
        }
        ImageOperations.resetOCRFinished();
    }

}
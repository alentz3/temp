package image;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import image.BusinessLogic.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

/**
 * This class holds paths to domain URLs for the image to be sent to and processed. Much of the image processing is
 * outsourced to other classes, such as ImageOperations.
 */
@Path("/files")
public class RestAPI {

    private UploadAndExtractMD uploader;
    private DisplayLogic displayLogic;
    private SearchLogic searchLogic;
    private EditAndDeleteLogic eddellogic;

    //constructor: calls method that gets spring beans and creates an application context if such is yet to exist
    public RestAPI() {
        if(uploader== null)
            createSpring();
    }

    private void createSpring() {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        uploader = context.getBean(UploadAndExtractMDImpl.class);
        displayLogic = context.getBean(DisplayLogicImpl.class);
        searchLogic = context.getBean(SearchLogicImpl.class);
        eddellogic = context.getBean(EditAndDeleteLogicImpl.class);

    }

    /**
     * Uploads an image file to be sent to the database.
     *
     * @throws IOException
     * @throws SQLException if the image data could not be inserted into the database
     */
    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader)
            throws IOException, SQLException {

        return Response.status(200).entity(uploader.processInputStream(inputStream)).build();
    }

    /**
     * Uploads an image URL to be sent to the database.
     *
     * @throws IOException
     * @throws SQLException if the image data could not be inserted into the database
     */
    @POST
    @Path("/url")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response uploadURL(String url)throws SQLException {
        try {
            InputStream inputStream = new URL(url).openStream();
            return Response.status(200).entity(uploader.processInputStream(inputStream, url)).build();
        }catch(IOException e){
            return Response.status(500).entity("Error: Invalid URL").build();
        }
    }

    @POST
    @Path("/uploadfolder")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadFolder(
            @FormDataParam("file") InputStream inputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader)
            throws IOException, SQLException {

        return Response.status(200).entity(uploader.processInputStream(inputStream)).build();
    }

    /**
     * GET request for the image in Blob (Binary Large Object) form
     */
    @GET
    @Path("/images/{id}")
    @Produces("image/jpeg")
    public BufferedImage getImage(@PathParam("id") String imageID) throws IOException, SQLException {
        return displayLogic.getImage(imageID);
    }

    /**
     * GET request for the image's latitude coordinate
     */
    @GET
    @Path("/images/{id}/latitude")
    @Produces("text/plain")
    public Response getLatitudeCoordinate(@PathParam("id") String imageID) {
        return Response.ok(displayLogic.getLatitudeCoordinate(imageID)).build();
    }

    /**
     * GET request for the image's longitude coordinate
     */
    @GET
    @Path("/images/{id}/longitude")
    @Produces("text/plain")
    public Response getLongitudeCoordinate(@PathParam("id") String imageID) {
        return Response.ok(displayLogic.getLongitudeCoordinate(imageID)).build();
    }

    /**
     * GET request for the image's metadata as a JSON object
     */
    @GET
    @Path("/images/{id}/metadata")
    @Produces("application/json")
    public Response getMetaData(@PathParam("id") String imageID) {;
        return Response.ok(displayLogic.getMetadata(imageID)).build();
    }

    /**
     * GET request for an image's tags
     */
    @GET
    @Path("/images/{id}/tags")
    @Produces("text/plain")
    public Response getTags(@PathParam("id") String imageID) {
        return Response.ok(displayLogic.getTags(imageID)).build();
    }

    /**
     * POST request for all images that match any of the specified tags
     */
    @POST
    @Path("/images/searchtags")
    @Consumes("text/plain")
    @Produces("text/plain")
    public Response getImagesByTags(String tags) {
        return Response.ok(searchLogic.getImagesByTags(tags)).build();
    }

    /**
     * POST request to search for images in the database using the search page
     */
    @POST
    @Path("/images/query")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> searchDataBase(String json){
        return searchLogic.searchDataBase(json);
    }

    /**
     * POST request for the user to geo-tag an image
     */
    @POST
    @Path("/images/{id}/edit_coord")
    @Consumes(MediaType.TEXT_PLAIN)
    public void edit_coor(String coordinates, @PathParam("id") String imageId) {
        eddellogic.edit_coor(coordinates,imageId);
    }

    /**
     * GET request for all images with the specified latitude and longitude
     */
    @GET
    @Path("/search_coordinates")
    @Produces(MediaType.TEXT_PLAIN)
    public Response searchGeotaggedImages() {
        return Response.ok(searchLogic.searchGeotaggedImages()).build();
    }

    /**
     * GET request for an image's imgur filepath to the frontend
     */
    @GET
    @Path("/{id}/public_url")
    @Produces(MediaType.TEXT_PLAIN)
    public Response createPublicURL(@PathParam("id") String imageId) throws SQLException, IOException, UnirestException {
        return Response.ok(searchLogic.createPublicURL(imageId)).build();
    }

    /**
     * GET request for an image's filepath to the frontend
     */
    @GET
    @Path("/image_url/delete")
    public Response createPublicURL() throws IOException, UnirestException {
        searchLogic.deleteIfromI();
        return Response.ok().build();
    }

    /**
     * Returns a 200 Response when the method finishes processing.
     * @throws InterruptedException
     */
    @GET
    @Path("/loading/ocr")
    @Produces(MediaType.TEXT_PLAIN)
    public Response loadingOCRFinished() throws InterruptedException{
        uploader.waitforOCR();
        return Response.ok("200").build();
    }

    /**
     * Returns a 200 Response when the method finishes processing.
     * @throws InterruptedException
     */
    @GET
    @Path("/loading/metadata")
    @Produces(MediaType.TEXT_PLAIN)
    public Response loadingMetadataFinished() throws InterruptedException{
        uploader.waitforMetaData();
        return Response.ok("200").build();
    }

    /**
     * Returns a 200 Response when the method finishes processing.
     * @throws InterruptedException
     */
    @GET
    @Path("/loading/reverse_geo")
    @Produces(MediaType.TEXT_PLAIN)
    public Response loadingReverseGeocodeFinished() throws InterruptedException{
        uploader.waitforGeocode();
        return Response.ok("200").build();
    }
}
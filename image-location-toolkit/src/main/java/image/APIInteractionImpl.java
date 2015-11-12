package image;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import image.Entity.Image;
import image.Entity.Tag;
import image.Entity.TagRelation;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;

public class APIInteractionImpl implements APIInteraction{

    // API Authentication Keys
    final String THESAURUS_API_KEY = "5417a943f7638d1799736fed27e33791";
    final String TAG_API_ENCODED_KEY = "YWNjX2NjMDBiNjk5ZDNjOGJhYjpjY2Y5MzZmNTM0MDk3YWE4YmQ0Njg5Y2YyMTEyODJhZg==";
    final String IMGUR_API_CLIENT_ID = "a69fae3934f843e";
    final String IMGUR_API_CLIENT_ID2 = "ed93ab06df74273";
    final String IMGUR_API_CLIENT_SECRET = "8cc76f3e591a9f4f63553b4c1eb2a26c707c47df";
    final String IMGUR_API_CLIENT_SECRET2 = "d58eca37875e6b4940e75518591bc259a3dd4f64";
    final String GOOG_GEOCODE_KEY = "AIzaSyDxfqOdxsbjjFzIeJzCyDb0M_pwIhxAmB8";


    // API URLs
    final String TAG_API_REST_URL_ENDPOINT = "http://api.imagga.com/v1/tagging?version=2&url=";
    final String TAG_API_REST_CONTENT_ENDPOINT = "http://api.imagga.com/v1/tagging?version=2&content=";
    final String TAG_API_REST_FILE_ENDPOINT =  "http://api.imagga.com/v1/content";
    final String IMGUR_API_UPLOAD_ENDPOINT = "https://api.imgur.com/3/image";
    final String IMGUR_API_DELETE_ENDPOINT = "https://api.imgur.com/3/image/{id}";
    final String GOOG_GEOCODE_ENDPOINT = "https://maps.googleapis.com/maps/api/geocode/json?";

    private static String imgurDeleteHash = "";
    final double MINIMUM_TAG_CONFIDENCE = 40.0;           // 40.0%

    @Autowired
    private ImageDao iDao;

    /**
     * Connects to The Big Huge Thesaurus API.
     *
     * @param word the word to be searched
     * @return synonyms, antonyms, and other thesaurus info as a String in JSON format
     */
    @Override
    public ArrayList<String> searchThesaurus(String word) {
        ArrayList<String> synonyms = new ArrayList<>();
        try {
            // Connects to thesaurus API
            URL url = new URL("http://words.bighugelabs.com/api/2/" + THESAURUS_API_KEY + "/" + word + "/json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Allows return of JSON object
            connection.setRequestProperty("Accept", "application/json");

            // Connection properties, indicates GET method, and connection accepts information to and from server
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // Connection check
            if (connection.getResponseCode() != 200) {
                return synonyms;
            }

            // Retrieves the input stream
            InputStream inputStream = connection.getInputStream();
            String tempString = IOUtils.toString(inputStream, "UTF-8");

            // Formats input String into JSON objects and Arrays
            JSONObject jsonObject = new JSONObject(tempString);
            if(tempString.contains("\"syn\"")) {
                String[] speechParts = new String[]{"noun","adjective","verb"};
                for(String s : speechParts){
                    if(tempString.contains("\"" + s + "\"")) {
                        checkJSONforSynonyms(s, jsonObject, synonyms);
                    }
                } 
            }
            // Formats and returns JSON information as a String
            return synonyms;
        } catch (IOException e) {
            e.getStackTrace();
        }
        return synonyms;
    }

    public void checkJSONforSynonyms(String speechPart, JSONObject jsonObject, ArrayList<String> synonyms) {
        JSONObject jsonObject1 = jsonObject.getJSONObject(speechPart);
        JSONArray jsonArray = jsonObject1.getJSONArray("syn");
        System.out.println(jsonArray.toString());
        // Loops through each Tag JSON object
        for (int i = 0; i < jsonArray.length(); i++) {
            String wordName = jsonArray.getString(i);
            synonyms.add(wordName);
        }


    }

    //@Override
    public void deleteImageFromImgur() throws IOException, UnirestException {

        HttpResponse<JsonNode> jsonResponse;

        // Create DELETE request with Unirest to delete image file from Imgur
        try {
            jsonResponse = Unirest.delete(IMGUR_API_DELETE_ENDPOINT)
                    .header("Authorization", "Client-ID " + IMGUR_API_CLIENT_ID2)
                    .routeParam("id", imgurDeleteHash)
                    .header("accept", "application/json")
                    .field("parameter", "value")
                    .asJson();

            if (jsonResponse.getStatus() != 200) {
                throw new RuntimeException("Connection Failed: " + jsonResponse.getStatusText());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to upload an image file to Imagga.com, retrieve image code.
     *
     * @param imageFile String path to the image file location
     * @return the image code to use with the tagImageUrl() method
     */
    //@Override
    public String uploadImage(File imageFile) throws IOException, UnirestException {

        HttpResponse<JsonNode> jsonResponse;
        String imageUrl = "";

        try {
            // Create POST request with Unirest to upload file to Imgur, gets response data
            jsonResponse = Unirest.post(IMGUR_API_UPLOAD_ENDPOINT)
                    .header("Authorization", "Client-ID " + IMGUR_API_CLIENT_ID2)
                    .header("accept", "application/json")
                    .field("parameter", "value")
                    .field("image", imageFile)
                    .asJson();

            if (jsonResponse.getStatus() != 200) {
                throw new RuntimeException("Connection Failed: " + jsonResponse.getStatusText());
            }

            // Formats response to get the URL of the uploaded image
            JSONObject jsonObject = new JSONObject(jsonResponse.getBody());
            JSONArray jsonArray = jsonObject.getJSONArray("array");
            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
            JSONObject jsonObject2 = jsonObject1.getJSONObject("data");

            // Stores the image url link and delete hash as strings
            imageUrl = jsonObject2.getString("link");
            imgurDeleteHash = jsonObject2.getString("deletehash");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUrl;
    }

    /**
     * Used to connect to Imagga.com image-tagging API.
     *
     * @param imageAddress the targeted URL of the Image to be tagged
     * @return the image tags and confidence (%) as a String in JSON format
     */
    //@Override
    public void tagImageUrl(Image image, String imageAddress) {

        try {
            URL url = new URL(TAG_API_REST_URL_ENDPOINT + imageAddress);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Basic authentication
            connection.setRequestProperty("Authorization", "Basic " + TAG_API_ENCODED_KEY);

            // Allows return of JSON object
            connection.setRequestProperty("Accept", "application/json");

            // Connection properties, indicates GET method, and connection accepts information to and from server
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            // Connection check
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Connection Failed: "
                        + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            // Retrieves the input stream
            InputStream inputStream = connection.getInputStream();
            String tempString = IOUtils.toString(inputStream, "UTF-8");

            // Formats input String into JSON objects and Arrays
            JSONObject jsonObject = new JSONObject(tempString);
            JSONArray jsonArrayTemp = jsonObject.getJSONArray("results");

            // Selects the first object (only returning one JSON object so set it to 0)
            JSONObject jsonObject1 = jsonArrayTemp.getJSONObject(0);

            // Stores the array of Tags and confidence levels
            JSONArray jsonArray = jsonObject1.getJSONArray("tags");

            int tagCounter = 0;
            // Loops through each Tag JSON object
            for (int i = 0; i < jsonArray.length(); i++) {

                // Creates a temporary JSON object
                JSONObject tagObject = jsonArray.getJSONObject(i);

                // Extract the tag name and confidence level
                String tagName = tagObject.getString("tag");
                double tagConfidence = tagObject.getDouble("confidence");

                // Only creates a tag if the API confidence level is >= the minimum tag confidence level OR there are < 5 tags.
                // Will take maximum of 5 tags
                if (tagConfidence >= MINIMUM_TAG_CONFIDENCE || tagCounter < 5) {

                    // Checks to see if the tag already exists in the database
                    Tag tag = iDao.checkIfTagExists(tagName);

                    // If the tag DOES exist, just create the image-tag relation
                    if (tag != null) {
                        TagRelation tagRelation = new TagRelation();
                        tagRelation.setTagId(tag.getId());
                        tagRelation.setImageId(image.getID());
                        tagRelation.setTagConfidence(tagConfidence);

                        try {
                            iDao.addEntity(tagRelation);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        tagCounter++;
                        // If the tag DOES NOT exist, create a new tag, set name, and then create the relation with the image
                        // NOTE: ID should be auto-generated via Hibernate and the database.
                    } else {
                        Tag newTag = new Tag();
                        newTag.setTag(tagName);
                        try {
                            iDao.addEntity(newTag);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        TagRelation tagRelation = new TagRelation();
                        tagRelation.setTagId(newTag.getId());
                        tagRelation.setImageId(image.getID());
                        tagRelation.setTagConfidence(tagConfidence);
                        try {
                            iDao.addEntity(tagRelation);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        tagCounter++;
                    }
                }
            }
            connection.disconnect();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    //Used to get Thesaurus matches for user input and create query segments the DAO can use to search for those matches
    //among the original search content.
    @Override
    public String[] getThesaurusQuery(boolean ThesChoice, String[] keys, String[] values) {

        if(ThesChoice) {
            for(int i = 0; i < keys.length; i++) {
                if(keys[i].equals("tag")) {
                    String TagQuery = "";
                    if (keys[i].equals("tag")) {
                        ArrayList<String> synonyms;
                        String s = keys[i] + " LIKE '" + values[i] + "' " + "OR" + " ";
                        TagQuery += s;
                        //Use of Thesaurus
                        if (ThesChoice) {

                            synonyms = searchThesaurus(values[i]);
                            for (String str : synonyms) {
                                str = str.replace("'", "");
                                String s2 = keys[i] + " LIKE '" + str + "' " + "OR" + " ";
                                TagQuery += s2;
                            }
                        }
                        TagQuery = TagQuery.substring(0, TagQuery.length() - 4);
                    }
                    values[i] = TagQuery;
                }

            }
            return values;
        }
        return values;
    }




    //use google API
    @Override
    public void reverseGeocode(Image image) throws Exception {
        if (image.getLatitude() != null && image.getLongitude() != null) {
            GeoApiContext context = new GeoApiContext().setApiKey(GOOG_GEOCODE_KEY);
            LatLng ltlng = new LatLng((Double.parseDouble(image.getLatitude())), (Double.parseDouble(image.getLongitude())));
            GeocodingResult[] rgResults = GeocodingApi.newRequest(context).latlng(ltlng).await();
            image.setLocation(rgResults[0].formattedAddress);
        }
    }
}
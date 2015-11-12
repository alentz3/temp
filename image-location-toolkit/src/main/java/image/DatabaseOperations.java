package image;

import image.Entity.Entity;
import image.Entity.Image;
import image.Entity.Tag;
import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.FileNotFoundException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;

/**
 * Handles all database CRUD operations. addImage(Image image) expects the Image object to have already been constructed.
 */
public class DatabaseOperations implements ImageDao {

    // Database Table and Column names for Tag Relation INSERT method(s)
    final String IMAGE_TAG_RELATION_TABLE = "image_tag_relation";
    final String TAG_TABLE_IMAGE_ID = "image_id";
    final String TAG_TABLE_TAG_ID = "tag_id";
    final String TAG_TABLE_CONFIDENCE = "tag_confidence";
    /**
     * Reads all Images in IMAGEDATA and prints the fields for each.
     *
     * @return imageList list of all Images
     */
    @Override
    public List<Image> readImages() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<Image> imageList = null;

        try {
            transaction = session.beginTransaction();
            /*
            NOTE: "Image" is mapped to table "IMAGEDATA" via Image.hbm.xml
             */
            imageList = session.createQuery("FROM Image").list();

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return imageList;
    }

    /*
     * Adds an  object to a database table.
     *
     * @param entity the Entity object to be added (NOTE: Fields need to have already been filled.)
     * @throws FileNotFoundException
     * @throws SQLException
     */
    @Override
    public void addEntity(Entity entity) throws FileNotFoundException, SQLException {
        Session session = null;
        if(entity instanceof Tag) {
           session = image.HibernateUtil.getSessionFactory().openSession();
        }
        else {
            session = HibernateUtil.getSessionFactory().openSession();
        }
        Transaction transaction = null;

        try {

            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            if(entity instanceof Image){
                Image image = (Image)entity;
                image.getImage().free();
            }
            session.close();
        }
    }

    /*
    Checks to see if an image blob (or data of the actual graphics of the image) already exists
     */
    @Override
    public Image imageBlobAlreadyExists(Image image) throws SQLException{
        Image tempImage = new Image();
        tempImage = image;
        Blob blob = tempImage.getImage();
        byte[] imageBlobAsBytes = blob.getBytes(1,(int) blob.length());
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<Image> list = new ArrayList<>();
        Blob tempBlob = null;
        byte[] blobAsBytes;
        try {
            transaction = session.beginTransaction();
            list = session.createQuery("FROM Image").list();
            transaction.commit();
        }
        catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }  finally {
            session.flush();
            session.close();
            for(Image i : list) {
                tempBlob = i.getImage();
                blobAsBytes = tempBlob.getBytes(1, (int) tempBlob.length());
                if (Arrays.equals(blobAsBytes, imageBlobAsBytes)){
                    return i;
                }

            }
        }
        return null;
    }

    /**
     * Returns the Image object with the specified ID.
     *
     * @param id the unique ID of the Image to return
     * @return the requested Image object
     */
    @Override
    public Image getImageById(int id) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        Image image = null;

        try {
            transaction = session.beginTransaction();

            List<Image> list = session.createQuery("FROM Image WHERE ID=" + id).list();
            Iterator iter = list.iterator();
            if (iter.hasNext()) {
                image = (Image) iter.next();
            }

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return image;
    }

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
    @Override
    public List<Integer> getImageByParams(boolean allAnyChoice, boolean ThesChoice, String[] keys, String[] values) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<Image> imageList = new ArrayList<>();
        List<Tag> TagList  = new ArrayList<>();
        List<Integer> IDs = new ArrayList<>();
        List<Integer> IDsFromTags = new ArrayList<>();
        List<Integer> IDsFromBoth = new ArrayList<>();
        List<String> Synonyms = new ArrayList<>();
        ArrayList<Integer> TagIDs = new ArrayList<>();
        ArrayList<ArrayList<Integer>> TagIDSets = new ArrayList<ArrayList<Integer>>();

        boolean isTags = false;
        boolean checkingfortags = false;
        // Set whether or not to require all or any of the search terms
        String allAny = (allAnyChoice) ? "AND" : "OR";
        // Form search query to send via Hibernate
        String query = "";
        String TagQuery = "";
        try {
            transaction = session.beginTransaction();
            for (int i = 0; i < keys.length; i++) {
                if (keys[i] != null) {
                    if (keys[i].equals("tag")) {

                        checkingfortags = true;
                        if(ThesChoice) {
                            TagList = session.createQuery("FROM Tag WHERE " + /*TagQuery*/values[i]).list();
                        }
                        else {
                            String s = keys[i] + " LIKE '" + values[i] + "' " + allAny + " ";
                            TagQuery += s;
                        //Use of Thesaurus
                            TagQuery = TagQuery.substring(0, TagQuery.length() - 4);
                            TagList = session.createQuery("FROM Tag WHERE " + TagQuery).list();
                        }
                        TagQuery = "";
                        TagIDs = new ArrayList<>();
                        for (Tag t : TagList) {
                            TagIDs.add(t.getId());
                            isTags = true;
                        }
                        TagIDSets.add(TagIDs);
                    }
                    else {
                        String s = keys[i] + " LIKE '" + "%" + values[i] + "%" + "' " + allAny + " ";
                        query += s;
                    }
                }
            }

            // Trim end
            if(!query.equals("")) {
                query = query.substring(0, query.length() - allAny.length() - 2);
            }

            // NOTE: "Image" is mapped to table "IMAGEDATA" via Image.hbm.xml
            if(!query.equals("")) {
                imageList = session.createQuery("FROM Image WHERE " + query).list();
            }

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        // Get image IDs from image query
        for(Image i : imageList){
            IDs.add(i.getID());
        }
        // Get Image IDs from tag IDs
        if(isTags) {
            IDsFromTags = getImageIdsByTagIDSets(allAnyChoice, TagIDSets);
        }

        // If allAnyChoice is on and there is queries using tags and something else, then check to make sure image fits both
        if(allAnyChoice && checkingfortags && !query.equals("")){
            for(Integer i: IDsFromTags){
                if(IDs.contains(i)){
                    IDsFromBoth.add(i);
                }
            }
            IDs = IDsFromBoth;
        }else {
            IDs.addAll(IDsFromTags);
        }
        eraseDuplicates(IDs);
        return IDs;
    }

    /*Used by method above and method below
     */
    private void eraseDuplicates(List<Integer> a){
        int size = a.size();
        int duplicates = 0;
        // Erase duplicates algorithm

        Collections.sort(a);
        for (int i = 0; i < size - 1; i++) {
            if(a.get(i) == a.get(i+1)) {
                duplicates++;
                a.remove(i+1);
                i--;
                size--;
            }
        }
    }

    @Override
    public List<Integer> getImageIdsByTagIDSets(boolean allAnyChoice, ArrayList<ArrayList<Integer>> TagIDSets){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        String TagQuery = "";
        ArrayList<List<Integer>> ImageIDSets = new ArrayList<>();
        List<Integer> ImageIDs = new ArrayList<>();
        List<Integer> retImageIDs = new ArrayList<>();
        boolean notInAllSets = false;

        try{
            transaction = session.beginTransaction();
            for(ArrayList<Integer> arrayList: TagIDSets) {
                for (Integer tagID : arrayList) {
                    String s = "tag_id = " + tagID + " OR ";
                    TagQuery += s;
                }
                ImageIDs = new ArrayList<>();
                if(!TagQuery.equals("")) {
                    TagQuery = TagQuery.substring(0, TagQuery.length() - 4);
                    ImageIDs = session.createSQLQuery("SELECT image_id FROM " + IMAGE_TAG_RELATION_TABLE + " WHERE " + TagQuery).list();
                }
                ImageIDSets.add(ImageIDs);
                TagQuery = "";
            }
            for(List<Integer> a: ImageIDSets){
                eraseDuplicates(a);
            }
            if(allAnyChoice) {
                for (Integer i : ImageIDSets.get(0)) {
                    for (List<Integer> a : ImageIDSets) {
                        if (!a.contains(i)) {
                            notInAllSets = true;
                        }
                    }
                    if (notInAllSets == false) {
                        retImageIDs.add(i);
                    }
                    notInAllSets = false;
                }
            }
            else{
                for (List<Integer> a : ImageIDSets) {
                    for(Integer i: a){
                        retImageIDs.add(i);
                    }
                }
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return retImageIDs;
    }

    /**
     * Deletes the specified Image from the database table and returns its ID.
     *
     * @param id the ID of the Image to delete
     * @return Integer representation of the ID of the deleted Image
     */
    @Override
    public Integer deleteImageById(int id) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        Integer imageID = null;

        try {
            transaction = session.beginTransaction();
            Image image = (Image)session.get(Image.class, id);
            imageID = image.getID();
            session.delete(image);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return imageID;
    }


    @Override
    public Tag checkIfTagExists(String tagName) {

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        String hibernateQuery = "from Tag where tag = :targetName";
        Query query = session.createQuery(hibernateQuery);
        query.setMaxResults(1);
        query.setParameter("targetName", tagName);
        Tag tempTag = (Tag) query.uniqueResult();

        session.getTransaction().commit();
        session.close();

        // If tag is not NULL, returns that Tag object
        if (tempTag != null) {
            return tempTag;
        } else
            return null;
    }


    @Override
    public List<Integer> getImageIdByTagId(List<Integer> tagIDs){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        String TagQuery = "";
        List<Integer> ImageIDs = new ArrayList<>();

        for(Integer tagID: tagIDs) {
            String s = "tag_id = " + tagID + " OR ";
            TagQuery += s;
        }

        if(TagQuery.equals("")){
            return ImageIDs;
        }
        TagQuery = TagQuery.substring(0, TagQuery.length() - 4);
        try {
            transaction = session.beginTransaction();
            ImageIDs = session.createSQLQuery("SELECT image_id FROM " + IMAGE_TAG_RELATION_TABLE + " WHERE " + TagQuery ).list();
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ImageIDs;
    }

    @Override
    public String getTagsById(int imageId) {

        List<Integer> tagResults;
        List<String> tagNames = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        tagResults = session.createSQLQuery("SELECT tag_id FROM " + IMAGE_TAG_RELATION_TABLE
                + " WHERE " + "image_id = " + imageId).list();

        session.getTransaction().commit();
        session.close();

        String tempName, stringNames;

        // Creates a List of String objects
        Iterator iterator = tagResults.listIterator();
        while (iterator.hasNext()) {
            tempName = getTagByTagId((int) iterator.next());
            tagNames.add(tempName);
        }

        // Format the list of String Objects into a single string
        stringNames = StringUtils.join(tagNames, ", ");
        stringNames = stringNames.replace("[", "");
        stringNames = stringNames.replace("]", "");

        return stringNames;
    }

    private String getTagByTagId(int tagId) {

        List<String> tagName;

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        tagName = session.createSQLQuery("SELECT tag FROM tags WHERE id = " + tagId).list();

        session.getTransaction().commit();
        session.close();

        return tagName.toString();
    }

    /**
     * Changes the location data (coordinates and reverse-geocoded location) when the user wants to
     * geotag the image on the view_image page.
     */
    @Override
    public void editLocationData(int imageId, String latitude, String longitude, String location) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        Query query;
        if (location == null) {
            query = session.createSQLQuery("UPDATE Image SET latitude = " + latitude
                    + ", longitude = " + longitude + ", location = NULL WHERE id = " + imageId);
        }
        else {
            query = session.createSQLQuery("UPDATE Image SET latitude = " + latitude
                    + ", longitude = " + longitude + ", location = '" + location + "' WHERE id = " + imageId);
        }
        query.executeUpdate();

        // Close the session
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public List<Integer> SearchImagesForTags(String tagList) {
        List<Integer> ImageIDs = new ArrayList<Integer>();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        List<Tag> TagList  = new ArrayList<>();
        List<Integer> TagIDs = new ArrayList<>();
        String[] tagArr = tagList.split(",");
        String TagQuery = "";
        int arr_size = 0;

        for(int i= 0; i < tagArr.length; i++) {
            if(i !=0){
                // Remove space before each tag in comma-separated list
                tagArr[i] = tagArr[i].substring(1);
            }
            String s = "tag" + " LIKE '" + tagArr[i] + "'" + " OR ";
            TagQuery += s;
        }
        // Trim end
        if(!TagQuery.equals("")){
            TagQuery = TagQuery.substring(0, TagQuery.length() - 3);
        }

        try {
            transaction = session.beginTransaction();

            if(!TagQuery.equals("")) {
                TagList = session.createQuery("FROM Tag WHERE " + TagQuery).list();
            }

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }

        // Get tag IDs from tag query
        for(Tag i : TagList){
            TagIDs.add(i.getId());
        }
        // Get image IDs from tag Ids and add to ID return set
        ImageIDs.addAll(getImageIdByTagId(TagIDs));

        int size = ImageIDs.size();
        int duplicates = 0;

        // Erase duplicate algorithm
        for (int i = 0; i < size - 1; i++) {
            for (int j = i + 1; j < size; j++) {
                if (ImageIDs.get(j) != (ImageIDs.get(i)))
                    continue;
                duplicates++;
                ImageIDs.remove(j);
                j--;
                size--;
            }
        }
        return ImageIDs;
    }
}
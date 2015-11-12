package image;

import image.Entity.Image;
import junit.framework.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by 571743 on 11/12/2015.
 */
public class APIInteractionImplTest {
    private APIInteractionImpl a;
    @Test
    public void testReverseGeocode() throws Exception {
        a = new APIInteractionImpl();
        Image image = new Image();
        //coordinates below are for apartment in Towson
        image.setLatitude("39.392441");
        image.setLongitude("-76.585812");
        a.reverseGeocode(image);
        Assert.assertTrue(image.getLocation().contains("Towson"));
    }
}
import image.Dictionary;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

//import org.springframework.util.Assert;
//import image.Dictionary;


public class DictionaryTest {
    private String nonsense;
    private Dictionary dict;


    @Before
    public void setUp() {
        //first is setup for testfind matches
        //this string has only one word the findmatches should recognize

        //Next is setup for testtostring
        dict = new Dictionary();


    }
    @Test
    public void testFindMatches() throws Exception {
        //from nonsense string
        String expected = "the";
        nonsense ="xxxixixixxThezxxixixixix";
        ArrayList arr = dict.findMatches(nonsense);
        //result should only have found one dictionary word from string, so we take the first result in the arrayList to match said word
        String result = arr.get(0).toString();
        Assert.assertEquals(expected, result);

        //now let's make sure a string with no matches doesn't make the method crash, should return empty arraylist
        ArrayList ALexpected = new ArrayList();
        nonsense = "xxxxxxxx";
        arr = dict.findMatches(nonsense);
        Assert.assertEquals(ALexpected, arr);

    }

    @Test
    public void testToString() throws Exception {
        String expected = "example here shown ";
        //create a string of matches for the new class to process, so that it has matches in local variables to actually turn to strings
        //the words have to be in alphabetical order, because it checks to see if each dictionary word is in the string in order
        //no words less than 3 characters can be used because it ignores those
        dict.findMatches("xxxxexamplezxxxxxxxxshownxxxxxxxhere");
        String result = dict.toString();
        Assert.assertEquals(expected,result);
    }
}



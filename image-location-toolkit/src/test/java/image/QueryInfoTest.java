package image;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static net.sf.ezmorph.test.ArrayAssertions.assertEquals;

/**
 * Created by 571743 on 10/22/2015.
 */
public class QueryInfoTest {
    private String querySent;
    private QueryInfo qI;
    @Before
    public void setup() {
        querySent= "thesaurus=on&select1=location&text1=London&select2=date&text2=Tues&select3=location&text3=United Kingdom";
        qI = new QueryInfoImpl();
        qI.buildQueryInfo(querySent);
        testbuildQueryInfo();
    }

    @Test
    public void testbuildQueryInfo() {
        String[] expectedKeys = new String[]{"location, date, location"};
        String[] expectedValues = new String[]{"London, Tues, United Kingdom"};
        assertFalse(qI.getAllAnyChoice());
        assertTrue(qI.getThesChoice());
        assertEquals(querySent,qI.getJsonString());
        assertEquals(Arrays.toString(expectedKeys),Arrays.toString(qI.getKeys()));
        assertEquals(Arrays.toString(expectedValues), Arrays.toString(qI.getValues()));
    }

}
package image;

import junit.framework.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

import static net.sf.ezmorph.test.ArrayAssertions.assertEquals;

/**
 * Created by 571743 on 10/23/2015.
 */
public class ImageOperationsTest {

    @Test
    public void testStreamToFile() throws Exception, FileNotFoundException {
    InputStream inputStream = ImageOperations.class.getClassLoader().getResourceAsStream("testFile.txt");
     Scanner scanner = new Scanner(ImageOperations.streamToFile(inputStream));
        File file = new File("src/test/java/image/testfile.txt");
        Scanner scanner2 = new Scanner(file);
        assertEquals(scanner.nextLine(),scanner2.nextLine());
    }

    @Test
    public void testConvertAbbreviation() throws Exception {
        //test if correct input is used example
        String expected = "Alabama";
        String result = ImageOperations.convertStateAbbreviation("AL");
        Assert.assertEquals(expected,result);
        //test if incorrect input is used example
        String expected2 = "foo";
        String result2 = ImageOperations.convertStateAbbreviation("foo");
        Assert.assertEquals(expected2,result2);
    }

    @Test
    public void testConvertStateAbbreviation() throws Exception {
        //test if correct input is used example
        String expected = "ALBANIA";
        String result = ImageOperations.convertAbbreviation("AL");
        Assert.assertEquals(expected,result);
        //test if incorrect input is used example
        String expected2 = "foo";
        String result2 = ImageOperations.convertAbbreviation("foo");
        Assert.assertEquals(expected2,result2);

    }
}
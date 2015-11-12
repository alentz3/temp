package image.BusinessLogic;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * Created by 571743 on 11/5/2015.
 */
public interface UploadAndExtractMD {
    String processInputStream(InputStream inputStream) throws IOException, SQLException;

    String processInputStream(InputStream inputStream, String url) throws IOException, SQLException   /* NOTE: Change domain if switching to another server. */;

    void waitforGeocode() throws InterruptedException;

    void waitforMetaData() throws InterruptedException;

    void waitforOCR() throws InterruptedException;
}

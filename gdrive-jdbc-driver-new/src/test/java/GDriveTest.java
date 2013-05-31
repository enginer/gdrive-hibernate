import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.urlshortener.UrlshortenerScopes;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GDriveTest {
    /** Global instance of the HTTP transport. */
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    @Test
    public void testAuth() throws GeneralSecurityException, IOException {

        /*GoogleCredential credential = new GoogleCredential.Builder().setTransport(HTTP_TRANSPORT).setJsonFactory(JSON_FACTORY)
                .setServiceAccountId("569342713848@developer.gserviceaccount.com")
                .setServiceAccountScopes(DriveScopes.DRIVE)
                .setServiceAccountPrivateKeyFromP12File(new File("D:\\projects\\gdrive-jdbc\\src\\main\\resources\\a2bfa2ef8ddb4b4cd5edd2ebd40235b07938ecfb-privatekey.p12"))
                .setServiceAccountUser("user@domain.com")
                .build();

        Drive drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
        Drive.Files.Get request = drive.files().get("0Ap8EH764Qp9rdHBxSUM0cmlSSzlySmlQSV90VWJQclE");
        com.google.api.services.drive.model.File file = request.execute();*/

        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setServiceAccountId("569342713848@developer.gserviceaccount.com")
                .setServiceAccountScopes(UrlshortenerScopes.URLSHORTENER)
                .setServiceAccountPrivateKeyFromP12File(
                        new java.io.File("D:\\projects\\gdrive-jdbc\\src\\main\\resources\\a2bfa2ef8ddb4b4cd5edd2ebd40235b07938ecfb-privatekey.p12"))
                .build();
//        Urlshortener service = new Urlshortener.Builder(httpTransport, jsonFactory, null)
//                .setHttpRequestInitializer(credential).build();
//        UrlHistory history = service.url().list().execute();
        Drive drive = new Drive.Builder(httpTransport, jsonFactory, null)
                .setHttpRequestInitializer(credential).build();

        FileList list = drive.files().list().execute();
//        Urlshortener.Url.List list = service.url().list();
    }
}

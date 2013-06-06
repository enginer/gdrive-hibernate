package org.gdrive;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestGdata {
    @Test
    public void test() throws ServiceException, IOException {
        String username = "";
        String password = "";
        String tableKey = "";
        SpreadsheetService spreadsheetsService = new SpreadsheetService("test");
        spreadsheetsService.setUserCredentials(username, password);

        WorksheetFeed worksheetFeed = spreadsheetsService.getFeed(FeedURLFactory.getDefault().getWorksheetFeedUrl(
                tableKey, "private", "full"), WorksheetFeed.class);
        assertTrue(worksheetFeed.getEntries().size() > 0);

        for(WorksheetEntry worksheet : worksheetFeed.getEntries()) {
            URL listFeedUrl = worksheet.getListFeedUrl();
            ListFeed feed = spreadsheetsService.getFeed(listFeedUrl, ListFeed.class);
            assertTrue(feed.getEntries().size() > 0);
            for (ListEntry entry : feed.getEntries()) {
                assertTrue(entry.getCustomElements().getTags().size() > 0);
                for (String tag : entry.getCustomElements().getTags()) {
                    String value = entry.getCustomElements().getValue(tag);
                    assertNotNull(value);
                }

            }
        }
    }
}

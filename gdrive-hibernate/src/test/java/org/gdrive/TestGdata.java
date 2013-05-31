package org.gdrive;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestGdata {
    @Test
    public void test() throws ServiceException, IOException {
        String username = "<email>";
        String password = "<password>";
        SpreadsheetService spreadsheetsService = new SpreadsheetService("test");
        spreadsheetsService.setUserCredentials(username, password);

        SpreadsheetFeed spreadsheetFeed = spreadsheetsService.getFeed(FeedURLFactory.getDefault().getSpreadsheetsFeedUrl(),
                    SpreadsheetFeed.class);
        assertTrue(spreadsheetFeed.getEntries().size() > 0);
        for (SpreadsheetEntry spreadsheetEntry : spreadsheetFeed.getEntries()) {
            spreadsheetEntry.getKey();
            List<WorksheetEntry> worksheets = spreadsheetEntry.getWorksheets();

            URL columnListFeedUrl = worksheets.get(0).getListFeedUrl();
            ListFeed columnFeed = spreadsheetsService.getFeed(columnListFeedUrl, ListFeed.class);

            assertTrue(worksheets.size() > 0);

            for(WorksheetEntry worksheet : worksheets) {
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
}

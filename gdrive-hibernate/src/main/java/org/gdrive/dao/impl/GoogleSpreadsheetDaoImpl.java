package org.gdrive.dao.impl;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import org.gdrive.dao.GoogleSpreadsheetDao;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Property;
import org.hibernate.property.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class GoogleSpreadsheetDaoImpl implements GoogleSpreadsheetDao {

    @Value("${google.email}") String username;
    @Value("${google.password}") String password;

    @Autowired SessionFactoryImplementor sessionFactoryImplementor;

    @Override
    public <E> List<E> getTable(Class<E> mappedClass, String tableKey, Iterator<Property> propertyIterator) {
        List<E> list = new ArrayList<E>();
        List<Property> properties = new ArrayList<Property>();
        while (propertyIterator.hasNext()) {
            properties.add(propertyIterator.next());
        }

        SpreadsheetService spreadsheetsService = new SpreadsheetService("Gtest");
        try {
            spreadsheetsService.setUserCredentials(username, password);

            WorksheetFeed worksheetFeed = spreadsheetsService.getFeed(FeedURLFactory.getDefault().getWorksheetFeedUrl(
                    tableKey, "private", "full"), WorksheetFeed.class);

            WorksheetEntry worksheet = worksheetFeed.getEntries().get(0);
            URL listFeedUrl = worksheet.getListFeedUrl();
            ListFeed feed = spreadsheetsService.getFeed(listFeedUrl, ListFeed.class);

            for (ListEntry entry : feed.getEntries()) {
                E item = mappedClass.newInstance();
                for (Property property : properties) {
                    Column column = (Column) property.getColumnIterator().next();
                    String value = entry.getCustomElements().getValue(column.getName());
                    Setter setter = property.getSetter(mappedClass);
                    setter.set(item, value, sessionFactoryImplementor);
                }
                list.add(item);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}

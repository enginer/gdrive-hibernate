package org.gdrive;

import org.gdrive.dao.CommonDao;
import org.gdrive.dao.GoogleSpreadsheetDao;
import org.hibernate.mapping.PersistentClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;

@Component
public class GoogleImportService {
    @Autowired GoogleSpreadsheetDao googleSpreadsheetDao;
    @Autowired LocalSessionFactoryBean sessionFactory;
    @Autowired CommonDao commonDao;

    /**
     * Import google spreadsheet data for each @Entity
     */
    @SuppressWarnings("unchecked")
    @PostConstruct
    private void importDB() {
        Iterator<PersistentClass> iterator = sessionFactory.getConfiguration().getClassMappings();
        while ( iterator.hasNext() ) {
            PersistentClass persistentClass = iterator.next();
            Class mappedClass = persistentClass.getMappedClass();
            List all = googleSpreadsheetDao.getTable(mappedClass, persistentClass.getTable().getName(),
                    persistentClass.getPropertyIterator());
            commonDao.saveList(mappedClass, all);
        }
    }
}

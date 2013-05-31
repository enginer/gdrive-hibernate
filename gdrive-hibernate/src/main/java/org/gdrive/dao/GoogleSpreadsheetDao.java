package org.gdrive.dao;

import org.hibernate.mapping.Property;

import java.util.Iterator;
import java.util.List;

public interface GoogleSpreadsheetDao {
    <E> List<E> getAll(Class<E> mappedClass, Iterator<Property> propertyIterator);
}

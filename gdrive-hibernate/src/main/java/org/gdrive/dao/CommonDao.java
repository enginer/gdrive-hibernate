package org.gdrive.dao;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

public interface CommonDao {
    List getAll(Class<?> clazz);

    @Nullable
    <E> E get(Class<E> clazz, Serializable id);
    <E> E get(Class<E> clazz, String property, Object value);
    <E> E save(Class<E> clazz, E o);
    <E> void saveList(Class<E> clazz, List<E> list);
}

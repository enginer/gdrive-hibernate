package org.gdrive.dao.impl;

import org.gdrive.dao.CommonDao;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Repository
@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class CommonDaoImpl implements CommonDao {

    @Autowired
    private SessionFactory sessionFactory;


    @Override
    public List getAll(Class<?> clazz) {
        return sessionFactory.getCurrentSession().createCriteria(clazz).list();
    }

    @Nullable
    @Override
    public <E> E get(Class<E> clazz, Serializable id) {
        return (E) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public <E> E get(Class<E> clazz, String property, Object value) {
        return (E) sessionFactory.getCurrentSession()
                .createCriteria(clazz).add(Restrictions.eq(property, value))
                .uniqueResult();
    }
}
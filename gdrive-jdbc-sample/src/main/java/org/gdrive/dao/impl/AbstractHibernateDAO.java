package org.gdrive.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.metadata.ClassMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import static org.apache.commons.beanutils.PropertyUtils.setProperty;
import static org.hibernate.criterion.Restrictions.eq;
import static org.springframework.util.Assert.notNull;

@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public abstract class AbstractHibernateDAO<E> {

    protected final Class<E> entityClass;
    @Autowired
    private SessionFactory sessionFactory;

    public AbstractHibernateDAO(Class<E> entityClass) {
        notNull(entityClass, "entityClass must not be null");
        this.entityClass = entityClass;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public E save(E entity) {
        try {
            ClassMetadata metadata = sessionFactory.getClassMetadata(entityClass);
            Object id = getProperty(entity, metadata.getIdentifierPropertyName());
            if (id != null && id.equals(new Integer(0))) {
                setProperty(entity, metadata.getIdentifierPropertyName(), null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        currentSession().saveOrUpdate(entity);
        return entity;
    }

    @Nullable
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public E merge(@Nullable E entity) {
        return (E) currentSession().merge(entity);
    }

    protected Criteria criteria() {
        return currentSession().createCriteria(entityClass);
    }

    protected Query query(String hql) {
        return currentSession().createQuery(hql);
    }

    protected Query queryByName(String queryName) {
        return currentSession().getNamedQuery(queryName);
    }

    protected Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    protected List<E> all() {
        return list(criteria());
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

        /* === BEGIN GENERICS SUPPRESSION WRAPPERS === */

    protected List<E> list(Criteria criteria) {
        return list(criteria, true);
    }

    @SuppressWarnings("unchecked")
    protected List<E> list(Criteria criteria, boolean cache) {
        criteria.setCacheable(cache);
        return criteria.list();
    }

    protected List<E> list(Query query) {
        return list(query, true);
    }

    @SuppressWarnings("unchecked")
    protected List<E> list(Query query, boolean cache) {
        query.setCacheable(cache);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    protected E uniqueResult(Criteria criteria) {
        return (E) criteria.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    protected E uniqueResult(Query query) {
        return (E) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public E get(Serializable id) {
        return (E) currentSession().get(entityClass, id);
    }

    @Nullable
    public E get(String property, @NotNull Object o) {
        return (E) criteria()
                .add(eq(property, o))
                .uniqueResult();
    }

    @Nullable
    public List<E> getList(String property, @NotNull Object o) {
        return criteria()
                .add(eq(property, o))
                .list();
    }

    @SuppressWarnings("unchecked")
    protected E load(Serializable id) {
        return (E) currentSession().load(entityClass, id);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void remove(Serializable id) {
        E entity = (E) currentSession().get(entityClass, id);
        currentSession().delete(entity);
    }

    public List<E> getPage(int skip, int pageSize, String sortField, String sortDirection) {
        Criteria criteria = currentSession().createCriteria(entityClass)
                .setFirstResult(skip)
                .setMaxResults(pageSize);

        if (sortField != null && !sortField.isEmpty()) {
            criteria.addOrder("asc".equals(sortDirection) ? Order.asc(sortField) : Order.desc(sortField));
        }

        return (List<E>) criteria.list();
    }

    public List<E> getPage(int skip, int pageSize, String sortField, String sortDirection, Set<Criterion> filter) {
        Criteria criteria = currentSession().createCriteria(entityClass)
                .setFirstResult(skip)
                .setMaxResults(pageSize);

        if (sortField != null && !sortField.isEmpty()) {
            criteria.addOrder("asc".equals(sortDirection) ? Order.asc(sortField) : Order.desc(sortField));
        }

        for (Criterion item : filter) {
            criteria.add(item);
        }

        return (List<E>) criteria.list();
    }

    public Long getCount() {
        return (Long) currentSession().createQuery("select count (*) from "+entityClass.getName())
                .uniqueResult();
    }

    public Long getCount(Set<Criterion> filter) {
        Criteria criteria = currentSession().createCriteria(entityClass)
                .setProjection(Projections.rowCount());
        for (Criterion item : filter) {
            criteria.add(item);
        }
        return (Long) criteria.uniqueResult();
    }

    public List<E> getAll() {
        return criteria()
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
                .list();
    }

    @Nullable
    public E getRandom(@Nullable Criterion restriction) {
        Criteria criteria = criteria();
        criteria.add(restriction);
        criteria.setProjection(Projections.rowCount());
        int count = ((Number) criteria.uniqueResult()).intValue();
        if (0 != count) {
            int index = new Random().nextInt(count);
            criteria = criteria();
            criteria.add(restriction);
            return (E) criteria.setFirstResult(index).setMaxResults(1).uniqueResult();
        }
        return null;
    }
}
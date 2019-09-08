package com.sambatech.challenge.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Transactional
@Repository
public abstract class GenericDAO<T> {

  @PersistenceContext protected EntityManager entityManager;

  private Class<T> clazz = null;

  public Class<T> getClazz() {
    return this.clazz;
  }

  public void setClazz(Class<T> clazz) {
    this.clazz = clazz;
  }

  private void checkState() {
    if (clazz == null) {
      throw new IllegalStateException(
          "DAO not initialized. You need to call setClazz() "
              + "with the appropriate class to use this. For convenience, you can set a "
              + "constructor in your subclass that handles this.");
    }
  }

  public T findOne(T id) {
    checkState();
    if (id == null) {
      return null;
    }
    return entityManager.find(clazz, id);
  }

  public List<T> findAll() {
    checkState();
    return entityManager.createQuery("from " + clazz.getName()).getResultList();
  }

  public void insert(T t) {
    entityManager.persist(t);
  }

  public void update(T t) {
    entityManager.merge(t);
  }

  public void delete(T entity) {
    entityManager.remove(entity);
  }

  public void deleteById(T entityId) {
    checkState();
    T entity = entityManager.getReference(clazz, entityId);
    entityManager.remove(entity);
  }
}

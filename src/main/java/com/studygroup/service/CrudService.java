package com.studygroup.service;

import java.util.List;

public interface CrudService<T, ID> {
    T save(T entity);

    T findById(ID id);

    List<T> findAll();

    void deleteById(ID id);
}

package cn.yayi.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface BaseDao<T> {

    void save(T entity);


    void update(T entity);

    void delete(Serializable... ids);

    T find(Serializable id);

    List<T> list();

    List<T> section(Map<String, Object> para);

    int count(Map<String, Object> para);

}

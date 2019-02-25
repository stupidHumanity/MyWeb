package cn.yayi.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SuppressWarnings("unused")
public abstract class GenericService<T> {

    public Class<T> getEntityClass() {
        Type type = getClass().getGenericSuperclass();
        Type trueType = ((ParameterizedType) type).getActualTypeArguments()[0];
        return (Class<T>) trueType;

    }
}

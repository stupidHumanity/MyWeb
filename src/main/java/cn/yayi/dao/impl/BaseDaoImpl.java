package cn.yayi.dao.impl;

import io.jsonwebtoken.lang.Assert;
import net.dgg.accountingtools.common.base.dao.BaseDao;
import net.dgg.accountingtools.common.base.result.PageResult;
import net.dgg.accountingtools.common.util.EmptyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;

import static org.springframework.data.mongodb.core.query.Criteria.where;


@Repository
public abstract class BaseDaoImpl<T> implements BaseDao<T> {

    private Class<T> getEntityClass() {
        Type type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return (Class<T>) type;
    }

    @Autowired
    protected MongoTemplate mgt;

    @Override
    public void save(T entity) {
        mgt.save(entity);
    }

    @Override
    public void update(T entity) {

        // 反向解析对象
        Map<String, Object> map = null;
        try {
            map = parseEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ID字段
        String idName = null;
        Object idValue = null;

        // 生成参数
        Update update = new Update();
        if (EmptyUtil.isNotEmpty(map)) {
            for (String key : map.keySet()) {
                if (key.indexOf("{") != -1) {
                    // 设置ID
                    idName = key.substring(key.indexOf("{") + 1, key.indexOf("}"));
                    idValue = map.get(key);
                } else {
                    update.set(key, map.get(key));
                }
            }
        }
        mgt.updateFirst(new Query().addCriteria(where(idName).is(idValue)), update, getEntityClass());
    }

    @Override
    public void delete(Serializable... ids) {
        if (EmptyUtil.isNotEmpty(ids)) {
            for (Serializable id : ids) {
                mgt.remove(mgt.findById(id, getEntityClass()));
            }
        }

    }

    @Override
    public T find(Serializable id) {
        return mgt.findById(id, getEntityClass());
    }






    /**
     * 分页查询
     *
     * @param para 分页参数 pageSize, pageIndex(start with 0)
     * @param para 排序参数 orderBy：如 id  或   id asc  或  id asc,name desc
     * @return
     */
    public PageResult<T> pageQuery(Map<String, Object> para) {
        String key_size = "pageSize", key_index = "pageIndex", key_order = "orderBy";
        Integer index, size;
        String order;
        //分页参数
        Assert.isTrue(para.containsKey(key_size) && para.containsKey(key_index), String.format("缺少分页参数%s 或 %s 。", key_index, key_size));

        index = Integer.parseInt(String.valueOf(para.get(key_index)));
        size = Integer.parseInt(String.valueOf(para.get(key_size)));
        para.remove(key_index);
        para.remove(key_size);
        //筛选条件
        String[] keys = new String[para.size()];
        Object[] values = new Object[para.size()];
        Iterator<String> it = para.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            String key = it.next();
            keys[i] = key;
            values[i] = values;
            i++;
        }
        //排序
        order = para.containsKey(key_order) ? String.valueOf(para.get(key_order)) : null;

        return pageByProps(index, size, keys, values, order);
    }





    private List<T> list(int pageNo, int pageSize, String[] params, Object[] values, String order) {
        // 创建分页模型对象
        PageResult<T> page = new PageResult<>(pageNo, pageSize);

        // 查询总记录数
        int count = cout(params, values);
        page.setTotalCount(count);

        // 查询数据列表
        Query query = createQuery(params, values, order);

        // 设置分页信息
        query.skip(page.getFirstResult());
        query.limit(page.getPageSize());

        // 封装结果数据
        page.setList(mgt.find(query, getEntityClass()));

        return page;
    }

    @Override
    public int count(String[] params, Object[] values) {
        Query query = createQuery(params, values, null);
        Long count = mgt.count(query, getEntityClass());
        return count.intValue();
    }

    /**
     * 创建带有where条件（只支持等值）和排序的Query对象
     *
     * @param params 参数数组
     * @param values 参数值数组
     * @param order  排序
     * @return Query对象
     */
    protected Query createQuery(String[] params, Object[] values, String order) {
        Query query = new Query();

        // where 条件
        if (EmptyUtil.isNotEmpty(params) && EmptyUtil.isNotEmpty(values)) {
            for (int i = 0; i < params.length; i++) {
                query.addCriteria(where(params[i]).is(values[i]));
            }
        }

        // 排序
        List<Sort.Order> orderList = parseOrder(order);
        if (EmptyUtil.isNotEmpty(orderList)) {
            query.with(new Sort(orderList));
        }

        return query;
    }

    /**
     * 解析Order字符串为所需参数
     *
     * @param order 排序参数，如[id]、[id asc]、[id asc,name desc]
     * @return Order对象集合
     */
    private List<Sort.Order> parseOrder(String order) {
        List<Sort.Order> list = null;
        if (EmptyUtil.isNotEmpty(order)) {
            list = new ArrayList<Sort.Order>();
            // 共有几组排序字段
            String[] fields = order.split(",");
            Sort.Order o = null;
            String[] item = null;
            for (int i = 0; i < fields.length; i++) {
                if (EmptyUtil.isEmpty(fields[i])) {
                    continue;
                }
                item = fields[i].split(" ");
                if (item.length == 1) {
                    o = new Sort.Order(Direction.ASC, item[0]);
                } else if (item.length == 2) {
                    o = new Sort.Order("desc".equalsIgnoreCase(item[1]) ? Direction.DESC : Direction.ASC, item[0]);
                } else {
                    throw new RuntimeException("排序字段参数解析出错");
                }
                list.add(o);
            }
        }
        return list;
    }

    /**
     * 将对象的字段及值反射解析为Map对象<br>
     * 这里使用Java反射机制手动解析，并且可以识别注解为主键的字段，以达到根据id进行更新实体的目的<br>
     * key：字段名称，value：字段对应的值
     *
     * @param t 要修改的对象
     * @return Map对象，注意：id字段的key封装为“{id字段名称}”，以供后续识别
     * @throws Exception
     */
    protected Map<String, Object> parseEntity(T t) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        /*
         * 解析ID
         */
        String idName = "";
        Field[] declaredFields = getEntityClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                map.put("{" + field.getName() + "}", field.get(t));
                idName = field.getName();
                break;
            }
        }
        /*
         * 解析其他属性
         */
        Method[] methods = getEntityClass().getDeclaredMethods();
        if (EmptyUtil.isNotEmpty(methods)) {
            for (Method method : methods) {
                if (method.getName().startsWith("get") && method.getModifiers() == Modifier.PUBLIC) {
                    String fieldName = parse2FieldName(method.getName());
                    if (!fieldName.equals(idName)) {
                        map.put(fieldName, method.invoke(t));
                    }
                }
            }
        }

        return map;
    }

    /**
     * 将get方法名转换为对应的字段名称
     *
     * @param methodName 如：getName
     * @return 如：name
     */
    private String parse2FieldName(String methodName) {
        String name = methodName.replace("get", "");
        name = name.substring(0, 1).toLowerCase() + name.substring(1);
        return name;
    }

    /**
     * 根据多个参数查询唯一结果<br>
     * [HQL]
     *
     * @param companyId       账套公司ID
     * @param propName        相等参数KEY数组
     * @param propValue       相等参数VALUE数组
     * @param notPropName     不相等参数KEY数组
     * @param notPropValue    不相等参数VALUE数组
     * @param notNullPropName 不为空参数数组
     * @return
     */
    @Override
    public T findOneByProps(Long companyId, String[] propName, Object[] propValue,
                            String[] notPropName, Object[] notPropValue, String[] notNullPropName) {
        Query query = new Query();
        Criteria criteria = Criteria.where("companyId").is(companyId);
        // where is 条件
        if (EmptyUtil.isNotEmpty(propName) && EmptyUtil.isNotEmpty(propValue)) {
            for (int i = 0; i < propName.length; i++) {
                criteria.and(propName[i]).is(propValue[i]);
            }
        }
        // where ne 条件
        if (EmptyUtil.isNotEmpty(notPropName) && EmptyUtil.isNotEmpty(notPropValue)) {
            for (int i = 0; i < notPropName.length; i++) {
                criteria.and(notPropName[i]).ne(notPropValue[i]);
            }
        }

        // where ne null 条件
        if (EmptyUtil.isNotEmpty(notNullPropName)) {
            for (int i = 0; i < notNullPropName.length; i++) {
                criteria.and(notNullPropName[i]).ne("").ne(null);
            }
        }
        query.addCriteria(criteria);
        return mgt.findOne(query, getEntityClass());
    }

    /**
     * 根据多个参数查询集合数据
     * [不分页]
     *
     * @param companyId       账套公司ID
     * @param propName        相等参数KEY数组
     * @param propValue       相等参数VALUE数组
     * @param notPropName     不相等参数KEY数组
     * @param notPropValue    不相等参数VALUE数组
     * @param notNullPropName 不为空参数数组
     * @return
     */
    @Override
    public List<T> findListByProps(Long companyId, String[] propName, Object[] propValue,
                                   String[] notPropName, Object[] notPropValue, String[] notNullPropName) {
        Query query = new Query();
        Criteria criteria = Criteria.where("companyId").is(companyId);

        // where is 条件
        if (EmptyUtil.isNotEmpty(propName) && EmptyUtil.isNotEmpty(propValue)) {
            for (int i = 0; i < propName.length; i++) {
                criteria.and(propName[i]).is(propValue[i]);
            }
        }

        // where ne 条件
        if (EmptyUtil.isNotEmpty(notPropName) && EmptyUtil.isNotEmpty(notPropValue)) {
            for (int i = 0; i < notPropName.length; i++) {
                criteria.and(notPropName[i]).ne(notPropValue[i]);
            }
        }

        // where ne null 条件
        if (EmptyUtil.isNotEmpty(notNullPropName)) {
            for (int i = 0; i < notNullPropName.length; i++) {
                criteria.and(notNullPropName[i]).ne("").ne(null);
            }
        }

        query.addCriteria(criteria);
        return mgt.find(query, getEntityClass());
    }


}

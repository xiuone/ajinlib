package com.xy.baselib.db;

import android.content.Context;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.DatabaseConnection;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/13.
 */

public class DaoHelp<T> extends DaoBaseHelp<T> {
    private DatabaseConnection connection;
    private Savepoint savePoint;

    public DaoHelp(String TABLE_NAME, int VERSION, Context context, Class<T> beanClass) {
        super(TABLE_NAME, VERSION, context);
        try {
            aclass = beanClass.newInstance();
            getDa(beanClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public QueryBuilder<T, Integer> getQueryBuilder() {
        return dao.queryBuilder();
    }

    public void getDa(Class<T> beanClass) throws SQLException {
        if (dao == null) {
            dao = getDao(beanClass);
        }
    }
    /*
     * *************新增数据方法*********************************
     */

    /**
     * 新增单条数据
     */
    public void insert(T object) {
        try {
            dao.createIfNotExists(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 存在更新，没有增加
     */
    public void save(T object) {
        try {
            dao.createOrUpdate(object);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 批量新增
     */
    public void insert(Collection<T> collection) {
        try {
            dao.create(collection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /*
     * *************查询数据方法*********************************
     */

    /**
     * 使用迭代器查询表中所用记录
     */
    public List<T> queryAllData() {
        List<T> datalist = new ArrayList<>();
        CloseableIterator<T> iterator = dao.closeableIterator();
        try {
            while (iterator.hasNext()) {
                T data = iterator.next();
                datalist.add(data);
            }
        } finally {
            // close it at the end to close underlying SQL statement
            try {
                iterator.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return datalist;
    }

    /**
     * 根据传入的字段与value值的map匹配查询
     */
    public List<T> queryDataEqByClause(Map<String, Object> clause) throws SQLException {
        // queryBuild构建多条件查询
        List<T> result = dao.queryForFieldValuesArgs(clause);
        return result;
    }

    /**
     * 返回查询结果的总数
     *
     * @see "对应SQL：SELECT COUNT(*) FROM 'table'"
     */
    public long queryCount() {
        try {
            return dao.countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 返回所有行的第一行。
     */
    public T queryForFirst() {
        try {
            return getQueryBuilder().queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
     * *************更新数据方法*********************************
     */

    /**
     * 使用对象更新一条记录
     */
    public void updateData(T object) throws SQLException {
        dao.update(object);
    }

    /*
     * *************删除数据方法*********************************
     */

    /**
     * 使用对象删除一条记录
     */
    public void delectData(T object) throws SQLException {
        dao.delete(object);
    }

    /**
     * 批量删除
     */
    public void delectDatas(Collection<T> datas) throws SQLException {
        dao.delete(datas);
    }

    /**
     * 批量删除
     */
    public boolean deleteAll() {
        try {
            return dao.executeRaw("DELETE FROM " + dao.getTableName()) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

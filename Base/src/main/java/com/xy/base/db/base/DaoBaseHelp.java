package com.xy.base.db.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Administrator on 2018/1/13.
 */

public class DaoBaseHelp<T> extends OrmLiteSqliteOpenHelper {
    public T aclass;
    //利用生成的daoHelp对象来生成Dao对象，该对象是处理数据库的关键要素
    public Dao<T, Integer> dao;

    public DaoBaseHelp(String TABLE_NAME, int VERSION, Context context) {
        super(context, TABLE_NAME, null, VERSION); //必须实现父类的方法，其中第二个参数是创建的数据库名，第4个参数是版本号，用于升级等操作

    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, aclass.getClass());   //根据PersonalBean来进行创建操作
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, aclass.getClass(), true); //如果版本有更新则会执行onUpgrade方法，
            TableUtils.createTable(connectionSource, aclass.getClass()); //更新数据库先删除数据库再创建一个新的
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //释放资源
    @Override
    public void close() {
        super.close();
        dao = null;
    }
}

package com.xy.db.base;


import android.content.Context;

import com.xy.db.MultiEntry;

import org.greenrobot.greendao.AbstractDao;

import java.util.List;

/**
 * author       : wangyalei
 * time         : 19-12-17
 * description  :
 * history      :
 */
public class BaseDaoManager<D extends MultiEntry,S extends AbstractDao> {
    private Class mDaoClass;
    private Context context;
    public BaseDaoManager(Context context,Class daoClass) {
        this.mDaoClass = daoClass;
        this.context = context;
    }

    public S getDao(){
        DBManager dbManager = DBManager.getInstance();
        if(dbManager != null && dbManager.getSesstion(context) != null){
            AbstractDao abstractDao =  dbManager.getSesstion(context).getDao(mDaoClass);
            return (S) abstractDao;
        }
        return null;
    }

    public long add(D data){
        AbstractDao dao = getDao();
        if(dao == null){
            return 0;
        }
        long id = dao.insertOrReplace(data);
        return id;
    }

    public boolean del(D data){
        AbstractDao dao = getDao();
        if(dao == null || data == null){
            return false;
        }

        dao.delete(data);
        return true;
    }

    public boolean del(List<D> list){
        AbstractDao dao = getDao();
        if(dao == null){
            return false;
        }

        for (D item: list) {
            dao.delete(item);
        }

        return true;
    }

    public boolean update(D data){
        AbstractDao dao = getDao();
        if(dao == null){
            return false;
        }

        dao.update(data);
        return true;
    }

    public List<D> queryAll(){
        AbstractDao dao = getDao();
        if(dao == null){
            return null;
        }

        return dao.loadAll();
    }


    public boolean replaceAll(List<D> dataList){
        AbstractDao dao = getDao();
        if(dao == null){
            return false;
        }
        dao.deleteAll();
        dao.insertInTx(dataList);
        return true;
    }

    public void clear(){
        AbstractDao dao = getDao();
        if(dao == null){
            return;
        }
        dao.deleteAll();
    }
}

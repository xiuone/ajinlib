package com.xy.db.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.xy.db.gen.DaoMaster;
import com.xy.db.gen.DaoSession;

import org.greenrobot.greendao.identityscope.IdentityScopeType;


public class DBManager {
    public static final String DB_NAME = "xy.db";
    private static DBManager sManager;
    private DaoSession mSesstion;
    private DaoSession mNoCacheSesstion;

    private void initDb(Context context){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        mSesstion = new DaoMaster(db).newSession();
        mNoCacheSesstion = new DaoMaster(db).newSession(IdentityScopeType.None);
    }

    public static DBManager getInstance(){
        if(sManager == null){
            synchronized (DBManager.class){
                if(sManager == null){
                    sManager = new DBManager();
                }
            }
        }

        return sManager;
    }


    public DaoSession getSesstion(Context context) {
        if(mSesstion == null){
            initDb(context);
        }

        return mSesstion;
    }

    public DaoSession getNoCacheSesstion(Context context) {
        if(mNoCacheSesstion == null){
            initDb(context);
        }
        return mNoCacheSesstion;
    }
}

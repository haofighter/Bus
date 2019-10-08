package com.szxb.buspay.db.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.szxb.buspay.db.dao.BlackListCardDao;
import com.szxb.buspay.db.dao.BlackListEntityDao;
import com.szxb.buspay.db.dao.ConsumeCardDao;
import com.szxb.buspay.db.dao.DaoMaster;
import com.szxb.buspay.db.dao.LineInfoEntityDao;
import com.szxb.buspay.db.dao.MacKeyEntityDao;
import com.szxb.buspay.db.dao.PublicKeyEntityDao;
import com.szxb.buspay.db.dao.ScanInfoEntityDao;
import com.szxb.buspay.db.dao.UnionAidEntityDao;
import com.szxb.buspay.db.dao.UnionPayEntityDao;
import com.szxb.mlog.SLog;

import org.greenrobot.greendao.database.Database;


/**
 * 作者：Tangren_ on 2017/3/23 0023.
 * 邮箱：wu_tangren@163.com
 * TODO：更新数据库
 */


public class DBHelper extends DaoMaster.OpenHelper {
    DBHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        SLog.d("onUpgrade(DBHelper.java:46)升级");
        update(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        super.onDowngrade(db, oldVersion, newVersion);
        SLog.d("onDowngrade(DBHelper.java:52)降级");
        update(db, oldVersion, newVersion);
    }

    private void update(SQLiteDatabase db, int oldVersion, int newVersion) {
        SLog.d("update(DBHelper.java:57))oldVersion=" + oldVersion + "newVersion=" + newVersion);
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                    @Override
                    public void onCreateAllTables(Database db, boolean ifNotExists) {
                        DaoMaster.createAllTables(db, ifNotExists);
                    }

                    @Override
                    public void onDropAllTables(Database db, boolean ifExists) {
                        DaoMaster.dropAllTables(db, ifExists);
                    }
                },
                BlackListCardDao.class,
                BlackListEntityDao.class,
                ConsumeCardDao.class,
                LineInfoEntityDao.class,
                MacKeyEntityDao.class,
                PublicKeyEntityDao.class,
                ScanInfoEntityDao.class,
                UnionAidEntityDao.class,
                UnionPayEntityDao.class
        );
    }

}

package com.szxb.buspay.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.szxb.buspay.db.entity.bean.LINEGuideEntity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LINEGUIDE_ENTITY".
*/
public class LINEGuideEntityDao extends AbstractDao<LINEGuideEntity, Long> {

    public static final String TABLENAME = "LINEGUIDE_ENTITY";

    /**
     * Properties of entity LINEGuideEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Acnt = new Property(1, String.class, "acnt", false, "ACNT");
        public final static Property Routeno = new Property(2, String.class, "routeno", false, "ROUTENO");
        public final static Property Routeversion = new Property(3, String.class, "routeversion", false, "ROUTEVERSION");
        public final static Property Routevname = new Property(4, String.class, "routevname", false, "ROUTEVNAME");
        public final static Property FileName = new Property(5, String.class, "fileName", false, "FILE_NAME");
        public final static Property NeedUpdate = new Property(6, boolean.class, "needUpdate", false, "NEED_UPDATE");
    }


    public LINEGuideEntityDao(DaoConfig config) {
        super(config);
    }
    
    public LINEGuideEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LINEGUIDE_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"ACNT\" TEXT," + // 1: acnt
                "\"ROUTENO\" TEXT," + // 2: routeno
                "\"ROUTEVERSION\" TEXT," + // 3: routeversion
                "\"ROUTEVNAME\" TEXT," + // 4: routevname
                "\"FILE_NAME\" TEXT," + // 5: fileName
                "\"NEED_UPDATE\" INTEGER NOT NULL );"); // 6: needUpdate
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LINEGUIDE_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, LINEGuideEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String acnt = entity.getAcnt();
        if (acnt != null) {
            stmt.bindString(2, acnt);
        }
 
        String routeno = entity.getRouteno();
        if (routeno != null) {
            stmt.bindString(3, routeno);
        }
 
        String routeversion = entity.getRouteversion();
        if (routeversion != null) {
            stmt.bindString(4, routeversion);
        }
 
        String routevname = entity.getRoutevname();
        if (routevname != null) {
            stmt.bindString(5, routevname);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(6, fileName);
        }
        stmt.bindLong(7, entity.getNeedUpdate() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, LINEGuideEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String acnt = entity.getAcnt();
        if (acnt != null) {
            stmt.bindString(2, acnt);
        }
 
        String routeno = entity.getRouteno();
        if (routeno != null) {
            stmt.bindString(3, routeno);
        }
 
        String routeversion = entity.getRouteversion();
        if (routeversion != null) {
            stmt.bindString(4, routeversion);
        }
 
        String routevname = entity.getRoutevname();
        if (routevname != null) {
            stmt.bindString(5, routevname);
        }
 
        String fileName = entity.getFileName();
        if (fileName != null) {
            stmt.bindString(6, fileName);
        }
        stmt.bindLong(7, entity.getNeedUpdate() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public LINEGuideEntity readEntity(Cursor cursor, int offset) {
        LINEGuideEntity entity = new LINEGuideEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // acnt
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // routeno
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // routeversion
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // routevname
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // fileName
            cursor.getShort(offset + 6) != 0 // needUpdate
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, LINEGuideEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAcnt(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setRouteno(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRouteversion(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRoutevname(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setFileName(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setNeedUpdate(cursor.getShort(offset + 6) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(LINEGuideEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(LINEGuideEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(LINEGuideEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

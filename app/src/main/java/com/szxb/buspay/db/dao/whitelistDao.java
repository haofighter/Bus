package com.szxb.buspay.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.szxb.buspay.db.entity.bean.whitelist;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "WHITELIST".
*/
public class whitelistDao extends AbstractDao<whitelist, Long> {

    public static final String TABLENAME = "WHITELIST";

    /**
     * Properties of entity whitelist.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property OrganizationCode = new Property(1, String.class, "organizationCode", false, "ORGANIZATION_CODE");
        public final static Property PAN = new Property(2, String.class, "PAN", false, "PAN");
        public final static Property Description = new Property(3, String.class, "description", false, "DESCRIPTION");
    }


    public whitelistDao(DaoConfig config) {
        super(config);
    }
    
    public whitelistDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"WHITELIST\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"ORGANIZATION_CODE\" TEXT UNIQUE ," + // 1: organizationCode
                "\"PAN\" TEXT UNIQUE ," + // 2: PAN
                "\"DESCRIPTION\" TEXT);"); // 3: description
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"WHITELIST\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, whitelist entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String organizationCode = entity.getOrganizationCode();
        if (organizationCode != null) {
            stmt.bindString(2, organizationCode);
        }
 
        String PAN = entity.getPAN();
        if (PAN != null) {
            stmt.bindString(3, PAN);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(4, description);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, whitelist entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String organizationCode = entity.getOrganizationCode();
        if (organizationCode != null) {
            stmt.bindString(2, organizationCode);
        }
 
        String PAN = entity.getPAN();
        if (PAN != null) {
            stmt.bindString(3, PAN);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(4, description);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public whitelist readEntity(Cursor cursor, int offset) {
        whitelist entity = new whitelist( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // organizationCode
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // PAN
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // description
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, whitelist entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setOrganizationCode(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setPAN(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDescription(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(whitelist entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(whitelist entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(whitelist entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}

package com.szxb.buspay.db.entity.scan;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者: Tangren on 2017/9/1
 * 包名：szxb.com.commonbus.entity
 * 邮箱：996489865@qq.com
 * TODO:mac密钥表
 */
@Entity
public class MacKeyEntity {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String key_id;
    @Unique
    private String pubkey;
    private String time;
    @Generated(hash = 1214124432)
    public MacKeyEntity(Long id, String key_id, String pubkey, String time) {
        this.id = id;
        this.key_id = key_id;
        this.pubkey = pubkey;
        this.time = time;
    }
    @Generated(hash = 1027612983)
    public MacKeyEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getKey_id() {
        return this.key_id;
    }
    public void setKey_id(String key_id) {
        this.key_id = key_id;
    }
    public String getPubkey() {
        return this.pubkey;
    }
    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }

}

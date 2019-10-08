package com.szxb.buspay.db.entity.scan;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者: Tangren on 2017-09-08
 * 包名：szxb.com.commonbus.entity
 * 邮箱：996489865@qq.com
 * TODO:公钥表
 */
@Entity
public class PublicKeyEntity {
    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String key_id;
    @Unique
    private String pubkey;
    private String remark_1;
    private String remark_2;
    @Generated(hash = 1127101467)
    public PublicKeyEntity(Long id, String key_id, String pubkey, String remark_1,
            String remark_2) {
        this.id = id;
        this.key_id = key_id;
        this.pubkey = pubkey;
        this.remark_1 = remark_1;
        this.remark_2 = remark_2;
    }
    @Generated(hash = 145688750)
    public PublicKeyEntity() {
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
    public String getRemark_1() {
        return this.remark_1;
    }
    public void setRemark_1(String remark_1) {
        this.remark_1 = remark_1;
    }
    public String getRemark_2() {
        return this.remark_2;
    }
    public void setRemark_2(String remark_2) {
        this.remark_2 = remark_2;
    }


}

package com.szxb.buspay.db.entity.scan;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者: Tangren on 2017/7/18
 * 包名：com.szxb.buspay.entity
 * 邮箱：996489865@qq.com
 * TODO:黑名单数据表
 */
@Entity
public class BlackListEntity {
    @Id(autoincrement = true)
    private Long id;

    private String open_id;

    private long time;

    @Generated(hash = 1839420203)
    public BlackListEntity(Long id, String open_id, long time) {
        this.id = id;
        this.open_id = open_id;
        this.time = time;
    }

    @Generated(hash = 1391692307)
    public BlackListEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOpen_id() {
        return this.open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}

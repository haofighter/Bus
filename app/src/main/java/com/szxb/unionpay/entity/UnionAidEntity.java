package com.szxb.unionpay.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者：Tangren on 2018-07-07
 * 包名：com.szxb.unionpay.entity
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */
@Entity
public class UnionAidEntity {
    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String icParam;
    private String reserve;
    @Generated(hash = 807722692)
    public UnionAidEntity(Long id, String icParam, String reserve) {
        this.id = id;
        this.icParam = icParam;
        this.reserve = reserve;
    }
    @Generated(hash = 1818613999)
    public UnionAidEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getIcParam() {
        return this.icParam;
    }
    public void setIcParam(String icParam) {
        this.icParam = icParam;
    }
    public String getReserve() {
        return this.reserve;
    }
    public void setReserve(String reserve) {
        this.reserve = reserve;
    }
}

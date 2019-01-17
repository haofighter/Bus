package com.szxb.buspay.db.entity.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.db.entity.bean
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

@Entity
public class LINEGuideEntity {
    @Id(autoincrement = true)
    private Long id;

    String acnt;
    String routeno;
    String routeversion;
    String routevname;
    String fileName;
    @Generated(hash = 139818251)
    public LINEGuideEntity(Long id, String acnt, String routeno,
            String routeversion, String routevname, String fileName) {
        this.id = id;
        this.acnt = acnt;
        this.routeno = routeno;
        this.routeversion = routeversion;
        this.routevname = routevname;
        this.fileName = fileName;
    }
    @Generated(hash = 538097070)
    public LINEGuideEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getAcnt() {
        return this.acnt;
    }
    public void setAcnt(String acnt) {
        this.acnt = acnt;
    }
    public String getRouteno() {
        return this.routeno;
    }
    public void setRouteno(String routeno) {
        this.routeno = routeno;
    }
    public String getRouteversion() {
        return this.routeversion;
    }
    public void setRouteversion(String routeversion) {
        this.routeversion = routeversion;
    }
    public String getRoutevname() {
        return this.routevname;
    }
    public void setRoutevname(String routevname) {
        this.routevname = routevname;
    }
    public String getFileName() {
        return this.fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
   
}

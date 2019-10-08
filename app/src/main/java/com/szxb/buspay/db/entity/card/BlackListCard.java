package com.szxb.buspay.db.entity.card;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;


@Entity
public class BlackListCard {
    @Id(autoincrement = true)
    private Long id;

    @Index(unique = true)
    private String card_id;

    private String reserve_1;
    private String reserve_2;
    private String reserve_3;
    private String reserve_4;
    @Generated(hash = 1411962833)
    public BlackListCard(Long id, String card_id, String reserve_1,
            String reserve_2, String reserve_3, String reserve_4) {
        this.id = id;
        this.card_id = card_id;
        this.reserve_1 = reserve_1;
        this.reserve_2 = reserve_2;
        this.reserve_3 = reserve_3;
        this.reserve_4 = reserve_4;
    }
    @Generated(hash = 1656671282)
    public BlackListCard() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCard_id() {
        return this.card_id;
    }
    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }
    public String getReserve_1() {
        return this.reserve_1;
    }
    public void setReserve_1(String reserve_1) {
        this.reserve_1 = reserve_1;
    }
    public String getReserve_2() {
        return this.reserve_2;
    }
    public void setReserve_2(String reserve_2) {
        this.reserve_2 = reserve_2;
    }
    public String getReserve_3() {
        return this.reserve_3;
    }
    public void setReserve_3(String reserve_3) {
        this.reserve_3 = reserve_3;
    }
    public String getReserve_4() {
        return this.reserve_4;
    }
    public void setReserve_4(String reserve_4) {
        this.reserve_4 = reserve_4;
    }

}

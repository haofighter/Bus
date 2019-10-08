package com.szxb.buspay.util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.szxb.buspay.db.entity.bean.MainEntity;

import java.util.List;

/**
 * 作者: Tangren on 2017-12-11
 * 包名：szxb.com.commonbus.adapter
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class HomeParentAdapter extends BaseAdapter {

    public HomeParentAdapter(Context context, List<MainEntity> mList, RecyclerView mRecyclerView) {
        super(context, mList, mRecyclerView);
    }

    @Override
    protected void convert(BaseHolder holder, MainEntity t, int position) {
        holder.card_id.setText(t.getCard_id());
        holder.card_money.setVisibility(View.GONE);
        holder.pay_money.setVisibility(View.GONE);
        holder.time.setVisibility(View.GONE);
    }
}

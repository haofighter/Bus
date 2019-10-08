package com.szxb.buspay.util.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;

import com.szxb.buspay.R;
import com.szxb.buspay.db.entity.bean.MainEntity;

import java.util.List;

/**
 * 作者：Tangren on 2018-07-17
 * 包名：com.szxb.buspay.util.adapter
 * TODO:一句话描述
 */

public class RecordAdapter extends BaseAdapter {
    private Context context;

    public RecordAdapter(Context context, List<MainEntity> mList, RecyclerView mRecyclerView) {
        super(context, mList, mRecyclerView);
        this.context = context;
    }

    @Override
    protected void convert(BaseHolder holder, MainEntity t, int position) {
        if (position == 0) {
            holder.card_id.setTextSize(20);
            holder.card_id.setTextColor(context.getResources().getColor(R.color.colorBlack));

            holder.card_money.setTextSize(20);
            holder.card_money.setTextColor(context.getResources().getColor(R.color.colorBlack));

            holder.pay_money.setTextSize(20);
            holder.pay_money.setTextColor(context.getResources().getColor(R.color.colorBlack));

            holder.time.setTextSize(20);
            holder.time.setTextColor(context.getResources().getColor(R.color.colorBlack));

        }

        holder.card_id.setCompoundDrawablesWithIntrinsicBounds(
                position == 0 ? null : context.getResources().getDrawable(t.getStatus() == 1
                        ? R.mipmap.status_un
                        : R.mipmap.status_end),
                null,
                null,
                null);

        holder.card_money.setText(t.getCard_money());
        if (t.getType()==2){
         holder.card_id.setTextColor(Color.parseColor("#FFFFFF"));
        }else if (t.getType()==3){
            holder.card_id.setTextColor(Color.parseColor("#EEEE00"));
        }else {
            holder.card_id.setTextColor(Color.parseColor("#FFFFFF"));
        }
        holder.card_id.setText(t.getCard_id());
        holder.pay_money.setText(t.getPay_money());
        holder.time.setText(t.getTime());
    }
}

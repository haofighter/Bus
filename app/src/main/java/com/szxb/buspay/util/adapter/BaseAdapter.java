package com.szxb.buspay.util.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.szxb.buspay.R;
import com.szxb.buspay.db.entity.bean.MainEntity;

import java.util.List;


/**
 * 作者: Tangren on 2017-12-11
 * 包名：szxb.com.commonbus.adapter
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public abstract class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseHolder> {

    private List<MainEntity> mList;
    private SparseBooleanArray mBooleanArray;
    public int mLastCheckPosition = -1;
    public int position = 0;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager manager;
    private boolean suppSelect = false;

    public BaseAdapter(Context context, List<MainEntity> mList, RecyclerView mRecyclerView) {
        this.mList = mList;
        this.mRecyclerView = mRecyclerView;
        manager = new LinearLayoutManager(context);
        mBooleanArray = new SparseBooleanArray(mList.size());
    }

    public void refreshData(List<MainEntity> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    public void addData(List<MainEntity> list) {
        this.mList.addAll(0, list);
        notifyDataSetChanged();
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new BaseHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {
        if (!mBooleanArray.get(position)) {
            //未被选中
            holder.itemView.setBackgroundResource(R.color.colorToast);
        } else {
            holder.itemView.setBackgroundResource(R.color.yellow3);
        }
        convert(holder, mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public void setItemChecked(int position) {

        mBooleanArray.put(position, true);
        if (mLastCheckPosition > -1) {
            mBooleanArray.put(mLastCheckPosition, false);
            notifyItemChanged(mLastCheckPosition);
        }
        notifyDataSetChanged();
        mLastCheckPosition = position;
    }

    //得到请求的position
    public int getCurrentItemPosition() {
        return mLastCheckPosition;
    }

    //得到请求的position的Str
    public MainEntity getCurrentItemPositionStr() {
        return mList.get(getCurrentItemPosition());
    }

    //下一个
    public void downKey() {
        if (position == mList.size() - 1) {
            position = 0;
        } else {
            position++;
        }
        move();
    }

    //上一个
    public void upKey() {
        if (position == 0) {
            position = mList.size() - 1;
        } else {
            position--;
        }
        move();
    }

    private void move() {
        moveToPosition(position);
        setItemChecked(position);
        notifyDataSetChanged();
    }

    private void moveToPosition(int n) {
        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(n);
        }
    }

    protected abstract void convert(BaseHolder holder, MainEntity t, int position);

    class BaseHolder extends RecyclerView.ViewHolder {
        public TextView card_id;
        public TextView card_money;
        public TextView pay_money;
        public TextView time;

        public BaseHolder(View itemView) {
            super(itemView);
            card_id = (TextView) itemView.findViewById(R.id.card_id);
            card_money = (TextView) itemView.findViewById(R.id.card_money);
            pay_money = (TextView) itemView.findViewById(R.id.pay_money);
            time = (TextView) itemView.findViewById(R.id.time);
        }
    }
}

package com.szxb.buspay.util.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.szxb.buspay.db.entity.bean.LINEGuideEntity;
import com.szxb.buspay.db.entity.card.LineInfoEntity;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.util.AppUtil;
import com.szxb.buspay.util.update.BaseRequest;
import com.szxb.buspay.util.update.DownloadLineRequest;

import java.util.List;

/**
 * 作者：Tangren on 2018-07-17
 * 包名：com.szxb.buspay.util.adapter
 * TODO:一句话描述
 */

public class LineAdapter extends BaseLineAdapter {
    private Context context;

    public LineAdapter(Context context, List<LINEGuideEntity> mList, RecyclerView mRecyclerView) {
        super(context, mList, mRecyclerView);
        this.context = context;
    }

    @Override
    protected void convert(BaseHolder holder, LINEGuideEntity t, int position) {

        String company = t.getAcnt();
        for (int j = 0; j < 3 - t.getAcnt().length(); j++) {
            company = "0" + company;
        }

        String lineNum = t.getRouteno();
        for (int j = 0; j < 3 - t.getRouteno().length(); j++) {
            lineNum = "0" + lineNum;
        }

        holder.card_money.setText(company + lineNum + "     " + t.getRoutevname());
    }

    @Override
    public void select(LINEGuideEntity lineGuideEntity) {
        LineInfoEntity lineInfoEntity = DBManager.getLineInfoByFileName(lineGuideEntity.getAcnt() + "," + lineGuideEntity.getRouteno() + ".json");
        if (lineInfoEntity != null) {
            Log.i("设置线路", "不需要下载");
            DownloadLineRequest.setNowLine(lineInfoEntity);
        } else {
            Log.i("设置线路", "需要下载");
            List<BaseRequest> taskList = AppUtil.getDownloadAppointFileList(lineGuideEntity.getFileName(), null);
            AppUtil.run(taskList, null);
        }
    }
}

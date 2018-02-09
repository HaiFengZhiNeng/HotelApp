package com.fanfan.hotel.service.item;

import android.view.View;

import com.fanfan.hotel.R;
import com.fanfan.hotel.model.VoiceBean;
import com.fanfan.hotel.ui.recyclerview.tree.ViewHolder;
import com.fanfan.hotel.ui.recyclerview.tree.item.TreeItem;
import com.seabreeze.log.Print;

/**
 * Created by android on 2017/12/20.
 */

public class VoiceTextItem extends LocalChildItem<VoiceBean> {

    @Override
    public void onBindViewHolder(ViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.setText(R.id.tv_content, getData().getShowTitle());
    }

}

package com.fanfan.hotel.service.item;

import com.fanfan.hotel.R;
import com.fanfan.hotel.model.NavigationBean;
import com.fanfan.hotel.ui.recyclerview.tree.ViewHolder;
import com.fanfan.hotel.ui.recyclerview.tree.item.TreeItem;
import com.seabreeze.log.Print;

/**
 * Created by android on 2017/12/20.
 */

public class NavigationTextItem extends LocalChildItem<NavigationBean> {

    @Override
    public void onBindViewHolder(ViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.setText(R.id.tv_content, getData().getShowTitle());
    }

    protected void onShowToast() {
        Print.e(getData().getShowTitle());
        getData().getShowTitle();
    }
}

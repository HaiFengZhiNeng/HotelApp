package com.fanfan.hotel.service.item;

import android.view.View;
import android.widget.Toast;

import com.fanfan.hotel.R;
import com.fanfan.hotel.model.LocalBean;
import com.fanfan.hotel.ui.recyclerview.tree.ViewHolder;
import com.fanfan.hotel.ui.recyclerview.tree.factory.ItemHelperFactory;
import com.fanfan.hotel.ui.recyclerview.tree.item.TreeItem;
import com.fanfan.hotel.ui.recyclerview.tree.item.TreeItemGroup;

import java.util.List;

/**
 * Created by android on 2017/12/20.
 */

public class LocalGroupItem extends TreeItemGroup<LocalBean> {


    @Override
    protected List<TreeItem> initChildList(LocalBean data) {
        List<TreeItem> treeItemList = ItemHelperFactory.createTreeItemList(data.getSingleBeen(), this);
        return treeItemList;
    }

    @Override
    protected int initLayoutId() {
        return R.layout.item_sort_group;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder) {
        holder.setText(R.id.tv_content, data.getTitle());
        holder.setOnClickListener(R.id.group, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.showToast(data.getTitle());
            }
        });
    }
}

package com.fanfan.hotel.adapter;

import com.fanfan.hotel.common.base.BaseRecyclerAdapter;
import com.fanfan.hotel.ui.recyclerview.manager.ItemManageImpl;
import com.fanfan.hotel.ui.recyclerview.manager.ItemManager;
import com.fanfan.hotel.ui.recyclerview.tree.ViewHolder;
import com.fanfan.hotel.ui.recyclerview.tree.factory.ItemHelperFactory;
import com.fanfan.hotel.ui.recyclerview.tree.item.TreeItem;

import java.util.List;

public class TreeRecyclerAdapter extends BaseRecyclerAdapter<TreeItem> {

    private ItemManager<TreeItem> mItemManager;

    @Override
    public void setDatas(List<TreeItem> items) {
        if (null == items) {
            return;
        }
        getDatas().clear();
        assembleItems(items);
    }

    private void assembleItems(List<TreeItem> items) {
        List<TreeItem> datas = getDatas();
        datas.addAll(ItemHelperFactory.getChildItemsWithType(items));
    }

    public ItemManager<TreeItem> getItemManager() {
        if (mItemManager == null) {
            mItemManager = new ItemManageImpl<>(this);
        }
        return mItemManager;
    }

    public void setItemManager(ItemManager<TreeItem> itemManage) {
        this.mItemManager = itemManage;
    }

    @Override
    public int getLayoutId(int position) {
        return getDatas().get(position).getLayoutId();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TreeItem t = getDatas().get(position);
        checkItemManage(t);
        t.onBindViewHolder(holder);
    }

    private void checkItemManage(TreeItem item) {
        if (item.getItemManager() == null) {
            item.setItemManager(getItemManager());
        }
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, TreeItem item, int position) {

    }

}

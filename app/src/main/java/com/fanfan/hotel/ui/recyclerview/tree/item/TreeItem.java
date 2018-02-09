package com.fanfan.hotel.ui.recyclerview.tree.item;

import android.content.res.Resources;

import com.fanfan.hotel.ui.recyclerview.tree.ViewHolder;
import com.fanfan.hotel.ui.recyclerview.manager.ItemManager;

public abstract class TreeItem<D> {
    /**
     * 当前item的数据
     */
    protected D data;
    private TreeItemGroup parentItem;
    private ItemManager mItemManager;

    public void setParentItem(TreeItemGroup parentItem) {
        this.parentItem = parentItem;
    }

    public TreeItemGroup getParentItem() {
        return parentItem;
    }

    public ItemManager getItemManager() {
        return mItemManager;
    }

    public void setItemManager(ItemManager itemManager) {
        mItemManager = itemManager;
    }

    public int getLayoutId() {
        if (initLayoutId() <= 0) {
            throw new Resources.NotFoundException("请设置布局Id");
        }
        return initLayoutId();
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    protected abstract int initLayoutId();

    public abstract void onBindViewHolder(ViewHolder holder);

}

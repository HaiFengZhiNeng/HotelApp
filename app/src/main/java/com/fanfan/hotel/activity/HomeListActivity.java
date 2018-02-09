package com.fanfan.hotel.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.fanfan.hotel.adapter.HeaderFooterAdapter;
import com.fanfan.hotel.common.activity.SimpleRefreshRecyclerFragment;
import com.fanfan.hotel.model.RoomInfo;
import com.fanfan.hotel.presenter.SynthesizerPresenter;
import com.fanfan.hotel.presenter.ipresenter.ISynthesizerPresenter;
import com.fanfan.hotel.service.event.GetRoomInfoListEvent;
import com.fanfan.hotel.service.provider.RoomInfoProvider;

import java.util.List;

public class HomeListActivity extends SimpleRefreshRecyclerFragment<RoomInfo, GetRoomInfoListEvent>
        implements ISynthesizerPresenter.ITtsView {

    private boolean isFirstLaunch = true;

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, HomeListActivity.class);
        context.startActivity(intent);
    }

    private SynthesizerPresenter mTtsPresenter;

    private static final String speak0 = "欢迎查看房间列表";

    @Override
    protected void initData() {
        super.initData();
        setTitle("房间列表");
        mTtsPresenter = new SynthesizerPresenter(this);
        mTtsPresenter.start();
    }

    @Override
    public void initData(HeaderFooterAdapter adapter) {
        List<Object> rooms = mDataCache.getRoomInfoListObj();
        if (null != rooms && rooms.size() > 0) {
            pageIndex = mConfig.getRoomListPageIndex();
            adapter.addDatas(rooms);
            if (isFirstLaunch) {
                int lastPosition = mConfig.getRoomListLastPosition();
                mRecyclerView.getLayoutManager().scrollToPosition(lastPosition);
                isFirstAddFooter = false;
                isFirstLaunch = false;
            }
        } else {
            loadMore();
        }
    }

    @Override
    protected void setAdapterRegister(Context context, RecyclerView recyclerView, HeaderFooterAdapter adapter) {
        adapter.register(RoomInfo.class, new RoomInfoProvider(this));
    }

    @NonNull
    @Override
    protected String request(int offset, int limit) {
        return mOnlineData.getRoomList(null, offset, limit);
    }

    @Override
    protected void onRefresh(GetRoomInfoListEvent event, HeaderFooterAdapter adapter) {
        super.onRefresh(event, adapter);
        mDataCache.saveRoomInfoListObj(adapter.getDatas());
    }

    @Override
    protected void onLoadMore(GetRoomInfoListEvent event, HeaderFooterAdapter adapter) {
        super.onLoadMore(event, adapter);
        mDataCache.saveRoomInfoListObj(adapter.getDatas());
    }

    @Override
    protected void onResume() {
        super.onResume();
        addSpeakAnswer(speak0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTtsPresenter.stopTts();
        mTtsPresenter.stopHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTtsPresenter.finish();
        mConfig.saveRoomListPageIndex(pageIndex);
        View view = mRecyclerView.getLayoutManager().getChildAt(0);
        int lastPosition = mRecyclerView.getLayoutManager().getPosition(view);
        mConfig.saveRoomListPosition(lastPosition);
    }

    private void addSpeakAnswer(String messageContent) {
        mTtsPresenter.doAnswer(messageContent);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    @Override
    public void showMsg(int msg) {
        showToast(msg);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onRunable() {

    }
}

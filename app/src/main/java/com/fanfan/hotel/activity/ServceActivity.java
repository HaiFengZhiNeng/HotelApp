package com.fanfan.hotel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanfan.hotel.R;
import com.fanfan.hotel.adapter.TreeRecyclerAdapter;
import com.fanfan.hotel.common.ChatConst;
import com.fanfan.hotel.common.activity.BarBaseActivity;
import com.fanfan.hotel.common.app.HotelApp;
import com.fanfan.hotel.common.base.BaseRecyclerAdapter;
import com.fanfan.hotel.common.enums.SpecialType;
import com.fanfan.hotel.db.manager.NavigationDBManager;
import com.fanfan.hotel.db.manager.VideoDBManager;
import com.fanfan.hotel.db.manager.VoiceDBManager;
import com.fanfan.hotel.model.LocalBean;
import com.fanfan.hotel.model.NavigationBean;
import com.fanfan.hotel.model.SerialBean;
import com.fanfan.hotel.model.VideoBean;
import com.fanfan.hotel.model.VoiceBean;
import com.fanfan.hotel.presenter.LocalSoundPresenter;
import com.fanfan.hotel.presenter.SerialPresenter;
import com.fanfan.hotel.presenter.ipresenter.ILocalSoundPresenter;
import com.fanfan.hotel.presenter.ipresenter.ISerialPresenter;
import com.fanfan.hotel.service.SerialService;
import com.fanfan.hotel.service.animator.SlideInOutBottomItemAnimator;
import com.fanfan.hotel.service.event.ChildItemEvent;
import com.fanfan.hotel.service.event.ReceiveEvent;
import com.fanfan.hotel.service.event.ServiceToActivityEvent;
import com.fanfan.hotel.service.item.LocalGroupItem;
import com.fanfan.hotel.service.item.NavigationTextItem;
import com.fanfan.hotel.service.item.VideoTextItem;
import com.fanfan.hotel.service.item.VoiceTextItem;
import com.fanfan.hotel.service.udp.SocketManager;
import com.fanfan.hotel.ui.ChatTextView;
import com.fanfan.hotel.ui.recyclerview.tree.ViewHolder;
import com.fanfan.hotel.ui.recyclerview.tree.base.BaseItemData;
import com.fanfan.hotel.ui.recyclerview.tree.factory.ItemConfig;
import com.fanfan.hotel.ui.recyclerview.tree.factory.ItemHelperFactory;
import com.fanfan.hotel.ui.recyclerview.tree.item.TreeItem;
import com.iflytek.cloud.SpeechConstant;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ServceActivity extends BarBaseActivity implements ILocalSoundPresenter.ILocalSoundView, ISerialPresenter.ISerialView {


    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.chat_content)
    ChatTextView chatContent;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.show_image)
    ImageView showImage;
    @BindView(R.id.rl_show_image)
    RelativeLayout rlShowImage;

    private TreeRecyclerAdapter mTreeRecyclerAdapter;
    private List<TreeItem> mTreeItems;

    private LocalSoundPresenter mSoundPresenter;
    private SerialPresenter mSerialPresenter;

    private VoiceDBManager mVoiceDBManager;
    private VideoDBManager mVideoDBManager;
    private NavigationDBManager mNavigationDBManager;

    private boolean isShowing;

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, ServceActivity.class);
        context.startActivity(intent);
    }

    private static final String[] LETTERS = new String[]{"语音介绍", "视频介绍", "导航"};

    @Override
    protected int getLayoutId() {
        return R.layout.activity_servce;
    }

    @Override
    protected void initView() {
        super.initView();
        hideImage();
        Glide.with(this).load(R.mipmap.grzx_tx_s).diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivIcon);
        chatContent.setSpanText(mHandler, getResources().getString(R.string.welcome), true);

        mSoundPresenter = new LocalSoundPresenter(this);
        mSoundPresenter.start();
        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();

        ItemConfig.addTreeHolderType(ChatConst.LOCAL_NOMAL, LocalGroupItem.class);
        ItemConfig.addTreeHolderType(ChatConst.LOCAL_VOICE, VoiceTextItem.class);
        ItemConfig.addTreeHolderType(ChatConst.LOCAL_VIDEO, VideoTextItem.class);
        ItemConfig.addTreeHolderType(ChatConst.LOCAL_NAVIGATION, NavigationTextItem.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new SlideInOutBottomItemAnimator(recyclerView));
        mTreeRecyclerAdapter = new TreeRecyclerAdapter();
        recyclerView.setAdapter(mTreeRecyclerAdapter);

    }

    @Override
    protected void initData() {
        mVoiceDBManager = new VoiceDBManager();
        mVideoDBManager = new VideoDBManager();
        mNavigationDBManager = new NavigationDBManager();

        List<VoiceBean> voiceBeans = mVoiceDBManager.loadAll();
        List<VideoBean> videoBeans = mVideoDBManager.loadAll();
        List<NavigationBean> navigationBeans = mNavigationDBManager.loadAll();

        List<BaseItemData> localBeanList = loadData(voiceBeans, videoBeans, navigationBeans);

        mTreeItems = ItemHelperFactory.createTreeItemList(localBeanList, null);
        mTreeRecyclerAdapter.setDatas(mTreeItems);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isShowing) {
                    hideImage();
                } else {
                    finish();
                }
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (isShowing) {
            hideImage();
            return;
        }
        super.onBackPressed();
    }

    @NonNull
    private List<BaseItemData> loadData(List<VoiceBean> voiceBeans, List<VideoBean> videoBeans, List<NavigationBean> navigationBeans) {
        List<BaseItemData> localBeanList = new ArrayList<>();
        LocalBean localVoice = new LocalBean();
        localVoice.setTitle(LETTERS[0]);
        localVoice.setViewItemType(ChatConst.LOCAL_NOMAL);
        for (VoiceBean voiceBean : voiceBeans) {
            voiceBean.setViewItemType(voiceBean.getLocalType());
        }
        localVoice.setSingleBeen(voiceBeans);
        localBeanList.add(localVoice);

        LocalBean localVideo = new LocalBean();
        localVideo.setTitle(LETTERS[1]);
        localVideo.setViewItemType(ChatConst.LOCAL_NOMAL);
        for (VideoBean videoBean : videoBeans) {
            videoBean.setViewItemType(videoBean.getLocalType());
        }
        localVideo.setSingleBeen(videoBeans);
        localBeanList.add(localVideo);

        LocalBean localnavigation = new LocalBean();
        localnavigation.setTitle(LETTERS[2]);
        localnavigation.setViewItemType(ChatConst.LOCAL_NOMAL);
        for (NavigationBean navigationBean : navigationBeans) {
            navigationBean.setViewItemType(navigationBean.getLocalType());
        }
        localnavigation.setSingleBeen(navigationBeans);
        localBeanList.add(localnavigation);
        return localBeanList;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClickEvent(ChildItemEvent event) {
        BaseItemData itemData = event.getBean();
        judgeItem(itemData);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(ServiceToActivityEvent event) {
        if (event.isOk()) {
            SerialBean serialBean = event.getBean();
            mSerialPresenter.onDataReceiverd(serialBean);
        } else {
            Print.e("ReceiveEvent error");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(ReceiveEvent event) {
        if (event.isOk()) {
            DatagramPacket packet = event.getBean();
            if (!SocketManager.getInstance().isGetTcpIp) {
                SocketManager.getInstance().setUdpIp(packet.getAddress().getHostAddress(), packet.getPort());
            }
            String recvStr = new String(packet.getData(), 0, packet.getLength());
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, recvStr);
            Print.e(recvStr);
        } else {
            Print.e("ReceiveEvent error");
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mSoundPresenter.startRecognizerListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        HotelApp.getInstance().setEngineType(SpeechConstant.TYPE_LOCAL);
        mSoundPresenter.buildTts();
        mSoundPresenter.buildIat();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSoundPresenter.stopTts();
        mSoundPresenter.stopRecognizerListener();
        mSoundPresenter.stopHandler();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSoundPresenter.finish();
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

    private String text;

    @Override
    public void refLocalPage(String result) {
        List<VoiceBean> voiceBeans = mVoiceDBManager.queryLikeVoiceByQuestion(result);
        List<VideoBean> videoBeans = mVideoDBManager.queryLikeVideoByQuestion(result);
        List<NavigationBean> navigationBeans = mNavigationDBManager.queryLikeNavigationByQuestion(result);
        List<BaseItemData> itemDataList = new ArrayList<>();
        itemDataList.addAll(voiceBeans);
        itemDataList.addAll(videoBeans);
        itemDataList.addAll(navigationBeans);

        if (itemDataList != null && itemDataList.size() > 0) {
            if (itemDataList.size() == 1) {
                BaseItemData itemData = itemDataList.get(new Random().nextInt(itemDataList.size()));
                judgeItem(itemData);
            } else {
                BaseItemData itemData = itemDataList.get(new Random().nextInt(itemDataList.size()));
                judgeItem(itemData);
            }
        } else {
            if (new Random().nextBoolean()) {
                text = resFoFinal(R.array.no_result);
            } else {
                text = resFoFinal(R.array.no_voice);
            }
        }
        addSpeakAnswer(text);
    }

    @Override
    public void spakeMove(SpecialType type, String result) {
        mSoundPresenter.onCompleted();
        switch (type) {
            case Forward:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038002AA");
                break;
            case Backoff:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038008AA");
                break;
            case Turnleft:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038004AA");
                break;
            case Turnright:
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, "A5038006AA");
                break;
        }
    }

    @Override
    public void back() {
        if (isShowing) {
            hideImage();
        } else {
            finish();
        }
    }

    private void judgeItem(BaseItemData itemData) {
        if (itemData instanceof VoiceBean) {
            refVoice((VoiceBean) itemData);
        } else if (itemData instanceof VideoBean) {
            refVideo((VideoBean) itemData);
        } else if (itemData instanceof NavigationBean) {
            refNavigation((NavigationBean) itemData);
        }
        addSpeakAnswer(text);
    }

    private void refVoice(VoiceBean itemData) {
        VoiceBean voiceBean = itemData;
        text = voiceBean.getVoiceAnswer();

        if (voiceBean.getActionData() != null) {
            if (voiceBean.getActionData().equals("A50C80E1AA")) {
//                    DanceUtils.getInstance().startDance(MainActivity.this);
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, voiceBean.getActionData());
                return;
            } else if (voiceBean.getActionData().equals("A50C80E2AA")) {
//                    DanceUtils.getInstance().stopDance();
            }
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, voiceBean.getActionData());
        }
        if (voiceBean.getExpressionData() != null) {
            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, voiceBean.getExpressionData());
        }
        if (voiceBean.getImgUrl() != null) {
            if (new File(voiceBean.getImgUrl()).exists()) {
                showImage(voiceBean.getShowTitle(), voiceBean.getImgUrl());
            }
        }
    }

    private void refVideo(VideoBean itemData) {
        VideoBean videoBean = itemData;
        if (videoBean.getVideoUrl() != null) {
            VideoDetailActivity.newInstance(this, videoBean.getVideoUrl());
            return;
        } else {
            text = "找不到视频链接";
        }
    }

    private void refNavigation(NavigationBean itemData) {
        NavigationBean navigationBean = itemData;
        text = navigationBean.getNavigation();
        if (navigationBean.getImgUrl() != null) {
            if (new File(navigationBean.getImgUrl()).exists()) {
                showImage(navigationBean.getShowTitle(), navigationBean.getImgUrl());
            }
        }
    }

    private void showImage(String title, String imageUrl) {
        isShowing = true;
        setTitle(title);
        rlShowImage.setVisibility(View.VISIBLE);
        Glide.with(this).load(imageUrl)
                .error(R.mipmap.test_image)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(showImage);
    }

    private void hideImage() {
        isShowing = false;
        setTitle("服务");
        rlShowImage.setVisibility(View.GONE);
    }

    private void addSpeakAnswer(String messageContent) {
        mSoundPresenter.doAnswer(messageContent);
        chatContent.setSpanText(mHandler, messageContent, true);
    }

    @Override
    public void stopAll() {

    }

    @Override
    public void onMoveStop() {

    }
}
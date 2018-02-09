package com.fanfan.hotel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanfan.hotel.R;
import com.fanfan.hotel.adapter.ChatRecyclerAdapter;
import com.fanfan.hotel.common.Constants;
import com.fanfan.hotel.common.UserManage;
import com.fanfan.hotel.common.activity.BarBaseActivity;
import com.fanfan.hotel.common.ChatConst;
import com.fanfan.hotel.common.app.HotelApp;
import com.fanfan.hotel.common.enums.RobotType;
import com.fanfan.hotel.common.enums.SpecialType;
import com.fanfan.hotel.db.manager.NavigationDBManager;
import com.fanfan.hotel.db.manager.VideoDBManager;
import com.fanfan.hotel.db.manager.VoiceDBManager;
import com.fanfan.hotel.im.init.LoginBusiness;
import com.fanfan.hotel.model.ChatMessageBean;
import com.fanfan.hotel.model.NavigationBean;
import com.fanfan.hotel.model.RobotBean;
import com.fanfan.hotel.model.SerialBean;
import com.fanfan.hotel.model.VideoBean;
import com.fanfan.hotel.model.VoiceBean;
import com.fanfan.hotel.model.xf.service.Cookbook;
import com.fanfan.hotel.model.xf.service.News;
import com.fanfan.hotel.model.xf.service.Poetry;
import com.fanfan.hotel.model.xf.service.englishEveryday.EnglishEveryday;
import com.fanfan.hotel.model.xf.service.radio.Radio;
import com.fanfan.hotel.presenter.ChatPresenter;
import com.fanfan.hotel.presenter.LineSoundPresenter;
import com.fanfan.hotel.presenter.SerialPresenter;
import com.fanfan.hotel.presenter.SynthesizerPresenter;
import com.fanfan.hotel.presenter.ipresenter.IChatPresenter;
import com.fanfan.hotel.presenter.ipresenter.ILineSoundPresenter;
import com.fanfan.hotel.presenter.ipresenter.ISerialPresenter;
import com.fanfan.hotel.presenter.ipresenter.ISynthesizerPresenter;
import com.fanfan.hotel.service.SerialService;
import com.fanfan.hotel.service.UdpService;
import com.fanfan.hotel.service.animator.SlideInOutBottomItemAnimator;
import com.fanfan.hotel.service.event.ReceiveEvent;
import com.fanfan.hotel.service.event.ServiceToActivityEvent;
import com.fanfan.hotel.service.udp.SocketManager;
import com.fanfan.hotel.ui.manager.WrapContentLinearLayoutManager;
import com.fanfan.hotel.utils.FileUtil;
import com.fanfan.hotel.utils.FucUtil;
import com.fanfan.hotel.utils.PhoneUtil;
import com.fanfan.hotel.utils.PreferencesUtils;
import com.fanfan.youtu.utils.GsonUtil;
import com.iflytek.cloud.SpeechConstant;
import com.seabreeze.log.Print;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversationType;
import com.tencent.TIMMessage;
import com.tencent.callsdk.ILVCallManager;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BarBaseActivity implements ILineSoundPresenter.ILineSoundView,
        IChatPresenter.IChatView, ISerialPresenter.ISerialView, ISynthesizerPresenter.ITtsView {

    @BindView(R.id.tv_check_in)
    RelativeLayout tvCheckIn;
    @BindView(R.id.tv_check_out)
    RelativeLayout tvCheckOut;
    @BindView(R.id.tv_service)
    RelativeLayout tvService;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.trcycler_title)
    TextView trcyclerTitle;

    private ChatRecyclerAdapter mChatRecyclerAdapter;
    private List<ChatMessageBean> cmbList;

    private SynthesizerPresenter mTtsPresenter;
    private LineSoundPresenter mSoundPresenter;
    private ChatPresenter mChatPresenter;
    private SerialPresenter mSerialPresenter;

    private boolean quit = false; //设置退出标识

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();

        mTtsPresenter = new SynthesizerPresenter(this);
        mTtsPresenter.start();

        mSoundPresenter = new LineSoundPresenter(this);
        mSoundPresenter.start();

        mChatPresenter = new ChatPresenter(this, TIMConversationType.C2C, UserManage.getInstance().getControlName());
        mChatPresenter.start();

        mSerialPresenter = new SerialPresenter(this);
        mSerialPresenter.start();
    }

    @Override
    protected void initData() {
        cmbList = new ArrayList<>();
        mChatRecyclerAdapter = new ChatRecyclerAdapter(this, cmbList);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new SlideInOutBottomItemAnimator(recyclerView));
        recyclerView.setAdapter(mChatRecyclerAdapter);

        if (!PreferencesUtils.getBoolean(this, "firstInsert", false)) {
            PreferencesUtils.putBoolean(this, "firstInsert", true);
            VoiceDBManager voiceDBManager = new VoiceDBManager();
            String[] arrVoiceQuestion = FucUtil.resArray(this, R.array.local_voice_question);
            String[] arrVoiceAnswer = FucUtil.resArray(this, R.array.local_voice_answer);
            String[] expression = FucUtil.resArray(this, R.array.expression);
            String[] expressionData = FucUtil.resArray(this, R.array.expression_data);
            String[] action = FucUtil.resArray(this, R.array.action);
            String[] actionOrder = FucUtil.resArray(this, R.array.action_order);
            for (int i = 0; i < arrVoiceQuestion.length; i++) {
                VoiceBean voiceBean = new VoiceBean();
                voiceBean.setShowTitle(arrVoiceQuestion[i]);
                voiceBean.setSaveTime(System.currentTimeMillis());
                voiceBean.setLocalType(ChatConst.LOCAL_VOICE);
                voiceBean.setVoiceAnswer(arrVoiceAnswer[i]);
                voiceBean.setImgUrl("voice_text");
                int ia = resFoInter(action);
                int ie = resFoInter(expression);
                voiceBean.setAction(action[ia]);
                voiceBean.setExpression(expression[ie]);
                voiceBean.setActionData(actionOrder[ia]);
                voiceBean.setExpressionData(expressionData[ie]);
                voiceDBManager.insert(voiceBean);
            }

            VideoDBManager videoDBManager = new VideoDBManager();
            String[] arrVideoQuestion = FucUtil.resArray(this, R.array.local_video_question);
            String[] arrVideoAnswer = FucUtil.resArray(this, R.array.local_video_answer);
            for (int i = 0; i < arrVideoQuestion.length; i++) {
                VideoBean voiceBean = new VideoBean();
                voiceBean.setShowTitle(arrVideoQuestion[i]);
                voiceBean.setSaveTime(System.currentTimeMillis());
                voiceBean.setLocalType(ChatConst.LOCAL_VIDEO);
                voiceBean.setVideoName(arrVideoAnswer[i]);
                voiceBean.setSize(-1);
                voiceBean.setVideoUrl(null);
                voiceBean.setVideoImage(null);
                videoDBManager.insert(voiceBean);
            }
            NavigationDBManager navigationDBManager = new NavigationDBManager();
            String[] arrNavigationQuestion = FucUtil.resArray(this, R.array.local_navigation_question);
            String[] arrNavigationAnswer = FucUtil.resArray(this, R.array.local_navigation_answer);
            for (int i = 0; i < arrNavigationQuestion.length; i++) {
                NavigationBean navigationBean = new NavigationBean();
                navigationBean.setShowTitle(arrNavigationQuestion[i]);
                navigationBean.setSaveTime(System.currentTimeMillis());
                navigationBean.setLocalType(ChatConst.LOCAL_NAVIGATION);
                navigationBean.setNavigation(arrNavigationAnswer[i]);
                navigationBean.setImgUrl(null);
                navigationBean.setNavigationData(null);
                navigationDBManager.insert(navigationBean);
            }
        }
    }

    @Override
    protected void callStop() {
        super.callStop();
        stopAll();
    }

    @OnClick({R.id.trcycler_title, R.id.tv_check_in, R.id.tv_check_out, R.id.tv_service, R.id.recycler_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.trcycler_title:
                stopAll();
                break;
            case R.id.tv_check_in:
                HomeListActivity.newInstance(this);
                break;
            case R.id.tv_check_out:
                AuthenticationActivity.newInstance(this);
                break;
            case R.id.tv_service:
                ServceActivity.newInstance(this);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HotelApp.getInstance().setEngineType(SpeechConstant.TYPE_CLOUD);
        mTtsPresenter.buildTts();
        mSoundPresenter.buildIat();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTtsPresenter.stopTts();
        mTtsPresenter.stopHandler();
        mSoundPresenter.stopRecognizerListener();
        mSoundPresenter.stopVoice();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, UdpService.class));
        stopService(new Intent(this, SerialService.class));
//        ILVCallManager.getInstance().removeIncomingListener(this);
//        ILVCallManager.getInstance().removeCallListener(this);
        super.onDestroy();
        mTtsPresenter.finish();
        mSoundPresenter.finish();
        mChatPresenter.finish();
    }

    @Override
    public void onBackPressed() {
        if (!quit) { //询问退出程序
            showMsg("再按一次退出程序");
            new Timer(true).schedule(new TimerTask() { //启动定时任务
                @Override
                public void run() {
                    quit = false; //重置退出标识
                }
            }, 2000);
            quit = true;
        } else { //确认退出程序
            super.onBackPressed();
            finish();
            //退出时杀掉所有进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onResultEvent(ReceiveEvent event) {
        if (event.isOk()) {
            DatagramPacket packet = event.getBean();
            if (!SocketManager.getInstance().isGetTcpIp) {
                SocketManager.getInstance().setUdpIp(packet.getAddress().getHostAddress(), packet.getPort());
            }
            String recvStr = new String(packet.getData(), 0, packet.getLength());
            if (recvStr.contains("udp")) {
                addfromInitinfo(recvStr);
            } else {
                mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, recvStr);
            }
            Print.e(recvStr);
        } else {
            Print.e("ReceiveEvent error");
        }
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
    public void doAiuiAnwer(String anwer) {
        addSpeakAnswer(anwer);
    }

    @Override
    public void refHomePage(String question) {
        if (question != null && !question.equals("")) {
            mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.TO_USER_MSG, question));
        }
        smoothScroll();
    }

    @Override
    public void refHomePage(String question, String finalText) {
        if (question != null && !question.equals("")) {
            mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.TO_USER_MSG, question));
        }
        if (finalText != null) {
            mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, finalText));
        }
        smoothScroll();
    }

    @Override
    public void refHomePage(String question, String finalText, String url) {
        if (url != null) {
            mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.TO_USER_IMG, url));
        }
        refHomePage(question, finalText);
    }

    @Override
    public void refHomePage(String question, News news) {
        if (question != null) {
            mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.TO_USER_MSG, question));
        }
        mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, news.getTitle()));
        mChatRecyclerAdapter.addItem(getChatMessageImage(ChatRecyclerAdapter.FROM_USER_IMG, news.getImgUrl()));
        if (isEmpty(news.getContent())) {
            mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, news.getContent()));
        }
        smoothScroll();
    }

    @Override
    public void refHomePage(String question, Radio radio) {
        if (question != null) {
            mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.TO_USER_MSG, question));
        }
        mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, radio.getName()));
        mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, radio.getDescription()));
        mChatRecyclerAdapter.addItem(getChatMessageImage(ChatRecyclerAdapter.FROM_USER_IMG, radio.getImg()));
        smoothScroll();
    }

    @Override
    public void refHomePage(String question, Poetry poetry) {
        if (question != null) {
            mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.TO_USER_MSG, question));
        }
        mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, poetry.getTitle()));
        mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, poetry.getAuthor()));
        mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, poetry.getContent()));
        smoothScroll();
    }

    @Override
    public void refHomePage(String question, Cookbook cookbook) {
        if (question != null) {
            mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.TO_USER_MSG, question));
        }
        mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, cookbook.getTitle()));
        mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, cookbook.getSteps()));
        mChatRecyclerAdapter.addItem(getChatMessageImage(ChatRecyclerAdapter.FROM_USER_IMG, cookbook.getImgUrl()));
        smoothScroll();
    }

    @Override
    public void refHomePage(String question, EnglishEveryday englishEveryday) {
        if (question != null) {
            mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.TO_USER_MSG, question));
        }
        mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, englishEveryday.getTranslation()));
        mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, englishEveryday.getContent()));
        mChatRecyclerAdapter.addItem(getChatMessageImage(ChatRecyclerAdapter.FROM_USER_IMG, englishEveryday.getImgUrl()));
        smoothScroll();
    }

    @Override
    public void special(String result, SpecialType type) {
        mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.TO_USER_MSG, result));
        smoothScroll();
        switch (type) {
            case Story:
                String[] arrStory = getResources().getStringArray(R.array.local_story);
                String finalStory = arrStory[new Random().nextInt(arrStory.length)];
                mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, finalStory));
                smoothScroll();
                addSpeakAnswer(finalStory);
                break;
            case Music:
//                mMusicPresenter.playMusic();
                addSpeakAnswer("暂未添加");
                break;
            case Joke:
                String[] arrJoke = getResources().getStringArray(R.array.local_joke);
                String finalJoke = arrJoke[new Random().nextInt(arrJoke.length)];
                mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, finalJoke));
                smoothScroll();
                addSpeakAnswer(finalJoke);
                break;
        }
    }

    @Override
    public void doCallPhone(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void startPage(SpecialType specialType) {
        switch (specialType) {
            case CheckIn:
                HomeListActivity.newInstance(this);
                break;
            case CheckOut:
                AuthenticationActivity.newInstance(this);
                break;
            case Servce:
                ServceActivity.newInstance(this);
                break;
        }
    }

    @Override
    public void spakeMove(SpecialType type, String result) {
        mChatRecyclerAdapter.addItem(getChatMessage(ChatRecyclerAdapter.TO_USER_MSG, result));
        smoothScroll();
        mTtsPresenter.onCompleted();
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
    public void spakeLogout() {
        LoginBusiness.logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                showMsg("退出登录失败，请稍后重试");
            }

            @Override
            public void onSuccess() {
//                liveLogout();
                logout();
            }
        });
    }

    @Override
    public void addfromInitinfo(String msg) {
        ChatMessageBean smb = getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, msg);
        cmbList.add(smb);
        mChatRecyclerAdapter.notifyDataSetChanged();
        smoothScroll();
    }

    @Override
    public void onCompleted() {
        mTtsPresenter.onCompleted();
    }

    @Override
    public void initIatFinish() {
        if (mChatRecyclerAdapter.getDatas().size() == 0) {
            addSpeakAnswer(getResources().getString(R.string.welcome));
            refHomePage("", getResources().getString(R.string.welcome));
        }
    }

    private void liveLogout() {
        ILiveLoginManager.getInstance().iLiveLogout(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                logout();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                showMsg("退出登录失败，请稍后重试");
            }
        });
    }

    //**********************************************************************************************
    private void addSpeakAnswer(String messageContent) {
        mTtsPresenter.doAnswer(messageContent);
    }

    private void adapterEmpty() {
        mChatRecyclerAdapter.clear();
    }

    private void smoothScroll() {
        if (cmbList != null && cmbList.size() > 0) {
            recyclerView.smoothScrollToPosition(cmbList.size() - 1);
        }
    }

    //**********************************************************************************************

    public ChatMessageBean getChatMessage(int messagetype, String messageContent) {
        ChatMessageBean chatMessageBean = new ChatMessageBean();
        chatMessageBean.setMessagetype(messagetype);
        chatMessageBean.setMessageContent(messageContent);
        chatMessageBean.setTime(returnTime());
        chatMessageBean.setSendState(ChatConst.COMPLETED);
//        mChatDbManager.insert(chatMessageBean);
        return chatMessageBean;
    }

    public ChatMessageBean getChatMessageImage(int messagetype, String imageUrl) {
        ChatMessageBean chatMessageBean = new ChatMessageBean();
        chatMessageBean.setMessagetype(messagetype);
        chatMessageBean.setImageUrl(imageUrl);
        chatMessageBean.setTime(returnTime());
        chatMessageBean.setSendState(ChatConst.COMPLETED);
//        mChatDbManager.insert(chatMessageBean);
        return chatMessageBean;
    }


    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    private boolean isEmpty(String content) {
        if (content != null && content.length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSendMessageSuccess(TIMMessage message) {

    }

    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {

    }

    @Override
    public void parseMsgcomplete(String str) {
        addfromInitinfo(str);
        addSpeakAnswer(str);
    }

    @Override
    public void parseCustomMsgcomplete(String customMsg) {
        RobotBean bean = GsonUtil.GsonToBean(customMsg, RobotBean.class);
        if (bean == null || bean.getType().equals("") || bean.getOrder().equals("")) {
            return;
        }
        RobotType robotType = bean.getType();
        //公共
        if (robotType == RobotType.VoiceSwitch) {

            ChatMessageBean smb = getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, bean.getOrder());
            cmbList.add(smb);
            mChatRecyclerAdapter.notifyDataSetChanged();
            boolean isSpeech = bean.getOrder().equals("语音开");
            mSoundPresenter.setSpeech(isSpeech);

        } else if (robotType == RobotType.SmartChat) {//发音人

//            mSoundPresenter.setSpokesman(bean.getOrder());

        } else if (robotType == RobotType.AutoAction) {

//            mSoundPresenter.autoAction();

        } else if (robotType == RobotType.Motion) {

            mSerialPresenter.receiveMotion(SerialService.DEV_BAUDRATE, bean.getOrder());

        } else if (robotType == RobotType.GETIP) {
            Constants.CONNECT_IP = bean.getOrder();
            if (Constants.IP != null && Constants.PORT > 0) {
                try {
                    JSONObject object = new JSONObject();
                    object.put("robotIp", Constants.IP);
                    object.put("robotPort", Constants.PORT);
                    RobotBean robotBean = new RobotBean();
                    robotBean.setOrder(object.toString());
                    robotBean.setType(RobotType.GETIP);
                    Print.e("发送: " + object.toString());
                    mChatPresenter.sendCustomMessage(robotBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    JSONObject object = new JSONObject();
                    object.put("robotIp", "");
                    object.put("robotPort", Constants.PORT);
                    RobotBean robotBean = new RobotBean();
                    robotBean.setOrder(object.toString());
                    robotBean.setType(RobotType.GETIP);
                    Print.e("发送: " + object.toString());
                    mChatPresenter.sendCustomMessage(robotBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (robotType == RobotType.LocalVoice) {
//            List<VoiceBean> voiceBeanList = mVoiceDBManager.loadAll();
//            List<String> anwers = new ArrayList<>();
//            if (voiceBeanList != null && voiceBeanList.size() > 0) {
//                for (VoiceBean voiceBean : voiceBeanList) {
//                    anwers.add(voiceBean.getShowTitle());
//                }
//                String voiceJson = GsonUtil.GsonString(anwers);
//                RobotBean localVoice = new RobotBean();
//                localVoice.setType(RobotType.LocalVoice);
//                localVoice.setOrder(voiceJson);
//                mChatPresenter.sendCustomMessage(localVoice);
//
//            }
        }
        //判断界面
        if (robotType == RobotType.Text) {
            ChatMessageBean smb = getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, bean.getOrder());
            cmbList.add(smb);
            mChatRecyclerAdapter.notifyDataSetChanged();
            addSpeakAnswer(smb.getMessageContent());
        } else if (robotType == RobotType.AutoAction) {
            ChatMessageBean smb = getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, bean.getOrder());
            cmbList.add(smb);
            mChatRecyclerAdapter.notifyDataSetChanged();
        } else if (robotType == RobotType.SmartChat) {
            ChatMessageBean smb = getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, "发言人变更为 " + bean.getOrder());
            cmbList.add(smb);
            mChatRecyclerAdapter.notifyDataSetChanged();
        } else if (robotType == RobotType.GETIP) {
            addfromInitinfo("IP : " + Constants.CONNECT_IP + " 连接");
        } else if (robotType == RobotType.Anwser) {
            ChatMessageBean smb = getChatMessage(ChatRecyclerAdapter.FROM_USER_MSG, bean.getOrder());
            cmbList.add(smb);
            mChatRecyclerAdapter.notifyDataSetChanged();
            addSpeakAnswer(smb.getMessageContent());
        }
        smoothScroll();
    }

    @Override
    public void stopAll() {
        mSoundPresenter.stopVoice();
        mTtsPresenter.stopAll();
    }

    @Override
    public void onMoveStop() {

    }

    @Override
    public void onSpeakBegin() {
        mSoundPresenter.stopRecognizerListener();
    }

    @Override
    public void onRunable() {
        mSoundPresenter.startRecognizerListener();
    }
}

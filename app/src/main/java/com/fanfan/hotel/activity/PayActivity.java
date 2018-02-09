package com.fanfan.hotel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fanfan.hotel.R;
import com.fanfan.hotel.common.InfoManage;
import com.fanfan.hotel.common.activity.BarBaseActivity;
import com.fanfan.hotel.model.PersonInfo;
import com.fanfan.hotel.model.RoomInfo;
import com.fanfan.hotel.utils.BitmapUtils;
import com.fanfan.hotel.utils.PreferencesUtils;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.face.bean.Newperson;
import com.fanfan.youtu.api.face.event.NewPersonEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class PayActivity extends BarBaseActivity {

    public static final int PAY_TO_INFO_REQUESTCODE = 103;
    public static final int PAY_TO_INFO_RESULTCODE = 203;

    public static void newInstance(Activity context, int requestCode) {
        Intent intent = new Intent(context, PayActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    private Youtucode youtucode;

    private RoomInfo roomInfo;
    private PersonInfo personInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("支付");

        youtucode = Youtucode.getSingleInstance();

        roomInfo = InfoManage.getInstance().getRoomInfo();
        personInfo = InfoManage.getInstance().getPersonInfo();
    }


    @Override
    protected void initData() {
        showToast("10s后跳转支付完成页面,模拟支付完成");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String personId = personInfo.getIDCard();
                String headUrl = personInfo.getHeadUrl();
                if ((new File(headUrl)).exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(headUrl);
                    youtucode.newPerson(bitmap, personId, personInfo.getName());
                } else {
                    Print.e("文件缺失");
                }

            }
        }, 10000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAY_TO_INFO_REQUESTCODE) {
            if (resultCode == PAY_TO_INFO_RESULTCODE) {
                setResult(ConfirmActivity.CONFIRM_TO_PAY_RESULTCODE);
                finish();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(NewPersonEvent event) {
        if (event.isOk()) {
            Newperson newperson = event.getBean();
            Print.e(newperson);
            if (newperson.getErrorcode() == 0) {

                String person_id = newperson.getPerson_id();
                PreferencesUtils.putInt(this, person_id, roomInfo.getId());
                InfoCompleteActivity.newInstance(PayActivity.this, PAY_TO_INFO_REQUESTCODE, InfoCompleteActivity.CHECK_IN);
            } else {
                Print.e("onError : " + newperson.getErrorcode() + "  " + newperson.getErrormsg());
            }
        } else {
            Print.e("onError : " + event.getCode() + "  " + event.getCodeDescribe());
        }
    }
}

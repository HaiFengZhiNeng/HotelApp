package com.fanfan.hotel.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanfan.hotel.R;
import com.fanfan.hotel.common.Constants;
import com.fanfan.hotel.common.InfoManage;
import com.fanfan.hotel.common.activity.BarBaseActivity;
import com.fanfan.hotel.model.PersonInfo;
import com.fanfan.hotel.model.RoomInfo;
import com.fanfan.hotel.utils.PreferencesUtils;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.face.bean.Delperson;
import com.fanfan.youtu.api.face.event.DelPersonEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by android on 2017/12/22.
 */

public class ConfirmActivity extends BarBaseActivity {

    public static final int CONFIRM_TO_PAY_REQUESTCODE = 102;
    public static final int CONFIRM_TO_PAY_RESULTCODE = 202;

    public static final String BITMAP = "bitmap";

    public static final int CHECK_IN = 1;
    public static final int CHECK_OUT = 2;

    public static final String CHECK = "check";

    public static void newInstance(Context context, int check, Bitmap bitmap) {
        Intent intent = new Intent(context, ConfirmActivity.class);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        intent.putExtra(BITMAP, bytes);
        intent.putExtra(CHECK, check);
        context.startActivity(intent);
    }

    public static void newInstance(Context context, int check) {
        Intent intent = new Intent(context, ConfirmActivity.class);
        intent.putExtra(CHECK, check);
        context.startActivity(intent);
    }

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, ConfirmActivity.class);
        context.startActivity(intent);
    }


    @BindView(R.id.iv_person)
    ImageView ivPerson;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_family)
    TextView tvFamily;
    @BindView(R.id.tv_birth)
    TextView tvBirth;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.iv_room)
    ImageView ivRoom;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_details)
    TextView tvDetails;
    @BindView(R.id.tv_many_details)
    TextView tvManyDetails;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;
    @BindView(R.id.extra_consumption)
    LinearLayout extraConsumption;
    @BindView(R.id.tv_extra)
    TextView tvExtra;

    private Youtucode youtucode;

    private int check;

    private RoomInfo roomInfo;
    private PersonInfo personInfo;
    private Bitmap bitmap;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_confirm;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initView() {
        super.initView();
        check = getIntent().getIntExtra(CHECK, -1);

        youtucode = Youtucode.getSingleInstance();

        if (check == -1) {
            return;
        }

        if (check == CHECK_OUT) {

            setTitle("入住信息");
            personInfo = InfoManage.getInstance().getPersonInfo();

            extraConsumption.setVisibility(View.VISIBLE);
            tvConfirm.setBackgroundResource(R.drawable.bg_btn_style_bule);

            byte[] bytes = getIntent().getByteArrayExtra(BITMAP);
            if (bytes != null) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }

            setPersonInfo();

            int roomId = PreferencesUtils.getInt(this, personInfo.getIDCard(), -1);
            if (roomId != -1) {
                roomInfo = new RoomInfo();
                roomInfo.setId(roomId);
                roomInfo.setTitle("本地预定酒店  " + roomId);
                InfoManage.getInstance().setRoomInfo(roomInfo);
            } else {
                Print.e("本地暂未存储");
                roomInfo = new RoomInfo();
                roomInfo.setId(2);
                roomInfo.setTitle("测试预定酒店");
                InfoManage.getInstance().setRoomInfo(roomInfo);
            }

            setRoomInfo();
            tvConfirm.setText("确认退房");
            tvConfirm.setEnabled(false);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvExtra.setText("您无额外消费，如确定退房，请点击下方退房按钮。");
                    tvConfirm.setEnabled(true);
                }
            }, 5000);

        } else if (check == CHECK_IN) {
            setTitle("信息确认");

            roomInfo = InfoManage.getInstance().getRoomInfo();
            personInfo = InfoManage.getInstance().getPersonInfo();
            byte[] bytes = getIntent().getByteArrayExtra(BITMAP);

            extraConsumption.setVisibility(View.GONE);

            if (bytes != null) {
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }

            if (personInfo != null)
                setPersonInfo();

            if (roomInfo != null)
                setRoomInfo();

            tvConfirm.setText("确认信息");
        }


    }

    private void setPersonInfo() {
        if (new File(personInfo.getHeadUrl()).exists()) {
            Glide.with(this).load(personInfo.getHeadUrl())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivPerson);
        } else {
            if (bitmap != null) {
                ivPerson.setImageBitmap(bitmap);
            }
        }
        tvName.setText(personInfo.getName());
        tvGender.setText(personInfo.getGender());
        tvFamily.setText(personInfo.getGender());
        tvBirth.setText(personInfo.getBirth());
        tvAddress.setText(personInfo.getAddress());
    }

    @Override
    protected void initData() {

    }


    private void setRoomInfo() {
        Glide.with(this).load(roomInfo.getImageUrl())
                .placeholder(R.mipmap.ic_head)
                .error(R.mipmap.iv_room)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(ivRoom);
        tvTitle.setText(roomInfo.getTitle());
        StringBuffer sb = new StringBuffer();
        //2张1.2米宽单人床    入住2人    24平方米左右 有窗
        sb.append(roomInfo.getBedNum() + "张");
        sb.append(roomInfo.getBedWid() + "米");
        sb.append(roomInfo.getBedType());
        sb.append("   入住" + roomInfo.getPeopleNum() + "人");
        sb.append("   " + roomInfo.getAcreage() + "平方米左右");
        sb.append(roomInfo.isHasCasement() ? "   有窗" : "   无窗");
        tvDetails.setText(sb.toString());
        tvManyDetails.setText(roomInfo.getManyDetails());
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

    @OnClick({R.id.tv_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_confirm:
                if (check == CHECK_OUT) {
                    String personId = personInfo.getIDCard();
                    youtucode.delPerson(personId);
                } else if (check == CHECK_IN) {
                    PayActivity.newInstance(this, CONFIRM_TO_PAY_REQUESTCODE);
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONFIRM_TO_PAY_REQUESTCODE) {
            if (resultCode == CONFIRM_TO_PAY_RESULTCODE) {
                finish();
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(DelPersonEvent event) {
        if (event.isOk()) {
            Delperson delperson = event.getBean();
            Print.e(delperson);
            if (delperson.getErrorcode() == 0) {

                String person_id = delperson.getPerson_id();

                File file = new File(Constants.PROJECT_PATH + AuthenticationActivity.NEW_PERSON + File.separator + person_id + ".jpg");
                if (file.exists())
                    file.delete();
                InfoCompleteActivity.newInstance(this, CONFIRM_TO_PAY_REQUESTCODE, InfoCompleteActivity.CHECK_OUT);
                finish();
            } else {
                Print.e("onError : " + delperson.getErrorcode() + "  " + delperson.getErrormsg());
            }
        } else {
            Print.e("onError : " + event.getCode() + "  " + event.getCodeDescribe());
        }

    }

}

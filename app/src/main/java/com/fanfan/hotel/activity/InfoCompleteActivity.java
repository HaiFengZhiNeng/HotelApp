package com.fanfan.hotel.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanfan.hotel.R;
import com.fanfan.hotel.common.InfoManage;
import com.fanfan.hotel.common.activity.BarBaseActivity;
import com.fanfan.hotel.model.PersonInfo;
import com.fanfan.hotel.model.RoomInfo;

import butterknife.BindView;

public class InfoCompleteActivity extends BarBaseActivity {


    public static final int CHECK_IN = 1;
    public static final int CHECK_OUT = 2;

    public static final String CHECK = "check";
    @BindView(R.id.tv_pay_tip)
    TextView tvPayTip;
    @BindView(R.id.tv_peice)
    TextView tvPeice;
    @BindView(R.id.rl_peice)
    RelativeLayout rlPeice;
    @BindView(R.id.tv_person)
    TextView tvPerson;
    @BindView(R.id.rl_person)
    RelativeLayout rlPerson;
    @BindView(R.id.tv_hometype)
    TextView tvHometype;
    @BindView(R.id.rl_hometype)
    RelativeLayout rlHometype;
    @BindView(R.id.tv_homepos)
    TextView tvHomepos;
    @BindView(R.id.rl_homepos)
    RelativeLayout rlHomepos;
    @BindView(R.id.tv_tipinfo)
    TextView tvTipinfo;
    @BindView(R.id.rl_tipinfp)
    RelativeLayout rlTipinfp;
    @BindView(R.id.tv_hello)
    TextView tvHello;
    @BindView(R.id.rl_hello)
    RelativeLayout rlHello;

    public static void newInstance(Activity context, int requestCode, int check) {
        Intent intent = new Intent(context, InfoCompleteActivity.class);
        intent.putExtra(CHECK, check);
        context.startActivityForResult(intent, requestCode);
    }

    private int check;
    private RoomInfo roomInfo;
    private PersonInfo personInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_info_complete;
    }

    @Override
    protected void initView() {
        super.initView();

        check = getIntent().getIntExtra(CHECK, -1);
        roomInfo = InfoManage.getInstance().getRoomInfo();
        personInfo = InfoManage.getInstance().getPersonInfo();

        if (check == -1) {
            return;
        }

        if (check == CHECK_OUT) {
            setTitle("退房结果");
            rlPeice.setVisibility(View.GONE);
            rlHomepos.setVisibility(View.GONE);

            tvPayTip.setText("退房成功");
            tvTipinfo.setText("请将门禁卡放在右侧储存区，感谢您的配合");

        } else if (check == CHECK_IN) {
            setTitle("支付结果");

            tvPayTip.setText("支付成功");
            rlHello.setVisibility(View.GONE);

        }

        tvPeice.setText(isNull(String.valueOf(roomInfo.getPrice())));
        tvPerson.setText(isNull(personInfo.getName()));
        tvHometype.setText(isNull(roomInfo.getBedType()));
        tvHomepos.setText(isNull(roomInfo.getAddress()));
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setResult() {
        if (check == CHECK_OUT) {

        } else if (check == CHECK_IN) {
            setResult(PayActivity.PAY_TO_INFO_RESULTCODE);
        }
    }

    private String isNull(String noll) {
        return noll == null ? " noll " : noll;
    }

    @Override
    public void onBackPressed() {
        setResult();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        InfoManage.getInstance().clear();
        super.onDestroy();
    }
}

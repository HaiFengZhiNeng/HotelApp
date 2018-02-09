package com.fanfan.hotel.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanfan.hotel.R;
import com.fanfan.hotel.common.Constants;
import com.fanfan.hotel.model.RoomInfo;

import butterknife.BindView;


/**
 * Created by zhangyuanyuan on 2017/9/22.
 */

public class RoomDetailsDialog extends Dialog {

    public RoomDetailsDialog(@NonNull Context context) {
        super(context);
    }

    public RoomDetailsDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {

        private Context context;
        private RoomInfo roomInfo;
        private OnDialogClickListener onDialogClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setRoomInfo(RoomInfo roomInfo) {
            this.roomInfo = roomInfo;
            return this;
        }

        public Builder setPositiveButton(OnDialogClickListener listener) {
            this.onDialogClickListener = listener;
            return this;
        }

        public RoomDetailsDialog build() {
            if (roomInfo == null) {
                throw new RuntimeException("roomInfo null");
            }
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final RoomDetailsDialog dialog = new RoomDetailsDialog(context);

            View layout = inflater.inflate(R.layout.dialog_room_details, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ((TextView) layout.findViewById(R.id.tv_title)).setText(roomInfo.getTitle());

            Window dlgwin = dialog.getWindow();
            WindowManager.LayoutParams lp = dlgwin.getAttributes();
            lp.width = (int) (Constants.displayWidth / 4 * 3); //设置宽度
            dialog.getWindow().setAttributes(lp);

            dialog.getWindow().setGravity(Gravity.CENTER);

            StringBuffer sb = new StringBuffer();
            //2张1.2米宽单人床    入住2人    24平方米左右 有窗
            sb.append(roomInfo.getBedNum() + "张");
            sb.append(roomInfo.getBedWid() + "米");
            sb.append(roomInfo.getBedType());
            sb.append("   入住" + roomInfo.getPeopleNum() + "人");
            sb.append("   " + roomInfo.getAcreage() + "平方米左右");
            sb.append(roomInfo.isHasCasement() ? "   有窗" : "   无窗");
            ((TextView) layout.findViewById(R.id.tv_details)).setText(sb.toString());

            ((TextView) layout.findViewById(R.id.tv_price)).setText("￥" + roomInfo.getPrice());

            ((TextView) layout.findViewById(R.id.tv_many_details)).setText(roomInfo.getManyDetails());

            ImageView imageView = layout.findViewById(R.id.iv_room);
            Glide.with(context).load(roomInfo.getImageUrl())
                    .placeholder(R.mipmap.ic_head)
                    .error(R.mipmap.iv_room)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);

            layout.findViewById(R.id.iv_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            if (onDialogClickListener != null) {
                layout.findViewById(R.id.btn_look).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        onDialogClickListener.onLookRoom();
                    }
                });
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }


    public interface OnDialogClickListener {
        void onLookRoom();
    }
}

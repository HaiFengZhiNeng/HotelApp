package com.fanfan.hotel.service.provider;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanfan.hotel.R;
import com.fanfan.hotel.activity.AuthenticationActivity;
import com.fanfan.hotel.common.holder.RecyclerViewHolder;
import com.fanfan.hotel.model.RoomInfo;
import com.fanfan.hotel.ui.dialog.RoomDetailsDialog;
import com.fanfan.hotel.ui.recyclerview.refresh.base.BaseViewProvider;

/**
 * Created by android on 2017/12/19.
 */

public class RoomInfoProvider extends BaseViewProvider<RoomInfo> {

    public RoomInfoProvider(@NonNull Context context) {
        super(context, R.layout.item_roominfo);
    }

    @Override
    public void onBindView(RecyclerViewHolder holder, final RoomInfo bean) {
        holder.setText(R.id.tv_title, bean.getTitle());
        StringBuffer sb = new StringBuffer();
        //2张1.2米宽单人床    入住2人    24平方米左右 有窗
        sb.append(bean.getBedNum() + "张");
        sb.append(bean.getBedWid() + "米");
        sb.append(bean.getBedType());
        sb.append("   入住" + bean.getPeopleNum() + "人");
        sb.append("   " + bean.getAcreage() + "平方米左右");
        sb.append(bean.isHasCasement() ? "   有窗" : "   无窗");
        holder.setText(R.id.tv_details, sb.toString());
        holder.setText(R.id.tv_price, "￥" + bean.getPrice());
        holder.setText(R.id.tv_surplus, bean.getSurplus());

        ImageView imageView = holder.get(R.id.iv_room);
        Glide.with(mContext).load(bean.getImageUrl())
                .placeholder(R.mipmap.ic_head)
                .error(R.mipmap.iv_room)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESULT).into(imageView);

        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomDetailsDialog.Builder builder = new RoomDetailsDialog.Builder(mContext);
                RoomDetailsDialog dialog = builder.setRoomInfo(bean)
                        .setPositiveButton(new RoomDetailsDialog.OnDialogClickListener() {
                            @Override
                            public void onLookRoom() {
                                AuthenticationActivity.newInstance(mContext, bean);
                            }
                        })
                        .build();
                dialog.show();
            }
        }, R.id.btn_look, R.id.item);
    }
}

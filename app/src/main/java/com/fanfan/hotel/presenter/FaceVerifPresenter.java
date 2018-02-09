package com.fanfan.hotel.presenter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;

import com.fanfan.hotel.R;
import com.fanfan.hotel.activity.AuthenticationActivity;
import com.fanfan.hotel.presenter.ipresenter.IFaceVerifPresenter;
import com.fanfan.hotel.utils.BitmapUtils;
import com.fanfan.youtu.Youtucode;
import com.fanfan.youtu.api.face.bean.FaceCompare;
import com.fanfan.youtu.api.face.bean.FaceIdentify;
import com.fanfan.youtu.api.face.bean.Newperson;
import com.fanfan.youtu.api.face.bean.PersonModify;
import com.fanfan.youtu.api.face.event.FaceCompareEvent;
import com.fanfan.youtu.api.face.event.FaceIdentifyEvent;
import com.fanfan.youtu.api.face.event.NewPersonEvent;
import com.fanfan.youtu.api.face.event.PersonModifyEvent;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by android on 2017/12/22.
 */

public class FaceVerifPresenter extends IFaceVerifPresenter {

    private IFaceverifView mFaceverifView;

    private Youtucode youtucode;

    private boolean isFaceCompare;
    private boolean isFaceIdentify;
    private boolean isNewPerson;
    private boolean isSaveFace;

    private int cutRatio;

    public FaceVerifPresenter(IFaceverifView baseView) {
        super(baseView);
        mFaceverifView = baseView;
        youtucode = Youtucode.getSingleInstance();
        cutRatio = 4;
    }

    @Override
    public void start() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void finish() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public Bitmap bitmapSaturation(Bitmap baseBitmap) {
        Bitmap copyBitmap = Bitmap.createBitmap(baseBitmap.getWidth(), baseBitmap.getHeight(), baseBitmap.getConfig());
        ColorMatrix mImageViewMatrix = new ColorMatrix();
        ColorMatrix mBaoheMatrix = new ColorMatrix();
        float sat = (float) 0.0;
        mBaoheMatrix.setSaturation(sat);
        mImageViewMatrix.postConcat(mBaoheMatrix);
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(mImageViewMatrix);//再把该mImageViewMatrix作为参数传入来实例化ColorMatrixColorFilter
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);//并把该过滤器设置给画笔
        Canvas canvas = new Canvas(copyBitmap);//将画纸固定在画布上
        canvas.drawBitmap(baseBitmap, new Matrix(), paint);//传如baseBitmap表示按照原图样式开始绘制，将得到是复制后的图片
        canvas.drawBitmap(baseBitmap, new Matrix(), paint);//传如baseBitmap表示按照原图样式开始绘制，将得到是复制后的图片
        return copyBitmap;
    }

    @Override
    public void faceCompare(Bitmap bitmapA, Bitmap bitmapB) {
        if (isFaceCompare)
            return;

        Print.e("摄像头图像与身份证头像比较中 ... ");
        isFaceCompare = true;

        Bitmap replicaBitmap = Bitmap.createBitmap(bitmapA);
        Bitmap copyBitmap = BitmapUtils.ImageCrop(replicaBitmap, cutRatio, cutRatio, true);

        mFaceverifView.showMsg("正在进行人脸比对，请稍后");
        try {
            youtucode.faceCompare(copyBitmap, bitmapB);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void newPerson(Bitmap bitmap, String IDCard, String name) {
        if (isNewPerson)
            return;
        isNewPerson = true;

        Bitmap replicaBitmap = Bitmap.createBitmap(bitmap);
        Bitmap copyBitmap = BitmapUtils.ImageCrop(replicaBitmap, cutRatio, cutRatio, true);

        youtucode.newPerson(copyBitmap, IDCard, name);
        mFaceverifView.transfer(copyBitmap);
    }

    @Override
    public void modifyPersonName(String personId, String authId) {
        youtucode.modifyPersonName(personId, authId);
    }

    @Override
    public void faceIdentifyFace(Bitmap bitmap) {
        if (isFaceIdentify)
            return;

        Print.e("从云端获取人脸信息详情 ... ");
        isFaceIdentify = true;
        Bitmap copyBitmap = bitmapSaturation(bitmap);
        youtucode.faceIdentify(copyBitmap);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void compareFace(FaceIdentify faceIdentify) {
        Print.e("云端获取成功后取得相似度最佳的一个");
        ArrayMap<FaceIdentify.IdentifyItem, Integer> countMap = new ArrayMap<>();

        ArrayList<FaceIdentify.IdentifyItem> identifyItems = faceIdentify.getCandidates();
        if (identifyItems != null && identifyItems.size() > 0) {
            for (int i = 0; i < identifyItems.size(); i++) {
                FaceIdentify.IdentifyItem identifyItem = identifyItems.get(i);

                if (countMap.containsKey(identifyItem)) {
                    countMap.put(identifyItem, countMap.get(identifyItem) + 1);
                } else {
                    countMap.put(identifyItem, 1);
                }
            }

            ArrayMap<Integer, List<FaceIdentify.IdentifyItem>> resultMap = new ArrayMap<>();
            List<Integer> tempList = new ArrayList<Integer>();

            Iterator iterator = countMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<FaceIdentify.IdentifyItem, Integer> entry = (Map.Entry<FaceIdentify.IdentifyItem, Integer>) iterator.next();

                FaceIdentify.IdentifyItem key = entry.getKey();
                int value = entry.getValue();

                if (resultMap.containsKey(value)) {
                    List list = resultMap.get(value);
                    list.add(key);
                } else {
                    List<FaceIdentify.IdentifyItem> list = new ArrayList<>();
                    list.add(key);
                    resultMap.put(value, list);
                    tempList.add(value);
                }
            }
            //对多个人脸进行排序
            Collections.sort(tempList);

            int size = tempList.size();
            List<FaceIdentify.IdentifyItem> list = resultMap.get(tempList.get(size - 1));
            //防止人脸都是 1 时，取辨识度最大
            Collections.sort(list);
            FaceIdentify.IdentifyItem identifyItem = list.get(0);

            if (identifyItem.getConfidence() >= 70) {
                String person = identifyItem.getPerson_id();
                mFaceverifView.identifyFaceFinish(person);
            } else {
                mFaceverifView.confidenceLow();
                isFaceIdentify = false;
            }
        } else {
            mFaceverifView.identifyNoFace();
            isFaceIdentify = false;
        }
    }

    @Override
    public void savePersonFace(Bitmap bitmap, String IDCard) {
        if (isSaveFace)
            return;
        isSaveFace = true;

        Bitmap replicaBitmap = Bitmap.createBitmap(bitmap);
        Bitmap copyBitmap = BitmapUtils.ImageCrop(replicaBitmap, cutRatio, cutRatio, true);

        BitmapUtils.saveBitmapToFile(copyBitmap, AuthenticationActivity.NEW_PERSON, IDCard + ".jpg");
        mFaceverifView.saveFinish(IDCard);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(FaceCompareEvent event) {
        if (event.isOk()) {
            FaceCompare faceCompare = event.getBean();
            Print.e(faceCompare);
            if (faceCompare.getErrorcode() == 0) {
                if (faceCompare.getSimilarity() > 70) {
                    mFaceverifView.compareSuccess();
                } else {
                    mFaceverifView.similarityLow(faceCompare.getSimilarity());
                    isFaceCompare = false;
                }
            } else {
                isFaceCompare = false;
                mFaceverifView.onError(faceCompare.getErrorcode(), faceCompare.getErrormsg());
            }
        } else {
            isFaceCompare = false;
            mFaceverifView.onError(event);
        }

    }


    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(FaceIdentifyEvent event) {
        if (event.isOk()) {
            FaceIdentify faceIdentify = event.getBean();
            Print.e(faceIdentify);
            if (faceIdentify.getErrorcode() == 0) {

                compareFace(faceIdentify);
            } else {
                isFaceIdentify = false;
                mFaceverifView.onError(faceIdentify.getErrorcode(), faceIdentify.getErrormsg());
            }
        } else {
            isFaceIdentify = false;
            mFaceverifView.onError(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(NewPersonEvent event) {
        if (event.isOk()) {
            Newperson newperson = event.getBean();
            Print.e(newperson);
            if (newperson.getErrorcode() == 0) {

                String person_id = newperson.getPerson_id();

                mFaceverifView.newpersonSuccess(person_id);
            } else {
                mFaceverifView.onError(newperson.getErrorcode(), newperson.getErrormsg());
                isNewPerson = false;
            }
        } else {
            mFaceverifView.onError(event);
            isNewPerson = false;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(PersonModifyEvent event) {
        if (event.isOk()) {
            PersonModify personModify = event.getBean();
            Print.e(personModify);
            if (personModify.getErrorcode() == 0) {

                String person_id = personModify.getPerson_id();
                mFaceverifView.personModify(person_id);

            } else {
                mFaceverifView.onError(personModify.getErrorcode(), personModify.getErrormsg());
                isNewPerson = false;
            }
        } else {
            mFaceverifView.onError(event);
            isNewPerson = false;
        }
    }

}

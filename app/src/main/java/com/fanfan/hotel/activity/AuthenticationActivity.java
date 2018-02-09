package com.fanfan.hotel.activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.fanfan.hotel.R;
import com.fanfan.hotel.common.Constants;
import com.fanfan.hotel.common.InfoManage;
import com.fanfan.hotel.common.activity.BarBaseActivity;
import com.fanfan.hotel.model.PersonInfo;
import com.fanfan.hotel.model.RoomInfo;
import com.fanfan.hotel.presenter.CameraPresenter;
import com.fanfan.hotel.presenter.FaceVerifPresenter;
import com.fanfan.hotel.presenter.HsOtgPresenter;
import com.fanfan.hotel.presenter.SynthesizerPresenter;
import com.fanfan.hotel.presenter.ipresenter.ICameraPresenter;
import com.fanfan.hotel.presenter.ipresenter.IFaceVerifPresenter;
import com.fanfan.hotel.presenter.ipresenter.IHsOtgPresenter;
import com.fanfan.hotel.presenter.ipresenter.ISynthesizerPresenter;
import com.fanfan.hotel.ui.camera.DetectOpenFaceView;
import com.fanfan.hotel.ui.camera.DetectionFaceView;
import com.fanfan.hotel.utils.BitmapUtils;
import com.fanfan.hotel.utils.PreferencesUtils;
import com.fanfan.youtu.api.base.event.BaseEvent;
import com.fanfan.youtu.utils.ErrorMsg;
import com.seabreeze.log.Print;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.facedetect.DetectionBasedTracker;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.OnClick;

public class AuthenticationActivity extends BarBaseActivity implements ICameraPresenter.ICameraView,
        SurfaceHolder.Callback, IFaceVerifPresenter.IFaceverifView, IHsOtgPresenter.IHsOtgView, ISynthesizerPresenter.ITtsView {

    public static final String ROOM_ID = "room_id";

    public static final String NEW_PERSON = "newPersons";

    @BindView(R.id.checkout_surfaceview)
    SurfaceView cameraSurfaceView;
    @BindView(R.id.takephoto)
    Button takephoto;

    @BindView(R.id.detection_face_view)
    DetectionFaceView detectionFaceView;
    @BindView(R.id.opencv_face_view)
    DetectOpenFaceView opencvFaceView;

    public static void newInstance(Context context) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        context.startActivity(intent);
    }

    public static void newInstance(Context context, RoomInfo roomInfo) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.putExtra(ROOM_ID, roomInfo);
        context.startActivity(intent);
    }

    private RoomInfo roomInfo;
    private PersonInfo personInfo;
    private boolean isCompare;

    //opencv
    private Mat mRgba;
    private Mat mGray;

    private File mCascadeFile;
    private CascadeClassifier mJavaDetector;
    private DetectionBasedTracker mNativeDetector;

    private int mAbsoluteFaceSize = 0;
    private float mRelativeFaceSize = 0.2f;
    private int mDetectorType = JAVA_DETECTOR;
    public static final int JAVA_DETECTOR = 0;
    public static final int NATIVE_DETECTOR = 1;


    private SynthesizerPresenter mTtsPresenter;

    private static final String speak0 = "将身份证放入感应区中，面对摄像头即可";
    static {
        if (!OpenCVLoader.initDebug()) {
            System.out.println("opencv 初始化失败！");
        } else {
            System.loadLibrary("detection_based_tracker");
        }
    }


    private static final String speak1 = "身份证识别完成，请保管好，以防丢失";

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    System.loadLibrary("detection_based_tracker");

                    try {
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);

                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            mJavaDetector = null;
                        } else {
                            mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);
                        }
                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private CameraPresenter mCameraPresenter;
    private FaceVerifPresenter mFaceVerifPresenter;
    private HsOtgPresenter mHsOtgPresenter;

    private Bitmap saveBitmap;
    private Bitmap bitmapB;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_authentication;
    }

    @Override
    protected void initView() {
        super.initView();
        setTitle("身份认证");
        SurfaceHolder holder = cameraSurfaceView.getHolder(); // 获得SurfaceHolder对象
        holder.addCallback(this); // 为SurfaceView添加状态监听
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mTtsPresenter = new SynthesizerPresenter(this);
        mTtsPresenter.start();

        mRgba = new Mat();
        mGray = new Mat();

        mCameraPresenter = new CameraPresenter(this, holder);
        mFaceVerifPresenter = new FaceVerifPresenter(this);
        mHsOtgPresenter = new HsOtgPresenter(this);

        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        opencvFaceView.setVisibility(View.GONE);

        mHsOtgPresenter.start();

//        mHandler.postDelayed(testRun, 5000);
    }

    Runnable testRun = new Runnable() {
        @Override
        public void run() {
            bitmapB = BitmapFactory.decodeResource(getResources(), R.mipmap.compare_a);
            personInfo = new PersonInfo();
            personInfo.setIDCard("142622199205180071");
            personInfo.setName("张涛");
            personInfo.setHeadUrl(Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib/compare_a.png");
        }
    };

    @Override
    protected void initData() {
        roomInfo = (RoomInfo) getIntent().getSerializableExtra(ROOM_ID);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFaceVerifPresenter.start();
        mHsOtgPresenter.onStart();
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
        mCameraPresenter.closeCamera();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFaceVerifPresenter.finish();
        mHsOtgPresenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTtsPresenter.finish();
        mHsOtgPresenter.finish();
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
    public void previewFinish() {

    }

    @Override
    public void pictureTakenSuccess(String savePath) {
        takephoto.setEnabled(true);
        showToast("拍照完成");
    }

    @Override
    public void pictureTakenFail() {
        mCameraPresenter.pictureTakenFinsih();
    }

    @Override
    public void autoFocusSuccess() {
        mCameraPresenter.cameraTakePicture();
    }

    @Override
    public void noFace() {
//        drawSufaceView.clear();
        opencvFaceView.clear();
        detectionFaceView.clear();
    }

    @Override
    public void tranBitmap(Bitmap bitmap, int num) {

        if (personInfo != null && bitmapB != null) {
            if (isCompare) {
                if (roomInfo == null) {
                    mFaceVerifPresenter.faceIdentifyFace(bitmap);
                } else {
//                    mFaceVerifPresenter.newPerson(bitmap);
                    mFaceVerifPresenter.savePersonFace(bitmap, personInfo.getIDCard());
                }
            } else {
                mFaceVerifPresenter.faceCompare(bitmap, bitmapB);
            }
        }

        if (!mCameraPresenter.cameraFaceDetection()) {
            opencvFaceView.setVisibility(View.VISIBLE);
            opencvFaceDetection(bitmap);
        }

    }

    private void opencvFaceDetection(Bitmap bitmap) {
        Utils.bitmapToMat(bitmap, mRgba);
        Mat mat1 = new Mat();
        Utils.bitmapToMat(bitmap, mat1);
        Imgproc.cvtColor(mat1, mGray, Imgproc.COLOR_BGR2GRAY);
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }
        MatOfRect faces = new MatOfRect();
        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null)

                mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        } else if (mDetectorType == NATIVE_DETECTOR) {
            if (mNativeDetector != null)
                mNativeDetector.detect(mGray, faces);
        }
        Rect[] facesArray = faces.toArray();
        if (facesArray.length > 0) {
            Print.e(facesArray);
            opencvFaceView.setFaces(facesArray, mCameraPresenter.getOrientionOfCamera());
        }
    }

    @Override
    public void setCameraFaces(Camera.Face[] faces) {
        if (faces.length > 0) {
            detectionFaceView.setFaces(faces, mCameraPresenter.getOrientionOfCamera());
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCameraPresenter.openCamera();
        mCameraPresenter.doStartPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCameraPresenter.setMatrix(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCameraPresenter.closeCamera();
    }


    @OnClick({R.id.takephoto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.takephoto:
                break;
        }
    }

    @Override
    public void onError(BaseEvent event) {
        Print.e("onError : " + event.getCode() + "  " + event.getCodeDescribe());
        mHsOtgPresenter.compareFail();
    }

    @Override
    public void onError(int code, String msg) {
        Print.e("onError  code : " + code + " ; msg : " + msg + " ; describe : " + ErrorMsg.getCodeDescribe(code));
        mHsOtgPresenter.compareFail();
    }

    @Override
    public void compareSuccess() {
        Print.e("身份证头像认证成功");
        isCompare = true;
        if (roomInfo != null) {
            showToast("身份认证成功，注册人脸信息，请正对摄像头");
        }
    }

    @Override
    public void similarityLow(float similarity) {
        showToast("识别度较低，相似度为 ： " + similarity + ", 请重新将省份证放入感应区");
        Print.e("识别度较低，相似度为 ： " + similarity);
        bitmapB = null;
        mHsOtgPresenter.compareFail();
    }

    @Override
    public void transfer(Bitmap bitmap) {
        saveBitmap = bitmap;
    }

    @Override
    public void newpersonSuccess(String personId) {

        BitmapUtils.saveBitmapToFile(saveBitmap, NEW_PERSON, personId + ".jpg");
        mFaceVerifPresenter.modifyPersonName(personId, personInfo.getName());
    }

    @Override
    public void personModify(String person_id) {

    }

    @Override
    public void identifyNoFace() {
        showToast("未检测到人脸或未注册信息");
    }

    @Override
    public void confidenceLow() {
        showToast("识别度较低，请正对屏幕");
    }

    @Override
    public void identifyFaceFinish(String person) {
        Print.e("云端验证完成 ... ");
        Print.e(person);
        personInfo.setSaveUrl(Constants.PROJECT_PATH + NEW_PERSON + File.separator + person + ".jpg");
        InfoManage.getInstance().setPersonInfo(personInfo);
        ConfirmActivity.newInstance(this, ConfirmActivity.CHECK_OUT);
        finish();
    }

    @Override
    public void saveFinish(String currentTimeStr) {
        showToast("添加人脸信息成功");
        personInfo.setSaveUrl(Constants.PROJECT_PATH + NEW_PERSON + File.separator + currentTimeStr + ".jpg");
        InfoManage.getInstance().setPersonInfo(personInfo);
        InfoManage.getInstance().setRoomInfo(roomInfo);
        ConfirmActivity.newInstance(this, ConfirmActivity.CHECK_IN);
        finish();
    }

    @Override
    public void init(int code) {
        if (code == 1) {
            Print.e("身份证已连接");
            mHsOtgPresenter.authenticate();
        } else {
            mHsOtgPresenter.authFail();
        }
    }

    @Override
    public void authenticate(int code) {
        switch (code) {
            case 1:
                Print.e("卡认证成功");
                mHsOtgPresenter.readCard();
                break;
            case 2:
                Print.e("卡认证失败");
                mHsOtgPresenter.authFail();
                break;
            case 0:
                Print.e("未连接");
                mHsOtgPresenter.authFail();
                break;
        }
    }

    @Override
    public void readCard(int code) {
        if (code == 1) {
            mHsOtgPresenter.identityRead(personInfo);
        } else {
            Print.e("读卡失败");
            mHsOtgPresenter.authFail();
        }
    }

    @Override
    public void identityFinish(PersonInfo info) {

        addSpeakAnswer(speak1);

        String headUrl = info.getHeadUrl();
        bitmapB = BitmapFactory.decodeFile(headUrl);
        personInfo = info;
        Print.e("");
    }

    @Override
    public void identityFail(String msg) {
        Print.e(msg);
        mHsOtgPresenter.authFail();
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onRunable() {

    }
}

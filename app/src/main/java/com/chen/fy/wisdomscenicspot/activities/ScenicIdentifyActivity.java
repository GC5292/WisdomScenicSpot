package com.chen.fy.wisdomscenicspot.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chen.fy.wisdomscenicspot.R;
import com.chen.fy.wisdomscenicspot.consts.Consts;
import com.chen.fy.wisdomscenicspot.utils.ScenicDescribeUtils;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.PermissionManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;

public class ScenicIdentifyActivity extends TakePhotoActivity {

    private static final String TAG = "ScenicIdentifyActivity";

    /**
     * 拍照,相册选择弹出框
     */
    private AlertDialog takePhotoDialog;

    /**
     * 拍照,相册选择弹出框
     */
    private AlertDialog CircleDialog;

    /**
     * 拍照控件
     */
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    /**
     * 图片剪切以及图片地址
     */
    private CropOptions cropOptions;
    private Uri uri;

    /**
     * 景物识别图片
     */
    private ImageView im_scenic_identify;
    /**
     * 景物描述信息
     */
    private TextView tv_scenic_describe;
    /**
     * 图片地址
     */
    private String imagePath;
    /**
     * 图片文件大小
     */
    private long fileLength;
    /**
     * 图片文件名
     */
    private final String imageName = "scenicIdentify.jpg";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scene_identify_layout);

        initView();
        //初始化点击景物识别时弹出的对话框
        initSelectBox();
        //初始化TakePhoto开源库,实现拍照以及从相册中选择图片
        initTakePhoto();
        //初始化图形进度条
        initProcessCircle();
    }

    /**
     * 初始化View
     */
    private void initView() {
        im_scenic_identify = findViewById(R.id.im_scenic_identify);
        tv_scenic_describe = findViewById(R.id.tv_scenic_describe);

        im_scenic_identify.setOnClickListener(new MyOnClickListener());
        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_scenic_identify);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /*
     *  初始化点击景物识别后显示的对话框
     */
    private void initSelectBox() {
        LayoutInflater inflater = LayoutInflater.from(this);
        //反射一个自定义的全新的对话框布局
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.photo_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        takePhotoDialog = builder.create();
        //在当前布局中找到控件对象
        Button take_photo = view.findViewById(R.id.take_photo_dialog);
        Button chosen_photo = view.findViewById(R.id.chosen_photo_dialog);
        //监听事件
        take_photo.setOnClickListener(new MyOnClickListener());
        chosen_photo.setOnClickListener(new MyOnClickListener());
    }

    /*
     *  初始化圆形进度条
     */
    private void initProcessCircle() {
        LayoutInflater inflater = LayoutInflater.from(this);
        //反射一个自定义的全新的对话框布局
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.process_circle_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        CircleDialog = builder.create();
        //设置不可以点击弹窗之后进行取消
        CircleDialog.setCanceledOnTouchOutside(false);
        //设置旋转
        Animation animation = new RotateAnimation(0.0f,720.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setFillAfter(true);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(1200);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        ImageView iv_process_circle = view.findViewById(R.id.iv_process_circle);
        iv_process_circle.setAnimation(animation);
    }

    /**
     * 初始化TakePhoto开源库,实现拍照以及从相册中选择图片
     */
    private void initTakePhoto() {
        //获得对象
        takePhoto = getTakePhoto();

        //获取外部存储位置的uri
        File file = new File(getExternalFilesDir(null), imageName);
        uri = Uri.fromFile(file);
        imagePath = uri.getPath();
        fileLength = file.length();

        //进行图片剪切
        int size = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        cropOptions = new CropOptions.Builder().setOutputX(size).setOutputX(size).setWithOwnCrop(false).create();  //true表示使用TakePhoto自带的裁剪工具

        //进行图片压缩
        CompressConfig compressConfig=new CompressConfig.Builder().
                //大小            像素
                setMaxSize(1024).setMaxPixel(400).create();
        /**
         * 启用图片压缩
         * @param config 压缩图片配置
         * @param showCompressDialog 压缩时是否显示进度对话框
         * @return
         */
        takePhoto.onEnableCompress(compressConfig,true);
    }

    /**
     * 获取景区描述
     *
     * @param serverBack 景物识别服务器返回的信息
     */
    private void getScenicDescribeInfo(String serverBack) {
        switch (serverBack) {
            case "rock":   //象山
                tv_scenic_describe.setText(ScenicDescribeUtils.getElephantHillInfo());
                break;
            case "tower":  //普贤塔
                tv_scenic_describe.setText(ScenicDescribeUtils.getTown());
                break;
            default:
                tv_scenic_describe.setText("识别失败...请重新识别");
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //以下代码为处理Android6.0、7.0动态权限所需(TakePhoto所需)
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    /**
     * 获取照片成功后成功回调
     */
    @Override
    public void takeSuccess(TResult result) {
        //将拍摄的照片显示出来
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            im_scenic_identify.setImageBitmap(bitmap);
            //显示进度条
            CircleDialog.show();
            //上传图片到服务器
            uploadImage();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /***
     * 上传图片到服务器进行处理
     */
    private void uploadImage() {
        NetworkTask networkTask = new NetworkTask();
        networkTask.execute(imagePath);
    }

    private String doPost(String imagePath) {

        Socket s = null;
        try {
            s = new Socket(Consts.SERVER_ADDRESS, Consts.PORT);
            OutputStream out = s.getOutputStream();

            //读照片流
            FileInputStream inputStream = new FileInputStream(new File(imagePath));

            byte[] buf = new byte[(int) fileLength];
            int len;
            //判断是否读到文件末尾
            while ((len = inputStream.read(buf)) != -1) {
                Log.d("Test111", "test11");
            }
            //发送图片文件大小
            String str = String.valueOf(fileLength);
            byte[] b = str.getBytes();
            out.write(b);
            //发射文件字节
            out.write(buf);
            //获取服务器传送的信息
            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String serverBack = "";
            do {
                serverBack = reader.readLine();
            } while (serverBack.isEmpty());
            //跳到UI线程进行UI操作
            final String finalServerBack = serverBack;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //得到景物识别的结果
                    getScenicDescribeInfo(finalServerBack);
                    //进度条消失
                    CircleDialog.dismiss();
                }
            });
            return "success";

        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return "error";
    }


    /**
     * 访问网络AsyncTask,访问网络在子线程进行并返回主线程通知访问的结果
     */
    class NetworkTask extends AsyncTask<String, Integer, String> {

        /**
         * 后台任务开始之前调用，通常用来初始化界面操作
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * 执行后台耗时操作，已在子线程中执行
         *
         * @return 对结果进行返回
         */
        @Override
        protected String doInBackground(String... params) {
            return doPost(params[0]);
        }

        /**
         * 当后台任务执行完毕时调用
         *
         * @param result 后台执行任务的返回值
         */
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "服务器响应" + result);
        }
    }

    /**
     * 点击事件
     */
    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.im_scenic_identify:           //点击添加图片
                    takePhotoDialog.show();
                    break;
                case R.id.take_photo_dialog:            //点击拍照
                    //相机获取照片并剪裁
                    takePhoto.onPickFromCaptureWithCrop(uri, cropOptions);
                    takePhotoDialog.dismiss();
                    break;
                case R.id.chosen_photo_dialog:          //点击相册
                    //相册获取照片并剪裁
                    takePhoto.onPickFromGalleryWithCrop(uri, cropOptions);
                    // takePhoto.onPickFromGallery();
                    takePhotoDialog.dismiss();
                    break;
            }
        }
    }

}

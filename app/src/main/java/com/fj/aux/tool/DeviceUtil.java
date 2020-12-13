package com.fj.aux.tool;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fj.aux.R;
import com.fj.aux.autojs.AutoJs;
import com.fj.aux.model.script.Scripts;
import com.fj.aux.network.DeviceService;
import com.fj.aux.ui.settings.VideoSettingsActivity_;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.pio.PFiles;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.stardust.autojs.runtime.ScriptRuntime.getApplicationContext;
import static jackpal.androidterm.TermDebug.LOG_TAG;

public class DeviceUtil {

    public static WindowManager.LayoutParams mParams;
    public static WindowManager mWindowManager;
    public static TextView tvStatus;
    public static View mFloatingWindow;
    public static final String PACKAGE_NAME = "com.fj.aux";
    /**
     * 获取设备标识
     * @return
     */
    public static String getDeviceKey() {
        String fingerPrint = android.os.Build.FINGERPRINT;
        String md5Str = md5(fingerPrint);
        return md5Str.substring(8,24);
    }

    /**
     * md5算法
     * @param val
     * @return
     */
    public static String md5(String val){
        String[] hexArray = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(val.getBytes());
            byte[] rawBit = md.digest();
            String outputMD5 = " ";
            for(int i = 0; i<16; i++){
                outputMD5 = outputMD5+hexArray[rawBit[i]>>>4& 0x0f];
                outputMD5 = outputMD5+hexArray[rawBit[i]& 0x0f];
            }
            return outputMD5.trim();
        }catch(NoSuchAlgorithmException e){
            Log.e(LOG_TAG,"计算MD5值发生错误");
        }
        return null;
    }

    /**
     * 获取时间戳
     * @return
     */
    public static String getCurrentTs(){
        String ts = System.currentTimeMillis()/1000+"";
        return ts;
    }

    /**
     * 参数签名
     * @param params
     * @return
     */
    public static String sign(Map<String,String> params){
        TreeSet<String> treeSet = new TreeSet<>();
        Set<String> keySet = params.keySet();
        for (String key:keySet){
            treeSet.add(key);
        }
        StringBuffer str = new StringBuffer();
        for (String key:treeSet){
            String val = params.get(key);
            str.append("&");
            str.append(key).append("=").append(val);
        }
        return md5(str.toString());
    }

    /**
     * 显示悬浮框
     * @param context
     * @param clickListener
     */
    public static void showFloatingWindow(Context context, View.OnClickListener clickListener) {
        //设置允许弹出悬浮窗口的权限
        requestWindowPermission(context);
        //创建窗口布局参数
        mParams = new WindowManager.LayoutParams(600,
                WindowManager.LayoutParams.WRAP_CONTENT, 0, 0, PixelFormat.TRANSPARENT);
        //设置悬浮窗坐标
        mParams.x = 100;
        mParams.y = 100;
        //表示该Window无需获取焦点，也不需要接收输入事件
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        Log.d("MainActivity", "sdk:" + Build.VERSION.SDK_INT);
        //设置window 类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//API Level 26
            mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        }
        //创建悬浮窗(其实就创建了一个Button,这里也可以创建其他类型的控件)
        if (null == mFloatingWindow) {
            mFloatingWindow = LayoutInflater.from(context).inflate(R.layout.win_floating_tool, null, false);
            ImageView ivRun = mFloatingWindow.findViewById(R.id.iv_run);
            ImageView ivClose = mFloatingWindow.findViewById(R.id.iv_close);
            tvStatus = mFloatingWindow.findViewById(R.id.tv_status);
            ImageView ivDrag = mFloatingWindow.findViewById(R.id.iv_drag);
//            mFloatingWindow.setOnTouchListener(this);
            ivRun.setOnClickListener(clickListener);
            ivClose.setOnClickListener(clickListener);
            mWindowManager.addView(mFloatingWindow, mParams);
        }
    }

    /**
     * 获取悬浮窗权限
     */
    public static void requestWindowPermission(Context context) {
        //android 6.0或者之后的版本需要发一个intent让用户授权
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + PACKAGE_NAME));
                context.startActivity(intent);
            }
        }
    }

    /**
     * 校验授权
     * @param context
     */
    public static void checkVerify(Context context) {
        String deviceKey = DeviceUtil.getDeviceKey();
        String ts = DeviceUtil.getCurrentTs();
        Map<String,String> params = new HashMap<>(2);
        params.put("deviceKey",deviceKey);
        params.put("ts",ts);
        String sign = DeviceUtil.sign(params);
//        MiniLoadingDialog dialog =  WidgetUtils.getMiniLoadingDialog(context);
//        dialog.show();
        DeviceService.getInstance().verify(deviceKey, ts, sign)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            Log.d(LOG_TAG, result.getMessage());
                            if (result.isSuccess()){
                                context.startActivity(new Intent(context, VideoSettingsActivity_.class));
                            }else{
                                DeviceUtil.showToast(context, R.string.text_verify_fail);
                            }
//                            dialog.dismiss();
//
//                            finish();
                        }
                        , error -> {
//                            dialog.dismiss();
                            error.printStackTrace();
                            Log.e("xpf", error.getMessage());
                            Toast.makeText(getApplicationContext(), R.string.text_verify_fail, Toast.LENGTH_SHORT).show();
                        });
    }

    public static void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int resourceId){
        Toast.makeText(context,resourceId,Toast.LENGTH_SHORT).show();
    }

    /**
     * 运行脚本
     *
     * @param showMessage
     * @return
     * @throws IOException
     */
    public static ScriptExecution runTask(Context context,boolean showMessage, String scriptName) {
        ScriptExecution execution = null;
        if (showMessage) {
            Toast.makeText(context, R.string.text_start_running, Toast.LENGTH_SHORT).show();
        }
        try{
            execution = Scripts.INSTANCE.run(new StringScriptSource(PFiles.read(context.getAssets().open(scriptName))));
            Log.e("xpf",execution.getId()+"");
            tvStatus.setText("运行中");
            tvStatus.setTextColor(Color.GREEN);
        }catch (IOException e){
            Log.e("xpf",e.getMessage());
        }
        return execution;
    }

    /**
     * 关闭所有任务
     */
    public static void stopAllTask(){
        AutoJs.getInstance().getScriptEngineService().stopAllAndToast();
        tvStatus.setText("未运行");
        tvStatus.setTextColor(Color.RED);
    }

}

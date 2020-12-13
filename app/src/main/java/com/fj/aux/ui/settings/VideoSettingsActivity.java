package com.fj.aux.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.fj.aux.R;
import com.fj.aux.tool.DeviceUtil;
import com.fj.aux.ui.BaseActivity;
import com.google.gson.Gson;
import com.xuexiang.xui.XUI;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/2/2.
 */
@EActivity(R.layout.activity_video_settings)
public class VideoSettingsActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {

    @ViewById(R.id.et_runtime)
    EditText etRuntime;
//    @ViewById(R.id.et_gap)
//    EditText etGap;
//    @ViewById(R.id.st_follow)
//    Switch switchFollow;
    @ViewById(R.id.st_praise)
    Switch switchPraise;
    @ViewById(R.id.st_comment)
    Switch switchComment;
//    @ViewById(R.id.st_message)
//    Switch switchMessage;
    @ViewById(R.id.st_collect)
    Switch switchCollect;
//    @ViewById(R.id.st_recomment)
//    Switch switchRecomment;
    private VideoSetting videoSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        XUI.initTheme(this);
        super.onCreate(savedInstanceState);
        DeviceUtil.mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }

    @AfterViews
    void setUpUI() {
        setUpToolbar();
        getVideoSettings();
    }

    private void setUpToolbar() {
        Toolbar toolbar = $(R.id.toolbar);
        toolbar.setTitle(R.string.text_video);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Click(R.id.btn_save)
    void saveAndRun() {
        if (TextUtils.isEmpty(etRuntime.getText())) {
            etRuntime.setText("10");
        }
//        if (TextUtils.isEmpty(etGap.getText())) {
//            etGap.setText("1");
//        }
        int runtime = Integer.parseInt(etRuntime.getText().toString());
//        int gap = Integer.parseInt(etGap.getText().toString());
//        boolean follow = switchFollow.isChecked();
        boolean praise = switchPraise.isChecked();
        boolean comment = switchComment.isChecked();
//        boolean message = switchMessage.isChecked();
        boolean collect = switchCollect.isChecked();
//        boolean recomment = switchRecomment.isChecked();
        VideoSetting videoSetting = new VideoSetting();
        videoSetting.setRuntime(runtime);
//        videoSetting.setGap(gap);
//        videoSetting.setFollow(follow);
        videoSetting.setPraise(praise);
        videoSetting.setComment(comment);
//        videoSetting.setMessage(message);
        videoSetting.setCollect(collect);
//        videoSetting.setRecomment(recomment);
        String data = new Gson().toJson(videoSetting);
        Log.d(TAG, data);
        try {
            //保存配置
            saveToFile(data);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
//            finish();
            //开启悬浮框
            DeviceUtil.showFloatingWindow(this, this);
            //运行脚本
//            runTask(true);
        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
    }

    private final static String TAG = "xpf";
    private final static String FILE_NAME = "video_settings.json";
//    private final static String SCRIPT_FILE_NAME = "sample/wechat/hello.js";
    private final static String SCRIPT_FILE_NAME = "sample/wechat/video.js";

    /**
     * 保存到文件
     *
     * @param val
     * @throws IOException
     */
    private void saveToFile(String val) throws IOException {
        FileOutputStream fout = null;
        BufferedWriter bw = null;
        try {
            fout = openFileOutput(FILE_NAME, MODE_PRIVATE);
            bw = new BufferedWriter(new OutputStreamWriter(fout));
            bw.write(val);
            bw.flush();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (bw != null) {
                bw.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }

    /**
     * 获取已有的配置
     */
    public void getVideoSettings() {
        if (getFileStreamPath(FILE_NAME).exists()) {
            Log.d(TAG, getFileStreamPath(FILE_NAME).getAbsolutePath());
            Observable.fromCallable(() -> loadSettingsFrom(openFileInput(FILE_NAME)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(setting -> {
                        videoSetting = setting;
                        etRuntime.setText(videoSetting.getRuntime() + "");
//                        etGap.setText(videoSetting.getGap() + "");
//                        switchFollow.setChecked(videoSetting.isFollow());
                        switchPraise.setChecked(videoSetting.isPraise());
                        switchComment.setChecked(videoSetting.isComment());
//                        switchMessage.setChecked(videoSetting.isMessage());
                        switchCollect.setChecked(videoSetting.isCollect());
//                        switchRecomment.setChecked(videoSetting.isRecomment());
                    });
        }
    }

    private VideoSetting loadSettingsFrom(InputStream inputStream) {
        Gson gson = new Gson();
        return gson.fromJson(new InputStreamReader(inputStream), VideoSetting.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_run) {
            DeviceUtil.runTask(this,true, SCRIPT_FILE_NAME);
        } else if (v.getId() == R.id.iv_close) {
            DeviceUtil.stopAllTask();
            if (null != DeviceUtil.mFloatingWindow) {
                DeviceUtil.mWindowManager.removeView(DeviceUtil.mFloatingWindow);
                DeviceUtil.mFloatingWindow = null;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int rawX=(int)event.getRawX();
        int rawY=(int)event.getRawY();
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                DeviceUtil.mParams.x=rawX;
                DeviceUtil.mParams.y=rawY;
                DeviceUtil.mWindowManager.updateViewLayout(DeviceUtil.mFloatingWindow,DeviceUtil.mParams);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != DeviceUtil.mFloatingWindow) {
            DeviceUtil.mWindowManager.removeView(DeviceUtil.mFloatingWindow);
        }
    }

    class VideoSetting implements Serializable {
        private int runtime;
        private int gap;
        private boolean follow;
        private boolean praise;
        private boolean comment;
        private boolean message;
        private boolean collect;
        private boolean recomment;

        public int getRuntime() {
            return runtime;
        }

        public void setRuntime(int runtime) {
            this.runtime = runtime;
        }

        public int getGap() {
            return gap;
        }

        public void setGap(int gap) {
            this.gap = gap;
        }

        public boolean isFollow() {
            return follow;
        }

        public void setFollow(boolean follow) {
            this.follow = follow;
        }

        public boolean isPraise() {
            return praise;
        }

        public void setPraise(boolean praise) {
            this.praise = praise;
        }

        public boolean isComment() {
            return comment;
        }

        public void setComment(boolean comment) {
            this.comment = comment;
        }

        public boolean isMessage() {
            return message;
        }

        public void setMessage(boolean message) {
            this.message = message;
        }

        public boolean isCollect() {
            return collect;
        }

        public void setCollect(boolean collect) {
            this.collect = collect;
        }

        public boolean isRecomment() {
            return recomment;
        }

        public void setRecomment(boolean recomment) {
            this.recomment = recomment;
        }
    }
}

package org.autojs.autojs.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import com.google.gson.Gson;
import com.stardust.autojs.execution.ScriptExecution;
import com.stardust.autojs.script.StringScriptSource;
import com.stardust.pio.PFile;
import com.stardust.pio.PFiles;
import com.stardust.theme.app.ColorSelectActivity;
import com.stardust.theme.util.ListBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.autojs.autojs.R;
import org.autojs.autojs.R2;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.ui.BaseActivity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    @ViewById(R.id.et_gap)
    EditText etGap;
    @ViewById(R.id.st_follow)
    Switch switchFollow;
    @ViewById(R.id.st_praise)
    Switch switchPraise;
    @ViewById(R.id.st_comment)
    Switch switchComment;
    @ViewById(R.id.st_message)
    Switch switchMessage;
    @ViewById(R.id.st_collect)
    Switch switchCollect;
    @ViewById(R.id.st_recomment)
    Switch switchRecomment;
    private VideoSetting videoSetting;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;
    TextView tvStatus;
    View mFloatingWindow;

    private static final List<Pair<Integer, Integer>> COLOR_ITEMS = new ListBuilder<Pair<Integer, Integer>>()
            .add(new Pair<>(R.color.theme_color_red, R.string.theme_color_red))
            .add(new Pair<>(R.color.theme_color_pink, R.string.theme_color_pink))
            .add(new Pair<>(R.color.theme_color_purple, R.string.theme_color_purple))
            .add(new Pair<>(R.color.theme_color_dark_purple, R.string.theme_color_dark_purple))
            .add(new Pair<>(R.color.theme_color_indigo, R.string.theme_color_indigo))
            .add(new Pair<>(R.color.theme_color_blue, R.string.theme_color_blue))
            .add(new Pair<>(R.color.theme_color_light_blue, R.string.theme_color_light_blue))
            .add(new Pair<>(R.color.theme_color_blue_green, R.string.theme_color_blue_green))
            .add(new Pair<>(R.color.theme_color_cyan, R.string.theme_color_cyan))
            .add(new Pair<>(R.color.theme_color_green, R.string.theme_color_green))
            .add(new Pair<>(R.color.theme_color_light_green, R.string.theme_color_light_green))
            .add(new Pair<>(R.color.theme_color_yellow_green, R.string.theme_color_yellow_green))
            .add(new Pair<>(R.color.theme_color_yellow, R.string.theme_color_yellow))
            .add(new Pair<>(R.color.theme_color_amber, R.string.theme_color_amber))
            .add(new Pair<>(R.color.theme_color_orange, R.string.theme_color_orange))
            .add(new Pair<>(R.color.theme_color_dark_orange, R.string.theme_color_dark_orange))
            .add(new Pair<>(R.color.theme_color_brown, R.string.theme_color_brown))
            .add(new Pair<>(R.color.theme_color_gray, R.string.theme_color_gray))
            .add(new Pair<>(R.color.theme_color_blue_gray, R.string.theme_color_blue_gray))
            .list();

    public static void selectThemeColor(Context context) {
        List<ColorSelectActivity.ColorItem> colorItems = new ArrayList<>(COLOR_ITEMS.size());
        for (Pair<Integer, Integer> item : COLOR_ITEMS) {
            colorItems.add(new ColorSelectActivity.ColorItem(context.getString(item.second),
                    context.getResources().getColor(item.first)));
        }
        ColorSelectActivity.startColorSelect(context, context.getString(R.string.mt_color_picker_title), colorItems);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }

    @AfterViews
    void setUpUI() {
        setUpToolbar();
        getVideoSettings(this);
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
//        if (TextUtils.isEmpty(etRuntime.getText())) {
//            etRuntime.setText("10");
//        }
//        if (TextUtils.isEmpty(etGap.getText())) {
//            etGap.setText("1");
//        }
//        int runtime = Integer.parseInt(etRuntime.getText().toString());
//        int gap = Integer.parseInt(etGap.getText().toString());
//        boolean follow = switchFollow.isChecked();
//        boolean praise = switchPraise.isChecked();
//        boolean comment = switchComment.isChecked();
//        boolean message = switchMessage.isChecked();
//        boolean collect = switchCollect.isChecked();
//        boolean recomment = switchRecomment.isChecked();
//        VideoSetting videoSetting = new VideoSetting();
//        videoSetting.setRuntime(runtime);
//        videoSetting.setGap(gap);
//        videoSetting.setFollow(follow);
//        videoSetting.setPraise(praise);
//        videoSetting.setComment(comment);
//        videoSetting.setMessage(message);
//        videoSetting.setCollect(collect);
//        videoSetting.setRecomment(recomment);
//        String data = new Gson().toJson(videoSetting);
//        Log.d(TAG, data);
//        try {
//            //保存配置
//            saveToFile(data);
//            //运行脚本
//            runTask(true);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        showFloatingWindwo();
    }

    private void showFloatingWindwo() {
        //设置允许弹出悬浮窗口的权限
        requestWindowPermission();
        //创建窗口布局参数
        mParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
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
            mFloatingWindow = getLayoutInflater().inflate(R.layout.win_floating_tool, null, false);
            ImageView ivRun = mFloatingWindow.findViewById(R.id.iv_run);
            ImageView ivClose = mFloatingWindow.findViewById(R.id.iv_close);
            tvStatus = mFloatingWindow.findViewById(R.id.tv_status);
            ImageView ivDrag = mFloatingWindow.findViewById(R.id.iv_drag);
            mFloatingWindow.setOnTouchListener(this);
            ivRun.setOnClickListener(this);
            ivClose.setOnClickListener(this);
            mWindowManager.addView(mFloatingWindow, mParams);
        }
    }

    /**
     * 获取悬浮窗权限
     */
    private void requestWindowPermission() {
        //android 6.0或者之后的版本需要发一个intent让用户授权
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 100);
            }
        }
    }

    /**
     * 运行脚本
     *
     * @param showMessage
     * @return
     * @throws IOException
     */
    private ScriptExecution runTask(boolean showMessage) throws IOException {
        if (showMessage) {
            Toast.makeText(this, R.string.text_start_running, Toast.LENGTH_SHORT).show();
        }
//        String fileParentPath = getFilesDir().getAbsolutePath();
//        if (!fileParentPath.endsWith("/")) {
//            fileParentPath += "/";
//        }
//        ScriptExecution execution = Scripts.INSTANCE.runWithBroadcastSender(new File(fileParentPath + SCRIPT_FILE_NAME));
        ScriptExecution execution = Scripts.INSTANCE.run(new StringScriptSource(PFiles.read(getAssets().open(SCRIPT_FILE_NAME))));
        if (execution == null) {
            return null;
        }
        Log.d(TAG, execution.getId() + "");
        return execution;
    }

    private final static String TAG = "xpf";
    private final static String FILE_NAME = "video_settings.json";
    private final static String SCRIPT_FILE_NAME = "sample/wechat/hello.js";

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
     *
     * @param context
     */
    public void getVideoSettings(Context context) {
        if (videoSetting != null) {
            return;
        }
        if (getFileStreamPath(FILE_NAME).exists()) {
            Log.d(TAG, getFileStreamPath(FILE_NAME).getAbsolutePath());
            Observable.fromCallable(() -> loadSettingsFrom(openFileInput(FILE_NAME)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(setting -> {
                        videoSetting = setting;
                        etRuntime.setText(videoSetting.getRuntime() + "");
                        etGap.setText(videoSetting.getGap() + "");
                        switchFollow.setChecked(videoSetting.isFollow());
                        switchPraise.setChecked(videoSetting.isPraise());
                        switchComment.setChecked(videoSetting.isComment());
                        switchMessage.setChecked(videoSetting.isMessage());
                        switchCollect.setChecked(videoSetting.isCollect());
                        switchRecomment.setChecked(videoSetting.isRecomment());
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
            try {
                runTask(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (v.getId() == R.id.iv_close) {
            if (null != mFloatingWindow) {
                mWindowManager.removeView(mFloatingWindow);
                mFloatingWindow = null;
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
                mParams.x=rawX;
                mParams.y=rawY;
                mWindowManager.updateViewLayout(mFloatingWindow,mParams);
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
        if (null != mFloatingWindow) {
            mWindowManager.removeView(mFloatingWindow);
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

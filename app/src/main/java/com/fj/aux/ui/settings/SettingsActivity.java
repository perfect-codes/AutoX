package com.fj.aux.ui.settings;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.fj.aux.Pref;
import com.fj.aux.R;
import com.fj.aux.tool.AccessibilityServiceTool;
import com.fj.aux.tool.DeviceUtil;
import com.fj.aux.ui.BaseActivity;
import com.fj.aux.ui.floating.FloatyWindowManger;
import com.stardust.autojs.core.accessibility.AccessibilityService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/2/2.
 */
@EActivity(R.layout.activity_settings)
public class SettingsActivity extends BaseActivity {

    @AfterViews
    void setUpUI() {
        setUpToolbar();
    }

    private void setUpToolbar() {
        Toolbar toolbar = $(R.id.toolbar);
        toolbar.setTitle(R.string.text_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Click(R.id.tv_key)
    public void showAppKey(){
        String key = DeviceUtil.getDeviceKey();
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("应用KEY").setMessage(key)
                .setPositiveButton("复制", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        copyContentToClipboard(key, SettingsActivity.this);
                        Toast.makeText(SettingsActivity.this,"复制成功",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                })
                .create();
        dialog.show();
    }

    @Click(R.id.tv_comment)
    public void setComment(){
        startActivity(new Intent(this, CommentSettingsActivity_.class));
    }

    @CheckedChange(R.id.st_wu)
    void enableOrDisableAccessibilityService(Switch view) {
        //启用无障碍服务
        boolean isAccessibilityServiceEnabled = isAccessibilityServiceEnabled();
        boolean checked = view.isChecked();
        if (checked && !isAccessibilityServiceEnabled) {
            enableAccessibilityService();
        } else if (!checked && isAccessibilityServiceEnabled) {
            if (!AccessibilityService.Companion.disable()) {
                AccessibilityServiceTool.goToAccessibilitySetting();
            }
        }
    }

    @CheckedChange(R.id.st_floating)
    void showOrDismissFloatingWindow(Switch view) {
        boolean isFloatingWindowShowing = FloatyWindowManger.isCircularMenuShowing();
        boolean checked = view.isChecked();
        if (checked && !isFloatingWindowShowing) {
            FloatyWindowManger.showCircularMenu();
        } else if (!checked && isFloatingWindowShowing) {
            FloatyWindowManger.hideCircularMenu();
        }
    }

    @Click(R.id.tv_about)
    public void about(){
        Toast.makeText(SettingsActivity.this,"关于",Toast.LENGTH_SHORT).show();
    }

    /**
     * 复制内容到剪贴板
     *
     * @param content
     * @param context
     */
    public void copyContentToClipboard(String content, Context context) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

    private boolean isAccessibilityServiceEnabled() {
        return AccessibilityServiceTool.isAccessibilityServiceEnabled(this);
    }

    private void enableAccessibilityService() {
        if (!Pref.shouldEnableAccessibilityServiceByRoot()) {
            AccessibilityServiceTool.goToAccessibilitySetting();
            return;
        }
        enableAccessibilityServiceByRoot();
    }

    private void enableAccessibilityServiceByRoot() {
//        setProgress(mAccessibilityServiceItem, true);
        Observable.fromCallable(() -> AccessibilityServiceTool.enableAccessibilityServiceByRootAndWaitFor(4000))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(succeed -> {
                    if (!succeed) {
                        Toast.makeText(this, R.string.text_enable_accessibitliy_service_by_root_failed, Toast.LENGTH_SHORT).show();
                        AccessibilityServiceTool.goToAccessibilitySetting();
                    }
//                    setProgress(mAccessibilityServiceItem, false);
                });
    }
}

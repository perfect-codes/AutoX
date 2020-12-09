package org.autojs.autojs.ui.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.afollestad.materialdialogs.util.DialogUtils;
import com.stardust.theme.preference.ThemeColorPreferenceFragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.autojs.autojs.R;
import org.autojs.autojs.ui.BaseActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import butterknife.OnClick;
import de.psdev.licensesdialog.LicensesDialog;

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
        String fingerPrint = android.os.Build.FINGERPRINT;
        String md5Str = md5(fingerPrint);
        String key = md5Str.substring(8,24);
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
        Toast.makeText(SettingsActivity.this,"设置评论",Toast.LENGTH_SHORT).show();
    }

    @CheckedChange(R.id.st_wu)
    public void changeWu(){
        Toast.makeText(SettingsActivity.this,"开启无障碍",Toast.LENGTH_SHORT).show();
    }

    @CheckedChange(R.id.st_floating)
    public void changeFloating(){
        Toast.makeText(SettingsActivity.this,"授权启用浮窗",Toast.LENGTH_SHORT).show();
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

    private String md5(String val){
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
            System.out.println("计算MD5值发生错误");
            e.printStackTrace();
        }
        return null;
    }
}

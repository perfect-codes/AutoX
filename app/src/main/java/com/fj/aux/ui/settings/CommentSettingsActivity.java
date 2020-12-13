package com.fj.aux.ui.settings;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.fj.aux.R;
import com.fj.aux.ui.BaseActivity;
import com.google.gson.Gson;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/2/2.
 */
@EActivity(R.layout.activity_comment_settings)
public class CommentSettingsActivity extends BaseActivity {

    @ViewById(R.id.et_comment)
    EditText etComment;
    @ViewById(R.id.et_message)
    EditText etMessage;

    List<String> comments, messages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @AfterViews
    void setUpUI() {
        setUpToolbar();
        getCurrentSettings();
    }

    private void setUpToolbar() {
        Toolbar toolbar = $(R.id.toolbar);
        toolbar.setTitle(R.string.text_comment_setting);
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
    void save() {
        if (!TextUtils.isEmpty(etComment.getText())) {
            String commentStr = etComment.getText().toString();
            String[] commentArray = commentStr.split("\n");
            if (comments == null) {
                comments = new ArrayList<>();
            }
            comments.clear();
            for (String item : commentArray) {
                comments.add(item);
            }
        }
        if (!TextUtils.isEmpty(etMessage.getText())) {
            String messageStr = etMessage.getText().toString();
            String[] messageArray = messageStr.split("\n");
            if (messages == null) {
                messages = new ArrayList<>();
            }
            messages.clear();
            for (String item : messageArray) {
                messages.add(item);
            }
        }
        Map<String, List<String>> map = new HashMap<>(2);
        map.put("comments", comments);
        map.put("messages", messages);
        String data = new Gson().toJson(map);
        Log.d(TAG, data);
        try {
            //保存配置
            saveToFile(data);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
            finish();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private final static String TAG = "xpf";
    private final static String FILE_NAME = "comment_settings.json";

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
    public void getCurrentSettings() {
        if (getFileStreamPath(FILE_NAME).exists()) {
            Log.d(TAG, getFileStreamPath(FILE_NAME).getAbsolutePath());
            Observable.fromCallable(() -> loadSettingsFrom(openFileInput(FILE_NAME)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(setting -> {
                        Log.d(TAG, setting.toString());
                        comments = setting.get("comments");
                        StringBuffer commentStr = new StringBuffer();
                        for (int i = 0; i < comments.size(); i++) {
                            if (i > 0) {
                                commentStr.append("\n");
                            }
                            commentStr.append(comments.get(i));
                        }
                        etComment.setText(commentStr.toString());
                        etComment.setSelection(etComment.getText().length());
                        messages = setting.get("messages");
                        StringBuffer messageStr = new StringBuffer();
                        for (int i = 0; i < messages.size(); i++) {
                            if (i > 0) {
                                messageStr.append("\n");
                            }
                            messageStr.append(messages.get(i));
                        }
                        etMessage.setText(messageStr);
                        etMessage.setSelection(etMessage.getText().length());//将光标移至文字末尾
                    });
        }
    }

    private Map<String, List<String>> loadSettingsFrom(InputStream inputStream) {
        Gson gson = new Gson();
        return gson.fromJson(new InputStreamReader(inputStream), Map.class);
    }

}

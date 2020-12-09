package org.autojs.autojs.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stardust.pio.PFiles;
import com.stardust.theme.app.ColorSelectActivity;
import com.stardust.theme.preference.ThemeColorPreferenceFragment;
import com.stardust.theme.util.ListBuilder;
import com.stardust.util.MapBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.autojs.autojs.R;
import org.autojs.autojs.model.indices.Module;
import org.autojs.autojs.tool.Observers;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.ui.error.IssueReporterActivity;
import org.autojs.autojs.ui.update.UpdateCheckDialog;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import de.psdev.licensesdialog.LicenseResolver;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.License;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/2/2.
 */
@EActivity(R.layout.activity_video_settings)
public class VideoSettingsActivity extends BaseActivity {

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
        if (TextUtils.isEmpty(etRuntime.getText())) {
            etRuntime.setText("10");
        }
        if (TextUtils.isEmpty(etGap.getText())) {
            etGap.setText("1");
        }
        int runtime = Integer.parseInt(etRuntime.getText().toString());
        int gap = Integer.parseInt(etGap.getText().toString());
        boolean follow = switchFollow.isChecked();
        boolean praise = switchPraise.isChecked();
        boolean comment = switchComment.isChecked();
        boolean message = switchMessage.isChecked();
        boolean collect = switchCollect.isChecked();
        boolean recomment = switchRecomment.isChecked();
        VideoSetting videoSetting = new VideoSetting();
        videoSetting.setRuntime(runtime);
        videoSetting.setGap(gap);
        videoSetting.setFollow(follow);
        videoSetting.setPraise(praise);
        videoSetting.setComment(comment);
        videoSetting.setMessage(message);
        videoSetting.setCollect(collect);
        videoSetting.setRecomment(recomment);
        String data = new Gson().toJson(videoSetting);
        Log.d(TAG, data);
        try {
            saveToFile(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        runTask();
    }

    private void runTask() {

    }

    private final static String TAG = "xpf";
    private final static String FILE_NAME = "video_settings.json";

    private void saveToFile(String val) throws IOException{
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
            if (bw!=null){
                bw.close();
            }
            if (fout!=null){
                fout.close();
            }
        }
//        save()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(Observers.emptyConsumer(), e -> {
//                    e.printStackTrace();
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
    }

//    public Observable<String> save() throws IOException {
//        this.getAssets().open("indices/video_settings.json");
//        PFiles.append();
//        Uri mUri = Uri.fromFile()
////        String path = mUri.getPath();
//        PFiles.move(path, path + ".bak");
//        return Observable.just(mEditor.getText())
//                .observeOn(Schedulers.io())
//                .doOnNext(s -> PFiles.write(getContext().getContentResolver().openOutputStream(mUri), s))
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext(s -> {
//                    mEditor.markTextAsSaved();
//                    setMenuItemStatus(R.id.save, false);
//                });
//    }

    public void getVideoSettings(Context context) {
        if (videoSetting != null) {
            return;
        }
        if (getFileStreamPath(FILE_NAME).exists()){
            Log.d(TAG,getFileStreamPath(FILE_NAME).getAbsolutePath());
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

package com.fj.aux.external.shortcut;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.fj.aux.model.script.PathChecker;
import com.fj.aux.external.ScriptIntents;
import com.fj.aux.model.script.ScriptFile;
import com.fj.aux.model.script.Scripts;

/**
 * Created by Stardust on 2017/1/23.
 */
public class ShortcutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String path = getIntent().getStringExtra(ScriptIntents.EXTRA_KEY_PATH);
        if (new PathChecker(this).checkAndToastError(path)) {
            runScriptFile(path);
        }
        finish();
    }

    private void runScriptFile(String path) {
        try {
            Scripts.INSTANCE.run(new ScriptFile(path));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}

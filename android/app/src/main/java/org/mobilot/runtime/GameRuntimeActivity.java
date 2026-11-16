package org.mobilot.runtime;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.godotengine.godot.Godot;
import org.godotengine.godot.GodotFragment;
import org.godotengine.godot.GodotHost;
import org.godotengine.godot.plugin.GodotPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class GameRuntimeActivity extends AppCompatActivity implements GodotHost {
    private GodotFragment godotFragment;
    private String pckPath;
    private String driver = "opengl3";
    private String method = "gl_compatibility";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            String p = intent.getStringExtra("pck_path");
            if (p != null) pckPath = p;
            String d = intent.getStringExtra("driver");
            if (d != null) driver = d;
            String m = intent.getStringExtra("method");
            if (m != null) method = m;
        }

        FrameLayout root = new FrameLayout(this);
        FrameLayout container = new FrameLayout(this);
        int containerId = View.generateViewId();
        container.setId(containerId);
        root.addView(container, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        Button exitBtn = new Button(this);
        exitBtn.setText("退出游戏");
        exitBtn.setBackgroundColor(Color.argb(140, 0, 0, 0));
        exitBtn.setTextColor(Color.WHITE);
        exitBtn.setOnClickListener(v -> terminateRuntimeProcess());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.END);
        lp.setMargins(24, 24, 24, 24);
        root.addView(exitBtn, lp);

        setContentView(root);

        FragmentManager fm = getSupportFragmentManager();
        Fragment current = fm.findFragmentById(containerId);
        if (current instanceof GodotFragment) {
            godotFragment = (GodotFragment) current;
        } else {
            godotFragment = new GodotFragment();
            fm.beginTransaction()
                    .replace(containerId, godotFragment)
                    .commitNowAllowingStateLoss();
        }
    }

    @Override public Activity getActivity() { return this; }
    @Override public Godot getGodot() { return godotFragment != null ? godotFragment.getGodot() : null; }

    @Override
    public List<String> getCommandLine() {
        if (pckPath == null) {
            return Arrays.asList("--rendering-driver", driver);
        }
        return Arrays.asList(
                "--main-pack", pckPath,
                "--rendering-driver", driver,
                "--rendering-method", method);
    }

    @Override
    public Set<GodotPlugin> getHostPlugins(@NonNull Godot engine) {
        return Collections.singleton(new MobilotPlugin(engine));
    }

    @Override
    public void onGodotForceQuit(Godot instance) {
        terminateRuntimeProcess();
    }

    private void terminateRuntimeProcess() {
        finishAndRemoveTask();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

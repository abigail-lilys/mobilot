package org.mobilot.runtime;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class LogViewerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File gameDir = new File(getFilesDir(), "games");
        File godotLog = new File(gameDir, "runtime_godot.log");
        File crashLog = new File(gameDir, "runtime_crash.log");

        String godotContent = readFile(godotLog);
        String crashContent = readFile(crashLog);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);

        Button btnGodot = new Button(this);
        btnGodot.setText("Godot 日志");
        btnGodot.setOnClickListener(v -> showContent("runtime_godot.log", godotContent, godotLog));
        bar.addView(btnGodot);

        Button btnCrash = new Button(this);
        btnCrash.setText("崩溃日志");
        btnCrash.setOnClickListener(v -> showContent("runtime_crash.log", crashContent, crashLog));
        bar.addView(btnCrash);

        root.addView(bar);

        TextView tv = new TextView(this);
        tv.setTextSize(11f);
        tv.setPadding(16, 16, 16, 16);
        tv.setText("选择上方按钮查看对应日志。\n\n"
                + "Godot 日志路径：\n" + godotLog.getAbsolutePath() + "\n\n"
                + "崩溃日志路径：\n" + crashLog.getAbsolutePath());

        ScrollView scroll = new ScrollView(this);
        scroll.addView(tv);
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        setContentView(root);
    }

    private void showContent(String title, String content, File f) {
        String display;
        if (!f.exists()) {
            display = "文件不存在：\n" + f.getAbsolutePath();
        } else if (content.isEmpty()) {
            display = "文件为空：\n" + f.getAbsolutePath();
        } else {
            display = "== " + title + " ==\n" + content;
        }

        TextView tv = new TextView(this);
        tv.setTextSize(10f);
        tv.setTextIsSelectable(true);
        tv.setText(display);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(tv);
        setContentView(scroll);
    }

    private String readFile(File f) {
        if (!f.exists()) return "";
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            sb.append("读取失败: ").append(e.getMessage());
        }
        return sb.toString();
    }
}

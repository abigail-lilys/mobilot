package org.mobilot.runtime;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {
    private static final int REQ_PICK_PCK = 1001;
    private TextView statusText;
    private String importedPckPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NativeBridge.nativeInit(getFilesDir().getAbsolutePath());

        statusText = new TextView(this);
        statusText.setTextSize(15f);
        statusText.setPadding(32, 48, 32, 32);
        statusText.setText("Mobilot Phase 1\n" + NativeBridge.nativeVersion()
                + "\n\n请选择一个 .pck 游戏包");

        Button pickBtn = new Button(this);
        pickBtn.setText("选择 .pck 游戏包");
        pickBtn.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");
            startActivityForResult(i, REQ_PICK_PCK);
        });

        Button launchBtn = new Button(this);
        launchBtn.setText("启动运行时 (:godot_runtime)");
        launchBtn.setOnClickListener(v -> launchGame());

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.addView(statusText);
        root.addView(pickBtn);
        root.addView(launchBtn);
        setContentView(root);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_PCK && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) importPck(uri);
        }
    }

    private void importPck(Uri uri) {
        statusText.setText("正在导入...");
        new Thread(() -> {
            try {
                File dest = new File(getFilesDir(), "games/active_game.pck");
                if (dest.getParentFile() != null) dest.getParentFile().mkdirs();

                try (InputStream in = getContentResolver().openInputStream(uri);
                     OutputStream out = new FileOutputStream(dest)) {
                    if (in == null) throw new IllegalStateException("openInputStream returned null");
                    byte[] buf = new byte[256 * 1024];
                    int n;
                    long total = 0;
                    while ((n = in.read(buf)) > 0) {
                        out.write(buf, 0, n);
                        total += n;
                    }
                    final long finalTotal = total;
                    runOnUiThread(() -> {
                        importedPckPath = dest.getAbsolutePath();
                        statusText.setText("导入成功 (" + (finalTotal / 1024) + " KB)\n"
                                + dest.getAbsolutePath());
                    });
                }
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "导入失败: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void launchGame() {
        if (importedPckPath == null) {
            Toast.makeText(this, "请先选择 .pck 游戏包", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(this, GameRuntimeActivity.class);
        i.putExtra("pck_path", importedPckPath);
        startActivity(i);
    }
}

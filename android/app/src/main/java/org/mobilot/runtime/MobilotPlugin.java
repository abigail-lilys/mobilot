package org.mobilot.runtime;

import java.util.Collections;
import java.util.Set;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;

/**
 * 运行时插件：向任意外部 .pck 自动注入 assets 中的 mobilot_bridge.gdextension。
 * 从而无需改动目标游戏包，就能让 C++ fuse + Rust core 在 Godot 启动时挂载。
 */
public class MobilotPlugin extends GodotPlugin {
    static {
        System.loadLibrary("mobilot_core");
        System.loadLibrary("mobilot_fuse");
    }

    public MobilotPlugin(Godot godot) {
        super(godot);
    }

    @Override
    public String getPluginName() {
        return "Mobilot";
    }

    @Override
    public Set<String> getPluginGDExtensionLibrariesPaths() {
        return Collections.singleton("mobilot_runtime/mobilot_bridge.gdextension");
    }
}

// mobilot_core —— Rust 底层核心
// 同时暴露两套符号：
//   * C ABI: mobilot_core_on_godot_init / deinit  —— 供 C++ fuse 链接
//   * JNI  : Java_org_mobilot_runtime_NativeBridge_* —— 供 Kotlin/Java 调用
// Phase 1 只验证链路，具体 pck/gpu 逻辑在 Phase 1.1 补进来。

use log::info;

// ---------- Android 日志初始化 ----------
pub fn init_android_logger() {
    #[cfg(target_os = "android")]
    {
        use android_logger::Config;
        use log::LevelFilter;
        android_logger::init_once(
            Config::default()
                .with_max_level(LevelFilter::Info)
                .with_tag("MobilotCore"),
        );
    }
}

// ---------- C ABI（供 C++ fuse 链接）----------
#[no_mangle]
pub extern "C" fn mobilot_core_on_godot_init(init_level: i32) {
    init_android_logger();
    info!("mobilot_core: Godot initialized at level {}", init_level);
}

#[no_mangle]
pub extern "C" fn mobilot_core_on_godot_deinit(init_level: i32) {
    info!("mobilot_core: Godot deinitialized at level {}", init_level);
}

// ---------- JNI（供 Java NativeBridge 调用）----------
#[cfg(target_os = "android")]
mod android_jni {
    use super::*;
    use jni::objects::{JClass, JString};
    use jni::sys::jstring;
    use jni::JNIEnv;

    #[no_mangle]
    pub extern "system" fn Java_org_mobilot_runtime_NativeBridge_nativeVersion<'local>(
        mut env: JNIEnv<'local>,
        _class: JClass<'local>,
    ) -> jstring {
        init_android_logger();
        env.new_string("mobilot 0.1.0-phase1")
            .expect("new_string")
            .into_raw()
    }

    #[no_mangle]
    pub extern "system" fn Java_org_mobilot_runtime_NativeBridge_nativeInit<'local>(
        mut env: JNIEnv<'local>,
        _class: JClass<'local>,
        assets_path: JString<'local>,
    ) {
        init_android_logger();
        let path: String = env.get_string(&assets_path).map(Into::into).unwrap_or_default();
        info!("mobilot_core: nativeInit assets={}", path);
    }
}

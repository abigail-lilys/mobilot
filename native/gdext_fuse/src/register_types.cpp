#include "register_types.h"
#include "mobilot_core.h"

#include <gdextension_interface.h>
#include <godot_cpp/core/defs.hpp>
#include <godot_cpp/godot.hpp>

void initialize_mobilot_fuse(godot::ModuleInitializationLevel p_level) {
    // Rust 核心在任意初始化级别都会收到回调，自己做分流
    mobilot_core_on_godot_init(static_cast<int32_t>(p_level));
    if (p_level != godot::MODULE_INITIALIZATION_LEVEL_SCENE) return;
    // Phase 1: 暂不注册任何自定义类；Phase 2 起在这里注册 KiriBridge 等
}

void uninitialize_mobilot_fuse(godot::ModuleInitializationLevel p_level) {
    if (p_level == godot::MODULE_INITIALIZATION_LEVEL_SCENE) {
        // Phase 2: 反注册单例
    }
    mobilot_core_on_godot_deinit(static_cast<int32_t>(p_level));
}

extern "C" {
GDExtensionBool GDE_EXPORT mobilot_fuse_init(
    GDExtensionInterfaceGetProcAddress p_get_proc_address,
    const GDExtensionClassLibraryPtr p_library,
    GDExtensionInitialization *r_initialization) {
    godot::GDExtensionBinding::InitObject init_obj(
        p_get_proc_address, p_library, r_initialization);
    init_obj.register_initializer(initialize_mobilot_fuse);
    init_obj.register_terminator(uninitialize_mobilot_fuse);
    init_obj.set_minimum_library_initialization_level(
        godot::MODULE_INITIALIZATION_LEVEL_SCENE);
    return init_obj.init();
}
} // extern "C"

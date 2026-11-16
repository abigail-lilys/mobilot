#pragma once
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif
void mobilot_core_on_godot_init(int32_t init_level);
void mobilot_core_on_godot_deinit(int32_t init_level);
#ifdef __cplusplus
}
#endif

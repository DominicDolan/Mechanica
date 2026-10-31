#pragma once
#include <stdint.h>
uint32_t mp_world_create(float gx, float gy);
void mp_world_destroy(uint32_t world);
void mp_world_step(uint32_t world, float dt, int substeps);
uint64_t mp_body_create(uint32_t world, int dynamic, float x, float y, float angle);
void mp_body_destroy(uint64_t body);
void mp_body_add_circle(uint64_t body, float x, float y, float radius, float density, float friction, float restitution);
void mp_body_add_box(uint64_t body, float x, float y, float half_width, float half_height, float angle, float density, float friction, float restitution);
float mp_body_position_x(uint64_t body);
float mp_body_position_y(uint64_t body);
float mp_body_angle(uint64_t body);
void mp_body_set_transform(uint64_t body, float x, float y, float angle);
float mp_body_velocity_x(uint64_t body);
float mp_body_velocity_y(uint64_t body);
void mp_body_set_velocity(uint64_t body, float x, float y);
float mp_body_angular_velocity(uint64_t body);
void mp_body_set_angular_velocity(uint64_t body, float velocity);

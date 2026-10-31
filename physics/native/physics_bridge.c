#include "physics_bridge.h"
#include <box2d/box2d.h>

uint32_t mp_world_create(float gx, float gy) {
    b2WorldDef def = b2DefaultWorldDef();
    def.gravity = (b2Vec2){gx, gy};
    return b2StoreWorldId(b2CreateWorld(&def));
}
void mp_world_destroy(uint32_t world) { b2DestroyWorld(b2LoadWorldId(world)); }
void mp_world_step(uint32_t world, float dt, int substeps) { b2World_Step(b2LoadWorldId(world), dt, substeps); }
uint64_t mp_body_create(uint32_t world, int dynamic, float x, float y, float angle) {
    b2BodyDef def = b2DefaultBodyDef();
    def.type = dynamic ? b2_dynamicBody : b2_staticBody;
    def.position = (b2Vec2){x, y};
    def.rotation = b2MakeRot(angle);
    return b2StoreBodyId(b2CreateBody(b2LoadWorldId(world), &def));
}
void mp_body_destroy(uint64_t body) { b2DestroyBody(b2LoadBodyId(body)); }
static b2ShapeDef shape_def(float density, float friction, float restitution) {
    b2ShapeDef def = b2DefaultShapeDef();
    def.density = density;
    def.material.friction = friction;
    def.material.restitution = restitution;
    return def;
}
void mp_body_add_circle(uint64_t body, float x, float y, float radius, float density, float friction, float restitution) {
    b2ShapeDef def = shape_def(density, friction, restitution);
    b2Circle circle = {{x, y}, radius};
    b2CreateCircleShape(b2LoadBodyId(body), &def, &circle);
}
void mp_body_add_box(uint64_t body, float x, float y, float half_width, float half_height, float angle, float density, float friction, float restitution) {
    b2ShapeDef def = shape_def(density, friction, restitution);
    b2Polygon box = b2MakeOffsetBox(half_width, half_height, (b2Vec2){x, y}, b2MakeRot(angle));
    b2CreatePolygonShape(b2LoadBodyId(body), &def, &box);
}
float mp_body_position_x(uint64_t body) { return b2Body_GetPosition(b2LoadBodyId(body)).x; }
float mp_body_position_y(uint64_t body) { return b2Body_GetPosition(b2LoadBodyId(body)).y; }
float mp_body_angle(uint64_t body) { return b2Rot_GetAngle(b2Body_GetRotation(b2LoadBodyId(body))); }
void mp_body_set_transform(uint64_t body, float x, float y, float angle) { b2Body_SetTransform(b2LoadBodyId(body), (b2Vec2){x, y}, b2MakeRot(angle)); }
float mp_body_velocity_x(uint64_t body) { return b2Body_GetLinearVelocity(b2LoadBodyId(body)).x; }
float mp_body_velocity_y(uint64_t body) { return b2Body_GetLinearVelocity(b2LoadBodyId(body)).y; }
void mp_body_set_velocity(uint64_t body, float x, float y) { b2Body_SetLinearVelocity(b2LoadBodyId(body), (b2Vec2){x, y}); }
float mp_body_angular_velocity(uint64_t body) { return b2Body_GetAngularVelocity(b2LoadBodyId(body)); }
void mp_body_set_angular_velocity(uint64_t body, float velocity) { b2Body_SetAngularVelocity(b2LoadBodyId(body), velocity); }

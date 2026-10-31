#include "physics_bridge.h"
#include <box2d/box2d.h>
#include <stdlib.h>

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
uint64_t mp_wheel_create(uint32_t world, uint64_t a, uint64_t b, float anchor_x, float anchor_y, float axis_x, float axis_y, float hertz, float damping, float lower, float upper) {
    b2BodyId body_a = b2LoadBodyId(a), body_b = b2LoadBodyId(b);
    b2Vec2 anchor = {anchor_x, anchor_y}, axis = {axis_x, axis_y};
    b2WheelJointDef def = b2DefaultWheelJointDef();
    def.bodyIdA = body_a;
    def.bodyIdB = body_b;
    def.localAnchorA = b2Body_GetLocalPoint(body_a, anchor);
    def.localAnchorB = b2Body_GetLocalPoint(body_b, anchor);
    def.localAxisA = b2Body_GetLocalVector(body_a, axis);
    def.enableSpring = true;
    def.hertz = hertz;
    def.dampingRatio = damping;
    def.enableLimit = true;
    def.lowerTranslation = lower;
    def.upperTranslation = upper;
    return b2StoreJointId(b2CreateWheelJoint(b2LoadWorldId(world), &def));
}
void mp_wheel_destroy(uint64_t joint) { b2DestroyJoint(b2LoadJointId(joint)); }

// xy includes one ghost point at each end; only the interior edges collide.
int mp_body_add_chain(uint64_t body, const float* xy, int count, float friction) {
    b2Vec2* points = malloc(sizeof(b2Vec2) * count);
    if (!points) return 0;
    for (int i = 0; i < count; ++i) points[i] = (b2Vec2){xy[2*i], xy[2*i+1]};
    b2SurfaceMaterial material = b2DefaultSurfaceMaterial();
    material.friction = friction;
    b2ChainDef def = b2DefaultChainDef();
    def.points = points;
    def.count = count;
    def.materials = &material;
    def.materialCount = 1;
    b2ChainId chain = b2CreateChain(b2LoadBodyId(body), &def);
    free(points);
    return B2_IS_NON_NULL(chain);
}
void mp_body_fixed_rotation(uint64_t body, int fixed) { b2Body_SetFixedRotation(b2LoadBodyId(body), fixed != 0); }
// Query only static terrain; player bodies must not ground themselves.
static float ground_ray(b2ShapeId shape, b2Vec2 point, b2Vec2 normal, float fraction, void* context) {
    (void)point;
    if (b2Body_GetType(b2Shape_GetBody(shape)) != b2_staticBody || normal.y < 0.45f) return -1.0f;
    *(float*)context = fraction;
    return fraction;
}
float mp_world_ground_ray(uint32_t world, float x, float y, float length) {
    float fraction = -1.0f;
    b2World_CastRay(b2LoadWorldId(world), (b2Vec2){x,y}, (b2Vec2){0,-length}, b2DefaultQueryFilter(), ground_ray, &fraction);
    return fraction;
}

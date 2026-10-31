#include <jni.h>
#include "physics_bridge.h"
#define JNI_METHOD(name) Java_com_mechanica_engine_physics_internal_NativeBridge_##name
#define UNUSED (void)env; (void)self
JNIEXPORT jint JNICALL JNI_METHOD(worldCreate)(JNIEnv *env, jobject self, jfloat x, jfloat y) { UNUSED; return (jint)mp_world_create(x, y); }
JNIEXPORT void JNICALL JNI_METHOD(worldDestroy)(JNIEnv *env, jobject self, jint w) { UNUSED; mp_world_destroy((uint32_t)w); }
JNIEXPORT void JNICALL JNI_METHOD(worldStep)(JNIEnv *env, jobject self, jint w, jfloat dt, jint n) { UNUSED; mp_world_step((uint32_t)w, dt, n); }
JNIEXPORT jlong JNICALL JNI_METHOD(bodyCreate)(JNIEnv *env, jobject self, jint w, jboolean dynamic, jfloat x, jfloat y, jfloat angle) { UNUSED; return (jlong)mp_body_create((uint32_t)w, dynamic, x, y, angle); }
JNIEXPORT void JNICALL JNI_METHOD(bodyDestroy)(JNIEnv *env, jobject self, jlong b) { UNUSED; mp_body_destroy((uint64_t)b); }
JNIEXPORT void JNICALL JNI_METHOD(bodyAddCircle)(JNIEnv *env, jobject self, jlong b, jfloat x, jfloat y, jfloat radius, jfloat density, jfloat friction, jfloat restitution) { UNUSED; mp_body_add_circle((uint64_t)b, x, y, radius, density, friction, restitution); }
JNIEXPORT void JNICALL JNI_METHOD(bodyAddBox)(JNIEnv *env, jobject self, jlong b, jfloat x, jfloat y, jfloat hw, jfloat hh, jfloat angle, jfloat density, jfloat friction, jfloat restitution) { UNUSED; mp_body_add_box((uint64_t)b, x, y, hw, hh, angle, density, friction, restitution); }
JNIEXPORT jfloat JNICALL JNI_METHOD(bodyPositionX)(JNIEnv *env, jobject self, jlong b) { UNUSED; return mp_body_position_x((uint64_t)b); }
JNIEXPORT jfloat JNICALL JNI_METHOD(bodyPositionY)(JNIEnv *env, jobject self, jlong b) { UNUSED; return mp_body_position_y((uint64_t)b); }
JNIEXPORT jfloat JNICALL JNI_METHOD(bodyAngle)(JNIEnv *env, jobject self, jlong b) { UNUSED; return mp_body_angle((uint64_t)b); }
JNIEXPORT void JNICALL JNI_METHOD(bodySetTransform)(JNIEnv *env, jobject self, jlong b, jfloat x, jfloat y, jfloat angle) { UNUSED; mp_body_set_transform((uint64_t)b, x, y, angle); }
JNIEXPORT jfloat JNICALL JNI_METHOD(bodyVelocityX)(JNIEnv *env, jobject self, jlong b) { UNUSED; return mp_body_velocity_x((uint64_t)b); }
JNIEXPORT jfloat JNICALL JNI_METHOD(bodyVelocityY)(JNIEnv *env, jobject self, jlong b) { UNUSED; return mp_body_velocity_y((uint64_t)b); }
JNIEXPORT void JNICALL JNI_METHOD(bodySetVelocity)(JNIEnv *env, jobject self, jlong b, jfloat x, jfloat y) { UNUSED; mp_body_set_velocity((uint64_t)b, x, y); }
JNIEXPORT jfloat JNICALL JNI_METHOD(bodyAngularVelocity)(JNIEnv *env, jobject self, jlong b) { UNUSED; return mp_body_angular_velocity((uint64_t)b); }
JNIEXPORT void JNICALL JNI_METHOD(bodySetAngularVelocity)(JNIEnv *env, jobject self, jlong b, jfloat v) { UNUSED; mp_body_set_angular_velocity((uint64_t)b, v); }
JNIEXPORT jlong JNICALL JNI_METHOD(wheelCreate)(JNIEnv *env, jobject self, jint w, jlong a, jlong b, jfloat ax, jfloat ay, jfloat ux, jfloat uy, jfloat hz, jfloat damping, jfloat lower, jfloat upper) { UNUSED; return (jlong)mp_wheel_create((uint32_t)w, (uint64_t)a, (uint64_t)b, ax, ay, ux, uy, hz, damping, lower, upper); }
JNIEXPORT void JNICALL JNI_METHOD(wheelDestroy)(JNIEnv *env, jobject self, jlong joint) { UNUSED; mp_wheel_destroy((uint64_t)joint); }

JNIEXPORT jboolean JNICALL JNI_METHOD(bodyAddChain)(JNIEnv *env, jobject self, jlong b, jfloatArray xy, jfloat friction) {
    (void)self;
    jfloat* points = (*env)->GetFloatArrayElements(env, xy, NULL);
    if (!points) return JNI_FALSE;
    int result = mp_body_add_chain((uint64_t)b, points, (*env)->GetArrayLength(env, xy) / 2, friction);
    (*env)->ReleaseFloatArrayElements(env, xy, points, JNI_ABORT);
    return result ? JNI_TRUE : JNI_FALSE;
}
JNIEXPORT void JNICALL JNI_METHOD(bodyFixedRotation)(JNIEnv *env, jobject self, jlong b, jboolean fixed) { UNUSED; mp_body_fixed_rotation((uint64_t)b, fixed); }
JNIEXPORT jfloat JNICALL JNI_METHOD(worldGroundRay)(JNIEnv *env, jobject self, jint w, jfloat x, jfloat y, jfloat length) { UNUSED; return mp_world_ground_ray((uint32_t)w, x, y, length); }

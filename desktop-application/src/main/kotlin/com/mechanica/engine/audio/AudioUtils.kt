package com.mechanica.engine.audio

import org.lwjgl.openal.AL11.*

fun distanceModelEnumToALNum(dm: Listener.DistanceModel): Int {
    return when(dm) {
        Listener.DistanceModel.INVERSE -> if (dm.clamped) AL_INVERSE_DISTANCE_CLAMPED else AL_INVERSE_DISTANCE
        Listener.DistanceModel.LINEAR -> if (dm.clamped) AL_LINEAR_DISTANCE_CLAMPED else AL_LINEAR_DISTANCE
        Listener.DistanceModel.EXPONENTIAL -> if (dm.clamped) AL_EXPONENT_DISTANCE_CLAMPED else AL_EXPONENT_DISTANCE
    }
}

fun distanceModelALNumToEnum(dm: Int): Listener.DistanceModel {
    return when(dm) {
        AL_INVERSE_DISTANCE -> Listener.DistanceModel.INVERSE.also { it.clamped = false }
        AL_INVERSE_DISTANCE_CLAMPED -> Listener.DistanceModel.INVERSE.also { it.clamped = true }
        AL_EXPONENT_DISTANCE -> Listener.DistanceModel.EXPONENTIAL.also { it.clamped = false }
        AL_EXPONENT_DISTANCE_CLAMPED -> Listener.DistanceModel.EXPONENTIAL.also { it.clamped = true }
        AL_LINEAR_DISTANCE -> Listener.DistanceModel.LINEAR.also { it.clamped = false }
        AL_LINEAR_DISTANCE_CLAMPED -> Listener.DistanceModel.LINEAR.also { it.clamped = true }
        else -> Listener.DistanceModel.INVERSE
    }
}
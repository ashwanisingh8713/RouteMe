package com.ar.navigation.pathmodel

import com.google.ar.core.Anchor


/**
 * Created by Ashwani Kumar Singh on 16,March,2023.
 */

/**
 * Created by Ashwani Kumar Singh on 20,March,2023.
 *
#To move up, Y axis should be changed with +value
#To move down, Y axis should be changed with -value
#To move right, X axis should be changed with +value
#To move left, X axis should be changed with -value
#To move forward, Z axis should be changed with +value
#To move backward, Z axis should be changed with -value
 // This Anchor should be calculated with respect to Camera Position
 */
data class RouteAnchor(var X: Float, var Y: Float, var Z: Float,
                       var distanceToNext: Float = 0f,
                       var distanceCovered: Float = 0f,
                       var anchorAxis: FloatArray,
                       var directionToNext: RouteDirection = RouteDirection.UNIDENTIFIED,
                       var makeVisible: Boolean = false,
                       var anchor: Anchor? = null,
                       var angle: Double = 0.0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RouteAnchor

        if (X != other.X) return false
        if (Y != other.Y) return false
        if (Z != other.Z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = X.hashCode()
        result = 31 * result + Y.hashCode()
        result = 31 * result + Z.hashCode()
        return result
    }
}


enum class RouteDirection {
    LEFT, RIGHT, FORWARD, BACKWARD, LEFT_FORWARD,
    LEFT_BACKWARD, RIGHT_FORWARD, RIGHT_BACKWARD, UNIDENTIFIED, UP, DOWN
}



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
class RouteAnchor {

    var imgType: String = ""
    var x: Float = 0f
    var y: Float = 0f
    var z: Float = 0f
    var distanceToNext: Float = 0f
    var distanceCovered: Float = 0f
//    var anchorAxis: FloatArray? = null
    var directionToNext: RouteDirection = RouteDirection.UNIDENTIFIED
    var makeVisible: Boolean = false
    var anchor: Anchor? = null
    var angle: Double = 0.0
    var isStartPoint = false

    var nodePrev: RouteAnchor? = null
    var nodeNext: RouteAnchor? = null
    var childNode = mutableListOf<RouteAnchor>()

    fun addChildNode(node: RouteAnchor) {
        childNode.add(node)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RouteAnchor

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }
}


enum class RouteDirection {
    LEFT, RIGHT, FORWARD, BACKWARD, LEFT_FORWARD,
    LEFT_BACKWARD, RIGHT_FORWARD, RIGHT_BACKWARD, UNIDENTIFIED, UP, DOWN
}



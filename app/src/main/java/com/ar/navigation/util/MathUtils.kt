package com.ar.navigation.util

import com.ar.navigation.pathmodel.RouteAnchor
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Ashwani Kumar Singh on 01,May,2023.
 */
class MathUtils {

    companion object {

        private fun pointDistance(point1: RouteAnchor, point2: RouteAnchor): Float {
            var result: Float = 0.0f
            var x = point2.x - point1.x
            var y = point2.y - point1.y
            var z = point2.z - point1.z;

//        result = sqrtf(x * x + y * y + z * z)

            result = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            return result
        }

        private fun getClosestPoint(point: RouteAnchor, start: RouteAnchor, end: RouteAnchor): RouteAnchor {
            var segmentLength = pointDistance(point1 = start, point2 = end)
            if (segmentLength == 0.0f) {
                return start
            }
//        var diff = RouteAnchor(x: end.x - start.x, y: end.y - start.y, z: end.z - start.z)
            var diff = RouteAnchor().apply {
                x = end.x - start.x
                y = end.y - start.y
                z = end.z - start.z
            }
            var t =
                ((point.x - start.x) * diff.x + (point.y - start.y) * diff.y + (point.z - start.z) * diff.z) / (diff.x * diff.x + diff.y * diff.y + diff.z * diff.z)

            var orgT = t
            val cons0 = 0.0f
            val cons1 = 1.0f

            t = max(t, cons0)
            t = min(t, cons1)
//        var finalPoint = RouteAnchor(x: start.x + diff.x * t, y: start.y + diff.y * t, z: start.z + diff.z * t)
            var finalPoint = RouteAnchor().apply {
                x = (start.x + diff.x * t)
                y = (start.y + diff.y * t)
                z = (start.z + diff.z * t)
            }
            return finalPoint
        }


        fun pointSegmentDistance(
            point: RouteAnchor,
            startPoint: RouteAnchor,
            endPoint: RouteAnchor
        ): Float {
            var fp = getClosestPoint(point = point, start = startPoint, end = endPoint)
            return pointDistance(point1 = point, point2 = fp)
        }
    }

}


package com.route.util

import android.util.Log
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Created by Ashwani Kumar Singh on 16,February,2023.
 */
data class Vector3(val x: Float, val y: Float, val z: Float) {

    /*var x: Float = 0.0f
    var y:Float = 0.0f
    var z: Float = 0.0f*/


    fun DistanceTo(point: Vector3) : Float {
        val xd = point.x - x
        val yd = point.y - y
        val zd = point.z - z

        return sqrt(xd * xd + yd * yd + zd * zd)
    }


}

class AppMathUtils {

    companion object{
        @JvmStatic
        fun PointDistance(point1 : Vector3, point2 : Vector3): Float {
            var result : Float = 0.0F
            val x = point2.x - point1.x
            val y = point2.y - point1.y
            val z = point2.z - point1.z;

            result = sqrt(x * x + y * y + z * z)
            return result
        }


    }



    fun GetClosestPoint(point: Vector3, start: Vector3, end: Vector3): Vector3 {
        var segmentLength : Float = PointDistance(point1= start, point2= end)
        if (segmentLength==0.0f) {
            return start
        }
        var diff = Vector3(x= end.x - start.x, y= end.y - start.y, z= end.z - start.z)
        var t = ((point.x - start.x) * diff.x + (point.y - start.y) * diff.y + (point.z - start.z) * diff.z) / (diff.x * diff.x + diff.y * diff.y + diff.z * diff.z)

        var orgT = t

        t = max(t, 0.0f)
        t = min(t, 1.0f)
        var finalPoint = Vector3(x= start.x + diff.x * t, y= start.y + diff.y * t, z= start.z + diff.z * t)
        Log.i("Ashwani", "GetClosestPoint :: "+finalPoint)
        return finalPoint
    }


}
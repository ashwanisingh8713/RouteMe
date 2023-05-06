package com.ar.navigation.util

import android.util.Log
import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import com.google.ar.core.Session
import com.ar.navigation.pathmodel.RouteAnchor
import com.ar.navigation.pathmodel.RouteDirection
import com.route.modal.RoutesData
import kotlin.collections.ArrayList
import kotlin.math.*

/**
 * Created by Ashwani Kumar Singh on 16,March,2023.
 */
object AnchorPointsUtil {

    /**
     * https://stackoverflow.com/questions/1211212/how-to-calculate-an-angle-from-three-points
     * Calculates the angle (in radians) between two vectors pointing outward from one center
     *
     * @param p0 first point
     * @param p1 second point
     * @param c center point
     */
    private fun getAngleRadians(p0: DoubleArray, c: DoubleArray, p1: DoubleArray): Double {
        var p0c = sqrt(
            (c[0] - p0[0]).pow(2.0) +
                    (c[1] - p0[1]).pow(2.0)
        ); // p0->c (b)
        var p1c = sqrt(
            (c[0] - p1[0]).pow(2.0) +
                    (c[1] - p1[1]).pow(2.0)
        ); // p1->c (a)
        var p0p1 = sqrt(
            (p1[0] - p0[0]).pow(2.0) +
                    (p1[1] - p0[1]).pow(2.0)
        ); // p0->p1 (c)
        return acos((p1c * p1c + p0c * p0c - p0p1 * p0p1) / (2 * p1c * p0c));
    }

    private fun getAngleDegree(
        SPX: Double,
        SPY: Double,
        CPX: Double,
        CPY: Double,
        EPX: Double,
        EPY: Double
    ): Double {
        val numerator = SPY * (CPX - EPX) + CPY * (EPX - SPX) + EPY * (SPX - CPX)
        val denominator = (SPX - CPX) * (CPX - EPX) + (SPY - CPY) * (CPY - EPY)
        val ratio = numerator / denominator
        val angleRad: Double = atan(ratio)
        var angleDeg = angleRad * 180 / Math.PI
        if (angleDeg < 0) {
            angleDeg += 180
        }
        return angleDeg
    }

    fun vectorAngle(p1: FloatArray, p2: FloatArray, p3: FloatArray): Double {
        var dotProductP1P2 = 0.0 ; var dotProductP2P3 = 0.0
        p1.forEachIndexed { index, _ ->
            dotProductP1P2 += p1[index] * p2[index]
        }

        p2.forEachIndexed { index, _ ->
            dotProductP2P3 += p2[index] * p3[index]
        }

        var p1Magnitude = sqrt(p1[0] * p1[0] + p1[1] * p1[1] + p1[2] * p1[2])
        var p2Magnitude = sqrt(p2[0] * p2[0] + p2[1] * p2[1] + p2[2] * p2[2])
        var p3Magnitude = sqrt(p3[0] * p3[0] + p3[1] * p3[1] + p3[2] * p3[2])

//        var cosTheta = dotProductP1P2 / (p1Magnitude * p2Magnitude)
//        var theta = acos(cosTheta)


        var cosTheta = (dotProductP1P2*dotProductP2P3) / (p1Magnitude*p2Magnitude*p2Magnitude*p3Magnitude)
        var theta = acos(cosTheta)
        val degree = Math.toDegrees(theta)

        Log.i("CalculationAngle", "P1 :: ${p1.contentToString()}")
        Log.i("CalculationAngle", "P2 :: ${p2.contentToString()}")
        Log.i("CalculationAngle", "P3 :: ${p3.contentToString()}")

        Log.i("CalculationAngle", "dotProductP1P2 :: $dotProductP1P2")
        Log.i("CalculationAngle", "dotProductP2P3 :: $dotProductP2P3")

        Log.i("CalculationAngle", "p1Magnitude :: $p1Magnitude")
        Log.i("CalculationAngle", "p2Magnitude :: $p2Magnitude")
        Log.i("CalculationAngle", "p3Magnitude :: $p3Magnitude")


        Log.i("CalculationAngle", "CosTheta ::  $cosTheta")
        Log.i("CalculationAngle", "Theta ::  $theta")
        Log.i("CalculationAngle", "Degree ::  $degree")
        Log.i("CalculationAngle", "===================================================")

        return degree
    }

    fun vectorAngle(pos1: Pose, pos2: Pose, pos3: Pose): Double {
        var position1 = floatArrayOf(pos1.tx(), pos1.ty(), pos1.tz())
        var position2 = floatArrayOf(pos2.tx(), pos2.ty(), pos2.tz())
        var position3 = floatArrayOf(pos3.tx(), pos3.ty(), pos3.tz())
        return vectorAngle(position1, position2, position3)
    }



    fun getRouteAnchorsFromServerResponse_V1(session: Session, routeAnchorList: MutableList<RouteAnchor>): Pair<MutableList<RouteAnchor>, Float> {
        var consolidatedDistance: Float = 0f
        // Making server points
        val routeListSize = routeAnchorList.size-1
        for (i in 0 until routeListSize ) {
            val routeAnchor1 = routeAnchorList[i]
            val routeAnchor2 = routeAnchorList[i + 1]

            // Direction
            val direction = getDirection(routeAnchor1, routeAnchor2)

            // Create Anchor -1
            val position1 = floatArrayOf(
                routeAnchor1.x,
                routeAnchor1.y,
                routeAnchor1.z
            ) //  { x, y, z } position
            val rotation1 = floatArrayOf(0f, 0f, 0f, 1f)
            val anchor1: Anchor = session.createAnchor(Pose(position1, rotation1))

            // Create Anchor -2
            val position2 = floatArrayOf(
                routeAnchor2.x,
                routeAnchor2.y,
                routeAnchor2.z
            ) //  { x, y, z } position
            val rotation2 = floatArrayOf(0f, 0f, 0f, 1f)
            val anchor2: Anchor = session.createAnchor(Pose(position2, rotation2))

            // Get Pose -1               // Get Pose -2
            val pose1 = anchor1.pose;
            val pose2 = anchor2.pose

            // Distance
            val distance = calculateDistance(pose1, pose2)

            var vectorAngle = 0.0
            // Create Anchor -3 For 3d Angle Calculation
            if(routeListSize > i + 1) {
                val routeAnchor3 = routeAnchorList[i + 2]
                var position3 = floatArrayOf(
                    routeAnchor3.x,
                    routeAnchor3.y,
                    routeAnchor3.z
                ) //  { x, y, z } position

                // Calculating Angle Between two 3D Coordinates
                vectorAngle = vectorAngle(position1, position2, position3)

                Log.i("MyAngle", "$i :: $vectorAngle")
            }


            // Assigning anchor
            if (i == 0) {
                routeAnchor1.anchor = anchor1
                routeAnchor2.anchor = anchor2
            } else {
                routeAnchor2.anchor = anchor2
            }
            consolidatedDistance += distance

            routeAnchorList[i].distanceCovered = consolidatedDistance
            routeAnchorList[i].distanceToNext = distance
            routeAnchorList[i].directionToNext = direction
            routeAnchorList[i].angle = vectorAngle

        }

//        routeAnchorList.reverse()
        return Pair(routeAnchorList, consolidatedDistance)
    }

    fun getRouteAnchorsFromServerResponse_V2(session: Session, routeAnchorList: MutableList<RouteAnchor>): Pair<MutableList<RouteAnchor>, Float> {
        var consolidatedDistance = 0f

        // Making server points
        var routeListSize = routeAnchorList.size-1


        val subPointDistance = 0.5f
        var newRouteAnchorList = mutableListOf<RouteAnchor>()

        var inSegmentAnchorSize = newRouteAnchorList.size

        for (i in 0 until routeListSize ) {
            inSegmentAnchorSize = newRouteAnchorList.size
            var p1 = routeAnchorList[i]
            var p2 = routeAnchorList[i + 1]

            var direction = RouteDirection.UNIDENTIFIED
            /*if(i<routeListSize-1) {
                if(newRouteAnchorList.size > 0) {
                    var lastTurnAcnhor = newRouteAnchorList[newRouteAnchorList.size - 1]
                    direction = getDirection(lastTurnAcnhor, p2)
                } else {
                    direction = getDirection(p1, p2)
                }
                Log.i("NewLogic", "direction :: ${direction.name}")
            }*/

            var segmentLength = MathUtils.pointDistance(point1= p1, point2= p2)
            var subPointsCount = (segmentLength / subPointDistance).toInt() + 1


//            Log.i("NewLogic", "subPointsCount :: $subPointsCount")


            for (sp in 0 until subPointsCount-1) {
                var progress  = sp.toFloat() / subPointsCount.toFloat()

                var dx = p2.x - p1.x
                var dy = p2.y - p1.y
                var dz = p2.z - p1.z
                var routeAnchor = RouteAnchor()
                routeAnchor.x = p1.x + dx * progress
                routeAnchor.y = p1.y + dy * progress
                routeAnchor.z = p1.z + dz * progress

                val position = floatArrayOf(
                    routeAnchor.x,
                    routeAnchor.y,
                    routeAnchor.z
                )

                val rotation = floatArrayOf(0f, 0f, 0f, 1f)
                val anchor: Anchor = session.createAnchor(Pose(position, rotation))
                routeAnchor.anchor = anchor


                if(sp == subPointsCount-2 && i != 0) {
                    var turnAnchor = newRouteAnchorList[inSegmentAnchorSize-1]
                    direction = getDirection(turnAnchor, p2)
                    turnAnchor.directionToNext = direction
                    Log.i("NewLogic", "Turn Anchor ${inSegmentAnchorSize-1} direction  :: ${newRouteAnchorList[inSegmentAnchorSize-1].directionToNext.name}")
                }

                newRouteAnchorList.add(routeAnchor)
            }




            ///////////////////// Above is work in progress///////////////////////////////////////


        }

        return Pair(newRouteAnchorList, consolidatedDistance)
    }

    /**
     * Make Anchors from Server Response
     */
    fun makeAnchorsFromServerResponse(routeData: RoutesData): MutableList<RouteAnchor> {
        // Initialisation of RouteAnchor List
        val routeAnchorList: MutableList<RouteAnchor> = ArrayList()

        val document = routeData.documents[0]

        // Updating Anchor axis
        val ud = document.ud
        val updatedPoints = FloatArray(document.pts.size)
        for ((count, axis) in document.pts.withIndex()) {
//            val temp = (axis-2) * ud  // Here I am reducing 2
            val temp = (axis) * ud
            val roundTemp = (temp * 100).roundToInt() / 100f
            updatedPoints[count] = roundTemp
        }

        val totalVertex = updatedPoints.size / 3
        var index = 0
        for (i in 0 until totalVertex) {
            val temp = index + 3
            var x = updatedPoints[temp - 3]
            val y = updatedPoints[temp - 2]
            var z = -(updatedPoints[temp - 1]) // Here, Doing - with z axis to make in reverse

            // To set axis
            val axisArray = FloatArray(3)
            axisArray[0] = x
            axisArray[1] = y
            axisArray[2] = z

            val routeAnchor = RouteAnchor().apply {
                this.x = x
                this.y = y
                this.z = z
            }

            // Anchor is start point or not
            routeAnchor.isStartPoint = i == 0

            routeAnchorList.add(routeAnchor)
            index = temp//
        }

        return routeAnchorList
    }

    private fun getDirection(previous: RouteAnchor, next: RouteAnchor): RouteDirection {
        // move up, Y axis should be changed with +value
        // move down, Y axis should be changed with -value

        // move right, X axis should be changed with +value (x1<x2)
        // move left, X axis should be changed with -value  (x1>x2)

        // move forward, Z axis should be changed with +value   (z1<z2)
        // move backward, Z axis should be changed with -value  (z1>z2)

        val x1 = previous.x.absoluteValue;
        val x2 = next.x.absoluteValue
        val y1 = previous.y.absoluteValue;
        val y2 = next.y.absoluteValue
        val z1 = previous.z.absoluteValue;
        val z2 = next.z.absoluteValue

        /*if (x1 < x2 && z1 < z2) {
            return RouteDirection.RIGHT_FORWARD
        } else if (x1 < x2 && z1 > z2) {
            return RouteDirection.RIGHT_BACKWARD
        } else if (x1 > x2 && z1 < z2) {
            return RouteDirection.LEFT_FORWARD
        } else if (x1 > x2 && z1 > z2) {
            return RouteDirection.LEFT_BACKWARD
        } else */if (x1 < x2) {
            return RouteDirection.RIGHT
        } else if (x1 > x2) {
            return RouteDirection.LEFT
        } else if (z1 < z2) {
            return RouteDirection.FORWARD
        } else if (z1 > z2) {
            return RouteDirection.BACKWARD
        }

        return RouteDirection.UNIDENTIFIED
    }

    /**
     * It calculates distance between two Anchor-Pose
     */
    fun calculateDistance(objectPose0: Pose, objectPose1: Pose): Float {
        return calculateDistance(
            objectPose0.tx() - objectPose1.tx(),
            objectPose0.ty() - objectPose1.ty(),
            objectPose0.tz() - objectPose1.tz()
        )
    }


    fun measureDistanceOf2Points(distanceMeter: Float) {
        val distanceTextCM = changeUnit(distanceMeter, "cm")
    }

    // distance = √[(x1 - x2)² + (y1 - y2)² + (z1 - z2)²]
    fun calculateDistance(x: Float, y: Float, z: Float): Float {
        return sqrt(x.pow(2) + y.pow(2) + z.pow(2))
    }


    private fun changeUnit(distanceMeter: Float, unit: String): Float {
        return when (unit) {
            "cm" -> distanceMeter * 100
            "mm" -> distanceMeter * 1000
            else -> distanceMeter
        }
    }


    fun calculateDistance(p1: FloatArray, p2: FloatArray): Float {
        return calculateDistance(
            p1[0] - p2[0],
            p1[1] - p2[1],
            p1[2] - p2[2]
        )
    }

}
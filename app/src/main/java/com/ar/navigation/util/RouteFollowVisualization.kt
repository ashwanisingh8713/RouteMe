package com.ar.navigation.util

import android.util.Log
import com.ar.navigation.pathmodel.RouteAnchor

/**
 * Created by Ashwani Kumar Singh on 01,May,2023.
 */
class RouteFollowVisualization {

    val degreesToRadians: Double = 1.0 / 360.0 * 2 * Math.PI
    val radiansToDegrees: Double = 360.0 / (2 * Math.PI)

    enum class PointType {
        start,
        turn,
        end
    }

    var turnIcons = mutableListOf<RouteAnchor>()
    var turnLookAtPrev = mutableListOf<RouteAnchor>()
    var turnLookAtNext = mutableListOf<RouteAnchor>()
    var points = mutableListOf<RouteAnchor>()
    var turnDistances = mutableListOf<Float>()


    var onCameraNode: RouteAnchor? = null
    var onCameraSubNode: RouteAnchor? = null
    var onCameraLocalPointNode: RouteAnchor? = null

    var userDistance: Float = 0f
    var routeDistance: Float = 0f
    var progress: Float = 0f
    var currentSegment: Int = 0

    var disable: Boolean = false

    /*fun buildRoute(subPointDistance: Float, route: List<RouteAnchor>) {

        var routeNode = RouteAnchor()
        routeNode.addChildNode(routeNode)

        print("BuildRoute! Sub Point Distance: ($subPointDistance)")

        turnDistances.add(0.0f)

        onCameraNode = RouteAnchor()
        onCameraSubNode = RouteAnchor()
        onCameraNode!!.addChildNode(onCameraSubNode!!)
        onCameraLocalPointNode = RouteAnchor()
        onCameraNode!!.addChildNode(onCameraLocalPointNode!!)

        for (p in route.indices) {
            Log.i("Point: (p+1) ", "${route.size}")
            var point = route[p]

            var pt = PointType.turn
            if (p == 0) {
                pt = PointType.start
            } else if (p == route.size - 1) {
                pt = PointType.end
            }

            var turnNode = buildTurnNode(type = pt)
            turnDistances.add(routeDistance)
            Log.i("routeDistance", "$routeDistance")

            routeNode.addChildNode(turnNode)
            turnNode.position = SCNVector3Make(point.x, point.y, point.z)
            turns.append(turnNode)

            //prev node will have a rotation based on previous node looking at it (basically the direction of the previous segment)
            var nodePrev = buildEmptyNode()
            nodePrev.worldPosition = turnNode.worldPosition

            //next node is in the prev node space, looking at the next node
            var nodeNext = buildEmptyNode()
            nodePrev.addChildNode(nodeNext)
            nodeNext.worldPosition = turnNode.worldPosition

            turnLookAtPrev.add(nodePrev)
            turnLookAtNext.add(nodeNext)
            turnPointsWorldPositions.append(Vector3(v: turnNode. worldPosition))

            if (p < route.Points.count - 1) {
                var p1 = route.Points[p]
                var p2 = route.Points[p + 1]

                var segmentLength = MathUtils.pointDistance(point1: p1, point2: p2)
                var subPointsCount: Int = Int(segmentLength / subPointDistance) + 1

                print("Segment \(p+1) Length: \(segmentLength), Sub Points Count \(subPointsCount)")

                if (subPointsCount > 0) {
                    for (sp in 0..subPointsCount - 1) {
                        var progress: Float = Float(sp) / Float(subPointsCount)

                        var subPoint: SCNNode

                        if (sp == 0) {
                            subPoint = buildEmptyNode()
                        } else {
                            subPoint = buildSCNPointNode()
                        }
                        routeNode.addChildNode(subPoint)

                        var dx = p2.x - p1.x
                        var dy = p2.y - p1.y
                        var dz = p2.z - p1.z

                        var pos = SCNVector3Make(
                            p1.x + dx * progress,
                            p1.y + dy * progress,
                            p1.z + dz * progress
                        )
                        subPoint.position = pos

                        points.append(subPoint)
                        pointSegments.append(p)
                        pointPositions.append(
                            Vector3(x: p1. x +dx * progress,
                            y: p1. y +dy * progress, z: p1.z+dz * progress))
                        pointDistances.append(routeDistance + segmentLength * progress)
                    }
                }
                routeDistance += segmentLength
            }

            if (p == route.Points.count - 1) {
                var subPoint = buildEmptyNode()
                subPoint.position = route.Points[p].SCNVector3()
                points.append(subPoint)
                pointSegments.append(p - 1)
                pointPositions.append(route.Points[p])
                pointDistances.append(routeDistance)
            }
        }

        //Setup turns
        for (p in 0..route.size - 1) {
            if (p > 0 && p < route.size - 1) {
                var prev = turnPointsWorldPositions[p - 1]
                var current = turnPointsWorldPositions[p]
                var next = turnPointsWorldPositions[p + 1]

                var lookAtPos: Vector3 = Vector3(v: current)
                lookAtPos.subtract(v: prev)
                lookAtPos.add(v: current)

                //Prev node will look in the direction of the previous segment
                turnLookAtPrev[p].look(at: lookAtPos. SCNVector3 ())
                //Next node will liik directly at the next turn point
                turnLookAtNext[p].look(at: next. SCNVector3 ())

                var parentAngle: Vector3 = Vector3(v: turnLookAtPrev[p].eulerAngles)
                parentAngle.y *= (180.0 / Float.pi)

                var angle: Vector3 = Vector3(v: turnLookAtNext[p].eulerAngles)
                angle.y *= (180.0 / Float.pi)

                var finalAngle: Float = (angle.y)

                while (finalAngle < -180) {
                    finalAngle += 360
                }

                while (finalAngle > 180) {
                    finalAngle -= 360
                }

                Log.i(
                    "Point (p) | Angles - Parent: (parentAngle.y), Child: (angle.y), Final: (finalAngle)",
                    ""
                )

                var turnImg: UIImage
                if (angle.y > 112.5) { //hard left
                    turnImg = UIImage(named:"leftLight")!
                } else if (angle.y > 67.5) { //left
                    turnImg = UIImage(named:"leftLight")!
                } else if (angle.y > 22.5) { //light left
                    turnImg = UIImage(named:"leftLight")!
                } else if (angle.y > -67.5) { //light right
                    turnImg = UIImage(named:"rightLight")!
                } else if (angle.y > -112.5) { //right
                    turnImg = UIImage(named:"rightLight")!
                } else { //hardright
                    turnImg = UIImage(named:"rightLight")!
                }

                turnIcons[p].geometry?.firstMaterial?.diffuse.contents = turnImg
            }
        }

        if (points.size > 1) {
            for (i in 0..points.size - 1) {
                points[i].look(at: turns[pointSegments[i] + 1].worldPosition)
            }
        }

//        ViewController.Instance.routeFollowModule.setup(visualization: self)
    }*/


    fun buildTurnNode(type: PointType): RouteAnchor {
        var node = RouteAnchor()
        var poleNode = RouteAnchor()
        var turnIconNode = RouteAnchor()
        turnIcons.add(turnIconNode)

        node.addChildNode(poleNode)
        node.addChildNode(turnIconNode)

        /* poleNode.geometry = SCNPlane(width: 0.00, height: 0.00)
         poleNode.position = SCNVector3Make(0, 0.375, 0)

         turnIconNode.geometry = SCNPlane(width: 0.25, height: 0.25)
         turnIconNode.position = SCNVector3Make(0, 1.0, 0)*/

        var turnImg = if (type == PointType.end) {
            "finish"
        } else {
            "straight"
        }

        turnIconNode.imgType = turnImg

        return node;
    }

}
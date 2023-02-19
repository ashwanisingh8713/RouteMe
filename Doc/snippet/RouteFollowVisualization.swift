//
//  RouteFollowVisualization.swift
//  RouteMeAppClipStoryboard
//
//  Created by Krzysztof Gnutek on 20/01/2022.
//

import Foundation
//import RealityKit
import ARKit

enum PointType
{
    case start
    case turn
    case end
}

public class RouteFollowVisualization
{
    var routeNode: SCNNode!
    var rootNode: SCNNode!
    var route: Route!

    var finishNode: SCNNode!

    var onCameraNode: SCNNode!
    var onCameraSubNode: SCNNode!
    var onCameraLocalPointNode: SCNNode!

    var turns: Array<SCNNode> = Array<SCNNode>()
    var turnIcons: Array<SCNNode> = Array<SCNNode>()
    var turnLookAtPrev: Array<SCNNode> = Array<SCNNode>()
    var turnLookAtNext: Array<SCNNode> = Array<SCNNode>()

    var turnDistances: Array<Float> = Array<Float>()
    var turnRotations: Array<Float> = Array<Float>()

    var points: Array<SCNNode> = Array<SCNNode>()
    var pointDistances: Array<Float> = Array<Float>()
    var pointPositions: Array<Vector3> = Array<Vector3>()
    var pointSegments: Array<Int> = Array<Int>()

    var turnPointsWorldPositions: Array<Vector3> = Array<Vector3>()

    var offset: Vector3!
    var rotation: Vector3!

    let degreesToRadians: Float = 1.0 / 360.0 * 2 * Float.pi
    let radiansToDegrees: Float = 360.0 / (2 * Float.pi)

    var userDistance: Float = 0
    var routeDistance: Float = 0
    var progress: Float = 0
    var currentSegment: Int = 0

    var disable: Bool = false

    func SetRoute(route: Route, rootNode: SCNNode!, offset: Vector3, rotation: Vector3){
        self.rootNode = rootNode
        self.route = route
        self.offset = offset
        self.rotation = rotation
    }

    func BuildRoute(subPointDistance: Float) {
        route.SimplifyPath(width: 1.0)

        routeNode = SCNNode()
        rootNode.addChildNode(routeNode)
        routeNode.position = SCNVector3Make(offset.x, offset.y, -offset.z)
        routeNode.eulerAngles = SCNVector3Make((rotation.x) * degreesToRadians, (-rotation.y) * degreesToRadians, (rotation.z) * degreesToRadians)

        print("BuildRoute! Sub Point Distance: \(subPointDistance)")

        turnDistances.append(0)

        onCameraNode = BuildEmptyNode()
        onCameraSubNode = BuildEmptyNode()
        onCameraNode.addChildNode(onCameraSubNode)
        onCameraLocalPointNode = BuildEmptyNode()
        onCameraNode.addChildNode(onCameraLocalPointNode)

        for p in 0...route.Points.count - 1 {
            print("Point: \(p+1) / \(route.Points.count)")
            let point = route.Points[p]

            var pt = PointType.turn
            if (p == 0)
            {
                pt = PointType.start
            }
            else if (p == route.Points.count - 1)
            {
                pt = PointType.end
            }

            let turnNode = BuildTurnNode(type: pt)
            turnDistances.append(routeDistance)
            routeNode.addChildNode(turnNode)
            turnNode.position = SCNVector3Make(point.x, point.y, point.z)
            turns.append(turnNode)

            //prev node will have a rotation based on previous node looking at it (basically the direction of the previous segment)
            let nodePrev = BuildEmptyNode()
//            routeNode.addChildNode(nodePrev)
            nodePrev.worldPosition = turnNode.worldPosition

            //next node is in the prev node space, looking at the next node
            let nodeNext = BuildEmptyNode()
            nodePrev.addChildNode(nodeNext)
            nodeNext.worldPosition = turnNode.worldPosition

            turnLookAtPrev.append(nodePrev)
            turnLookAtNext.append(nodeNext)
            turnPointsWorldPositions.append(Vector3(v: turnNode.worldPosition))


            if (p < route.Points.count - 1) {
                let p1 = route.Points[p]
                let p2 = route.Points[p + 1]

                let segmentLength = MathUtils.PointDistance(point1: p1, point2: p2)
                let subPointsCount : Int = Int(segmentLength / subPointDistance) + 1

                print("Segment \(p+1) Length: \(segmentLength), Sub Points Count \(subPointsCount)")

                if (subPointsCount > 0) {
                    for sp in 0...subPointsCount - 1 {
                        let progress : Float = Float(sp) / Float(subPointsCount)

                        var subPoint: SCNNode

                        if (sp == 0)
                        {
                            subPoint = BuildEmptyNode()
                        }
                        else
                        {
                            subPoint = BuildSCNPointNode()
                        }
                        routeNode.addChildNode(subPoint)

                        let dx = p2.x - p1.x
                        let dy = p2.y - p1.y
                        let dz = p2.z - p1.z

                        let pos = SCNVector3Make(p1.x + dx * progress, p1.y + dy * progress, p1.z + dz * progress)
                        subPoint.position = pos

                        points.append(subPoint)
                        pointSegments.append(p)
                        pointPositions.append(Vector3(x: p1.x + dx * progress, y: p1.y + dy * progress, z: p1.z + dz * progress))
                        pointDistances.append(routeDistance + segmentLength * progress)
                    }
                }
                routeDistance += segmentLength
            }

            if (p == route.Points.count - 1) {
                var subPoint = BuildEmptyNode()
                subPoint.position = route.Points[p].SCNVector3()
                points.append(subPoint)
                pointSegments.append(p - 1)
                pointPositions.append(route.Points[p])
                pointDistances.append(routeDistance)
            }
        }

        //Setup turns
        for p in 0...route.Points.count - 1 {
            if (p > 0 && p < route.Points.count - 1) {
                var prev = turnPointsWorldPositions[p - 1]
                var current = turnPointsWorldPositions[p]
                var next = turnPointsWorldPositions[p + 1]

                var lookAtPos: Vector3 = Vector3(v: current)
                lookAtPos.Subtract(v: prev)
                lookAtPos.Add(v: current)

                //Prev node will look in the direction of the previous segment
                turnLookAtPrev[p].look(at: lookAtPos.SCNVector3())
                //Next node will liik directly at the next turn point
                turnLookAtNext[p].look(at: next.SCNVector3())

                var parentAngle: Vector3 = Vector3(v: turnLookAtPrev[p].eulerAngles)
                parentAngle.y *= (180.0 / Float.pi)

                var angle: Vector3 = Vector3(v: turnLookAtNext[p].eulerAngles)
                angle.y *= (180.0 / Float.pi)


                //angle.x -= parentAngle.x
                //angle.y -= parentAngle.y
                //angle.z -= parentAngle.z

                //angle.x *= (360.0 / 3.1415)
                //angle.z *= (360.0 / 3.1415)

                var finalAngle: Float = (angle.y)

                while (finalAngle < -180)
                {
                    finalAngle += 360
                }
                while (finalAngle > 180)
                {
                    finalAngle -= 360
                }

                print("Point \(p) | Angles - Parent: \(parentAngle.y), Child: \(angle.y), Final: \(finalAngle)")

                var turnImg : UIImage
                if (angle.y > 112.5) //hard left
                {
                    turnImg = UIImage(named:"leftHard")!
                }
                else if (angle.y > 67.5) //left
                {
                    turnImg = UIImage(named:"left")!
                }
                else if (angle.y > 22.5) //light left
                {
                    turnImg = UIImage(named:"leftLight")!
                }
                else if (angle.y > -22.5) //straight
                {
                    turnImg = UIImage(named:"straight")!
                }
                else if (angle.y > -67.5) //light right
                {
                    turnImg = UIImage(named:"rightLight")!
                }
                else if (angle.y > -112.5) //right
                {
                    turnImg = UIImage(named:"right")!
                }
                else //hardright
                {
                    turnImg = UIImage(named:"rightHard")!
                }

                turnIcons[p].geometry?.firstMaterial?.diffuse.contents = turnImg
            }
        }

        if (points.count > 1)
        {
            for i in 0...points.count - 1
            {
                points[i].look(at: turns[pointSegments[i] + 1].worldPosition)
            }
        }

        ViewController.Instance.RouteFollowModule.Setup(visualization: self)
    }

// ##############################################################################

    func BuildEmptyNode() -> SCNNode {
        let node = SCNNode()
        return node;
    }

// ##############################################################################
    func BuildSCNPointNode() -> SCNNode {
        let node = SCNNode()
        let imgNode = SCNNode()
        let img = UIImage(named:"iconArrow")
        imgNode.geometry = SCNPlane(width: 0.4, height: 0.4)
        imgNode.geometry?.firstMaterial?.diffuse.contents = img
        imgNode.eulerAngles = SCNVector3(-Float.pi / 2, 0, 0)
        node.addChildNode(imgNode)
        return node;
    }

// ##############################################################################
    func BuildTurnNode(type : PointType) -> SCNNode {
        let node = SCNNode()
        let poleNode = SCNNode()
        let turnIconNode = SCNNode()
        turnIcons.append(turnIconNode)

        node.addChildNode(poleNode)
        node.addChildNode(turnIconNode)

        poleNode.geometry = SCNPlane(width: 0.05, height: 0.75)
        poleNode.position = SCNVector3Make(0, 0.375, 0)

        turnIconNode.geometry = SCNPlane(width: 0.25, height: 0.25)
        turnIconNode.position = SCNVector3Make(0, 1.0, 0)
        //turnIconNode.eulerAngles = SCNVector3(0, 0, Float.pi)

        var turnImg : UIImage

        if (type == PointType.end)
        {
            finishNode = node;
            turnImg = UIImage(named:"finish")!
        }
        else if (type == PointType.start)
        {
            turnImg = UIImage(named:"straight")!
        }
        else
        {
            turnImg = UIImage(named:"straight")!
        }

        turnIconNode.geometry?.firstMaterial?.diffuse.contents = turnImg

        return node;
    }

// ##############################################################################
    func SetCamera(camera: ARCamera!)
    {
        var angle = camera.eulerAngles.y
        var pos = camera.transform[3]

        onCameraNode.worldPosition = SCNVector3(pos.x, pos.y, pos.z)
        onCameraNode.eulerAngles = SCNVector3(0, angle, 0)
        print("Camera Angle: \(angle)")
    }



// ##############################################################################
    func SetCameraPosition(position: Vector3)
    {
        if (disable)
        {
            return
        }

        let pointsToShow: Int = 4

        let startPoint = turns[0]
        let startPos = Vector3(v: startPoint.worldPosition)
        let dist = startPos.DistanceTo(point: position)

        var debug = ""

        debug += String("Camera\nx: \(position.x)\ny: \(position.y)\nz: \(position.z)\n")
        //debug += String("\nRoute\nx: \(startPos.x)\ny: \(startPos.y)\nz: \(startPos.z)\n")
        debug += String("\nDistance: \(dist)\n")

        var minDistance: Float = 99999
        var index: Int = 0

        for i in 0...points.count
        {
            if (i >= points.count - 1)
            {
                continue;
            }

            let pPos1 = Vector3(v: points[i].worldPosition)
            let pPos2 = Vector3(v: points[i + 1].worldPosition)

            let cPt = MathUtils.GetClosestPoint(point: position, start: pPos1, end: pPos2)

            let subDistance = position.DistanceTo(point: cPt)

            //print("p1: \(pPos1.x), \(pPos1.y), \(pPos1.z) | p2: \(pPos2.x), \(pPos2.y), \(pPos2.z)")
            //print("subDistance \(i): \(subDistance) | \(pointDistances[i]) -> \(pointDistances[i + 1]) | phone: \(position.x), \(position.y), \(position.z) | cpt: \(cPt.x), \(cPt.y), \(cPt.z)")

            if (subDistance < minDistance)
            {
                minDistance = subDistance
                index = i
                let wholeDist = pPos1.DistanceTo(point: pPos2)
                let subDist = pPos1.DistanceTo(point: cPt)

                var subProgress = subDist / wholeDist
                if (subProgress < 0)
                {
                    subProgress = 0
                }
                if (subProgress > 1)
                {
                    subProgress = 1
                }

                userDistance = pointDistances[i] + wholeDist * subProgress
            }
        }

        var dirIndex: Int = 0
        var dirCount: Int = 0
        var arrowAngle: Float = 0

        if (true)
        {
            dirIndex = index + 3
            dirCount = points.count
            if (dirIndex > points.count - 1)
            {
                dirIndex = points.count - 1
            }
            var p = points[dirIndex].worldPosition
            p.y = onCameraSubNode.worldPosition.y

            onCameraLocalPointNode.worldPosition = p
            var l = onCameraLocalPointNode.position
            var l3 = Vector3(v: l)


            print("Local Pos:\n\(l3.x)\n\(l3.z)\n\n")

            let localForward = Vector3(x: 0, y: 0, z: -1)
            var dotAngle = MathUtils.RadiansBetweenVectors(v1: localForward, v2: l3)

            if (l3.x < 0)
            {
                dotAngle = -dotAngle
            }

            arrowAngle = dotAngle

            print("Dot Angle: \(dotAngle * radiansToDegrees)")

        }
        else
        {
            dirIndex = pointSegments[index] + 1
            dirCount = turns.count
            if (dirIndex > turns.count - 1)
            {
                dirIndex = turns.count - 1
            }
            var p = turns[dirIndex].worldPosition
            onCameraSubNode.look(at: p)
            //print("Camera: \(position.x), \(position.y), \(position.z)")
            //print("Point: \(p.x), \(p.y), \(p.z)")
        }


        //let subDir = onCameraSubNode.eulerAngles
        arrowAngle *= radiansToDegrees
        ViewController.Instance.SetArrowAngle(angle: arrowAngle)
        print("Index: \(dirIndex) / \(dirCount) | Arrow Direction: \(arrowAngle)")

        let firstPoint = index
        let lastPoint = min(points.count - 1, firstPoint + pointsToShow)
        for i in 0...points.count - 1
        {
            if (i == dirIndex)
            {
                points[i].scale = SCNVector3(1.0, 1.0, 1.0)
            }
            else if (i >= firstPoint && i <= lastPoint)
            {
                points[i].scale = SCNVector3(1, 1, 1)
            }
            else
            {
                points[i].scale = SCNVector3(0, 0, 0)
            }
        }
        let d = Int(routeDistance - userDistance)
        var pr = userDistance / routeDistance

        //print("Distance: \(userDistance) / \(routeDistance)")

        ViewController.Instance.SetPersonProgress(progress: pr)
        ViewController.Instance.LabelDistance.text = String("\(d) meters")
        ViewController.Instance.Debug.text = debug

        if (userDistance > routeDistance - 1)
        {
            disable = true
            ViewController.Instance.UIParent.isHidden = true
            ViewController.Instance.DestinationReached.isHidden = false
        }

        //ViewController.Instance.SetArrowAngle(angle: position.x * 360.0)

        for i in 0...turns.count - 1
        {
            var camPos = position
            var turnPos = turns[i].worldPosition

            //turns[i].eulerAngles = SCNVector3(x: 0, y: 1, z: 0)
            turns[i].look(at: SCNVector3(x: turnPos.x * 2 - camPos.x, y: turnPos.y, z: turnPos.z * 2 - camPos.z))
            //var angle = turns[i].eulerAngles
            //angle.x = -3.1415
            //angle.y += 3.1415
            //angle.z = -3.1415

//            if (angle.y > 3.1415)
//            {
//                angle.y -= (3.1415)
//            }

            //turns[i].eulerAngles = SCNVector3(x: angle.x, y: angle.y, z: angle.z)

            //print("Turn \(i), Angle: \(angle.x * radiansToDegrees), \(angle.y * radiansToDegrees), \(angle.z * radiansToDegrees)")

            if (i < turns.count - 1)
            {
                if (i == pointSegments[index] + 1)
                {
                    turns[i].scale = SCNVector3(1, 1, 1)
                }
                else
                {
                    turns[i].scale = SCNVector3(0, 0, 0)
                }
            }
        }
    }
}

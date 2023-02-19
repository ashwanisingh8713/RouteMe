//
//  MathUtils.swift
//  RouteMeAppClipStoryboard
//
//  Created by Krzysztof Gnutek on 21/02/2022.
//

import Foundation
import SceneKit

class Vector3
{
    var x, y, z: Float

    init(x: Float, y: Float, z: Float)
    {
        self.x = x
        self.y = y
        self.z = z
    }

    init(v: SCNVector3)
    {
        x = v.x
        y = v.y
        z = v.z
    }

    init(v: Vector3)
    {
        x = v.x
        y = v.y
        z = v.z
    }

    func DistanceTo(point: Vector3) -> Float
    {
        let xd = point.x - x
        let yd = point.y - y
        let zd = point.z - z

        return sqrt(xd * xd + yd * yd + zd * zd)
    }

    func Add(v: Vector3)
    {
        x += v.x
        y += v.y
        z += v.z
    }

    func Subtract(v: Vector3)
    {
        x -= v.x
        y -= v.y
        z -= v.z
    }

    func SCNVector3() -> SCNVector3
    {
        return SCNVector3Make(x, y, z)
    }
}

public class MathUtils
{
    static func PointDistance(point1 : Vector3, point2 : Vector3) -> Float
    {
        var result : Float = 0.0
        let x = point2.x - point1.x
        let y = point2.y - point1.y
        let z = point2.z - point1.z;

        result = sqrtf(x * x + y * y + z * z)
        return result
    }

    static func GetClosestPoint(point: Vector3, start: Vector3, end: Vector3) -> Vector3
    {
        let segmentLength : Float = PointDistance(point1: start, point2: end)
        if (segmentLength == 0)
        {
            return start
        }
        let diff = Vector3(x: end.x - start.x, y: end.y - start.y, z: end.z - start.z)
        var t = ((point.x - start.x) * diff.x + (point.y - start.y) * diff.y + (point.z - start.z) * diff.z) / (diff.x * diff.x + diff.y * diff.y + diff.z * diff.z)

        let orgT = t

        t = max(t, 0)
        t = min(t, 1)
        let finalPoint = Vector3(x: start.x + diff.x * t, y: start.y + diff.y * t, z: start.z + diff.z * t)
        return finalPoint
    }

    static func PointSegmentDistance(point : Vector3, startPoint : Vector3, endPoint : Vector3) -> Float
    {
        let fp = MathUtils.GetClosestPoint(point: point, start: startPoint, end: endPoint)
        return PointDistance(point1: point, point2: fp)
    }

    static func VectorMagnitude(v: Vector3) -> Float
    {
        var result: Float = sqrtf(v.x * v.x + v.y * v.y + v.z * v.z)
        return result
    }

    static func DotProduct(v1: Vector3, v2: Vector3) -> Float
    {
        var result: Float = 0.0
        result += v1.x * v2.x
        result += v1.y * v2.y
        result += v1.z * v2.z
        return result
    }

    static func RadiansBetweenVectors(v1: Vector3, v2: Vector3) -> Float
    {
        let dot = DotProduct(v1: v1, v2: v2)
        let m1 = VectorMagnitude(v: v1)
        let m2 = VectorMagnitude(v: v2)

        print("Dot Product: \(dot) | m1: \(m1) | m2: \(m2) |")
        return acosf(dot / (VectorMagnitude(v: v1) * VectorMagnitude(v: v2)))
    }
}

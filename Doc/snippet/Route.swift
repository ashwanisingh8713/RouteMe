class Route
{
    var Version: Int = 1;
    let WalkingMetersPerMinute: Float = 80

    var RouteUID: String = ""
    var CreationTimeStamp: Float = 0

    var Latitude: Float = 0
    var Longitude: Float = 0
    var CityUID: String = ""
    var Location: String = ""

    var StartPoint: String = ""
    var EndPoint: String = ""

    var StartAnchorIdentifier: String = ""
    var EndAnchorIdentifier: String = ""

    var Public: Bool = true
    var Category: String = ""
    var Length: Float = 0

    var Completions: Int = 0
    var ThumbsUp: Int = 0
    var ThumbsDown: Int = 0

    var UnitDistance: Float = 0.01
    var Points: [Vector3] = Array<Vector3>()

    func PrintOutDebug()
    {
        var distance : Float = 0.0
        var subDistance : Float = 0.0

        for i in 0...Points.count - 1
        {
//            print("Point \(i) \(Points[i].x), \(Points[i].y), \(Points[i].z) - \(subDistance) / \(distance)")
            if (i < Points.count - 1)
            {
                subDistance = MathUtils.pointDistance(point1: Points[i], point2: Points[i + 1])
                distance += subDistance
            }
        }
    }

    func SimplifyPath(width : Float)
    {
        SimplifyPath(points: &Points, width: width)
    }

    func SimplifyPath(points : inout [Vector3], width : Float)
    {
        var keepPoint : [Bool] = Array<Bool>()
        var startIndices : [Int] = Array<Int>()
        var endIndices : [Int] = Array<Int>()

        for _ in 1...points.count
        {
            keepPoint.append(false)
        }

        startIndices.append(0)
        endIndices.append(points.count - 1)
​
        var maxLoops = points.count

        while (startIndices.count > 0 && maxLoops > 0)
        {
            maxLoops -= 1
​
            let startIndex = startIndices[0]
            let endIndex = endIndices[0]
​
            keepPoint[startIndex] = true
            keepPoint[endIndex] = true

            startIndices.remove(at: 0)
            endIndices.remove(at: 0)

            var furthestIndex : Int = -1
            var furthestDistance : Float = 0.0

            let p1 = points[startIndex]
            let p2 = points[endIndex]

            if (startIndex < endIndex - 2)
            {
                for i in startIndex + 1...endIndex - 1
                {
                    let distance = MathUtils.pointSegmentDistance(point: points[i], startPoint: p1, endPoint: p2)
                    if (distance > furthestDistance)
                    {
                        furthestDistance = distance
                        furthestIndex = i
                    }
//                    print("Point distance: \(distance) | \(startIndex) < \(i) < \(endIndex) | (\(p1.x), \(p1.y), \(p1.z)) > (\(points[i].x), \(points[i].y), \(points[i].z)) > (\(p2.x), \(p2.y), \(p2.z)) | \(furthestDistance)")
                }
            }
            else
            {
                continue
            }

            let fp = points[furthestIndex]

//            print("\(startIndex) -> \(endIndex) - Furthest Distance: \(furthestDistance) at \(furthestIndex) | \(p1.x),\(p1.y),\(p1.z) -> \(p2.x),\(p2.y),\(p2.z) ? \(fp.x),\(fp.y),\(fp.z)")

            if (furthestDistance > width / 2)
            {
//                print("Spliting and testing: \(startIndex) -> \(furthestIndex) -> \(endIndex)")
                startIndices.append(startIndex)
                endIndices.append(furthestIndex)

                startIndices.append(furthestIndex)
                endIndices.append(endIndex)
            }
        }

        let originalCount = points.count
        var removedCount = 0
        let max : Int = points.count - 1
        for i in 0...max
        {
//            print("Point \(max - i) - Keep: \(keepPoint[max - i])")
            if (!keepPoint[max - i])
            {
                removedCount += 1
                points.remove(at: max - i)
            }
        }
//        print("Removed \(removedCount) points out of \(originalCount). Remaining: \(points.count)")
    }
​
    func MinutesForDisplay() -> String
    {
        let minutes = Length / WalkingMetersPerMinute

        if (minutes == 0)
        {
            return "0 minutes"
        }
        if (minutes == 1)
        {
            return "1 minute"
        }
        return "\(minutes) minutes"
    }
}
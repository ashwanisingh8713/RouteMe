//MinutesForDisplay
func setCameraPosition(position: Vector3) {
        if disable {
            return
        }

        let pointsToShow: Int = 4

        let startPoint = turns[0]
        let startPos = Vector3(v: startPoint.worldPosition)
        let dist = startPos.distanceTo(point: position)

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

            let cPt = MathUtils.getClosestPoint(point: position, start: pPos1, end: pPos2)

            let subDistance = position.distanceTo(point: cPt)

            //print("p1: \(pPos1.x), \(pPos1.y), \(pPos1.z) | p2: \(pPos2.x), \(pPos2.y), \(pPos2.z)")
            //print("subDistance \(i): \(subDistance) | \(pointDistances[i]) -> \(pointDistances[i + 1]) | phone: \(position.x), \(position.y), \(position.z) | cpt: \(cPt.x), \(cPt.y), \(cPt.z)")

            if (subDistance < minDistance)
            {
                minDistance = subDistance
                index = i
                let wholeDist = pPos1.distanceTo(point: pPos2)
                let subDist = pPos1.distanceTo(point: cPt)

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
            var dotAngle = MathUtils.radiansBetweenVectors(v1: localForward, v2: l3)

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
        ViewController.Instance.setArrow(angle: arrowAngle)
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

        ViewController.Instance.setPerson(progress: pr)
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
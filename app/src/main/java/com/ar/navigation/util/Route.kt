package com.ar.navigation.util

import android.util.Log
import com.ar.navigation.pathmodel.RouteAnchor

/**
 * Created by Ashwani Kumar Singh on 01,May,2023.
 */
class Route {

    companion object {
        @JvmStatic
        fun SimplifyPath(points: MutableList<RouteAnchor>, width: Float):MutableList<RouteAnchor> {

            var keepPoint = mutableListOf<Boolean>()
            var startIndices = mutableListOf<Int>()
            var endIndices = mutableListOf<Int>()

            for ((index, value) in points.withIndex()) {
                keepPoint.add(false)
            }

            startIndices.add(0)
            endIndices.add(points.size - 1)

            var maxLoops = points.size

            while (startIndices.size > 0 && maxLoops > 0) {
                maxLoops -= 1

                var startIndex = startIndices[0]
                var endIndex = endIndices[0]

                keepPoint[startIndex] = true
                keepPoint[endIndex] = true

                startIndices.removeAt(0)
                endIndices.removeAt(0)

                var furthestIndex: Int = -1
                var furthestDistance: Float = 0.0f

                var p1 = points[startIndex]
                var p2 = points[endIndex]

                if (startIndex < endIndex - 2) {
                    for (i in startIndex + 1..endIndex - 1) {
                        var distance = MathUtils.pointSegmentDistance(
                            point = points[i],
                            startPoint = p1,
                            endPoint = p2
                        )
                        if (distance > furthestDistance) {
                            furthestDistance = distance
                            furthestIndex = i
                        }
                    Log.i("AshwaCalc", "Point distance: $distance | $startIndex < $i < $endIndex | (${p1.x}, ${p1.y}, ${p1.z}) > (${points[i].x}, ${points[i].y}, ${points[i].z}) > (${p2.x}, ${p2.y}, ${p2.z}) | $furthestDistance")
                    }
                } else {
                    continue
                }

                var fp = points[furthestIndex]

                Log.i("AshwaCalc","$startIndex -> $endIndex - Furthest Distance: $furthestDistance at $furthestIndex | ${p1.x},${p1.y},${p1.z} -> ${p2.x},${p2.y},${p2.z} ? ${fp.x},${fp.y},${fp.z}")

                if (furthestDistance > width / 2) {
                    Log.i("AshwaCalc","Spliting and testing: $startIndex -> $furthestIndex -> $endIndex")
                    startIndices.add(startIndex)
                    endIndices.add(furthestIndex)

                    startIndices.add(furthestIndex)
                    endIndices.add(endIndex)
                }
            }

            var originalCount = points.size
            var removedCount = 0
            var max: Int = points.size - 1
            for (i in 0..max) {
                Log.i("AshwaCalc","Point ${max - i} - Keep: ${keepPoint[max - i]}")
                if (!keepPoint[max - i]) {
                    removedCount += 1
                    points.removeAt(max - i)
                }
            }

            Log.i("", "")
            return points
            Log.i("AshwaCalc","Removed ($removedCount) points out of ($originalCount). Remaining: (${points.size})")
        }


    }
}
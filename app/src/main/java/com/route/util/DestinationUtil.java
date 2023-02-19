package com.route.util;

import android.util.Log;

import com.google.android.material.math.MathUtils;
import com.route.data.UpdatedPoints;
import com.route.modal.RoutesDocuments;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashwani Kumar Singh on 16,February,2023.
 */
public class DestinationUtil {


    public static float distanceFromVectoreV3(List<Vector3> Points) {
        float distance = 0.0f;
        float subDistance = 0.0f;
        for( int i=0; i<Points.size()-1;i++) {
            if (i < Points.size() - 1) {
                subDistance = AppMathUtils.PointDistance(Points.get(i), Points.get(i + 1));
                distance += subDistance;
            }
        }
        Log.i("Ashwani", "PrintOutDebug :: "+distance);
        return distance;
    }

    public static List<Vector3> makeVertexData(UpdatedPoints updatedPoints) {
        List<Vector3> vertexList = new ArrayList<>();
        double[] arPoints = updatedPoints.getPoints();
        int totalVertex = arPoints.length/3;
        int index = 0;
        for(int i = 0; i<totalVertex; i++) {
            int temp = index+3;
            Double[] ver = new Double[3];
            float x = (float) arPoints[temp-3];//0 //3
            float y = (float) arPoints[temp-2];//1 //4
            float z = (float) arPoints[temp-1];//2 //5
            Vector3 vector = new Vector3(x, y, z);
            vertexList.add(vector);
            index = temp;
        }
        Log.i("Ashwani", "Vertex Created ");
        //Collections.reverse(vertexList);
        return vertexList;
    }

    public static List<Double[]> makeVertexData_double(UpdatedPoints updatedPoints) {
        List<Double[]> vertexList = new ArrayList<>();
        double[] arPoints = updatedPoints.getPoints();
        int totalVertex = arPoints.length/3;
        int index = 0;
        for(int i = 0; i<totalVertex; i++) {
            int temp = index+3;
            Double[] ver = new Double[3];
            ver[0] = arPoints[temp-3];//0 //3
            ver[1] = arPoints[temp-2];//1 //4
            ver[2] = arPoints[temp-1];//2 //5
            vertexList.add(ver);
            index = temp;
        }
        Log.i("Ashwani", "Vertex Created ");
        //Collections.reverse(vertexList);
        return vertexList;
    }

    //https://shibuiyusuke.medium.com/measuring-distance-with-arcore-6eb15bf38a8f
    public static float distanceFromVertex(List<Double[]> placedAnchorNodes) {
        int len = placedAnchorNodes.size();
        double distance = 0.0f;
        double subDistance = 0.0f;
        if (len > 1) {
            for (int i= 0 ; i< len; i++){
                for (int j = i+1; j<len; j++){
                    double distanceMeter = calculateDistance(
                            placedAnchorNodes.get(i),
                            placedAnchorNodes.get(j));
                    subDistance = changeUnit(distanceMeter, "meter");
                    distance += subDistance;
//                    val distanceCMFloor = "%.2f".format(distanceCM)
//                    multipleDistances[i][j]!!.setText(distanceCMFloor)
//                    multipleDistances[j][i]!!.setText(distanceCMFloor)
                }
            }
        }

        Log.i("Ashwani", "distanceFromVertex ::"+distance);
        return (float) distance;
    }

    private static double calculateDistance(Double[] objectPose0, Double[] objectPose1) {
        return calculateDistance(
                objectPose0[0] - objectPose1[0],
                objectPose0[1] - objectPose1[1],
                objectPose0[2] - objectPose1[2]
        );
    }

    private static double calculateDistance(Double x, Double y, Double z){
        return Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2));
    }

    private static double changeUnit(double distanceMeter, String unit){
        if(unit.equalsIgnoreCase("cm")) {
            return distanceMeter * 100;
        } else if(unit.equalsIgnoreCase("mm")) {
            return distanceMeter * 1000;
        } else {
            return distanceMeter;
        }

    }



    public static float distanceFromPoints(RoutesDocuments route) {
        List<Double> pts = route.pts;

        double x1 = pts.get(0);
        double y1 = pts.get(1);

        int pointSize = pts.size();
        double x2 = pts.get(pointSize-3);
        double y2 = pts.get(pointSize-2);

        float distance = MathUtils.dist((float) x1, (float) y1, (float) x2, (float) y2);

        Log.i("Ashwani", "distanceFromPoints :: "+distance);

        return distance;

        /*for(int i; i< pts.size(); i++) {
            double point1 = pts.get(i);
            double point2 = pts.get(i+1);
//            subDistance = MathUtils.pointDistance(point1, point2);
            subDistance = MathUtils.distanceToFurthestCorner(point1, point2);
            distance += subDistance;
            i++;
        }*/

    }
}

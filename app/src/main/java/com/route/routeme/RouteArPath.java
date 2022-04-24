package com.route.routeme;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.route.data.UpdatedPoints;
import com.route.fragment.CustomArFragment;
import com.route.modal.RoutesDocuments;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import ke.tang.ruler.DrawableMarker;
import ke.tang.ruler.Marker;
import ke.tang.ruler.OnMarkerClickListener;
import ke.tang.ruler.RulerView;


public class RouteArPath extends AppCompatActivity implements CustomArFragment.OnCompleteListener {

    public static final String TAG = "RouteTag";

    @Override
    public void onComplete() {
        Log.i(TAG, "onComplete");
    }

    private enum AppAnchorState {
        NONE,
        HOSTING,
        HOSTED
    }

    private CustomArFragment arFragment;
    private UpdatedPoints updatedPoints;

    private TextView destinationTo;
    private TextView destinationValue;
    private TextView destinationBelongTo;
    private RulerView mRuler;
    private RoutesDocuments selectedRouteItem;

    private AppAnchorState appAnchorState = AppAnchorState.NONE;

    List<Double[]> vertexList;

    ModelRenderable modelRenderable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_ar_path);

        updatedPoints = getIntent().getExtras().getParcelable("Points");
        selectedRouteItem = getIntent().getExtras().getParcelable("selectedRouteItem");

        vertexList = new ArrayList<>();
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

        //Collections.reverse(vertexList);

        createRenderable();

        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        arFragment.setOnCompleteListener(this);

        mRuler = findViewById(R.id.ruler);

        destinationTo = findViewById(R.id.destinationTo);
        destinationValue = findViewById(R.id.destinationValue);
        destinationBelongTo = findViewById(R.id.destinationBelongTo);

        destinationValue.setText(selectedRouteItem.ept);
        destinationBelongTo.setText(selectedRouteItem.loc);


        mRuler.setMaxValue(18);
        mRuler.setValue(0);

        DrawableMarker marker = new DrawableMarker(R.drawable.ic_rocket, mRuler.getValue(), null);
        marker.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public void onMarkerClick(Marker m) {
                Toast.makeText(RouteArPath.this, "Marker is clicked : ", Toast.LENGTH_SHORT).show();
            }
        });
        mRuler.addMarker(marker);


        mRuler.setValue(9);
        mRuler.setIndicator(null);

        Log.i("", "");




        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            //Active only in Admin Mode
                Log.d("HIT_RESULT:", hitResult.toString());

                //anchor = arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                appAnchorState = AppAnchorState.HOSTING;

                //Log.i("Test", anchor.getCloudAnchorId());

                //showToast("Hosting...");
                //createCloudAnchorModel(anchor);

        });


        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {

            Frame frame = arFragment.getArSceneView().getArFrame();
            IntBuffer intBuffer = frame.acquirePointCloud().getIds();
            boolean hasDisplayGeometryChanged = frame.hasDisplayGeometryChanged();
            Pose pose = frame.getAndroidSensorPose();
            float[] xp = pose.getXAxis();
            float[] yp = pose.getYAxis();

            Log.i("UpdateTest", "" + hasDisplayGeometryChanged);
            Log.i("UpdateTest", "x" + xp.toString() +"   ::   y   "+yp.toString());

            if(intBuffer == null) {
                Log.i("UpdateTest", "intBuffer is NUll");
            } else {
                int[] intArray = toArray(intBuffer);
                if (intArray == null) {
                    Log.i("UpdateTest", "intArray is NUll");
                } else {
                    Log.i("UpdateTest", "intArray length is " + intArray.length);

                    if(intArray.length > 100) {
                        if(appAnchorState != AppAnchorState.HOSTED) {
                            appAnchorState = AppAnchorState.HOSTING;
                        }
                    }
                }
            }





            if (appAnchorState == AppAnchorState.NONE || appAnchorState == AppAnchorState.HOSTED)
                return;


            appAnchorState = AppAnchorState.HOSTED;


            for (Double[] updatedPoint : vertexList ) {
                Quaternion camQ = arFragment.getArSceneView().getScene().getCamera().getWorldRotation();
                float x = updatedPoint[0].floatValue();
                float y = updatedPoint[1].floatValue();
                float z = updatedPoint[2].floatValue();
                float[] f1 = new float[]{x, y, z};
                float[] f2 = new float[]{camQ.x, camQ.y, camQ.z, camQ.w};
//                Pose anchorPose = new Pose(f1, f2);
                Pose anchorPose = Pose.makeTranslation(x, y, z);

                // make an ARCore Anchor
                Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(anchorPose);

                AnchorNode anchorNode = new AnchorNode(anchor);

                anchorNode.setRenderable(modelRenderable);
                modelRenderable.setShadowCaster(false);
                modelRenderable.setShadowReceiver(false);
                //adding this to the scene
                arFragment.getArSceneView().getScene().addChild(anchorNode);
            }


            ///////



            ////// Ash



            //Anchor.CloudAnchorState cloudAnchorState = anchor.getCloudAnchorState();

            /*if (cloudAnchorState.isError()) {
                //showToast(cloudAnchorState.toString());
            } else if (cloudAnchorState == Anchor.CloudAnchorState.SUCCESS) {
                appAnchorState = AppAnchorState.HOSTED;

                String anchorId = anchor.getCloudAnchorId();
                System.out.println(anchorId);
                anchorList.add(anchorId);

            }*/
        });

    }

    private void createRenderable() {
        ModelRenderable
                .builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    this.modelRenderable = modelRenderable;
                });
    }


    /*private void createCloudAnchorModel(Anchor anchor) {
        ModelRenderable
                .builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(modelRenderable -> placeCloudAnchorModel(anchor, modelRenderable));

    }*/

    /*private void placeCloudAnchorModel(Anchor anchor, ModelRenderable modelRenderable) {
        *//*if(anchorNode == null) {
            anchorNode = new AnchorNode(anchor);
        } else {
            anchorNode.setAnchor(anchor);
        }*//*

        anchorNode = new AnchorNode(anchor);

        *//*AnchorNode cannot be zoomed in or moved
        So we create a TransformableNode with AnchorNode as the parent*//*
        float x = (float) updatedPoints.getPoints()[0];
        float y = (float)updatedPoints.getPoints()[1];
        float z = (float)updatedPoints.getPoints()[2];
        *//*Vector3 vector3 = new Vector3(x, y, z);*//*
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);



        //transformableNode.setLocalPosition(vector3);

        //anchorNode.setLocalPosition(vector3);

        //
        //transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(x, y, z), 225));

//        transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 180));
        *//*if (modelOptionsSpinner.getSelectedItem().toString().equals("Straight Arrow")) {
            transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 225));
        }
        if (modelOptionsSpinner.getSelectedItem().toString().equals("Right Arrow")) {
            transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 135));
        }
        if (modelOptionsSpinner.getSelectedItem().toString().equals("Left Arrow")) {
            transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 315));
        }*//*
//        transformableNode.setParent(anchorNode);
        //adding the model to the transformable node
        transformableNode.setRenderable(modelRenderable);
        modelRenderable.setShadowCaster(false);
        modelRenderable.setShadowReceiver(false);
        //adding this to the scene
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }*/



    private void cc() {

        float x = (float) updatedPoints.getPoints()[0];
        float y = (float)updatedPoints.getPoints()[1];
        float z = (float)updatedPoints.getPoints()[2];

        // prepare an anchor position
        Quaternion camQ = arFragment.getArSceneView().getScene().getCamera().getWorldRotation();
        float[] f1 = new float[]{x, y, z};
        float[] f2 = new float[]{camQ.x, camQ.y, camQ.z, camQ.w};
        Pose anchorPose = new Pose(f1, f2);

        // make an ARCore Anchor
        Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(anchorPose);
        // Node that is automatically positioned in world space based on the ARCore Anchor.

    }



    public int[] toArray(IntBuffer b) {
        if(b.hasArray()) {
            if(b.arrayOffset() == 0)
                return b.array();

            return Arrays.copyOfRange(b.array(), b.arrayOffset(), b.array().length);
        }

        b.rewind();
        int[] foo = new int[b.remaining()];
        b.get(foo);

        return foo;
    }

}

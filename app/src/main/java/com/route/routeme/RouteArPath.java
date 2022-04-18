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
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.route.data.UpdatedPoints;
import com.route.fragment.CustomArFragment;
import com.route.modal.RoutesDocuments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import ke.tang.ruler.DrawableMarker;
import ke.tang.ruler.Marker;
import ke.tang.ruler.OnMarkerClickListener;
import ke.tang.ruler.RulerView;


public class RouteArPath extends AppCompatActivity {

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
    private DefaultState mState;
    private RoutesDocuments selectedRouteItem;

    private ArrayList anchorList;
    private Anchor anchor;
    private AnchorNode anchorNode;
    private AppAnchorState appAnchorState = AppAnchorState.NONE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_ar_path);

        updatedPoints = getIntent().getExtras().getParcelable("Points");
        selectedRouteItem = getIntent().getExtras().getParcelable("selectedRouteItem");

        mRuler = findViewById(R.id.ruler);

        destinationTo = findViewById(R.id.destinationTo);
        destinationValue = findViewById(R.id.destinationValue);
        destinationBelongTo = findViewById(R.id.destinationBelongTo);

        destinationValue.setText(selectedRouteItem.ept);
        destinationBelongTo.setText(selectedRouteItem.loc);


        mState = new DefaultState(mRuler);
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


        Log.i("", "");


        anchorList = new ArrayList();
        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);


        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                for(Double[] dob : vertexList) {

                    try {

                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        //do stuff
                    }

                    double[] unboxed = Stream.of(dob).mapToDouble(Double::doubleValue).toArray();
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putDoubleArray("vertex", unboxed);
                    msg.setData(bundle);
                    handler.sendMessageDelayed(msg, 2000);

                }

            }
        }, 10000);*/

//        double[] unboxed = Stream.of(vertexList.get(0)).mapToDouble(Double::doubleValue).toArray();
//        Message msg = new Message();
//        Bundle bundle = new Bundle();
//        bundle.putDoubleArray("vertex", unboxed);
//        msg.setData(bundle);
//        handler.sendMessageDelayed(msg, 2000);

        double[] unboxed1 = Stream.of(vertexList.get(0)).mapToDouble(Double::doubleValue).toArray();
        Message msg1 = new Message();
        Bundle bundle1 = new Bundle();
        bundle1.putDoubleArray("vertex", unboxed1);
        msg1.setData(bundle1);
        //handler.sendMessageDelayed(msg1, 5000);


        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            //Active only in Admin Mode
                Log.d("HIT_RESULT:", hitResult.toString());
                anchor = arFragment.getArSceneView().getSession().hostCloudAnchor(hitResult.createAnchor());
                appAnchorState = AppAnchorState.HOSTING;

                Log.i("Test", anchor.getCloudAnchorId());

                //showToast("Hosting...");
                createCloudAnchorModel(anchor);

        });


        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {

            if (appAnchorState != AppAnchorState.HOSTING)
                return;
            Anchor.CloudAnchorState cloudAnchorState = anchor.getCloudAnchorState();

            if (cloudAnchorState.isError()) {
                //showToast(cloudAnchorState.toString());
            } else if (cloudAnchorState == Anchor.CloudAnchorState.SUCCESS) {
                appAnchorState = AppAnchorState.HOSTED;

                String anchorId = anchor.getCloudAnchorId();
                System.out.println(anchorId);
                anchorList.add(anchorId);

                /*if (FROM.equalsIgnoreCase(LauncherActivity.ELECTRONICS)) {
                    tinydb.putListString(ELECTRONICS, anchorList);
                } else if (FROM.equalsIgnoreCase(LauncherActivity.TOYS)) {
                    tinydb.putListString(TOYS, anchorList);
                } else if (FROM.equalsIgnoreCase(LauncherActivity.TV_APPLIANCES)) {
                    tinydb.putListString(TV_APPLIANCES, anchorList);
                } else if (FROM.equalsIgnoreCase(LauncherActivity.CLOTHING)) {
                    tinydb.putListString(CLOTHING, anchorList);
                }*/

                //showToast("Anchor hosted successfully. Anchor Id: " + anchorId);
            }
        });

    }


    Handler handler = new Handler(Looper.myLooper()) {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();

            double[] unboxed = bundle.getDoubleArray("vertex");
            double x = unboxed[0];
            double y = unboxed[1];
            double z = unboxed[2];

            Log.i("Test", ""+x);
            Log.i("Test", ""+y);
            Log.i("Test", ""+z);

            // t:[x:-0.266, y:-0.082, z:-0.672], q:[x:0.00, y:0.09, z:0.00, w:1.00]
            Quaternion camQ = arFragment.getArSceneView().getScene().getCamera().getWorldRotation();
//                float[] f1 = new float[]{-0.266f, -0.082f, -0.672f};
            float[] f1 = new float[]{(float) x,(float) y, (float)z};
            float[] f2 = new float[]{camQ.x, camQ.y, camQ.z, camQ.w};
            Pose anchorPose = new Pose(f1, f2);

            // make an ARCore Anchor
            Anchor anchor = arFragment.getArSceneView().getSession().createAnchor(anchorPose);
            createCloudAnchorModel(anchor);

        }


    };



    private void createCloudAnchorModel(Anchor anchor) {
        ModelRenderable
                .builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(modelRenderable -> placeCloudAnchorModel(anchor, modelRenderable));

    }

    private void placeCloudAnchorModel(Anchor anchor, ModelRenderable modelRenderable) {
        anchorNode = new AnchorNode(anchor);
        /*AnchorNode cannot be zoomed in or moved
        So we create a TransformableNode with AnchorNode as the parent*/
        float x = (float) updatedPoints.getPoints()[0];
        float y = (float)updatedPoints.getPoints()[1];
        float z = (float)updatedPoints.getPoints()[2];
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(x, y, z), 180));
//        transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 180));
        /*if (modelOptionsSpinner.getSelectedItem().toString().equals("Straight Arrow")) {
            transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 225));
        }
        if (modelOptionsSpinner.getSelectedItem().toString().equals("Right Arrow")) {
            transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 135));
        }
        if (modelOptionsSpinner.getSelectedItem().toString().equals("Left Arrow")) {
            transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1f, 0), 315));
        }*/
        transformableNode.setParent(anchorNode);
        //adding the model to the transformable node
        transformableNode.setRenderable(modelRenderable);
        modelRenderable.setShadowCaster(false);
        modelRenderable.setShadowReceiver(false);
        //adding this to the scene
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }



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

    private class DefaultState {
        private int mStepWidth;
        private ColorStateList mScaleColor;
        private ColorStateList mRulerColor;
        private int mSectionScaleCount;
        private Drawable mIndicator;
        private int mScaleMinHeight;
        private int mScaleMaxHeight;
        private int mScaleSize;
        private int mMaxValue;
        private int mMinValue;
        private int mValue;
        private float mTextSize;
        private ColorStateList mTextColor;

        public DefaultState(RulerView ruler) {
            mStepWidth = ruler.getStepWidth();
            mScaleColor = ruler.getScaleColor();
            mRulerColor = ruler.getRulerColor();
            mSectionScaleCount = ruler.getSectionScaleCount();
            mIndicator = ruler.getIndicator();
            mScaleMinHeight = ruler.getScaleMinHeight();
            mScaleMaxHeight = ruler.getScaleMaxHeight();
            mScaleSize = ruler.getScaleSize();
            mMaxValue = ruler.getMaxValue();
            mMinValue = ruler.getMinValue();
            mValue = ruler.getValue();
            mTextSize = ruler.getTextSize();
            mTextColor = ruler.getTextColor();
        }

        public int getStepWidth() {
            return mStepWidth;
        }

        public ColorStateList getScaleColor() {
            return mScaleColor;
        }

        public ColorStateList getRulerColor() {
            return mRulerColor;
        }

        public int getSectionScaleCount() {
            return mSectionScaleCount;
        }

        public Drawable getIndicator() {
            return mIndicator;
        }

        public int getScaleMinHeight() {
            return mScaleMinHeight;
        }

        public int getScaleMaxHeight() {
            return mScaleMaxHeight;
        }

        public int getScaleSize() {
            return mScaleSize;
        }

        public int getMaxValue() {
            return mMaxValue;
        }

        public int getMinValue() {
            return mMinValue;
        }

        public int getValue() {
            return mValue;
        }

        public float getTextSize() {
            return mTextSize;
        }

        public ColorStateList getTextColor() {
            return mTextColor;
        }
    }

}

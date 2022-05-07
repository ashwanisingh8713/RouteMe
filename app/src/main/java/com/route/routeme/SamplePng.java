package com.route.routeme;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.route.data.UpdatedPoints;
import com.route.fragment.CustomArFragment;
import com.route.modal.RoutesDocuments;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;


// https://itecnote.com/tecnote/android-how-to-display-png-image-in-arcore/

public class SamplePng extends AppCompatActivity implements CustomArFragment.OnCompleteListener {

    private CustomArFragment arFragment;

    List<Double[]> vertexList;
    private UpdatedPoints updatedPoints;
    private RoutesDocuments selectedRouteItem;

//    ViewRenderable modelRenderable;
    ViewRenderable renderable ;

    private RouteArPath.AppAnchorState appAnchorState = RouteArPath.AppAnchorState.NONE;

    CompletableFuture<ViewRenderable> dataView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_png);

        updatedPoints = getIntent().getExtras().getParcelable("Points");
        selectedRouteItem = getIntent().getExtras().getParcelable("selectedRouteItem");
        vertexList = new ArrayList<>();

//        makeInfoView();

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

        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        arFragment.setOnCompleteListener(this);


        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {

                Anchor anchor = hitResult.createAnchor();
                placeObject(arFragment, anchor);
            }
        });


    }

    private void placeObject(ArFragment fragment , Anchor anchor) {
        ViewRenderable.builder()
                .setView(this, R.layout.imgboard)
                .build()
                .thenAccept(renderable -> {
//                    modelRenderable = renderable;
                    //ImageView imgView = (ImageView)renderable.getView();

                    Toast.makeText(SamplePng.this, "View created ", Toast.LENGTH_SHORT);
                    addControlsToScene(fragment, anchor, renderable);

                })
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {

                        Log.e("ERROR", "unable to load renderable.", throwable);
                        return null;
                    }
                });
    }

    private void addControlsToScene(ArFragment fragment, Anchor anchor , Renderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragment.getArSceneView().getScene().addChild(anchorNode);
    }

    @Override
    public void onComplete() {



//        viewRenderable();
//        createRenderable();
    }



    private void viewRenderable() {
        ViewRenderable.builder()
                .setView(this, R.layout.imgboard)
                .build()
                .thenAccept(renderable -> {
//                    modelRenderable = renderable;
                    //ImageView imgView = (ImageView)renderable.getView();

                    Toast.makeText(SamplePng.this, "View created ", Toast.LENGTH_SHORT);

                })
                .exceptionally(new Function<Throwable, Void>() {
                    @Override
                    public Void apply(Throwable throwable) {

                        Log.e("ERROR", "unable to load renderable.", throwable);
                        return null;
                    }
                })
        ;
    }


    private void createRenderable() {
        ModelRenderable
                .builder()
                .setSource(this, Uri.parse("model.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    //this.modelRenderable = modelRenderable;
                });
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




    public void makeInfoView() {
        //dataView = ViewRenderable.builder().setView(this, R.layout.imgboard).build();

        LayoutInflater inflater = this.getLayoutInflater();
        ImageView imageOverlay = (ImageView) inflater.inflate(R.layout.imgboard, null);

        ViewRenderable.builder()
                .setView(this, imageOverlay)
                .setVerticalAlignment(ViewRenderable.VerticalAlignment.CENTER)
                .setHorizontalAlignment(ViewRenderable.HorizontalAlignment.CENTER)
                .build()
                .thenAccept(renderable -> {
                  //  modelRenderable = renderable;
                    // Remove shadow from overlay image

                })
                .exceptionally(
                        throwable -> {
                            Log.e("Log", "unable to load renderable.", throwable);
                            return null;
                        }
                );

       /*ViewRenderable.builder().setView(this, R.layout.imgboard).build()
               .thenAccept(renderable -> {
                   modelRenderable = renderable;
                   //ImageView imgView = (ImageView)renderable.getView();
               })
               .exceptionally(new Function<Throwable, Void>() {
                   @Override
                   public Void apply(Throwable throwable) {

                       Log.e("ERROR", "unable to load renderable.", throwable);
                       return null;
                   }
               });*/

        //createLayoutRenderable();
    }

    public void createLayoutRenderable() {

        try {
            renderable = dataView.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

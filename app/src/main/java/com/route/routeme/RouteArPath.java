package com.route.routeme;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.route.data.UpdatedPoints;
import com.route.modal.RoutesDocuments;
import java.util.ArrayList;
import java.util.List;

import ke.tang.ruler.DrawableMarker;
import ke.tang.ruler.Marker;
import ke.tang.ruler.OnMarkerClickListener;
import ke.tang.ruler.RulerView;


public class RouteArPath extends AppCompatActivity  {

    public static final String TAG = "RouteTag";

    public enum AppAnchorState {
        NONE,
        HOSTING,
        HOSTED
    }

    private UpdatedPoints updatedPoints;
    private RoutesDocuments selectedRouteItem;
    private List<float[]> vertexList;
    private AppAnchorState appAnchorState = AppAnchorState.NONE;

    private TextView destinationTo;
    private TextView destinationValue;
    private TextView destinationBelongTo;
    private RulerView mRuler;
    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;


    private Session session;
    private boolean shouldConfigureSession = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_ar_path);

        surfaceView = findViewById(R.id.surfaceview);


        updatedPoints = getIntent().getExtras().getParcelable("Points");
        selectedRouteItem = getIntent().getExtras().getParcelable("selectedRouteItem");

        vertexList = new ArrayList<>();
        float[] arPoints = updatedPoints.getPoints();
        int totalVertex = arPoints.length/3;
        int index = 0;
        for(int i = 0; i<totalVertex; i++) {
            int temp = index+3;
            float[] ver = new float[3];
            ver[0] = arPoints[temp-3];//0 //3
            ver[1] = arPoints[temp-2];//1 //4
            ver[2] = arPoints[temp-1];//2 //5
            vertexList.add(ver);
            index = temp;
        }

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


    }


    @Override
    protected void onResume() {
        super.onResume();

        if(session == null) {
            try {
                session = new Session(this);
                if (shouldConfigureSession) {
                    configureSession();
                    shouldConfigureSession = false;
                }
            } catch (UnavailableArcoreNotInstalledException e) {
                e.printStackTrace();
            } catch (UnavailableApkTooOldException e) {
                e.printStackTrace();
            } catch (UnavailableSdkTooOldException e) {
                e.printStackTrace();
            } catch (UnavailableDeviceNotCompatibleException e) {
                e.printStackTrace();
            }
        }

    }

    private void configureSession() {
        Config config = new Config(session);
        config.setFocusMode(Config.FocusMode.AUTO);
        /*if (!mARDB.setupAugmentedImageDatabase(config)) {
            //messageSnackbarHelper.showError(this, "Could not setup augmented image database");
            Toast.makeText(this, "Could not setup augmented image database", Toast.LENGTH_LONG).show();
        }*/
        session.configure(config);
    }

}

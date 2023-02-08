package com.route.routeme;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

    private UpdatedPoints updatedPoints;

    private TextView destinationTo;
    private TextView destinationValue;
    private TextView destinationBelongTo;
    private RulerView mRuler;
    private RoutesDocuments selectedRouteItem;
    private List<Double[]> vertexList;

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




    private void cc() {

        float x = (float) updatedPoints.getPoints()[0];
        float y = (float)updatedPoints.getPoints()[1];
        float z = (float)updatedPoints.getPoints()[2];



    }





}

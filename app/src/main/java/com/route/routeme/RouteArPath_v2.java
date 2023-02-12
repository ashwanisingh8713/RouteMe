package com.route.routeme;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.route.data.UpdatedPoints;
import com.route.modal.RoutesDocuments;
import com.route.routeme.databinding.ActivityRouteArPathV2Binding;

import java.util.ArrayList;
import java.util.List;

import ke.tang.ruler.DrawableMarker;
import ke.tang.ruler.Marker;
import ke.tang.ruler.OnMarkerClickListener;


public class RouteArPath_v2 extends AppCompatActivity  {

    public static final String TAG = "RouteTag";

    private ActivityRouteArPathV2Binding binding;
    private UpdatedPoints updatedPoints;

    private RoutesDocuments selectedRouteItem;
    private List<Double[]> vertexList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRouteArPathV2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        updatedPoints = getIntent().getExtras().getParcelable("Points");
        selectedRouteItem = getIntent().getExtras().getParcelable("selectedRouteItem");

        vertexList = new ArrayList<>();
        // Uncomment Below commented code
        // Commented for Route Path UI Creation
       /* double[] arPoints = updatedPoints.getPoints();
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
        }*/

        //Collections.reverse(vertexList);


        // Uncomment Below commented code
        // Commented for Route Path UI Creation
//        destinationValue.setText(selectedRouteItem.ept);
//        destinationBelongTo.setText(selectedRouteItem.loc);

        binding.destinationValue.setText("EPT Value");
        binding.destinationBelongTo.setText("LOC Value");


        binding.ruler.setMaxValue(18);
        binding.ruler.setValue(0);

        DrawableMarker marker = new DrawableMarker(R.drawable.ic_rocket, binding.ruler.getValue(), null);
        marker.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public void onMarkerClick(Marker m) {
                Toast.makeText(RouteArPath_v2.this, "Marker is clicked : ", Toast.LENGTH_SHORT).show();
            }
        });
        binding.ruler.addMarker(marker);


        binding.ruler.setValue(9);
        binding.ruler.setIndicator(null);

    }


    private void cc() {

        float x = (float) updatedPoints.getPoints()[0];
        float y = (float)updatedPoints.getPoints()[1];
        float z = (float)updatedPoints.getPoints()[2];

    }





}

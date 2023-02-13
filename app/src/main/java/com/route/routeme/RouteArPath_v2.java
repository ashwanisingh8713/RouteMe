package com.route.routeme;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.route.alert.ErrorDialog;
import com.route.data.UpdatedPoints;
import com.route.modal.RoutesDocuments;
import com.route.routeme.databinding.ActivityRouteArPathV2Binding;
import com.route.viewmodel.RoutesDataViewModel;

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
    private List<Double[]> mVertexList;
    private RoutesDataViewModel model;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRouteArPathV2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        model = new ViewModelProvider(this).get(RoutesDataViewModel.class);

        model.loadRoutesDocument();

        String id = getIntent().getExtras().getString("id");

        model.getRoutesDocument().observe(this, routesData -> {

            List<RoutesDocuments> routes = routesData.getDocuments();
            Log.i("", "");
            RoutesDocuments routesItem = new RoutesDocuments();
            routesItem.id = id;

            int index = routes.indexOf(routesItem);
            if(index != -1) {
                selectedRouteItem = routes.get(index);
                Double ud = selectedRouteItem.ud;
                List<Double> arrowPoints = selectedRouteItem.pts;
                updatedPoints = new UpdatedPoints(arrowPoints.size());
                int count = 0 ;
                for(Double in : arrowPoints) {
                    Log.i("Check", "in :: "+in);
                    Log.i("Check", "ud :: "+ud);
                    double temp = in*ud;
                    Log.i("Check", "temp :: "+temp);
                    updatedPoints.addPoints(count, temp);
                    count++;

                }
            }

            binding.progressBar.setVisibility(View.GONE);

            makeVertexData();
            showDataOnUI();

        });

        model.getRoutesError().observe(this, error -> {
            ErrorDialog.showErrorDialog(getSupportFragmentManager(), "Error!", "Kindly try again");
        });


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

    private void makeVertexData() {
        mVertexList = new ArrayList<>();
        // Uncomment Below commented code
        // Commented for Route Path UI Creation
        double[] arPoints = updatedPoints.getPoints();
        int totalVertex = arPoints.length/3;
        int index = 0;
        for(int i = 0; i<totalVertex; i++) {
            int temp = index+3;
            Double[] ver = new Double[3];
            ver[0] = arPoints[temp-3];//0 //3
            ver[1] = arPoints[temp-2];//1 //4
            ver[2] = arPoints[temp-1];//2 //5
            mVertexList.add(ver);
            index = temp;
        }

        //Collections.reverse(vertexList);
    }

    private void showDataOnUI() {
        binding.destinationValue.setText(selectedRouteItem.ept);
        binding.destinationBelongTo.setText(selectedRouteItem.loc);
    }


    private void cc() {

        float x = (float) updatedPoints.getPoints()[0];
        float y = (float)updatedPoints.getPoints()[1];
        float z = (float)updatedPoints.getPoints()[2];

    }





}

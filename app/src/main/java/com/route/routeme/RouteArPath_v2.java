package com.route.routeme;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.route.alert.ErrorDialog;
import com.route.data.UpdatedPoints;
import com.route.modal.RoutesDocuments;
import com.route.routeme.databinding.ActivityRouteArPathV2Binding;
import com.route.util.DestinationUtil;
import com.route.util.Vector3;
import com.route.viewmodel.RoutesDataViewModel;

import java.util.List;


public class RouteArPath_v2 extends AppCompatActivity  {

    public static final String TAG = "RouteTag";

    private ActivityRouteArPathV2Binding binding;

    private RoutesDataViewModel model;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRouteArPathV2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        model = new ViewModelProvider(this).get(RoutesDataViewModel.class);

        // Init Making API Request
        model.loadRoutesDocument();

        String id = getIntent().getExtras().getString("id");

        model.getRoutesDocument().observe(this, routesData -> {
            List<RoutesDocuments> allRoutes = routesData.getDocuments();
            // Making selected route object to find
            // the particular route index from RouteList Data (allRoutes)
            final RoutesDocuments routesItem = new RoutesDocuments();
            routesItem.id = id;

            final int index = allRoutes.indexOf(routesItem);
            if(index != -1) {
                RoutesDocuments selectedRouteItem = allRoutes.get(index);
                Double ud = selectedRouteItem.ud;
                List<Double> arrowPoints = selectedRouteItem.pts;
                UpdatedPoints updatedPoints = new UpdatedPoints(arrowPoints.size());
                int count = 0 ;
                for(Double in : arrowPoints) {
                    Log.i("Check", "in :: "+in);
                    Log.i("Check", "ud :: "+ud);
                    double temp = in*ud;
                    Log.i("Check", "temp :: "+temp);
                    updatedPoints.addPoints(count, temp);
                    count++;

                }
                // Show Data on UI
                showDataOnUI(selectedRouteItem);

                float distance_point = DestinationUtil.distanceFromPoints(selectedRouteItem);
                Log.i("Distance", "distance_point :: "+distance_point);

                List<Double[]> vertexList = DestinationUtil.makeVertexData_double(updatedPoints);
                float distance_vertex = DestinationUtil.distanceFromVertex(vertexList);
                Log.i("Distance", "distance_vertex :: "+distance_vertex);

                List<Vector3> vertexList_v3 = DestinationUtil.makeVertexData(updatedPoints);
                float distance_vector = DestinationUtil.distanceFromVectoreV3(vertexList_v3);
                Log.i("Distance", "distance_vector :: "+distance_vector);

            } else {
                ErrorDialog.showErrorDialog(getSupportFragmentManager(), "Sorry!", "Route doesn't exist");
            }

            binding.progressBar.setVisibility(View.GONE);

        });

        model.getRoutesError().observe(this, error -> {
            binding.progressBar.setVisibility(View.GONE);
            ErrorDialog.showErrorDialog(getSupportFragmentManager(), "Error!", "Kindly try again");
        });

    }



    private void showDataOnUI(RoutesDocuments selectedRouteItem) {
        binding.destinationValue.setText(selectedRouteItem.ept);
        binding.destinationBelongTo.setText(selectedRouteItem.loc);
        if(selectedRouteItem.len > 1) {
            binding.txtDistance.setText(selectedRouteItem.len+" meters");
        } else if(selectedRouteItem.len == 1) {
            binding.txtDistance.setText(selectedRouteItem.len + " meter");
        }

    }




    /*private void cc() {

        float x = (float) updatedPoints.getPoints()[0];
        float y = (float)updatedPoints.getPoints()[1];
        float z = (float)updatedPoints.getPoints()[2];

    }*/





}

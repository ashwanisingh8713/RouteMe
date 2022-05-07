package com.route.routeme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.route.data.UpdatedPoints;
import com.route.modal.RoutesDocuments;
import com.route.routeme.databinding.ActivityMainBinding;
import com.route.routeme.databinding.ActivityRouteDetailBinding;
import com.route.viewmodel.RoutesDataViewModel;

import java.util.ArrayList;
import java.util.List;

public class RouteDetail extends AppCompatActivity {

    private RoutesDataViewModel model;
    private ActivityRouteDetailBinding activityRouteDetailBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityRouteDetailBinding = ActivityRouteDetailBinding.inflate(getLayoutInflater());
        setContentView(activityRouteDetailBinding.getRoot());

        String id = getIntent().getExtras().getString("id");
        Log.i("", "");

        model = new ViewModelProvider(this).get(RoutesDataViewModel.class);
        //model.clearDisposable();
//        model.loadRoutesDocumentJson();
        model.loadRoutesDocument();

        model.getRoutesDocument().observe(this, routesData -> {
            // update UI
            List<RoutesDocuments> routes = routesData.getDocuments();
            //binding.resText.setText(routes.get(0).id);
            Log.i("", "");
            RoutesDocuments routesItem = new RoutesDocuments();
            routesItem.id = id;

            int index = routes.indexOf(routesItem);
            if(index != -1) {
                RoutesDocuments selectedRouteItem = routes.get(index);
                Double ud = selectedRouteItem.ud;
                List<Double> arrowPoints = selectedRouteItem.pts;
                UpdatedPoints newArrowPoints = new UpdatedPoints(arrowPoints.size());
                int count = 0 ;
                for(Double in : arrowPoints) {
                    Log.i("Check", "in :: "+in);
                    Log.i("Check", "ud :: "+ud);
                    double temp = in*ud;
                    Log.i("Check", "temp :: "+temp);
                    newArrowPoints.addPoints(count, temp);
                    count++;


                    /*TextView textView = new TextView(this);
                    textView.setTextColor(getColor(android.R.color.darker_gray));
                    textView.setText(""+temp+"      =  ("+in+"*"+ud+")");
                    textView.setPadding(20, 20, 10, 20);
                    textView.setBackground(getDrawable(R.drawable.shape_rectangle));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
                    textView.setLayoutParams(params);
                    activityRouteDetailBinding.itemParent.addView(textView);*/
                }
                Log.i("", "");
                Intent intent = new Intent(RouteDetail.this, RouteArPath.class);
//                Intent intent = new Intent(RouteDetail.this, SamplePng.class);
                intent.putExtra("Points", newArrowPoints);
                intent.putExtra("selectedRouteItem", selectedRouteItem);
                startActivity(intent);
                finish();

            }

            activityRouteDetailBinding.progressBar.setVisibility(View.GONE);

            Log.i("", "");

        });

        model.getRoutesDocumentJson().observe(this, routesJsonData -> {
            // update UI
            Log.i("", "");
        });

        model.getRoutesError().observe(this, error -> {
            Log.i("", "");
        });


    }


}

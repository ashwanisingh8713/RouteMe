package com.route.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.route.apis.ApiManager;
import com.route.modal.RoutesDocuments;
import com.route.routeme.JavaBarCodeActivity;
import com.route.routeme.R;
import com.route.routeme.databinding.FragmentFirstBinding;
import com.route.viewmodel.RoutesDataViewModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FirstFragment extends Fragment {

    private static final String TAG = "GenAuth";

    protected final CompositeDisposable mDisposable = new CompositeDisposable();
    private FragmentFirstBinding binding;
    private RoutesDataViewModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });*/


        binding.barcodeBtn.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), JavaBarCodeActivity.class);
            someActivityResultLauncher.launch(intent);
        });


        model = new ViewModelProvider(requireActivity()).get(RoutesDataViewModel.class);
        model.clearDisposable();

        binding.databaseBtn.setOnClickListener(btnView -> {
            model.loadDatabaseList();
        });

        binding.collectionBtn.setOnClickListener(btnView -> {
            model.loadRoutesMeDataCollection();
        });

        binding.documentBtn.setOnClickListener(btnView -> {
            //model.loadRoutesDocument();
            model.loadRoutesDocumentJson();
        });

        model.getRoutesDocument().observe(requireActivity(), routesData -> {
            // update UI
            List<RoutesDocuments> routes = routesData.getDocuments();
            binding.resText.setText(routes.get(0).id);
        });

        model.getRoutesDocumentJson().observe(requireActivity(), routesJsonData -> {
            // update UI
            if(binding != null && binding.resText != null) {
                binding.resText.setText(routesJsonData.toString());
            }
        });

        model.getCollection().observe(requireActivity(), collectionJson -> {
            // update UI
            binding.resText.setText(collectionJson.toString());
        });

        model.getDatabaseList().observe(requireActivity(), databaseJson -> {
            // update UI
            binding.resText.setText(databaseJson.toString());
        });

        model.getRoutesError().observe(requireActivity(), error -> {
            binding.resText.setText(error);
        });

        String requiredValue = "http://routemeapp.com/TestCode01";
        NavController navController = NavHostFragment.findNavController(FirstFragment.this);
        Bundle bundle = new Bundle();
        bundle.putString("Url", requiredValue);
        DocumentFragment.s_URL = requiredValue;
        navController.setGraph(R.navigation.nav_graph, bundle);
        navController.navigate(R.id.action_FirstFragment_to_DocumentFragment);
        binding.title.setText("List of Routes");

    }

    @Override
    public void onResume() {
        super.onResume();
        binding.title.setText("Scan The Bar Code");
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        String requiredValue = data.getStringExtra("key");
                        Log.i("Response", ""+requiredValue);

                        NavController navController = NavHostFragment.findNavController(FirstFragment.this);
                        Bundle bundle = new Bundle();
                        bundle.putString("Url", requiredValue);
                        DocumentFragment.s_URL = requiredValue;
                        navController.setGraph(R.navigation.nav_graph, bundle);
                        navController.navigate(R.id.action_FirstFragment_to_DocumentFragment);
                        binding.title.setText("List of Routes");
                    }
                }});


    @Override
    public void onDestroyView() {
        mDisposable.clear();
        mDisposable.dispose();
        super.onDestroyView();
        binding = null;
    }

}
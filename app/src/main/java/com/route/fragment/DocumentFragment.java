package com.route.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavArgs;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.route.adapter.DocumentRecyclerAdapter;
import com.route.modal.RoutesDocuments;
import com.route.routeme.RouteArPath;
import com.route.routeme.RouteDetail;
import com.route.routeme.databinding.FragmentDocumentsBinding;
import com.route.viewmodel.DocumentDataViewModel;

import java.util.List;


public class DocumentFragment extends Fragment {

    private FragmentDocumentsBinding binding;
    private DocumentRecyclerAdapter mAdapter;
    private DocumentDataViewModel model;

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDocumentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(DocumentDataViewModel.class);
        mAdapter = new DocumentRecyclerAdapter();

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(mAdapter);

        // Older - 1
        model.loadQRCodesDocument();
        model.getQRCodesDocument().observe(requireActivity(), routesBeans -> {
            // update UI
            mAdapter.setDocumentList(routesBeans);
            mAdapter.notifyDataSetChanged();
            binding.progressBar.setVisibility(View.GONE);
        });

//        model.loadRoutesDocument();
        model.getRoutesDocument().observe(requireActivity(), routesData -> {
            // update UI
            Log.i("", "");
//            List<RoutesDocuments> routes = routesData.getDocuments();
//            mAdapter.setDocumentList(routes);
//            mAdapter.notifyDataSetChanged();
            binding.progressBar.setVisibility(View.GONE);
        });

        model.getRoutesError().observe(requireActivity(), routeError -> {
            Log.i("", "");
            binding.progressBar.setVisibility(View.GONE);
            binding.errorTxt.setVisibility(View.VISIBLE);
            binding.errorTxt.setText(routeError);
        });
    }
}

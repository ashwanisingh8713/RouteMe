package com.route.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.route.adapter.DocumentRecyclerAdapter;
import com.route.routeme.databinding.FragmentDocumentsBinding;
import com.route.viewmodel.DocumentDataViewModel;

public class DocumentFragment extends Fragment {

    private FragmentDocumentsBinding binding;
    private DocumentRecyclerAdapter mAdapter;
    private DocumentDataViewModel model;

    private String mUrl;

    @Override
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        mUrl = getArguments().getString("Url");
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


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        showToast("Send request to Server");
        // Older - 1
        /*model.loadQRCodesDocument();
        model.getQRCodesDocument().observe(requireActivity(), routesBeans -> {
            // update UI
            showToast("Success Count :: "+routesBeans.size());
            FileUtil.writeFile("Success :: Request from Server :: Response Count is :: "+routesBeans.size());
            mAdapter.setDocumentList(routesBeans);
            mAdapter.notifyDataSetChanged();
            binding.progressBar.setVisibility(View.GONE);
        });*/

        mAdapter = new DocumentRecyclerAdapter();
        model.loadAppClipCodesDocument(mUrl);

        binding.recyclerView.setAdapter(mAdapter);
        model.getRoutesDocument().observe(requireActivity(), routesData -> {
            // update UI
            Log.i("", "");
            mAdapter.setDocumentList(routesData);
            mAdapter.notifyDataSetChanged();
            binding.progressBar.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.errorTxt.setVisibility(View.GONE);
        });

        model.getRoutesError().observe(requireActivity(), routeError -> {
            showToast("Request Failed :: "+routeError);
            binding.progressBar.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.GONE);
            binding.errorTxt.setVisibility(View.VISIBLE);
            binding.errorTxt.setText(routeError);
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }
}

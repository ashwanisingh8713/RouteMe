package com.route.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.route.adapter.DocumentRecyclerAdapter;
import com.route.routeme.MainActivity;
import com.route.routeme.databinding.FragmentDocumentsBinding;
import com.route.viewmodel.DocumentDataViewModel;

public class DocumentFragment extends Fragment {

    private FragmentDocumentsBinding binding;
    private DocumentRecyclerAdapter mAdapter;
    private DocumentDataViewModel viewModel;

    private String mUrl;


    public static DocumentFragment getInstance(String url) {
        DocumentFragment fragment = new DocumentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Url", url);
        fragment.setArguments(bundle);
        return fragment;
    }

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

        viewModel = new ViewModelProvider(requireActivity()).get(DocumentDataViewModel.class);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());

        showToast("Send request to Server");

        mAdapter = new DocumentRecyclerAdapter();
        viewModel.loadAppClipCodesDocument(mUrl);

        binding.recyclerView.setAdapter(mAdapter);
        viewModel.getRoutesDocument().observe(requireActivity(), routesData -> {
            // update UI
            mAdapter.setDocumentList(routesData);
            mAdapter.notifyDataSetChanged();
            binding.progressBar.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.errorTxt.setVisibility(View.GONE);
        });

        viewModel.getRoutesError().observe(requireActivity(), routeError -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.GONE);
            binding.errorTxt.setVisibility(View.VISIBLE);
            binding.errorTxt.setText(routeError);
            finishActivityOnBackPress(true);
            registerNewScannerPage();
        });
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    private void finishActivityOnBackPress(boolean isFinishActivity) {
        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(isFinishActivity) {
            @Override
            public void handleOnBackPressed() {
                if (mAdapter == null || mAdapter.getItemCount() == 0) {
                    requireActivity().finish();
                }
            }
        });
    }

    private void registerNewScannerPage() {
        binding.errorTxt.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), MainActivity.class));
            requireActivity().finish();
        });
    }


}

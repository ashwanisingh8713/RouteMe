package com.route.routeme;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.route.apis.ApiManager;
import com.route.routeme.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GenAuth";

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    protected final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.databaseBtn.setOnClickListener(view -> {
            checkGetDatabase();
        });

        binding.collectionBtn.setOnClickListener(view -> {
            checkGetCollection();
        });

        binding.documentBtn.setOnClickListener(view -> {
            checkGetDocument();
        });


    }



    private void checkGetDocument() {
        mDisposable.add(ApiManager.getDocument()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value->{
                    Log.i(TAG, value.toString());
                    binding.resText.setText("Success Document :: "+value.toString());
                }, throwable -> {
                    Log.i(TAG, "ERROR Document :: "+throwable.toString());
                    binding.resText.setText("ERROR Document :: "+throwable.toString());
                }, () -> {

                }));
    }

    private void checkGetDatabase() {
        mDisposable.add(ApiManager.getAllDatabase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value->{
                    Log.i(TAG, value.toString());
                    binding.resText.setText("Success Database :: "+value.toString());
                }, throwable -> {
                    Log.i(TAG, "ERROR Database :: "+throwable.toString());
                    binding.resText.setText("ERROR Database :: "+throwable.toString());
                }, () -> {

                }));
    }

    private void checkGetCollection() {
        mDisposable.add(ApiManager.getAllCollection()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value->{
                    Log.i(TAG, value.toString());
                    binding.resText.setText("Success Collection :: "+value.toString());
                }, throwable -> {
                    Log.i(TAG, "ERROR Collection :: "+throwable.toString());
                    binding.resText.setText("ERROR Collection :: "+throwable.toString());
                }, () -> {

                }));
    }

    @Override
    protected void onDestroy() {
        mDisposable.clear();
        mDisposable.dispose();
        super.onDestroy();
    }
}
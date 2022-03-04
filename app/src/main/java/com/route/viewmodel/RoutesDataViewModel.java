package com.route.viewmodel;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonElement;
import com.route.apis.ApiManager;
import com.route.modal.RoutesData;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RoutesDataViewModel extends ViewModel {
    protected final CompositeDisposable mDisposable = new CompositeDisposable();
    private MutableLiveData<RoutesData> routesDocument;
    private MutableLiveData<JsonElement> documentJson;
    private MutableLiveData<JsonElement> collectionJson;
    private MutableLiveData<JsonElement> databaseJson;
    private MutableLiveData<String> error;

    public LiveData<RoutesData> getRoutesDocument() {
        if (routesDocument == null) {
            routesDocument = new MutableLiveData<>();
            error = new MutableLiveData<>();
        }
        return routesDocument;
    }

    public LiveData<JsonElement> getRoutesDocumentJson() {
        if (documentJson == null) {
            documentJson = new MutableLiveData<>();
            error = new MutableLiveData<>();
        }
        return documentJson;
    }

    public LiveData<JsonElement> getCollection() {
        if (collectionJson == null) {
            collectionJson = new MutableLiveData<>();
            error = new MutableLiveData<>();
        }
        return collectionJson;
    }

    public LiveData<JsonElement> getDatabaseList() {
        if (databaseJson == null) {
            databaseJson = new MutableLiveData<>();
            error = new MutableLiveData<>();
        }
        return databaseJson;
    }

    public LiveData<String> getRoutesError() {
        if (routesDocument == null) {
            error = new MutableLiveData<>();
        }
        return error;
    }

    public void loadRoutesDocument() {
        mDisposable.add(ApiManager.getDocument()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value->{
                    routesDocument.postValue(value);
                }, throwable -> {
                    error.postValue("Failed to load Routes Document :: "+throwable.getMessage());
                }, () -> {

                }));
    }

    public void loadRoutesDocumentJson() {
        mDisposable.add(ApiManager.getDocumentJson()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value->{
                    documentJson.postValue(value);
                }, throwable -> {
                    error.postValue("Failed to load Routes Document :: "+throwable.getMessage());
                }, () -> {

                }));
    }

    public void loadDatabaseList() {
        mDisposable.add(ApiManager.getAllDatabase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value->{
                    databaseJson.postValue(value);
                }, throwable -> {
                    error.postValue("Failed to load Database List :: "+throwable.getMessage());
                }, () -> {

                }));
    }

    public void loadRoutesMeDataCollection() {
        mDisposable.add(ApiManager.getAllCollection()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value->{
                    collectionJson.postValue(value);
                }, throwable -> {
                    error.postValue("Failed to load Collection :: "+throwable.getMessage());
                }, () -> {

                }));
    }

    public void clearDisposable() {
        mDisposable.clear();
        mDisposable.dispose();
    }
}

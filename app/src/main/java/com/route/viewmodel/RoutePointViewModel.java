package com.route.viewmodel;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ar.navigation.pathmodel.RouteAnchor;
import com.ar.navigation.util.AnchorPointsUtil;
import com.ar.navigation.util.Route;
import com.google.gson.JsonElement;
import com.route.apis.ApiManager;
import com.route.modal.RoutesData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Headers;

public class RoutePointViewModel extends ViewModel {
    protected final CompositeDisposable mDisposable = new CompositeDisposable();
    private MutableLiveData<List<RouteAnchor>> routesDocument;
    private MutableLiveData<String> error;


    // Keep
    public LiveData<List<RouteAnchor>> getRoutesDocument() {
        if (routesDocument == null) {
            routesDocument = new MutableLiveData<>();
            error = new MutableLiveData<>();
        }
        return routesDocument;
    }


    // Keep
    public LiveData<String> getRoutesError() {
        if (routesDocument == null) {
            error = new MutableLiveData<>();
        }
        return error;
    }


    // Keep
    public void getDocumentAppClipRouteIdBody(String routeId) {
        mDisposable.add(ApiManager.getDocumentAppClipRouteIdBody2(routeId)
                .subscribeOn(Schedulers.io())
                        .map(value->{
                            List<RouteAnchor> routeAnchorList = AnchorPointsUtil.INSTANCE.makeAnchorsFromServerResponse(value);
                            List<RouteAnchor> routes = Route.SimplifyPath(routeAnchorList, 1.0f);
                            Log.i("", "");
                            // return routes;
                            return routeAnchorList;
                        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value->{
                    Log.i("", "");
                    routesDocument.postValue(value);
                }, throwable -> {
                    error.postValue("Failed to load Routes Document :: "+throwable.getMessage());
                }, () -> {

                }));
    }


    public void clearDisposable() {
        mDisposable.clear();
        mDisposable.dispose();
    }
}

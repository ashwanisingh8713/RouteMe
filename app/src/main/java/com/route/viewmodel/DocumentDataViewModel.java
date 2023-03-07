package com.route.viewmodel;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.route.apis.ApiManager;
import com.route.modal.AnchorsBean;
import com.route.modal.QRCodes;
import com.route.modal.QRCodesDocuments;
import com.route.modal.RoutesBean;
import com.route.modal.RoutesData;
import com.route.modal.ktM2.Anchor;
import com.route.modal.ktM2.Document;
import com.route.modal.ktM2.Route;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DocumentDataViewModel extends ViewModel {
    protected final CompositeDisposable mDisposable = new CompositeDisposable();
    private MutableLiveData<List<Anchor>> routesDocument;
    private MutableLiveData<List<RoutesBean>> qrCodesDocument;
    private MutableLiveData<String> error;

    public LiveData<List<Anchor>> getRoutesDocument() {
        if (routesDocument == null) {
            routesDocument = new MutableLiveData<>();
            error = new MutableLiveData<>();
        }
        return routesDocument;
    }

    public LiveData<List<RoutesBean>> getQRCodesDocument() {
        if (qrCodesDocument == null) {
            qrCodesDocument = new MutableLiveData<>();
            error = new MutableLiveData<>();
        }
        return qrCodesDocument;
    }

    public LiveData<String> getRoutesError() {
        if (error == null) {
            error = new MutableLiveData<>();
        }
        return error;
    }


    public void loadAppClipCodesDocument(String url) {
        mDisposable.add(ApiManager.loadAppClipCodesDocument()
                .subscribeOn(Schedulers.io())
                .map(value -> {
                    final String[] paths = url.split("/");
                    String selectedId = "";
                    if(paths.length>0) {
                        selectedId = paths[paths.length-1];
                    }
                    List<Anchor> anchorsBeans = new ArrayList<>();

                    if (value.getDocuments() != null && value.getDocuments().size() > 0) {
                        for (Document qrCodesDocuments : value.getDocuments()) {
                            if (qrCodesDocuments.getAnchors() != null  && qrCodesDocuments.getId().equals(selectedId)) {
                                anchorsBeans.addAll(qrCodesDocuments.getAnchors());
                            }
                        }
                    }

                    return anchorsBeans;
                })
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(value -> {
                    Log.i("", "");
                    routesDocument.postValue(value);

                }, throwable -> {
                    error.postValue("Failed to load Routes Document :: " + throwable.getMessage());
                }, () -> {

                }));
    }



    public void clearDisposable() {
        mDisposable.clear();
        mDisposable.dispose();
    }
}

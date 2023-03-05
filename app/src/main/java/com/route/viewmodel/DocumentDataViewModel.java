package com.route.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.route.apis.ApiManager;
import com.route.modal.AnchorsBean;
import com.route.modal.QRCodesDocuments;
import com.route.modal.RoutesBean;
import com.route.modal.RoutesData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DocumentDataViewModel extends ViewModel {
    protected final CompositeDisposable mDisposable = new CompositeDisposable();
    private MutableLiveData<List<RoutesBean>> routesDocument;
    private MutableLiveData<List<RoutesBean>> qrCodesDocument;
    private MutableLiveData<String> error;

    public LiveData<List<RoutesBean>> getAppClipCodesDocument() {
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

    /*public void loadRoutesDocument() {
        mDisposable.add(ApiManager.getDocument()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value->{
                    routesDocument.postValue(value);
                }, throwable -> {
                    error.postValue("Failed to load Routes Document :: "+throwable.getMessage());
                }, () -> {

                }));
    }*/

    public void loadAppClipCodesDocument() {
        mDisposable.add(ApiManager.loadAppClipCodesDocument()
                .subscribeOn(Schedulers.io())
                .map(qrCodes -> {
                    List<AnchorsBean> anchorsBeans = new ArrayList<>();
                    List<RoutesBean> routesBeans = new ArrayList<>();
                    /*if(qrCodes.getDocuments() != null && qrCodes.getDocuments().size() > 0 ){
                        for(QRCodesDocuments qrCodesDocuments : qrCodes.getDocuments()) {
                            if(qrCodesDocuments.getAnchors() != null) {
                                anchorsBeans.addAll(qrCodesDocuments.getAnchors());
                            }
                        }
                    }
                    if(anchorsBeans.size() > 0) {
                        for(AnchorsBean anchorsBean : anchorsBeans) {
                            routesBeans.addAll(anchorsBean.getRoutes());
                        }
                    }*/
                    return routesBeans;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value->{
//                    routesDocument.postValue(value);
                    qrCodesDocument.postValue(value);
                }, throwable -> {
                    error.postValue("Failed to load Routes Document :: "+throwable.getMessage());
                }, () -> {

                }));
    }

    /////////////////////////////////

    public void loadQRCodesDocument() {
        mDisposable.add(ApiManager.loadQRCodesDocument()
                .subscribeOn(Schedulers.io())
                .map(qrCodes -> {
                    List<AnchorsBean> anchorsBeans = new ArrayList<>();
                    List<RoutesBean> routesBeans = new ArrayList<>();
                    if(qrCodes.getDocuments() != null && qrCodes.getDocuments().size() > 0 ){
                        for(QRCodesDocuments qrCodesDocuments : qrCodes.getDocuments()) {
                            if(qrCodesDocuments.getAnchors() != null) {
                                anchorsBeans.addAll(qrCodesDocuments.getAnchors());
                            }
                        }
                    }
                    if(anchorsBeans.size() > 0) {
                        for(AnchorsBean anchorsBean : anchorsBeans) {
                            routesBeans.addAll(anchorsBean.getRoutes());
                        }
                    }
                    return routesBeans;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(routesBeans->{
                if(routesBeans.size()>0) {
                    qrCodesDocument.postValue(routesBeans);
                } else {
                    error.postValue("Error! Anchor List is not available.");
                }
                }, throwable -> {
                    error.postValue("Error! "+throwable.getMessage());
                }, () -> {

                }));
    }


    public void clearDisposable() {
        mDisposable.clear();
        mDisposable.dispose();
    }
}

package com.route.apis;

import android.util.Log;

import com.google.gson.JsonElement;
import com.route.modal.RoutesData;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class ApiManager {

    private ApiManager() { }

    public static Observable<JsonElement> getAllDatabase() {
        final String method = "get";
        final String xmsVersion = ApiPathUtil.XMS_VERSION;
        final String UTCstring = ApiPathUtil.headerDate();
        final String url = "/dbs";

        return ServiceFactory.getServiceAPIs()
                .getAllDatabase("application/json",
                        ApiPathUtil.genAuthDatabase(method, UTCstring, url), UTCstring, xmsVersion)
                .subscribeOn(Schedulers.newThread())
                .map(value->{

                    return value;
                });
    }

    public static Observable<JsonElement> getAllCollection() {
        final String UTCstring = ApiPathUtil.headerDate();
        String generate = ApiPathUtil.generate2("get", "colls", "dbs/RouteMeData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", UTCstring);

        return ServiceFactory.getServiceAPIs()
                .getAllCollection(ApiPathUtil.DATABASE_NAME, "application/json",
                        generate, UTCstring, ApiPathUtil.XMS_VERSION)
                .subscribeOn(Schedulers.newThread())
                .map(value->{
                    return value;
                });
    }

    public static Observable<RoutesData> getDocument() {
        final String UTCstring = ApiPathUtil.headerDate();
        String generate = ApiPathUtil.generate2("get", "docs", "dbs/RouteMeData/colls/RoutesData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", UTCstring);

        return ServiceFactory.getServiceAPIs()
                .getDocument(ApiPathUtil.DATABASE_NAME, ApiPathUtil.COLLECTION_NAME, "application/json",
                        generate, UTCstring, ApiPathUtil.XMS_VERSION)
        .subscribeOn(Schedulers.newThread())
        .map(value->{

            return value;
        });
    }

    public static Observable<JsonElement> getDocumentJson() {
        final String UTCstring = ApiPathUtil.headerDate();
        String generate = ApiPathUtil.generate2("get", "docs", "dbs/RouteMeData/colls/RoutesData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", UTCstring);

        return ServiceFactory.getServiceAPIs()
                .getDocumentJson(ApiPathUtil.DATABASE_NAME, ApiPathUtil.COLLECTION_NAME, "application/json",
                        generate, UTCstring, ApiPathUtil.XMS_VERSION)
                .subscribeOn(Schedulers.newThread())
                .map(value->{

                    return value;
                });
    }



}

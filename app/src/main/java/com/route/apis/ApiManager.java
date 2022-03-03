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
        final String method = "get";
        final String xmsVersion = ApiPathUtil.XMS_VERSION;
        final String UTCstring = ApiPathUtil.headerDate();
        final String url = "https://routeme.documents.azure.com:443/dbs/RouteMeData/colls";
        String generate = ApiPathUtil.generate("get", "colls", "dbs/RouteMeData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", ApiPathUtil.getServerTime());

       // String auth = ApiPathUtil.genAuthCollection(method, UTCstring, url);



        Log.i("Collection_AUTH", generate);

        return ServiceFactory.getServiceAPIs()
                .getAllCollection(ApiPathUtil.DATABASE_NAME, "application/json",
                        generate, UTCstring, xmsVersion)
                .subscribeOn(Schedulers.newThread())
                .map(value->{
                    return value;
                });
    }

    public static Observable<RoutesData> getDocument() {
        final String method = "get";
        final String xmsVersion = ApiPathUtil.XMS_VERSION;
        final String UTCstring = ApiPathUtil.headerDate();
        final String url = "/dbs/RouteMeData/colls/RoutesData/docs";

        return ServiceFactory.getServiceAPIs()
                .getDocument(ApiPathUtil.DATABASE_NAME, ApiPathUtil.COLLECTION_NAME, "application/json",
                        ApiPathUtil.genAuthDatabase(method, UTCstring, url), UTCstring, xmsVersion)
        .subscribeOn(Schedulers.newThread())
        .map(value->{

            return value;
        });
    }

}

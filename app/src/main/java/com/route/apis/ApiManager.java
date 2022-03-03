package com.route.apis;

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
        final String url = "https://routeme.documents.azure.com:443/dbs";

        return ServiceFactory.getServiceAPIs()
                .getAllDatabase(ApiPathUtil.genAuth(method, UTCstring, url), UTCstring, xmsVersion)
                .subscribeOn(Schedulers.newThread())
                .map(value->{

                    return value;
                });
    }

    public static Observable<RoutesData> getDocument() {
        final String method = "get";
        final String xmsVersion = ApiPathUtil.XMS_VERSION;
        final String UTCstring = ApiPathUtil.headerDate();
        final String url = "https://routeme.documents.azure.com:443/dbs/RouteMeData/colls/RoutesData/docs";

        return ServiceFactory.getServiceAPIs()
                .getDocument(ApiPathUtil.DATABASE_NAME, ApiPathUtil.COLLECTION_NAME,
                        ApiPathUtil.genAuth(method, UTCstring, url), UTCstring, xmsVersion)
        .subscribeOn(Schedulers.newThread())
        .map(value->{

            return value;
        });
    }

}

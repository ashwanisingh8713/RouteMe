package com.route.apis;

import com.google.gson.JsonElement;
import com.route.modal.QRCodes;
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
                .map(value-> value);
    }

    public static Observable<JsonElement> getAllCollection() {
        final String UTCstring = ApiPathUtil.headerDate();
        String generate = ApiPathUtil.generate2("get", "colls", "dbs/RouteMeData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", UTCstring);

        return ServiceFactory.getServiceAPIs()
                .getAllCollection(ApiPathUtil.DATABASE_NAME, "application/json",
                        generate, UTCstring, ApiPathUtil.XMS_VERSION)
                .subscribeOn(Schedulers.newThread())
                .map(value-> value);
    }

    public static Observable<RoutesData> getDocument() {
        final String UTCstring = ApiPathUtil.headerDate();
        String generate = ApiPathUtil.generate2("get", "docs", "dbs/RouteMeData/colls/RoutesData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", UTCstring);

        return ServiceFactory.getServiceAPIs()
                .getDocument(ApiPathUtil.DATABASE_NAME, ApiPathUtil.COLLECTION_ROUTES_DATA, "application/json",
                        generate, UTCstring, ApiPathUtil.XMS_VERSION)
        .subscribeOn(Schedulers.newThread())
        .map(value-> value);
    }

    public static Observable<JsonElement> loadAppClipCodesDocument() {
        final String UTCstring = ApiPathUtil.headerDate();
        String generate = ApiPathUtil.generate2("get", "docs", "dbs/RouteMeData/colls/AppClipCodesData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", UTCstring);

        return ServiceFactory.getServiceAPIs()
                .getDocumentAppClip(ApiPathUtil.DATABASE_NAME, "AppClipCodesData", "application/json",
                        generate, UTCstring, ApiPathUtil.XMS_VERSION)
                .subscribeOn(Schedulers.newThread())
                .map(value-> value);
    }

    public static Observable<QRCodes> loadQRCodesDocument() {
        final String UTCstring = ApiPathUtil.headerDate();
        String generate = ApiPathUtil.generate2("get", "docs", "dbs/RouteMeData/colls/QRCodesData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", UTCstring);

        return ServiceFactory.getServiceAPIs()
                .getQRCodesDocument(ApiPathUtil.DATABASE_NAME, ApiPathUtil.COLLECTION_QRCODES_DATA, "application/json",
                        generate, UTCstring, ApiPathUtil.XMS_VERSION)
                .subscribeOn(Schedulers.newThread())
                .map(value-> value);
    }

    public static Observable<JsonElement> loadQRCodesDocumentJ() {
        final String UTCstring = ApiPathUtil.headerDate();
        String generate = ApiPathUtil.generate2("get", "docs", "dbs/RouteMeData/colls/QRCodesData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", UTCstring);

        return ServiceFactory.getServiceAPIs()
                .getQRCodesDocumentJ(ApiPathUtil.DATABASE_NAME, ApiPathUtil.COLLECTION_QRCODES_DATA, "application/json",
                        generate, UTCstring, ApiPathUtil.XMS_VERSION)
                .subscribeOn(Schedulers.newThread())
                .map(value-> value);
    }

    public static Observable<JsonElement> getDocumentJson() {
        final String UTCstring = ApiPathUtil.headerDate();
        String generate = ApiPathUtil.generate2("get", "docs", "dbs/RouteMeData/colls/RoutesData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", UTCstring);

        return ServiceFactory.getServiceAPIs()
                .getDocumentJson(ApiPathUtil.DATABASE_NAME, ApiPathUtil.COLLECTION_ROUTES_DATA, "application/json",
                        generate, UTCstring, ApiPathUtil.XMS_VERSION)
                .subscribeOn(Schedulers.newThread())
                .map(value-> value);
    }



}

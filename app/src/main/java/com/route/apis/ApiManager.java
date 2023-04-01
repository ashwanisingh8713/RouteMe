package com.route.apis;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.route.modal.QRCodes;
import com.route.modal.RoutesData;
import com.route.modal.ktM2.KtResM2;
import com.route.modal.m2.ResM2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Path;

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

    /*public static Observable<RoutesData> loadAppClipCodesDocument() {
        final String UTCstring = ApiPathUtil.headerDate();
        String generate = ApiPathUtil.generate2("get", "docs", "dbs/RouteMeData/colls/AppClipCodesData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", UTCstring);

        return ServiceFactory.getServiceAPIs()
                .getDocument(ApiPathUtil.DATABASE_NAME, ApiPathUtil.COLLECTION_ROUTES_DATA, "application/json",
                        generate, UTCstring, ApiPathUtil.XMS_VERSION)
                .subscribeOn(Schedulers.newThread())
                .map(value-> value);
    }*/

    public static Observable<KtResM2> loadAppClipCodesDocument() {
        final String UTCstring = ApiPathUtil.headerDate();
        String generate = ApiPathUtil.generate2("get", "docs", "dbs/RouteMeData/colls/AppClipCodesData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", UTCstring);

        return ServiceFactory.getServiceAPIs()
                .getDocumentAppClip(ApiPathUtil.DATABASE_NAME, "AppClipCodesData", "application/json",
                        generate, UTCstring, ApiPathUtil.XMS_VERSION)
                .subscribeOn(Schedulers.newThread())
                .map(value-> value);
    }

    public static Observable<Result<JsonElement>> getDocumentAppClipRouteIdBody() {

        String XMS_VERSION = "2015-12-16";
//        String query = "Select * FROM Data WHERE Data.id = 202303190718195131379789 AND (NOT(IsDefined(Data.DELETED)) OR (Data.DELETED != true))";
        String query = "Select * FROM Data WHERE Data.id = \'202303190718195131379789\'  AND (NOT(IsDefined(Data.DELETED)) OR (Data.DELETED != " +
                true +
                "))";

        JsonArray parameters = new JsonArray();

        JsonObject object = new JsonObject();
        object.addProperty("query", query);
        object.add("parameters", parameters);

        Gson gson = new Gson();

        ArrayList<String> arrayList= new ArrayList<>();
        arrayList.add("none");
//        JsonArray partition = gson.toJsonTree(arrayList);

        final String UTCstring = ApiPathUtil.headerDate();
        String generate = ApiPathUtil.generate2("post", "docs", "dbs/RouteMeData/colls/AppClipCodesData",
                ApiPathUtil.PRIMARY_KEY, "master", "1.0", UTCstring);

        Map<String, String> header = new HashMap<>();
        header.put("Accept", "application/json");
        header.put("Content-Type", "application/query+json");
        header.put("Cache-Control", "no-cashe");
        header.put("x-ms-date", UTCstring);
        header.put("x-ms-version", XMS_VERSION);
        header.put("x-ms-documentdb-isquery", "True");
        header.put("x-ms-documentdb-partitionkey", "[\"none\"]");
        header.put("authorization", generate);

        return ServiceFactory.getServiceAPIs()
                .getDocumentAppClipRouteIdBody(ApiPathUtil.DATABASE_NAME, "AppClipCodesData",
                        header, object)
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

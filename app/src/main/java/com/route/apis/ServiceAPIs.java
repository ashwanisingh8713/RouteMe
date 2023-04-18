package com.route.apis;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.route.modal.QRCodes;
import com.route.modal.RoutesData;
import com.route.modal.ktM2.KtResM2;
import com.route.modal.m2.ResM2;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.adapter.rxjava2.Result;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ServiceAPIs {

    @GET("/dbs")
    Observable<JsonElement> getAllDatabase(@Header("content-type") String accept,
                            @Header("Authorization") String authorization,
                            @Header("x-ms-date") String date,
                            @Header("x-ms-version") String xms_version);

    @GET("/dbs/{database}/colls")
    Observable<JsonElement> getAllCollection(@Path("database") String database, @Header("content-type") String accept,
                                             @Header("Authorization") String authorization,
                                       @Header("x-ms-date") String date,
                                       @Header("x-ms-version") String xms_version);

    @GET("/dbs/{database}/colls/{collection}/docs")
    Observable<RoutesData> getDocument(@Path("database") String database, @Path("collection") String collection,
                                       @Header("content-type") String accept,
                                       @Header("Authorization") String authorization,
                                       @Header("x-ms-date") String date,
                                       @Header("x-ms-version") String xms_version);


    @GET("/dbs/{database}/colls/{collection}/docs")
    Observable<QRCodes> getQRCodesDocument(@Path("database") String database, @Path("collection") String collection,
                                    @Header("content-type") String accept,
                                    @Header("Authorization") String authorization,
                                    @Header("x-ms-date") String date,
                                    @Header("x-ms-version") String xms_version);

    @GET("/dbs/{database}/colls/{collection}/docs")
    Observable<JsonElement> getQRCodesDocumentJ(@Path("database") String database, @Path("collection") String collection,
                                           @Header("content-type") String accept,
                                           @Header("Authorization") String authorization,
                                           @Header("x-ms-date") String date,
                                           @Header("x-ms-version") String xms_version);

    @GET("/dbs/{database}/colls/{collection}/docs")
    Observable<JsonElement> getDocumentJson(@Path("database") String database, @Path("collection") String collection,
                                       @Header("content-type") String accept,
                                       @Header("Authorization") String authorization,
                                       @Header("x-ms-date") String date,
                                       @Header("x-ms-version") String xms_version);



    @GET("/dbs/{database}/colls/{collection}/docs")
    Observable<KtResM2> getDocumentAppClip(@Path("database") String database,
                                           @Path("collection") String collection,
                                           @Header("content-type") String accept,
                                           @Header("Authorization") String authorization,
                                           @Header("x-ms-date") String date,
                                           @Header("x-ms-version") String xms_version);



    @POST("/dbs/{database}/colls/{collection}/docs")
    Observable<Result<JsonElement>> getDocumentAppClipRouteIdBody(@Path("database") String database,
                                                                  @Path("collection") String collection, @HeaderMap Map<String, String> headers,
                                                                  @Body JsonObject json);

    @POST("/dbs/{database}/colls/{collection}/docs")
    Observable<RoutesData> getDocumentAppClipRouteIdBody2(@Path("database") String database,
                                                                  @Path("collection") String collection, @HeaderMap Map<String, String> headers,
                                                                  @Body JsonObject json);

}

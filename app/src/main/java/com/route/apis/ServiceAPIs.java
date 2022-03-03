package com.route.apis;


import com.google.gson.JsonElement;
import com.route.modal.RoutesData;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
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



}

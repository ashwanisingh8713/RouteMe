package com.route.modal.m2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ashwani Kumar Singh on 05,March,2023.
 */

public class AnchorsDTO {
    @SerializedName("id")
    public String id;
    @SerializedName("offsets")
    public List<Double> offsets;
    @SerializedName("rotations")
    public List<Double> rotations;
    @SerializedName("sizes")
    public List<Double> sizes;
    @SerializedName("routes")
    public List<RoutesDTO> routes;
}

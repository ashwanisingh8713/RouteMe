package com.route.modal;

import java.util.List;

public class AnchorsBean {
    private String id;
    private List<Double> offsets;
    private List<Double> rotations;
    private List<Double> sizes;
    private List<RoutesBean> routes;

    public String getId() {
        return id;
    }

    public List<Double> getOffsets() {
        return offsets;
    }

    public List<Double> getRotations() {
        return rotations;
    }

    public List<Double> getSizes() {
        return sizes;
    }

    public List<RoutesBean> getRoutes() {
        return routes;
    }


}

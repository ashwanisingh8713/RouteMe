package com.route.modal;

import java.util.List;

public class RoutesData {


    private String _rid;
    private List<RoutesDocuments> Documents;
    private Integer _count;

    public String get_rid() {
        return _rid;
    }

    public List<RoutesDocuments> getDocuments() {
        return Documents;
    }

    public Integer get_count() {
        return _count;
    }
}

package com.route.modal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QRCodes {


    private String _rid;
    @SerializedName("Documents")
    private List<QRCodesDocuments> documents;
    private Integer _count;

    public String get_rid() {
        return _rid;
    }

    public List<QRCodesDocuments> getDocuments() {
        return documents;
    }

    public Integer get_count() {
        return _count;
    }
}

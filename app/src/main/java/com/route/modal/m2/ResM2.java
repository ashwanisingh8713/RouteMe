package com.route.modal.m2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ashwani Kumar Singh on 05,March,2023.
 */

public class ResM2 {


//    @SerializedName("_rid")
    public String _rid;
    @SerializedName("Documents")
    public List<DocumentsDTO> documents;
    @SerializedName("_count")
    public Integer count;
}

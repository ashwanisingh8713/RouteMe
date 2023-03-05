package com.route.modal.m2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ashwani Kumar Singh on 05,March,2023.
 */

public class DocumentsDTO {
    @SerializedName("id")
    public String id;
    @SerializedName("locId")
    public String locId;
    @SerializedName("part")
    public String part;
    @SerializedName("anchors")
    public List<AnchorsDTO> anchors;
    @SerializedName("_rid")
    public String rid;
    @SerializedName("_self")
    public String self;
    @SerializedName("_etag")
    public String etag;
    @SerializedName("_attachments")
    public String attachments;
    @SerializedName("_ts")
    public Integer ts;
}

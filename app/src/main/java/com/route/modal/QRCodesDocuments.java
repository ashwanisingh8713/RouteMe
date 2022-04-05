package com.route.modal;

import java.util.List;

public class QRCodesDocuments {
    private String id;
    private String part;
    private List<AnchorsBean> anchors;
    private String _rid;
    private String _self;
    private String _etag;
    private String _attachments;
    private Integer _ts;

    public String getId() {
        return id;
    }

    public String getPart() {
        return part;
    }

    public List<AnchorsBean> getAnchors() {
        return anchors;
    }

    public String get_rid() {
        return _rid;
    }

    public String get_self() {
        return _self;
    }

    public String get_etag() {
        return _etag;
    }

    public String get_attachments() {
        return _attachments;
    }

    public Integer get_ts() {
        return _ts;
    }


}

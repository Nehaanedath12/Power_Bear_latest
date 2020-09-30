package com.sangsolutions.powerbear.Adapter.POSelectAdapter;

public class POSelect {
   String DocNo,HeaderId;

    public POSelect(String docNo, String headerId) {
        this.DocNo = docNo;
        this.HeaderId = headerId;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getHeaderId() {
        return HeaderId;
    }

    public void setHeaderId(String headerId) {
        HeaderId = headerId;
    }
}

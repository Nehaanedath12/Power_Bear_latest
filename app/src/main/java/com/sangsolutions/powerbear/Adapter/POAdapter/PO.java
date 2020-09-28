package com.sangsolutions.powerbear.Adapter.POAdapter;

@SuppressWarnings("ALL")
public class PO {
    String DocNo, DocDate, Cusomer;

    public PO(String docNo, String docDate, String cusomer) {
        this.DocNo = docNo;
        this.DocDate = docDate;
        this.Cusomer = cusomer;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getDocDate() {
        return DocDate;
    }

    public void setDocDate(String docDate) {
        DocDate = docDate;
    }

    public String getCusomer() {
        return Cusomer;
    }

    public void setCusomer(String cusomer) {
        Cusomer = cusomer;
    }
}

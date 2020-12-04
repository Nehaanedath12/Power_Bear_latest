package com.sangsolutions.powerbear.Adapter.ProductDetailsSpareAdpater;

public class SpareClass {

    String  Imagescount,Filescount,iId,iProduct,iPosition,iSpareParts;
    String Name,Code2;

    public SpareClass(String imagescount, String filescount, String iId, String iProduct, String iPosition, String iSpareParts, String name, String code2) {
        Imagescount = imagescount;
        Filescount = filescount;
        this.iId = iId;
        this.iProduct = iProduct;
        this.iPosition = iPosition;
        this.iSpareParts = iSpareParts;
        Name = name;
        Code2 = code2;
    }

    public String getImagescount() {
        return Imagescount;
    }

    public String getFilescount() {
        return Filescount;
    }

    public String getiId() {
        return iId;
    }

    public String getiProduct() {
        return iProduct;
    }

    public String getiPosition() {
        return iPosition;
    }

    public String getiSpareParts() {
        return iSpareParts;
    }

    public String getName() {
        return Name;
    }

    public String getCode2() {
        return Code2;
    }
}
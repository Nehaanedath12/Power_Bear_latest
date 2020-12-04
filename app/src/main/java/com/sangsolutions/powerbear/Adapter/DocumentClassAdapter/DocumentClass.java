package com.sangsolutions.powerbear.Adapter.DocumentClassAdapter;

public class DocumentClass {

        String url_doc,FILE_NAME;

        public DocumentClass(String url_doc, String FILE_NAME) {
            this.url_doc = url_doc;
            this.FILE_NAME=FILE_NAME;
        }


        public String getUrl_doc() {
            return url_doc;
        }

        public String getFILE_NAME() {
            return FILE_NAME;
        }
}

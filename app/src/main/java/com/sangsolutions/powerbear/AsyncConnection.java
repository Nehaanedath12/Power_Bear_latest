package com.sangsolutions.powerbear;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AsyncConnection {
    private List<NameValuePair> list;
    private String m = "", line, URL;

    AsyncConnection(List<NameValuePair> list, String URL) {

        this.list = list;
        this.URL = URL;
    }

    public AsyncConnection(String URL) {
        this.URL = URL;
        list = new ArrayList<>();
    }

    public String execute() {

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(URL);
            post.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            m = builder.toString();
        } catch (Exception e) {
            Log.d("error", Objects.requireNonNull(e.getMessage()));

        }

        return m;
    }
}

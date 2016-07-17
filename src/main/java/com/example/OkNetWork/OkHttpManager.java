package com.example.OkNetWork;

import com.example.InterFaces.RunInterFace;
import okhttp3.*;
import org.apache.commons.io.FileUtils;
import org.apache.http.util.TextUtils;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/13.
 */
public class OkHttpManager {
    public static final String defaultPath = System.getProperty("user.dir") + "\\src\\main\\DownLoad\\";

    public void downFile(String url, final Map<String, String> map, final String filePath) {

        OkHttpClient client1 = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response originalResponse = chain.proceed(chain.request());
                //包装响应体并返回
                return originalResponse.newBuilder()
                        .body(originalResponse.body())
                        .build();
            }
        }).build();


        FormBody.Builder builder = null;
        if (map != null && map.size() > 0) {
            builder = new FormBody.Builder();
            for (String string : map.keySet()) {
                builder.add(string, map.get(string));
            }
        }

        Request.Builder request = new Request.Builder();

        if (builder != null) {
            request.post(builder.build());
        }

        request.url(url);


        client1.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final VolleyError v = new VolleyError();
                if (e instanceof UnknownHostException) {
                    v.setMes("没有网络");
                } else if (e instanceof SocketTimeoutException) {
                    v.setMes("网络超时");
                } else {
                    v.setMes("未知原因");
                }

            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                        saveFile(response, filePath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void saveFile(Response response, String path) throws IOException {

        try {

            File dir = null;
            if (!TextUtils.isEmpty(path)) {
                dir = new File(path);
            } else {
                dir = new File(defaultPath);
            }
            if (response.headers() != null && !TextUtils.isEmpty(response.header("Content-Disposition"))) {

                String tempFileName = response.header("Content-Disposition").replaceAll("\"", "");
                String fileName = tempFileName.substring(tempFileName.indexOf("=") + 1, tempFileName.length());
                fileName = fileName.replaceAll("/", "");

                File file = new File(dir, fileName);
                FileUtils.writeByteArrayToFile(file, response.body().bytes());
            } else {
                String[] s = response.request().url().toString().split("/");
                File file = new File(dir, s[s.length - 1]);
                FileUtils.writeByteArrayToFile(file, response.body().bytes());
            }
        } catch (Exception e) {

        }

    }
}

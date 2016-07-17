package com.example.OkNetWork;

/**
 * Created by Administrator on 2016/5/19.
 */
public interface ProgressResponseListener {

            void onResponseProgress(long bytesWritten, long contentLength, boolean done);
}


package ws;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import config.ApiConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class djangoWS {

    private final String TAG = djangoWS.class.getSimpleName();

    private Handler handler = new Handler();
    private static final djangoWS ourInstance = new djangoWS();

    private OkHttpClient CLIENT;
    private WebSocket mWebSocket;

    public static djangoWS getDefault() {
        return ourInstance;
    }

    private djangoWS() {
        CLIENT = new OkHttpClient.Builder()
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();
        connect();
    }

    //-new OkHttpClient;
    //-构造Request对象；
    //-通过前两步中的对象构建Call对象；
    //-通过Call#enqueue(Callback)方法来提交异步请求；
    //异步发起的请求会被加入到 Dispatcher 中的 runningAsyncCalls双端队列中通过线程池来执行。
    public void connect() {
        if (mWebSocket != null) {
            mWebSocket.cancel();
        }
        String url = ApiConfig.channelURL;
        Request request = new Request.Builder()
                .url(url)
                .build();
        mWebSocket = CLIENT.newWebSocket(request, new SocketListener());//清除连接池
        Call call = CLIENT.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
    }
    public void sendMessage(String message){
        mWebSocket.send(message);
    }

    public void sendMessage(byte... data){
        ByteString bs = ByteString.of(data);
        mWebSocket.send(bs);
    }

    public void close(int code, String reason){
        mWebSocket.close(code,reason);
    }

    private final class SocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            Log.i(TAG,"服务器连接成功:"+response);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            Log.i(TAG,"onMessage text="+text);
            Gson gson = new Gson();
            ArrayList<HashMap> msg = new ArrayList<>();//获取后端传来的Msg
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
            Log.i(TAG,"onMessage bytes="+bytes);
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            Log.i(TAG,"onClosing code="+code);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            Log.i(TAG,"onClosed code="+code);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
            Log.i(TAG,"onFailure t="+t.getMessage());
        }
    }
}

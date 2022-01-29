package ws;

import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reactivestreams.Subscriber;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.Observable;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class RxWebSocketUtil {

    private static RxWebSocketUtil instance;
    private OkHttpClient client;
    private Map<String, Observable<WebSocketInfo>> observableMap;
    private Map<String,WebSocket> webSocketMap;
    private boolean showLog;
    private String logTag = "RXWebSocket Info :";
    private long interval = 1;
    private TimeUnit reconnectIntervalTimeUnit = TimeUnit.SECONDS;

    private RxWebSocketUtil(){
        try{
            Class.forName("okhttp3.OkHttpClient"); //初始化OkHttpClient这个类
        }catch (ClassNotFoundException e){
            throw new RuntimeException("请先要安装okhttp3库");
        }
        try {
            Class.forName("io.reactivex.Observable");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("请先安装rxjava 2.x及以上版本");
        }
        try {
            Class.forName("io.reactivex.android.schedulers.AndroidSchedulers");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("请先安装rxandroid 2.x及以上版本");
        }
        observableMap = new ArrayMap<>();
        webSocketMap = new ArrayMap<>();
        client = new OkHttpClient();
    }

    public static RxWebSocketUtil getInstance(){
        if(instance == null){
            synchronized (RxWebSocketUtil.class){
                if(instance == null){
                    instance = new RxWebSocketUtil();
                }
            }
        }
        return instance;
    }

    public void setClient(OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException(" Are you stupid ? client == null");
        }
        this.client = client;
    }

    /**
     * wss support
     *
     * @param sslSocketFactory
     * @param trustManager
     */
    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
        client = client.newBuilder().sslSocketFactory(sslSocketFactory, trustManager).build();
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    public void setShowLog(boolean showLog, String logTag) {
        setShowLog(showLog);
        this.logTag = logTag;
    }

    public void setReconnectInterval(long interval, TimeUnit timeUnit) {
        this.interval = interval;
        this.reconnectIntervalTimeUnit =  timeUnit;

    }

    public Observable<WebSocketInfo> getWebSocketInfo(final String url,final long timeout,final TimeUnit timeUnit){
        Observable<WebSocketInfo> observable = observableMap.get(url);
        if(observable==null){
            observable = Observable.create(new WebSocketOnSubscribe(url))
                    .timeout(timeout,timeUnit)
                    .retry()//设置重连
                    .doOnDispose(new Action() {
                        @Override
                        public void run() throws Exception {
                            observableMap.remove(url);
                            webSocketMap.remove(url);
                            if(showLog){
                                Log.d(logTag,"关闭监听");
                            }
                        }
                        })
                    .doOnNext(new Consumer<WebSocketInfo>() {
                        @Override
                        public void accept(WebSocketInfo webSocketInfo) throws Exception {
                            if(webSocketInfo.isOnOpen()){
                                webSocketMap.put(url,webSocketInfo.getWebSocket());
                            }
                        }
                    })
                    .share()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            observableMap.put(url,observable);//绑定后放入observableMap表示状态存在
        }else{
            //如果已经存在Websocket监听事件，则去获取监听事件内的websocket对象
            WebSocket webSocket =webSocketMap.get(url);
            if(webSocket!=null){//如果websocket还未断开
                observable = observable.startWith(new WebSocketInfo(webSocket, true));
            }
        }
        return observable;

    }

    public Observable<WebSocketInfo> getWebSocketInfo(String url) {
        return getWebSocketInfo(url, 30, TimeUnit.DAYS);
    }

    public Observable<String> getWebSocketString(String url) {
        return getWebSocketInfo(url)
                .map(new Function<WebSocketInfo, String>() {
                    @Override
                    public String apply(WebSocketInfo webSocketInfo) throws Exception {
                        return webSocketInfo.getString();
                    }
                })
                .filter(new Predicate<String>() {

                    @Override
                    public boolean test(String s) throws Exception {
                        return s != null;
                    }
                });
    }

    public Observable<ByteString> getWebSocketByteString(String url) {
        return getWebSocketInfo(url)
                .map(new Function<WebSocketInfo, ByteString>() {
                    @Override
                    public ByteString apply(WebSocketInfo webSocketInfo) throws Exception {
                        return webSocketInfo.getByteString();
                    }
                })
                .filter(new Predicate<ByteString>() {
                    @Override
                    public boolean test(ByteString byteString) throws Exception {
                        return byteString != null;
                    }
                });
    }

    public Observable<WebSocket> getWebSocket(String url) {
        return getWebSocketInfo(url)
                .map(new Function<WebSocketInfo, WebSocket>() {
                    @Override
                    public WebSocket apply(WebSocketInfo webSocketInfo) throws Exception {
                        return webSocketInfo.getWebSocket();
                    }
                });
    }

    /**
     * 如果url的WebSocket已经打开,可以直接调用这个发送消息.
     *
     * @param url
     * @param msg
     */
    public void send(String url, String msg) {
        WebSocket webSocket = webSocketMap.get(url);
        if (webSocket != null) {
            webSocket.send(msg);
        } else {
            throw new IllegalStateException("The WebSokcet not open");
        }
    }

    /**
     * 如果url的WebSocket已经打开,可以直接调用这个发送消息.
     *
     * @param url
     * @param byteString
     */
    public void send(String url, ByteString byteString) {
        WebSocket webSocket = webSocketMap.get(url);
        if (webSocket != null) {
            webSocket.send(byteString);
        } else {
            throw new IllegalStateException("The WebSokcet not open");
        }
    }

    /**
     * 不用关心url 的WebSocket是否打开,可以直接发送
     *
     * @param url
     * @param msg
     */
    public void asyncSend(String url, final String msg) {
        getWebSocket(url)
                .firstElement()
                .subscribe(new Consumer<WebSocket>() {
                    @Override
                    public void accept(WebSocket webSocket) throws Exception {
                        webSocket.send(msg);
                    }
                });

    }

    /**
     * 不用关心url 的WebSocket是否打开,可以直接发送
     *
     * @param url
     * @param byteString
     */
    public void asyncSend(String url, final ByteString byteString) {
        getWebSocket(url)
                .firstElement()
                .subscribe(new Consumer<WebSocket>() {
                    @Override
                    public void accept(WebSocket webSocket) throws Exception {
                        webSocket.send(byteString);
                    }
                });
    }


    private Request getRequest(String url){
        return new Request.Builder().get().url(url).build();
    }

    private final class WebSocketOnSubscribe implements ObservableOnSubscribe<WebSocketInfo> {

        private String url;

        private WebSocket webSocket;

        public WebSocketOnSubscribe(String url) {
            this.url = url;
        }

        @Override
        public void subscribe(ObservableEmitter<WebSocketInfo> e) throws Exception {
            if(webSocket!=null){
                if(!"main".equals(Thread.currentThread().getName())){
                    long ms = reconnectIntervalTimeUnit.toMillis(interval);
                    if(ms == 0){
                        ms = 1000;
                    }
                    SystemClock.sleep(ms);
                    e.onNext(WebSocketInfo.createReconnect());
                }
            }
            initWebSocket(e);
        }

        private void initWebSocket(final ObservableEmitter<? super WebSocketInfo> e){
            webSocket = client.newWebSocket(getRequest(url), new WebSocketListener() {
                @Override
                public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                    if (showLog) {
                        Log.d(logTag, url + " --> onClosed:code = " + code + ", reason = " + reason);
                    }
                }

                @Override
                public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                    webSocket.close(1000, null);
                }

                @Override
                public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                    if (showLog) {
                        Log.e(logTag, t.toString() + webSocket.request().url().uri().getPath());
                    }
                    if (!e.isDisposed()) {
                        e.onError(t);
                    }
                }

                @Override
                public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                    if (!e.isDisposed()) {
                        e.onNext(new WebSocketInfo(webSocket, text));
                    }
                }

                @Override
                public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                    if (!e.isDisposed()) {
                        e.onNext(new WebSocketInfo(webSocket, bytes));
                    }
                }

                @Override
                public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                    if(showLog){
                        Log.d(logTag,url+"-->OnOpent");
                    }
                    webSocketMap.put(url,webSocket);
                    if (!e.isDisposed()) {
                        e.onNext(new WebSocketInfo(webSocket, true));
                    }
                }
            });
        }

    }
}

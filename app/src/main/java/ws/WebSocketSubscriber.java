package ws;

import org.reactivestreams.Subscriber;

import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import androidx.annotation.NonNull;
import okio.ByteString;
import okhttp3.WebSocket;

public abstract class WebSocketSubscriber implements Observer<WebSocketInfo> {
    private boolean hasOpened;

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public final void onNext(@NonNull WebSocketInfo webSocketInfo) {
        if (webSocketInfo.isOnOpen()) {
            hasOpened = true;
            onOpen(webSocketInfo.getWebSocket());
        } else if (webSocketInfo.getString() != null) {
            onMessage(webSocketInfo.getString());
        } else if (webSocketInfo.getByteString() != null) {
            onMessage(webSocketInfo.getByteString());
        } else if (webSocketInfo.isOnReconnect()) {
            onReconnect();
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public void onComplete() {
        if (hasOpened) {
            onClose();
        }
    }

    /**
     * Callback when the WebSocket is opened
     *
     * @param webSocket
     */
    protected void onOpen(@NonNull WebSocket webSocket) {
    }

    protected void onMessage(@NonNull String text) {
    }

    protected void onMessage(@NonNull ByteString byteString) {
    }

    /**
     * Callback when the WebSocket is reconnecting
     */
    protected void onReconnect() {
    }

    protected void onClose() {
    }

}
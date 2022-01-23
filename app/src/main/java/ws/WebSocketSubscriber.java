package ws;

import androidx.annotation.NonNull;
import okio.ByteString;
import rx.Subscriber;
import okhttp3.WebSocket;

public abstract class WebSocketSubscriber extends Subscriber<WebSocketInfo> {
    private boolean hasOpened;

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

    @Override
    public final void onCompleted() {
        if (hasOpened) {
            onClose();
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }
}
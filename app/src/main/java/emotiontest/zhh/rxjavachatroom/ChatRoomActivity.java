package emotiontest.zhh.rxjavachatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import commonObj.chatMsgObj;
import commonObj.userObj;
import config.ApiConfig;
import ws.RxWebSocket;
import ws.RxWebSocketUtil;
import ws.WebSocketInfo;
import ws.WebSocketSubscriber;
import ws.djangoWS;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.net.URI;
import java.util.ArrayList;



public class ChatRoomActivity extends RxAppCompatActivity {
    private Button sendMsgBtn;
    private EditText msgTextEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        initView();
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });

    }

    private void sendMsg(){
        String msg = msgTextEt.getText().toString();
        Log.i("客户端发送的msg消息",msg);
        RxWebSocket.get(ApiConfig.channelURL)
                .compose(this.<WebSocketInfo>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new WebSocketSubscriber() {
                    @Override
                    protected void onMessage(@NonNull String text) {
                        super.onMessage(text);
                    }

                    @Override
                    protected void onClose() {
                        super.onClose();
                    }
                });

    }

    private void initView(){
        sendMsgBtn = (Button)findViewById(R.id.sendMsg);
        msgTextEt = (EditText) findViewById(R.id.msgText);
    }
}

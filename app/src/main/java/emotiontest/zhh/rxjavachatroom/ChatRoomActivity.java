package emotiontest.zhh.rxjavachatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import Adapter.ChatAdapter;
import commonObj.chatMsgObj;
import commonObj.userObj;
import config.ApiConfig;
import constant.chatAboutConstants;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.net.URI;
import java.util.ArrayList;
import Loader.userLoader;


public class ChatRoomActivity extends RxAppCompatActivity {
    private Button sendMsgBtn;
    private EditText msgTextEt;
    private LinearLayoutManager layoutManager;

    RecyclerView recyclerView;
    ChatAdapter chatAdapter;
    ArrayList<chatMsgObj> msgList;

    userLoader mUserLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        initView();
        msgList.clear();

        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });

        //初始化设置监听
        setChatWsSubscribe();
    }

    private void sendMsg(){
        userObj mUser = userObj.getInstance();
        Gson mgson = new Gson();
        chatMsgObj msgObj = new chatMsgObj();
        String msgString = msgTextEt.getText().toString();
        msgObj.setCode(chatAboutConstants.chatMsgObj.CHATMSG_SEND_SUCCESS);//表示用户成功进入房间，发送成功进入房间code
        msgObj.setMsg(msgString);
        msgObj.setUserId(mUser.getId());
        String msgJson = mgson.toJson(msgObj);
        RxWebSocket.send(ApiConfig.channelURL,msgJson);
        msgTextEt.setText("");
    }

    private void setChatWsSubscribe(){
        RxWebSocket.get(ApiConfig.channelURL)
                .compose(this.<WebSocketInfo>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new WebSocketSubscriber() {
                    @Override
                    protected void onMessage(@NonNull String text) {
                        msgDealer(text);
                    }

                    @Override
                    protected void onClose() {
                        super.onClose();
                    }
                });
    }

    private void msgDealer(String text){
        Gson mgson = new Gson();
        chatMsgObj msgObj = new chatMsgObj();
        msgObj = mgson.fromJson(text,chatMsgObj.class);
        if(msgObj.getCode() == chatAboutConstants.chatMsgObj.CHATMSG_SEND_SUCCESS){
            msgList.add(msgObj);
            chatAdapter.notifyItemInserted(msgList.size()-1);
            recyclerView.scrollToPosition(msgList.size()-1);
        }
    }

    private void initView(){
        sendMsgBtn = (Button)findViewById(R.id.sendMsg);
        msgTextEt = (EditText) findViewById(R.id.msgText);
        msgList = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.chatRecycleView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //创建适配器
        chatAdapter = new ChatAdapter(msgList);
        recyclerView.setAdapter(chatAdapter);
    }
}

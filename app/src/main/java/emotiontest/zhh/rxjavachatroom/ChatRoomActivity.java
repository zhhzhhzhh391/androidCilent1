package emotiontest.zhh.rxjavachatroom;

import androidx.appcompat.app.AppCompatActivity;
import commonObj.chatMsgObj;
import commonObj.userObj;
import config.ApiConfig;
import okhttp3.OkHttpClient;
import rx.Observer;
import rx.Scheduler;
import rx.Subscriber;
import ws.djangoWS;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;

import java.net.URI;
import java.util.ArrayList;
import rx.Observable;


public class ChatRoomActivity extends AppCompatActivity {
    private Button enterRoomBtn;
    private ListView roomUserListView;
    private ArrayAdapter<userObj> adapter;

    private djangoWS listener;
    private ArrayList<userObj> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        setConnectServer();

        userList = new ArrayList<>();/*初始化房间内用户*/

        enterRoomBtn = (Button)findViewById(R.id.enterRoom);
        enterRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getConnect();
            }
        });

        roomUserListView = (ListView)findViewById(R.id.roomUserList);
        adapter = new ArrayAdapter<userObj>(ChatRoomActivity.this,R.layout.support_simple_spinner_dropdown_item,userList);

    }

    private void setConnectServer() {
        URI uri = URI.create(ApiConfig.channelURL);
        listener = new djangoWS(uri){
            @Override
            public void onMessage(String message){
                msgDealer(message);
            }
        };
    }

    private void getConnect(){
        try{
            listener.connectBlocking();//djangoWS.connectBlocking会多出一个等待操作，先连接再发送
        }catch (InterruptedException e ){
            e.printStackTrace();
        }
        userObj connectUser = new userObj();
        connectUser.setId(2);
        connectUser.setUsername("zhh");
        String strConnectUser = new Gson().toJson(connectUser);
        listener.send(strConnectUser);
    }

    //UI线程内关闭了连接，需要将对象置空，避免重复初始化对象
    private void closeConnect(){
        try{
            if(listener != null){
                listener.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            listener = null;
        }
    }

    /*
    处理django ws的消息
     */
    public void msgDealer(String message){
        Log.e("msgDealer",message);
        Gson gson = new Gson();
        chatMsgObj mChatMsgObj = new chatMsgObj();
        mChatMsgObj = gson.fromJson(message,chatMsgObj.class);
        if(mChatMsgObj.getCode() == 200){
            /*有新的账号进入房间，更新房间数据*/
            userList = mChatMsgObj.getMsg();
        }
    }

    /*进程杀死时，关闭ws连接*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeConnect();
        getDelegate().onDestroy();
    }
}

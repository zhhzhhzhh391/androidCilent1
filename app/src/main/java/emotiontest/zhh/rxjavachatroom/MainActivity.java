package emotiontest.zhh.rxjavachatroom;

import androidx.annotation.NonNull;
import Loader.userLoader;
import androidx.appcompat.app.AppCompatActivity;
import config.ApiConfig;
import constant.chatAboutConstants;
import okhttp3.WebSocket;
import okio.ByteString;
import tools.getRequestBody;
import commonObj.userObj;
import okhttp3.RequestBody;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Action;
import ws.Config;
import ws.RxWebSocket;
import ws.WebSocketInfo;
import ws.WebSocketSubscriber;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.RxLifecycle;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import org.reactivestreams.Subscription;

import java.util.ArrayList;

public class MainActivity extends RxAppCompatActivity {
    public static final String baseUrl = "http://192.168.137.1:6263/wechat/api/";

    private userLoader mUserLoader;

    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button loginBtn;

    private Button getUserListBtn;
    private TextView showMsgText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showMsgText = (TextView)findViewById(R.id.showMessage) ;
        getUserListBtn = (Button)findViewById(R.id.getUserList);
        getUserListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserList();
            }
        });

        usernameEdit = (EditText)findViewById(R.id.getusernameEdit);
        passwordEdit = (EditText)findViewById(R.id.getpasswordEdit);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        mUserLoader = new userLoader();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getUserList(){
        mUserLoader.getAllUser().subscribe(new Consumer<ArrayList<userObj>>() {
            @Override
            public void accept(ArrayList<userObj> userObjs) throws Exception {
                showMsgText.setText(userObjs.get(0).getUsername());
            }
        });
    }

    private void userLogin(){
        String loginUsername = usernameEdit.getText().toString();
        String loginPassword = passwordEdit.getText().toString();
        userObj mUser= new userObj();
        mUser.setUsername(loginUsername);
        mUser.setPassword(loginPassword);
        String userJson = new Gson().toJson(mUser);
        RequestBody requestBody = new getRequestBody(userJson).requestBodyBuilder();
        mUserLoader.userLogin(requestBody).subscribe(new Consumer<userObj>() {
            @Override
            public void accept(userObj userObj) throws Exception {
                try{
                    if(userObj!=null){
                        Log.i("userLogin",userObj.getUsername());
                        Log.i("userLogin","当前账号登录成功，准备连接ws");
                        userObj.setCode(chatAboutConstants.userObj.USER_LOGIN_SUCCESS);
                        setChatWsConnect(userObj);
                    }else{
                        Log.i("userLogin","账号密码错误，服务端没有返回user对象");
                    }
                }catch (Exception e){
                    Log.e("账号登录error:",e.getMessage());
                }
            }
        });
    }

    /*
    * 与聊天服务建立连接
    * */
    private void setChatWsConnect(userObj mUser){
        Gson mgson = new Gson();
        final String loginUserInfo = mgson.toJson(mUser,userObj.class);
        Config config = new Config.Builder()
                .setShowLog(true)           //show  log
                .build();
        RxWebSocket.setConfig(config);
        RxWebSocket.get(ApiConfig.channelURL)
                .compose(this.<WebSocketInfo>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new WebSocketSubscriber() {
                    @Override
                    public void onOpen(@NonNull WebSocket webSocket) {
                        RxWebSocket.send(ApiConfig.channelURL,loginUserInfo);
                        Log.d(Thread.currentThread().getName(), "onOpen1:");
                    }
                    @Override
                    public void onMessage(@NonNull String text) {
                        Log.d(Thread.currentThread().getName(), "返回数据:" + text);
                    }
                    @Override
                    public void onMessage(@NonNull ByteString byteString) {

                    }
                    @Override
                    protected void onReconnect() {
                        Log.d("MainActivity", "重连:");
                    }
                });
    }
}

package emotiontest.zhh.rxjavachatroom;

import androidx.annotation.NonNull;
import Loader.userLoader;
import commonObj.userTokenObj;
import config.ApiConfig;
import constant.chatAboutConstants;
import okhttp3.WebSocket;
import okio.ByteString;
import tools.getRequestBody;
import commonObj.userObj;
import okhttp3.RequestBody;
import io.reactivex.functions.Consumer;
import ws.Config;
import ws.RxWebSocket;
import ws.WebSocketInfo;
import ws.WebSocketSubscriber;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends RxAppCompatActivity {
    public static final String baseUrl = "http://192.168.137.1:6263/wechat/api/";

    private userLoader mUserLoader;

    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button loginBtn;
    private Button testSendBtn;

    private Button getUserListBtn;
    private TextView showMsgText;

    private Intent toChatActivity;//跳转到chatacitivity

    private  userObj mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getUserListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserList();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        testSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendTestMsg();
            }
        });

        toChatActivity = new Intent(this,ChatRoomActivity.class);

        mUserLoader = new userLoader();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getUserList(){
        mUserLoader.getAllUser().
                subscribe(new Consumer<ArrayList<userObj>>() {
            @Override
            public void accept(ArrayList<userObj> userObjs) throws Exception {
                showMsgText.setText(userObjs.get(0).getUsername());
            }
        });
    }

    private void userLogin(){
        String loginUsername = usernameEdit.getText().toString();
        String loginPassword = passwordEdit.getText().toString();
        //初始化登录的user对象
        mUser = userObj.getInstance();
        mUser.setUsername(loginUsername);
        mUser.setPassword(loginPassword);
        String userJson = new Gson().toJson(mUser);
        final RequestBody requestBody = new getRequestBody(userJson).requestBodyBuilder();
        mUserLoader.userLogin(requestBody).compose(this.<userObj>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<userObj>() {
            @Override
            public void accept(userObj userObj) throws Exception {
                try{
                    if(userObj!=null){
                        Log.i("userLogin",userObj.getUsername());
                        Log.i("userLogin","当前账号登录成功，准备连接ws");
                        mUser.setId(userObj.getId());
                        setToken(requestBody);//为登录的user对象保存token
                        userObj.setCode(chatAboutConstants.chatMsgObj.ENTERROOM_SUCCESS);
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
                    }
                    @Override
                    public void onMessage(@NonNull String text) {
                        msgDealer(text);
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
    private void msgDealer(String text){

        //跳转逻辑后面再封装处理
        if(text!=null){
            startActivity(toChatActivity);
            finish();
        }
    }

    private void sendTestMsg(){
        Gson mgson = new Gson();
        mUser.setCode(chatAboutConstants.userObj.USER_LOGIN_SUCCESS);
        mUser.setId(1);
        String testJson = mgson.toJson(mUser,userObj.class);
        RxWebSocket.send(ApiConfig.channelURL,testJson);
    }

    /*
    * 登录成功的账号设置token
    * */
    private void setToken(RequestBody requestBody){
        mUserLoader.getToken(requestBody)
                .compose(this.<userTokenObj>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<userTokenObj>() {
                    @Override
                    public void accept(userTokenObj userTokenObj) throws Exception {
                        mUser.setToken(userTokenObj.getToken());
                    }
                });
    }

    private void initView(){
        showMsgText = (TextView)findViewById(R.id.showMessage);
        getUserListBtn = (Button)findViewById(R.id.getUserList);
        usernameEdit = (EditText)findViewById(R.id.getusernameEdit);
        passwordEdit = (EditText)findViewById(R.id.getpasswordEdit);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        testSendBtn = (Button)findViewById(R.id.testSend);
    }
}


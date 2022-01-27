package emotiontest.zhh.rxjavachatroom;

import Loader.userLoader;
import androidx.appcompat.app.AppCompatActivity;
import tools.getRequestBody;
import commonObj.userObj;
import okhttp3.RequestBody;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Action;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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
                    }else{
                        Log.i("userLogin","账号密码错误，服务端没有返回user对象");
                    }
                }catch (Exception e){
                    Log.e("error:",e.getMessage());
                }
            }
        });
    }
}

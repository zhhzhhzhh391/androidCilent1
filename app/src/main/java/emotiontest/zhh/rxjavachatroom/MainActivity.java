package emotiontest.zhh.rxjavachatroom;

import Loader.userLoader;
import androidx.appcompat.app.AppCompatActivity;
import tools.getRequestBody;
import commonObj.userObj;
import okhttp3.RequestBody;
import rx.functions.Action1;

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
        mUserLoader.getAllUser().subscribe(new Action1<ArrayList<userObj>>() {
            @Override
            public void call(ArrayList<userObj> userObjs) {
                showMsgText.setText(userObjs.get(0).getUsername());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("TAG","errorMsg" + throwable.getMessage());
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
        mUserLoader.userLogin(requestBody).subscribe(new Action1<userObj>() {
            @Override
            public void call(userObj userObj) {
                if(userObj == null){

                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e("TAG","errorMsg" + throwable.getMessage());
            }
        });
    }
}

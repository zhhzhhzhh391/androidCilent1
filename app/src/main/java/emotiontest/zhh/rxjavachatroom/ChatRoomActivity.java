package emotiontest.zhh.rxjavachatroom;

import androidx.appcompat.app.AppCompatActivity;
import commonObj.userObj;
import okhttp3.OkHttpClient;
import ws.djangoWS;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {
    private Button enterRoomBtn;
    private djangoWS listener;
    private ArrayList<userObj> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        setConnectServer();

        enterRoomBtn = (Button)findViewById(R.id.enterRoom);
        enterRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setConnect();
            }
        });
    }

    private void setConnectServer() {
        listener = djangoWS.getDefault();
        OkHttpClient client = new OkHttpClient();
        client.dispatcher().executorService().shutdown();
    }

    private void setConnect(){
        userObj connectUser = new userObj();
        String strConnectUser = new Gson().toJson(connectUser);
        listener.sendMessage(strConnectUser);
    }
}

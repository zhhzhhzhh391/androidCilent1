package serviceInterface;

import commonObj.userInfoObj;
import commonObj.userObj;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import io.reactivex.Observable;
public interface UserAbout {

    //获取所有用户信息
    @GET("UserAbout")
    Observable<userInfoObj> getAllUser();

    @POST("UserAbout/userlogin/")
    Observable<userObj> userLogin(@Body RequestBody requestBody);

    @POST("UserAbout/loginOut/")
    Observable<userObj> userLoginOut(@Body RequestBody requestBody);
}

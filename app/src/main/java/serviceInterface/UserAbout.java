package serviceInterface;

import commonObj.userInfoObj;
import commonObj.userObj;
import commonObj.userTokenObj;
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
    //用户登录
    @POST("UserAbout/userlogin/")
    Observable<userObj> userLogin(@Body RequestBody requestBody);
    //获取登录对象的token
    @POST("UserAbout/getToken/")
    Observable<userTokenObj> getToken(@Body RequestBody requestBody);
    //登出 未实现
    @POST("UserAbout/loginOut/")
    Observable<userObj> userLoginOut(@Body RequestBody requestBody);
    //指定查询
    @POST("UserAbout/getUserInfo")
    Observable<userObj> getUserInfo(@Body RequestBody requestBody);
}

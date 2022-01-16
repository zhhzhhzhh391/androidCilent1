package serviceInterface;

import bean.userBean;
import bean.userInfoBean;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface UserAbout {

    //获取所有用户信息
    @GET("UserAbout")
    Observable<userInfoBean> getAllUser();

    @POST("UserAbout/userlogin/")
    Observable<userBean> userLogin(@Body RequestBody requestBody);

    @POST("UserAbout/loginOut/")
    Observable<userBean> userLoginOut(@Body RequestBody requestBody);
}

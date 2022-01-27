package manager;

import java.util.concurrent.TimeUnit;

import config.ApiConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServiceManager {

    private static final int DEFAULT_TIMEOUT = 5;//设置超时时间5
    private static final int DEFAULT_READTIMEOUT = 10;
    private Retrofit mRetrofit;

    private RetrofitServiceManager() {
        //创建okhttpclinet
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);//连接超时时间
        builder.readTimeout(DEFAULT_READTIMEOUT, TimeUnit.SECONDS);//读操作超时时间

        mRetrofit = new Retrofit.Builder()
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(ApiConfig.BASE_URL)
                .build();
    }

    private static class SingletonHolder {
        private static final RetrofitServiceManager INSTANCE = new RetrofitServiceManager();
    }//创建Retrofit单例

    /*
     * 获取Retrofit实例
     * */
    public static RetrofitServiceManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public <T> T create(Class<T> service) {
        return mRetrofit.create(service);

    }
}

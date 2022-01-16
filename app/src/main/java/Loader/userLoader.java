package Loader;

import android.graphics.Movie;

import java.util.ArrayList;
import java.util.List;

import Loader.ObjectLoader;
import Loader.payLoader;
import bean.BaseResponse;
import bean.userBean;
import bean.userInfoBean;
import okhttp3.RequestBody;
import rx.Observable;
import rx.functions.Func1;
import serviceInterface.UserAbout;
import manager.RetrofitServiceManager;


public class userLoader extends ObjectLoader {
    private UserAbout mUserAbout;
    public userLoader(){
        mUserAbout = RetrofitServiceManager.getInstance().create(UserAbout.class);
    }

    /**
     *获取所有用户数据
     *
     * @return
     */
    public Observable<ArrayList<userBean>> getAllUser(){
        return observe(mUserAbout.getAllUser())
                .map(new Func1<userInfoBean, ArrayList<userBean>>() {
                    @Override
                    public ArrayList<userBean> call(userInfoBean userInfoBean) {
                        if(userInfoBean.getCount() == 0){
                            return null;
                        }
                        return userInfoBean.getResults();
                    }
                });
//                .map(new payLoader<BaseResponse<userInfoBean>>());
    }

    /**
     *获取指定的用户数据
     */
    public Observable<userBean> userLogin(RequestBody requestBody){
        return observe(mUserAbout.userLogin(requestBody))
                .map(new Func1<userBean, userBean>() {
                    @Override
                    public userBean call(userBean userBean) {
                        if(userBean.getId() == null){
                            return null;
                        }
                        return null;
                    }
                });
    }

    /**
     * 注册新的用户
     */

}

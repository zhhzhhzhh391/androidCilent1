package Loader;

import java.util.ArrayList;

import commonObj.userInfoObj;
import commonObj.userObj;
import okhttp3.RequestBody;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

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
    public Observable<ArrayList<userObj>> getAllUser(){
        return observe(mUserAbout.getAllUser())
                .map(new Function<userInfoObj, ArrayList<userObj>>() {
                    @Override
                    public ArrayList<userObj> apply(userInfoObj userInfoObj) throws Exception {
                        return userInfoObj.getResults();
                    }
                });
//
    }

    /**
     *获取指定的用户数据
     */
    public Observable<userObj> userLogin(RequestBody requestBody){
        return observe(mUserAbout.userLogin(requestBody))
                .map(new Function<userObj, userObj>() {
                    @Override
                    public userObj apply(userObj userObj) throws Exception {
                        return userObj;
                    }
                });
    }

    /**
     * 注册新的用户
     */

}

package Loader;

import android.util.Log;

import java.util.ArrayList;

import commonObj.userInfoObj;
import commonObj.userObj;
import commonObj.userTokenObj;
import kotlin.jvm.Throws;
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
     * requestbody : username,password or userId
     *获取指定的用户数据 用于用户登录
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

    /*
    * 获取用户token
    * */
    public Observable<userTokenObj> getToken(RequestBody requestBody){
        return observe(mUserAbout.getToken(requestBody))
                .map(new Function<userTokenObj, userTokenObj>() {
                    @Override
                    public userTokenObj apply(userTokenObj userTokenObj) throws Exception {
                        //保护避免没有出现token的情况下报错
                        if(userTokenObj.getToken() == null){
                            return null;
                        }
                        return userTokenObj;
                    }
                });
    }

    /**
     * params:userId
     * return:userObj
     */
    public Observable<userObj> getUserInfo(final RequestBody requestBody){
        return observe(mUserAbout.getUserInfo(requestBody))
                .map(new Function<userObj, userObj>() {
                    @Override
                    public userObj apply(userObj userObj) throws Exception {
                        if(userObj==null){
                            Log.e("通过id获取账号数据，获取错误",requestBody.toString());
                            return null;
                        }else{
                            return userObj;
                        }
                    }
                });
    }
}

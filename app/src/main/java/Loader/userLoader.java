package Loader;

import java.util.ArrayList;

import commonObj.userInfoObj;
import commonObj.userObj;
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
    public Observable<ArrayList<userObj>> getAllUser(){
        return observe(mUserAbout.getAllUser())
                .map(new Func1<userInfoObj, ArrayList<userObj>>() {
                    @Override
                    public ArrayList<userObj> call(userInfoObj userInfoObj) {
                        if(userInfoObj.getCount() == 0){
                            return null;
                        }
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
                .map(new Func1<userObj, userObj>() {
                    @Override
                    public userObj call(userObj userObj) {
                        if(userObj.getId() == null){
                            return null;
                        }
                        return userObj;
                    }
                });
    }

    /**
     * 注册新的用户
     */

}

package Loader;

import commonObj.BaseResponse;
import rx.functions.Func1;


public class payLoader<T> implements Func1<BaseResponse<T>, T>{

    @Override
    public T call(BaseResponse<T> tBaseResponse) {
        return tBaseResponse.data;
    }
}
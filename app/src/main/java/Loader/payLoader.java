package Loader;

import commonObj.BaseResponse;
import io.reactivex.functions.Function;


public class payLoader<T> implements Function<BaseResponse<T>, T>{

    @Override
    public T apply(BaseResponse<T> tBaseResponse) throws Exception {
        return tBaseResponse.data;
    }
}
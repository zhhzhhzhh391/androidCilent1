package bean;

/***
 * 网络请求记录
 * */

public class BaseResponse<T>  {
    public int status;
    public String msg;
    public T data;
    public boolean isSuccess(){
        return status == 200;
    }
}

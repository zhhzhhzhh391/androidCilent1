package bean;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class getRequestBody {
    private String beanToJson;
     private RequestBody beanRequestBody; //返回的beanRequestBody

    //请求头
    private String header = "application/json;charset=utf-8";

    public getRequestBody(String beanToJson){
        this.beanToJson = beanToJson;
    }

    public RequestBody requestBodyBuilder(){
        beanRequestBody = RequestBody.create(MediaType.parse(header),beanToJson);
        return beanRequestBody;
    }

}

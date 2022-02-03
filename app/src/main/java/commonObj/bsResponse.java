package commonObj;

import java.util.ArrayList;

public class bsResponse<T> {
    private int code;

    public ArrayList<T> getMsg() {
        return msg;
    }

    public void setMsg(ArrayList<T> msg) {
        this.msg = msg;
    }

    public ArrayList<T> msg;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }



}

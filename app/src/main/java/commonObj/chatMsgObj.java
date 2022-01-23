package commonObj;

import java.util.ArrayList;

public class chatMsgObj {
    public chatMsgObj(){}

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<userObj> getMsg() {
        return msg;
    }

    public void setMsg(ArrayList<userObj> msg) {
        this.msg = msg;
    }

    private int code;
    private ArrayList<userObj> msg;
}

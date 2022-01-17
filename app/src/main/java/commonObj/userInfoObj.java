package commonObj;

import java.util.ArrayList;

public class userInfoObj {
    public userInfoObj(){}
    private String previous;

    public ArrayList<userObj> getResults() {
        return results;
    }

    public void setResults(ArrayList<userObj> results) {
        this.results = results;
    }

    private ArrayList<userObj> results;

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }


    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    private Integer count;
    private String next;

}

package bean;

import java.util.ArrayList;

public class userInfoBean {
    private Integer count;
    private String next;

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

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public ArrayList<userBean> getResults() {
        return results;
    }

    public void setResults(ArrayList<userBean> results) {
        this.results = results;
    }

    private String previous;
    private ArrayList<userBean> results;
}

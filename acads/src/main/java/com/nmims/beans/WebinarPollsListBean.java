package com.nmims.beans;

import java.io.Serializable;
import java.util.List;

public class WebinarPollsListBean  implements Serializable  {

    private List<WebinarPollsBean> polls;

    public List<WebinarPollsBean> getPolls() {
        return polls;
    }

    public void setPolls(List<WebinarPollsBean> polls) {
        this.polls = polls;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "WebinarPollsListBean{" +
                "webinarPollsBeanList=" + polls +
                '}';
    }
}
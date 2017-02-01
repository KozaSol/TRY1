package com.jobsearchtry.wrapper;

/**
 * Created by Koza on 11/7/2016.
 */

public class FeedbackType {
    private String feedbackid,feedbackname,feedbackname_local;

    public String getFeedbackid() {
        return feedbackid;
    }

    public String getFeedback() {
        return feedbackname;
    }

    public String getFeedback_local() {
        return feedbackname_local;
    }

    public void setFeedbackid(String feedbackid) {
        this.feedbackid = feedbackid;
    }

    public void setFeedback(String feedback) {
        this.feedbackname = feedback;
    }

    public void setFeedback_local(String feedback_local) {
        this.feedbackname_local = feedback_local;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FeedbackType)
            return ((FeedbackType) obj).getFeedback().equals(this.getFeedback());
        return false;
    }

}

package com.fanfan.youtu.api.face.bean;

import com.fanfan.youtu.api.base.bean.BaseError;

/**
 * Created by android on 2017/12/22.
 */

public class PersonModify extends BaseError {

    private String person_id;
    private String session_id;

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
}

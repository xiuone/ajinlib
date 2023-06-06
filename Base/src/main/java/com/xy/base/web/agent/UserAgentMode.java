package com.xy.base.web.agent;

import android.content.Context;


public class UserAgentMode {
    public String name;
    public String userAgent;
    public boolean isSelected;

    public UserAgentMode(Context context, String name, String userAgent) {
        this.name = name;
        this.userAgent = userAgent;
        isSelected = UserAgentObject.INSTANCE.getCurrentUserAgent(context).equals(userAgent);
    }
}

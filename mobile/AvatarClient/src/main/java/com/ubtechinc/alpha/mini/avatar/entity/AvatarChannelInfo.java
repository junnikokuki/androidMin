package com.ubtechinc.alpha.mini.avatar.entity;

public class AvatarChannelInfo {

    private String channelId;

    private String channelKey;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelKey() {
        return channelKey;
    }

    public void setChannelKey(String channelKey) {
        this.channelKey = channelKey;
    }


    @Override
    public String toString() {
        return "{channelId=" + channelId + ", channelKey=" + channelKey + "}";
    }
}

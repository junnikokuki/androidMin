package com.ubtechinc.alpha.mini.event;

import com.ubtechinc.nets.im.event.BaseImageEvent;

/**
 * @作者：liudongyang
 * @日期: 18/4/2 20:59
 * @描述:
 */

public class AlbumsDataChangeImageEvent extends BaseImageEvent {

    @Override
    public int getType() {
        return ALBUMS_DATA_CHANGE_EVENT;
    }


}

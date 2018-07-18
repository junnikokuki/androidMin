package com.ubtechinc.alpha.mini.repository.datasource;

import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtrobot.lib.sync.engine.ResultPacket;
import com.ubtrobot.param.sync.Params;

import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/3/22 14:23
 * @描述:
 */

public interface IAlbumsDataSource {


    public interface IGetAlbumsItemCallback {
        public void onSuccess(List<AlbumItem> items, ResultPacket<Params.PullResponse> pullResponseResultPacket);

        public void onError(int code, String msg);
    }

    public interface IGetAllAlbumsFromDBCallback{
        public void onSuccess(List<AlbumItem> items);

        public void onError(int code, String msg);
    }


    public interface IDelteAlbumItemCallback{
        public void onSuccess();

        public void onError(int code, String msg);
    }

}

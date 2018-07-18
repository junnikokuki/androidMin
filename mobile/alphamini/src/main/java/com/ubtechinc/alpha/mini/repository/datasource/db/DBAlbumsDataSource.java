package com.ubtechinc.alpha.mini.repository.datasource.db;

import com.ubtechinc.alpha.mini.database.AlbumsProvider;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.repository.datasource.IAlbumsDataSource;

import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/3/22 14:12
 * @描述:
 */

public class DBAlbumsDataSource implements IAlbumsDataSource{

    public DBAlbumsDataSource() {

    }

    public void loadAlbumsDataFromDB(IGetAllAlbumsFromDBCallback callback){
        String userId = MyRobotsLive.getInstance().getRobotUserId();
        List<AlbumItem> albums = AlbumsProvider.getInstance().getAllAlbumsDataByRobotId(userId);
        if (callback != null){
            callback.onSuccess(albums);
        }
    }

    public void saveAlbumsDate2DB(List<AlbumItem> albumItems){
        AlbumsProvider.getInstance().saveOrUpdateAll(albumItems);
    }

    public void deleteCheckedAlbumsInDB(List<AlbumItem> albumItems, IDelteAlbumItemCallback callback){
        AlbumsProvider.getInstance().deleteCheckedAlbumsInDB(albumItems);
        if (callback != null){
            callback.onSuccess();
        }
    }

    public void deleteOneAlbumInDB(AlbumItem item,  IDelteAlbumItemCallback callback){
        AlbumsProvider.getInstance().deleteById(item.getId());
        if (callback != null){
            callback.onSuccess();
        }
    }

    public void deleteRobotAlbumsCache(){
        AlbumsProvider.getInstance().deleteRobotAlbumsCache();
    }

}

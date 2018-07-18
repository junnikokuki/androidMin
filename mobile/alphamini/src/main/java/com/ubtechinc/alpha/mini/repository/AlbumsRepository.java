package com.ubtechinc.alpha.mini.repository;

import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.repository.datasource.IAlbumsDataSource;
import com.ubtechinc.alpha.mini.repository.datasource.IAlbumsDataSource.IGetAlbumsItemCallback;
import com.ubtechinc.alpha.mini.repository.datasource.IAlbumsDataSource.IDelteAlbumItemCallback;
import com.ubtechinc.alpha.mini.repository.datasource.db.DBAlbumsDataSource;
import com.ubtechinc.alpha.mini.repository.datasource.im.IMAlbumsDataSource;

import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/3/22 14:13
 * @描述: 相册列表数据仓库
 */

public class AlbumsRepository {

    private IMAlbumsDataSource mIMAlbumsDataSource;
    private DBAlbumsDataSource mDBAlbumsDataSource;

    public AlbumsRepository() {
        mIMAlbumsDataSource = new IMAlbumsDataSource();
        mDBAlbumsDataSource = new DBAlbumsDataSource();
    }


    public void loadAlbumsDataFromDB(IAlbumsDataSource.IGetAllAlbumsFromDBCallback callback){
        mDBAlbumsDataSource.loadAlbumsDataFromDB(callback);
    }


    public void syncAlbum(IGetAlbumsItemCallback callback){
        mIMAlbumsDataSource.syncAlbum(callback);
    }

    public void loadMoreAlbums(IGetAlbumsItemCallback callback, String context, boolean hasNext){
        mIMAlbumsDataSource.loadMoreAlbums(callback, context, hasNext);
    }

    public void saveSyncAlbums2DB(List<AlbumItem> albumItems){
        mDBAlbumsDataSource.saveAlbumsDate2DB(albumItems);
    }
    public void deleteRobotAlbumsCache(){
        mDBAlbumsDataSource.deleteRobotAlbumsCache();
    }

    public void deleteAlbumsListInRobot(List<AlbumItem> albumItems, IDelteAlbumItemCallback callback){
        mIMAlbumsDataSource.deleteAlbumListInRobot(albumItems, callback);
    }

    public void deleteAlbumListInDB(List<AlbumItem> albumItems, IDelteAlbumItemCallback callback){
        mDBAlbumsDataSource.deleteCheckedAlbumsInDB(albumItems, callback);
    }

    public void deleteOneAlbumInRobot(AlbumItem item, IDelteAlbumItemCallback callback){
        mIMAlbumsDataSource.deleteOneAlbum(item, callback);
    }

    public void deleteOneAlbumInDB(AlbumItem item, IDelteAlbumItemCallback callback){
        mDBAlbumsDataSource.deleteOneAlbumInDB(item, callback);
    }


}

package com.ubtechinc.alpha.mini.database;

import android.util.Log;

import com.ubtechinc.alpha.mini.AlphaMiniApplication;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.framework.db.EntityManager;
import com.ubtechinc.framework.db.EntityManagerFactory;
import com.ubtechinc.framework.db.sqlite.Selector;
import com.ubtechinc.framework.db.sqlite.WhereBuilder;

import java.util.List;

/**
 * @作者：liudongyang
 * @日期: 18/3/22 14:36
 * @描述: 相册数据库操作
 */

public class AlbumsProvider extends BaseProvider<AlbumItem> {

    private EntityManager<AlbumItem> mEntityManager;

    volatile private static AlbumsProvider mInstance = null;

    private String TAG = "AlbumsProvider";

    private AlbumsProvider() {

    }

    public static AlbumsProvider getInstance() {
        if (mInstance == null) {
            synchronized (AlbumsProvider.class) {
                if (mInstance == null) {
                    mInstance = new AlbumsProvider();
                }
            }
        }
        return mInstance;
    }

    @Override
    protected EntityManager<AlbumItem> entityManagerFactory() {
        mEntityManager = EntityManagerFactory.getInstance(AlphaMiniApplication.getInstance(),
                EntityManagerHelper.DB_VERSION,
                EntityManagerHelper.DB_ACCOUNT,
                null, null)
                .getEntityManager(AlbumItem.class,
                        EntityManagerHelper.DB_ALBUMS_LIST_TABLE);
        return mEntityManager;
    }


    public List<AlbumItem> getAllAlbumsDataByRobotId(String robotId) {
        Selector selector = Selector.create().where("mRobotId", "=", robotId);
        List<AlbumItem> contacts = mEntityManager.findAll(selector);
        return contacts;
    }


    public void deleteRobotAlbumsCache(){
        String userId = MyRobotsLive.getInstance().getRobotUserId();
        WhereBuilder builder = WhereBuilder.create();
        builder.and("mRobotId", "=", userId);
        mEntityManager.delete(builder);
    }

    public void deleteCheckedAlbumsInDB(List<AlbumItem> albumItems){
        mEntityManager.deleteAll(albumItems);
    }


    @Override
    public List<AlbumItem> getAllData() {
        return mEntityManager.findAll();
    }

    @Override
    public AlbumItem getDataById(String id) {
        return mEntityManager.findById(id);
    }

    @Override
    public void saveOrUpdateAll(List<AlbumItem> dataList) {
        for (AlbumItem item : dataList){
            Log.i(TAG, "saveOrUpdateAll: " + " item :" + item);
        }
        mEntityManager.saveOrUpdateAll(dataList);
    }

    @Override
    public void saveOrUpdate(AlbumItem item) {
        Log.i(TAG, "saveOrUpdate: " + item);
        mEntityManager.saveOrUpdate(item);
    }

    @Override
    public void deleteAll() {
        mEntityManager.deleteAll();
    }

    @Override
    public void deleteById(int id) {
        mEntityManager.deleteById(id);
    }

    @Override
    public void deleteById(String id) {
        mEntityManager.deleteById(id);
    }

}

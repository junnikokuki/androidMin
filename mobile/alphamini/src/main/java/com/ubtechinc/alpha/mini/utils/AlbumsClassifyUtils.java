package com.ubtechinc.alpha.mini.utils;

import android.util.Log;

import com.ubtechinc.alpha.mini.entity.AlbumItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @作者：liudongyang
 * @日期: 18/3/19 14:24
 * @描述: 相册分类工具(区分每一天的相片)
 */

public class AlbumsClassifyUtils {

    public static Map<String, List<AlbumItem>> classifyPhotos(List<AlbumItem> allAlbumItems){
        Map<String, List<AlbumItem>> maps = new LinkedHashMap<>();

        if (allAlbumItems == null || allAlbumItems.size() == 0){
            return maps;
        }

        for(int i = 0; i < allAlbumItems.size(); i++){
            AlbumItem currentAlbumItem = allAlbumItems.get(i);
            if (i == 0){
                List<AlbumItem> albumItems = new ArrayList<>();
                albumItems.add(currentAlbumItem);
                String time = Tools.getCurrentTime(currentAlbumItem.getImageUploadTime().substring(0,10));
                maps.put(time, albumItems);
                continue;
            }

            String currentTime = Tools.getCurrentTime(allAlbumItems.get(i).getImageUploadTime().substring(0, 10));

            String formerPhotoTime = Tools.getCurrentTime(allAlbumItems.get(i - 1).getImageUploadTime().substring(0, 10));
            List<AlbumItem> currentGroups;
            if (currentTime.equals(formerPhotoTime)){//日期为同一天
                currentGroups = maps.get(formerPhotoTime);
            }else{
                currentGroups = new ArrayList<>();
            }
            currentGroups.add(currentAlbumItem);
            maps.put(currentTime, currentGroups);
        }

        return maps;
    }



}

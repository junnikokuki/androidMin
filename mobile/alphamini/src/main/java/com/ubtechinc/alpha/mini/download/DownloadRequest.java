package com.ubtechinc.alpha.mini.download;

import com.ubtechinc.alpha.AlDwonloadfile;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtrobot.downloader.DownloadInfo;
import com.ubtrobot.downloader.util.FileUtils;

/**
 * Created by ubt on 2018/4/2.
 */

public class DownloadRequest {

    private int level;

    private DownloadInfo socketRequest;

    private AlDwonloadfile.AlDwonloadfileRequest imRequest;

    private AlbumItem item;

    private String robotUserId;

    public DownloadRequest(AlbumItem item, int level, String robotUserId) {
        this.item = item;
        this.level = level;
        this.robotUserId = robotUserId;
    }

    public AlDwonloadfile.AlDwonloadfileRequest getImRequest() {
        if (imRequest == null) {
            imRequest = createImRequest();
        }
        return imRequest;
    }

    public DownloadInfo getSocketRequest() {
        if (socketRequest == null) {
            socketRequest = createSocketRequest();
        }
        return socketRequest;
    }

    private AlDwonloadfile.AlDwonloadfileRequest createImRequest() {
        AlDwonloadfile.AlDwonloadfileRequest.Builder builder = AlDwonloadfile.AlDwonloadfileRequest.newBuilder();
        return builder.setName(item.getImageId()).setLevel(level).build();
    }

    private DownloadInfo createSocketRequest() {
        //TODO
        String path = "camera/";
        if (level == 1) {
            path = "camera/.thumbnail/";
        }else if(level == 2){
            path = "camera/.thumbnail1080/";
        }
        final String finalPath = path;
        DownloadInfo downloadInfo = new DownloadInfo() {
            @Override
            public String getUrl() {
                return finalPath + item.getImageId();
            }

            @Override
            public String getSavePath() {
                if (level == 1) {
                    return item.getImageThumbnailUrl();
                } else if (level == 0){
                    return item.getOriginUrl();
                }else{
                    return item.getImageHDUrl();
                }
            }

            @Override
            public long getSize() {
                return item.getSize();
            }

            @Override
            public int getId() {
                return FileUtils.generateId(getUrl());
            }
        };
        return downloadInfo;
    }

    public String getRobotUserId() {
        return robotUserId;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        if (item != null) {
            return item.getImageId();
        }
        return null;
    }
}

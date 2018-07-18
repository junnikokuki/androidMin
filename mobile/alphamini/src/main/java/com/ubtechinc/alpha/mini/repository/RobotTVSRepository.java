package com.ubtechinc.alpha.mini.repository;

import com.ubtechinc.alpha.mini.repository.datasource.IRobotTVSDataSource;
import com.ubtechinc.alpha.mini.repository.datasource.im.IMRobotTVSDataSource;

/**
 * RobotTVSRepository
 */

public class RobotTVSRepository {

    private static RobotTVSRepository INSTANCE = null;

    IRobotTVSDataSource iRobotTVSDataSource;

    private RobotTVSRepository() {
        iRobotTVSDataSource = new IMRobotTVSDataSource();
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @return the {@link RobotTVSRepository} instance
     */
    public static RobotTVSRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RobotTVSRepository();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    public void sendTVSAccessTokenData(String accessToken, String freshToken, Long expireTime, String clientId, final IRobotTVSDataSource.TVSSendAccessTokenCallback callback) {
        iRobotTVSDataSource.sendTVSAccessTokenData(accessToken, freshToken, expireTime, clientId, callback);
    }

    public void getTVSProductId(final IRobotTVSDataSource.GetTVSProductIdCallBack callback) {
        iRobotTVSDataSource.getTVSProductId(callback);

    }
}

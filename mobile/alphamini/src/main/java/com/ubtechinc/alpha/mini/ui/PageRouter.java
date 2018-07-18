package com.ubtechinc.alpha.mini.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;

import com.ubtechinc.alpha.GetRobotConfiguration;
import com.ubtechinc.alpha.ShowControlActivity;
import com.ubtechinc.alpha.mini.avatar.AvatarInject;
import com.ubtechinc.alpha.mini.common.ActivityManager;
import com.ubtechinc.alpha.mini.constants.Constants;
import com.ubtechinc.alpha.mini.entity.AlbumItem;
import com.ubtechinc.alpha.mini.entity.Contact;
import com.ubtechinc.alpha.mini.entity.Message;
import com.ubtechinc.alpha.mini.entity.RobotInfo;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.entity.observable.MyRobotsLive;
import com.ubtechinc.alpha.mini.net.CheckBindRobotModule;
import com.ubtechinc.alpha.mini.qqmusic.QQMusicActivity;
import com.ubtechinc.alpha.mini.support.avatar.AvatarActivity;
import com.ubtechinc.alpha.mini.support.avatar.AvatarServerImpl;
import com.ubtechinc.alpha.mini.support.avatar.ContextHelperImpl;
import com.ubtechinc.alpha.mini.support.avatar.RobotAvatarImpl;
import com.ubtechinc.alpha.mini.ui.albums.MiniAlbumsActivity;
import com.ubtechinc.alpha.mini.ui.albums.MiniHDAlbumsActivity;
import com.ubtechinc.alpha.mini.ui.bind.AlphaMiniUnbindActivity;
import com.ubtechinc.alpha.mini.ui.bind.UserList;
import com.ubtechinc.alpha.mini.ui.bluetooth.BleAutoConnectActivity;
import com.ubtechinc.alpha.mini.ui.bluetooth.ChooseWifiActivity;
import com.ubtechinc.alpha.mini.ui.bluetooth.ConnectWifiActivity;
import com.ubtechinc.alpha.mini.ui.bluetooth.OpenBluetoothActivity;
import com.ubtechinc.alpha.mini.ui.bluetooth.OpenPowerTipActivity;
import com.ubtechinc.alpha.mini.ui.bluetooth.RequestContrRightActivity;
import com.ubtechinc.alpha.mini.ui.bluetooth.SearchBluetoothActivity;
import com.ubtechinc.alpha.mini.ui.bluetooth.SettingChooseWifiActivity;
import com.ubtechinc.alpha.mini.ui.bluetooth.SettingConnectWiffActivity;
import com.ubtechinc.alpha.mini.ui.bluetooth.SettingShowRobotWifiActivity;
import com.ubtechinc.alpha.mini.ui.car.CarActivity;
import com.ubtechinc.alpha.mini.ui.bluetooth.ShowErrorDialogUtil;
import com.ubtechinc.alpha.mini.ui.codemao.CodeMaoActivity;
import com.ubtechinc.alpha.mini.ui.codemao.OpenBluetoothForCodingActivity;
import com.ubtechinc.alpha.mini.ui.codemao.RemindBindRobotActivity;
import com.ubtechinc.alpha.mini.ui.codemao.SearchBleForCodingActivity;
import com.ubtechinc.alpha.mini.ui.codemao.VideoPlayerActivity;
import com.ubtechinc.alpha.mini.ui.codemao.StarTrekActivity;
import com.ubtechinc.alpha.mini.ui.contacts.AlphaMiniContactActivity;
import com.ubtechinc.alpha.mini.ui.contacts.AlphaMiniContactDetailActivity;
import com.ubtechinc.alpha.mini.ui.contacts.AlphaMiniMobileContactActivity;
import com.ubtechinc.alpha.mini.ui.friend.AgreementActivity;
import com.ubtechinc.alpha.mini.ui.friend.AlphaMiniCameraPrivacyActivity;
import com.ubtechinc.alpha.mini.ui.friend.AlphaMiniFriendActivity;
import com.ubtechinc.alpha.mini.ui.friend.DetectMiniFriendActivity;
import com.ubtechinc.alpha.mini.ui.friend.FriendFaceInfo;
import com.ubtechinc.alpha.mini.ui.friend.InputMiniFriendNameActivity;
import com.ubtechinc.alpha.mini.ui.friend.MiniFriendInfoActivity;
import com.ubtechinc.alpha.mini.ui.friend.ModifyFriendAvatarActivity;
import com.ubtechinc.alpha.mini.ui.friend.ModifyFriendNameActivity;
import com.ubtechinc.alpha.mini.ui.msg.MessageActivity;
import com.ubtechinc.alpha.mini.ui.msg.MessageDetailActivity;
import com.ubtechinc.alpha.mini.ui.msg.MessageListActivity;
import com.ubtechinc.alpha.mini.ui.share.ShareActivity;
import com.ubtechinc.alpha.mini.ui.share.ShareListActivity;
import com.ubtechinc.alpha.mini.ui.share.SharePermissionUpdateActivity;
import com.ubtechinc.alpha.mini.ui.update.MiniAppUpdateActivity;
import com.ubtechinc.alpha.mini.ui.upgrade.UpgradeActivity;
import com.ubtechinc.bluetooth.UbtBluetoothManager;

import java.util.ArrayList;

public class PageRouter {
    public static final String SCANRESULT = "scanResult";
    public static final String SSID = "ssid";
    public static final String CAP = "cap";
    public static final String USER = "user";
    public static final String SERIAL = "serial";

    public static final String TOTALSPACE = "totalspace";
    public static final String AVAIBLESPACE = "avaiblespace";
    public static final String MAINAPPVERSION = "mainappversion";
    public static final String SYSTEMVERSION = "systemversion";
    public static final String HANDWEARVERSION = "handwearversion";
    public static final String BATTERYCAPACITY = "batterycapacity";
    public static final String IMEI = "imei";
    public static final String MEID = "meid";
    public static final String OPERATOR = "operator";
    public static final String FIRSTBIND = "firstbind";
    public static final String MAC = "mac";


    public static final String CURRENT_PHRASE = "current_phrase";
    public static final String FRIEND_NAME = "friend_name";
    public static final String FRIEND_INFO = "friend_info";
    public static final String FRIEND_INDEX = "friend_index";

    public static final String INTENT_KEY_ROBOT_INFO = "intent_key_robot_info";
    public static final String INTENT_KEY_ROBOT_USER_LIST = "intent_key_robot_user_list";

    public static final String INTENT_KEY_ROBOT_ALBUMS = "intent_key_robot_albums";
    public static final String INTENT_KEY_ROBOT_BIND_PHONENUMBER = "intent_key_robot_bind_phonenumber";
    public static final String INTENT_KEY_CURRENT_ALBUM = "intent_key_current_album";

    public static final String INTENT_KEY_NEW_PHONE = "intent_key_new_phone";
    public static final String INTENT_KEY_WIFINAME = "intent_key_wifi_name";
    public static final String INTENT_KEY_HASDEVICES = "intent_key_has_devices";
    public static final String INTENT_KEY_PAGE_TYPE = "page_type";
    public static final String INTENT_KEY_WEB_URL = "web_url";
    public static final String INTENT_DATA_PURPOSE_DEVICE = "purpose_device";
    public static final String INTENT_DATA_ENTER_TYPE = "enter_type";
    public static final String INTENT_DATA_CONNECTED_DEIVCE = "connected_device";
    public static final String INTENT_DATA_COURSE = "from_course";
    public static final String INTENT_KEY_MULTI_INTERACT_GET_RESULT = "intent_key_multi_get_result";
    public static final String INTENT_KEY_MULTI_INTERACT_RESULT = "intent_key_multi_result";

    public static boolean mainStarted = false;


    public static void toLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void toMain(Context context) {
        if(UbtBluetoothManager.getInstance().isFromCodeMao()){
            UbtBluetoothManager.getInstance().setFromCodeMao(false);
            toCodingActivity(context);
            ShowErrorDialogUtil.finishAllWifiAcitivity();

        }else {
            Intent intent = new Intent(context, MainActivity.class);
            if (!mainStarted) {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            mainStarted = true;
        }

    }

    public static void toMain(Context context, String serail) {
        Log.d("0503", " toMain -- serail : " + serail + " isChangeWifi : " + UbtBluetoothManager.getInstance().isChangeWifi());
        Intent intent = new Intent();
        if (UbtBluetoothManager.getInstance().isChangeWifi()) {
            intent = new Intent(context, AlphaMiniSettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            RobotInfo robotInfo = new RobotInfo();
            robotInfo.setMasterUserId(AuthLive.getInstance().getUserId());
            robotInfo.setOnlineState(RobotInfo.ROBOT_STATE_ONLINE);
            robotInfo.setRobotUserId(serail);
            robotInfo.setMasterUserName(AuthLive.getInstance().getCurrentUser().getNickName());
            MyRobotsLive.getInstance().insertNewRobot(robotInfo);
            if (UbtBluetoothManager.getInstance().isFromHome()) {
                intent = new Intent(context, MainActivity.class);
                if (!mainStarted) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                mainStarted = true;
            } else if(UbtBluetoothManager.getInstance().isFromCodeMao()){
                UbtBluetoothManager.getInstance().setFromCodeMao(false);
                toCodingActivity(context);
                ShowErrorDialogUtil.finishAllWifiAcitivity();

            }else {
                intent = new Intent(context, MineRobotInfoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            }
        }
        if (!TextUtils.isEmpty(serail)) {
            intent.putExtra(SERIAL, serail);
            MyRobotsLive.getInstance().setSelectToRobotIdOnline(serail);
        }
        context.startActivity(intent);
    }

    public static void toOpenBluetooth(Activity activity) {
        Intent intent = new Intent(activity, OpenBluetoothActivity.class);
        activity.startActivity(intent);
    }

    public static void toOpenBluetoothForCoding(Activity activity) {
        Intent intent = new Intent(activity, OpenBluetoothForCodingActivity.class);
        activity.startActivity(intent);
    }

    public static void toOpenBindPageForCoding(Activity activity) {
        Intent intent = new Intent(activity, OpenBluetoothForCodingActivity.class);
        activity.startActivity(intent);
    }

    public static void toSearchBluetooth(Activity activity) {
        Intent intent = new Intent(activity, SearchBluetoothActivity.class);
        activity.startActivity(intent);
    }

    public static void toOpenPowerTip(Activity activity) {
        Intent intent = new Intent(activity, OpenPowerTipActivity.class);
        activity.startActivity(intent);
    }

    public static void toBleAutoConnectActivity(Activity activity) {
        Intent intent = new Intent(activity, BleAutoConnectActivity.class);
        activity.startActivity(intent);
    }

    public static void toRequestContrRightActivity(Activity activity, CheckBindRobotModule.User user, String serial) {
        Intent intent = new Intent(activity, RequestContrRightActivity.class);
        intent.putExtra(USER, user);
        intent.putExtra(SERIAL, serial);
        activity.startActivity(intent);
    }

    public static void toChooseWifiActivity(Activity activity) {
        Intent intent = new Intent(activity, ChooseWifiActivity.class);
        activity.startActivity(intent);
    }

    public static void toConnectWifiActivity(Activity activity, String ssid, String cap) {
        Intent intent = new Intent(activity, ConnectWifiActivity.class);
        intent.putExtra(SSID, ssid);
        intent.putExtra(CAP, cap);
        activity.startActivity(intent);
    }

    public static void toCallActivity(Activity activity, String phoneNumber) {
        Intent phoneIntent = new Intent("android.intent.action.DIAL", Uri.parse("tel:"
                + phoneNumber.trim()));
        activity.startActivity(phoneIntent);
    }

    public static void toBangdingActivity(Activity activity) {
        //  根据绑定入口跳转
        if (ActivityManager.getInstance().contains(MineRobotDetailInfoActivity.class)) {
            toMineRobotActivityTop(activity);
        } else {
            toMain(activity);
        }
    }
    public static void toRemindBindRobotActivity(Activity activity) {
        Intent intent = new Intent(activity, RemindBindRobotActivity.class);
        activity.startActivity(intent);
    }

    public static void toCodingActivity(Context activity) {
        //  根据绑定入口跳转
        toCodeMaoActivityTop(activity, null);

    }
    public static void toCodingActivity(Activity activity, Bundle bundle) {
        //  根据绑定入口跳转
        toCodeMaoActivityTop(activity, bundle);

    }
    private static void toCodeMaoActivityTop(Context context, Bundle bundle) {
        Intent intent = new Intent(context, CodeMaoActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(bundle != null) {
            intent.putExtras(bundle);
        }

        context.startActivity(intent);
    }
    public static void toStarTrekActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, StarTrekActivity.class);
        if(bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }
    public static void toVideoActivity(Context context, Bundle bundle) {
        Intent intent = new Intent(context, VideoPlayerActivity.class);
        if(bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }



    public static void toAlphaInfoActivity(Activity activity) {
        Intent intent = new Intent(activity, AlphaMiniSettingActivity.class);
        activity.startActivity(intent);
    }

    public static void toAlphaInfoActivityTop(Activity activity) {
        Intent intent = new Intent(activity, AlphaMiniSettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static void toMiniModifyPhraseActivity(Activity activity, String currentPhrase, int requestCode) {
        Intent intent = new Intent(activity, AlphaMiniModifyPhraseActivity.class);
        intent.putExtra(CURRENT_PHRASE, currentPhrase);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void toMessageActivity(Activity activity) {
        Intent intent = new Intent(activity, MessageActivity.class);
        activity.startActivity(intent);
    }

    public static void toShareMessage(Activity activity) {
        Intent intent = new Intent(activity, MessageListActivity.class);
        intent.putExtra(MessageListActivity.TYPE_MSG, Message.TYPE_SHARE);
        activity.startActivity(intent);
    }

    public static void toSystemMessage(Activity activity) {
        Intent intent = new Intent(activity, MessageListActivity.class);
        intent.putExtra(MessageListActivity.TYPE_MSG, Message.TYPE_SYS);
        activity.startActivity(intent);
    }

    private static void toActivity(Activity activity, Class<? extends Activity> clazz) {
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
    }

    public static void toShareListActivity(Activity activity) {
        Intent shareListIntent = new Intent(activity, ShareListActivity.class);
        activity.startActivity(shareListIntent);
    }

    public static void toShareActivity(Context activity) {
        Intent shareListIntent = new Intent(activity, ShareActivity.class);
        activity.startActivity(shareListIntent);
    }

    public static void toFeedBackActivity(Activity activity) {
        Intent feedBackIntent = new Intent(activity, FeedBackActivity.class);
        activity.startActivity(feedBackIntent);
    }

    public static void toAboutActivity(Activity activity) {
        Intent aboutIntent = new Intent(activity, AboutActivity.class);
        activity.startActivity(aboutIntent);
    }

    public static void toMessageDetail(Activity activity, String noticeId, Message message, int sence) {
        Intent intent = new Intent(activity, MessageDetailActivity.class);
        intent.putExtra(MessageDetailActivity.NOTICE_ID, noticeId);
        intent.putExtra(MessageDetailActivity.MESSAGE, message);
        intent.putExtra(MessageDetailActivity.SCENCE, sence);
        activity.startActivityForResult(intent, 0);
    }

    public static void toSharePermissionUpdate(Context context, String userId, String userName, String relationDate) {
        Intent intent = new Intent(context, SharePermissionUpdateActivity.class);
        intent.putExtra(Constants.USERID_KEY, userId);
        intent.putExtra(Constants.USERNAME_KEY, userName);
        intent.putExtra(Constants.RELATIONDATE_KEY, relationDate);
        context.startActivity(intent);
    }

    public static void toMineRobotActivity(Context context) {
        Intent intent = new Intent(context, MineRobotInfoActivity.class);
        context.startActivity(intent);
    }

    public static void toMineRobotActivityTop(Context context) {
        Intent intent = new Intent(context, MineRobotInfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void toSettingConnectWifiActivity(Activity activity, String ssid, String cap) {
        Intent intent = new Intent(activity, SettingConnectWiffActivity.class);
        intent.putExtra(SSID, ssid);
        intent.putExtra(CAP, cap);
        activity.startActivity(intent);
    }

    public static void toSettingShowRobotWifiActivity(Activity activity) {
        toSettingShowRobotWifiActivity(activity, null);
    }

    public static void toSettingShowRobotWifiActivity(Activity activity, String wifiName) {
        Intent intent = new Intent(activity, SettingShowRobotWifiActivity.class);
        if (wifiName != null) {
            intent.putExtra(INTENT_KEY_WIFINAME, wifiName);
        }
        activity.startActivity(intent);
    }

    public static void toSettingChooseWifiActivity(Context activity) {
        Intent intent = new Intent(activity, SettingChooseWifiActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void toSystemInfoActivity(Activity activity, GetRobotConfiguration.GetRobotConfigurationResponse response) {
        Intent intent = new Intent(activity, SystemInfoActivity.class);
        intent.putExtra(TOTALSPACE, response.getTotalspace());
        intent.putExtra(AVAIBLESPACE, response.getAvailablespace());
        intent.putExtra(MAINAPPVERSION, response.getMainappversion());
        intent.putExtra(SYSTEMVERSION, response.getSysversion());
        intent.putExtra(SERIAL, response.getSerail());
        intent.putExtra(HANDWEARVERSION, response.getHardwareversion());
        intent.putExtra(BATTERYCAPACITY, response.getBatterycapacity());
        intent.putExtra(IMEI, response.getImei());
        intent.putExtra(MEID, response.getEmid());
        intent.putExtra(OPERATOR, response.getOperator());
        intent.putExtra(FIRSTBIND, response.getFirstbindtime());
        intent.putExtra(MAC, response.getMac());
        activity.startActivity(intent);
    }

    public static void toMiniFileActivity(Activity activity) {
        Intent intent = new Intent(activity, AlphaMiniFileActivity.class);
        activity.startActivity(intent);
    }

    public static void toMiniFriendActivity(Activity activity) {
        Intent intent = new Intent(activity, AlphaMiniFriendActivity.class);
        activity.startActivity(intent);
    }

    public static void toMiniCarActivity(Activity activity) {
        Intent intent = new Intent(activity, CarActivity.class);
        activity.startActivity(intent);
    }

    public static void toModifyMiniNameActivity(Activity activity, String name) {
        Intent intent = new Intent(activity, InputMiniFriendNameActivity.class);
        intent.putExtra(FRIEND_NAME, name);
        activity.startActivity(intent);
    }

    public static void toGalaryActivity(Activity activity) {
        Intent intent = new Intent(activity, MiniAlbumsActivity.class);
        activity.startActivity(intent);
    }

    public static void toAvatarActivity(Activity activity) {
        AvatarInject.getInstance().inject(RobotAvatarImpl.getInstance(), AvatarServerImpl.getInstance(), new ContextHelperImpl());
        Intent intent = new Intent(activity, AvatarActivity.class);
        activity.startActivity(intent);
    }

    public static void toMiniFriendInfoActivityNeedResult(Activity activity, FriendFaceInfo faceInfo, int requestcode) {
        Intent intent = new Intent(activity, MiniFriendInfoActivity.class);
        intent.putExtra(FRIEND_INFO, faceInfo);
        activity.startActivityForResult(intent, requestcode);
    }

    public static void toMiniFriendInfoActivity(Activity activity, FriendFaceInfo faceInfo) {
        Intent intent = new Intent(activity, MiniFriendInfoActivity.class);
        intent.putExtra(FRIEND_INFO, faceInfo);
        activity.startActivity(intent);
    }

    public static void toFaceDetectActivity(Activity activity, String friendname) {
        Intent intent = new Intent(activity, DetectMiniFriendActivity.class);
        intent.putExtra(FRIEND_NAME, friendname);
        activity.startActivity(intent);
    }

    public static void toModifyFriendAvatar(Activity activity) {
        Intent intent = new Intent(activity, ModifyFriendAvatarActivity.class);
        activity.startActivity(intent);
    }

    public static void toModifyFriendName(Activity activity, FriendFaceInfo faceInfo, int requestCode) {
        Intent intent = new Intent(activity, ModifyFriendNameActivity.class);
        intent.putExtra(FRIEND_INFO, faceInfo);
        activity.startActivityForResult(intent, requestCode);
    }


    public static void toMobileBindActivity(Activity activity) {
        Intent intent = new Intent(activity, SettingMobileActivity.class);
        activity.startActivity(intent);
    }

    public static void toRobotShowActivity(Activity activity) {
        Intent intent = new Intent(activity, ShowControlActivity.class);
        activity.startActivity(intent);
    }

    public static void toMiniContactsActivity(Activity activity, String phoneNum) {
        Intent intent = new Intent(activity, AlphaMiniContactActivity.class);
        intent.putExtra(AlphaMiniContactActivity.PHONE_KEY, phoneNum);
        activity.startActivity(intent);
    }

    public static void toCreateContact(Activity activity) {
        Intent intent = new Intent(activity, AlphaMiniContactDetailActivity.class);
        intent.putExtra(AlphaMiniContactDetailActivity.STATE_KEY, AlphaMiniContactDetailActivity.STATE_CREATE);
        activity.startActivityForResult(intent, AlphaMiniContactDetailActivity.REQUEST_CODE);
    }

    public static void toContactDetail(Contact contact, Activity activity) {
        Intent intent = new Intent(activity, AlphaMiniContactDetailActivity.class);
        intent.putExtra(AlphaMiniContactDetailActivity.STATE_KEY, AlphaMiniContactDetailActivity.STATE_NORMAL);
        intent.putExtra(AlphaMiniContactDetailActivity.CONTACT_KEY, contact);
        activity.startActivityForResult(intent, AlphaMiniContactDetailActivity.REQUEST_CODE);

    }

    public static void toCellDataSetting(Activity activity, boolean cellData, boolean roamData) {
        Intent intent = new Intent(activity, CellularSettingActivity.class);
        intent.putExtra(CellularSettingActivity.CELL_DATA_KEY, cellData);
        intent.putExtra(CellularSettingActivity.ROAM_DATA_KEY, roamData);
        activity.startActivity(intent);
    }


    public static void toUpgrade(Activity activity) {
        Intent intent = new Intent(activity, UpgradeActivity.class);
        activity.startActivity(intent);
    }

    public static void toMobileContact(Activity activity) {
        Intent intent = new Intent(activity, AlphaMiniMobileContactActivity.class);
        activity.startActivityForResult(intent, 0);
    }

    public static void toMineRobotDetail(Activity activity, RobotInfo robotInfo) {
        Intent intent = new Intent(activity, MineRobotDetailInfoActivity.class);
        intent.putExtra(INTENT_KEY_ROBOT_INFO, robotInfo);
        activity.startActivity(intent);
    }

    public static void toDeliverMasterRight(Activity activity, RobotInfo robotInfo, UserList lists) {
        Intent intent = new Intent(activity, AlphaMiniUnbindActivity.class);
        intent.putExtra(INTENT_KEY_ROBOT_USER_LIST, lists);
        intent.putExtra(INTENT_KEY_ROBOT_INFO, robotInfo);
        activity.startActivity(intent);
    }

    public static void toHDAlbums(Context activity, ArrayList<AlbumItem> albumItemList, AlbumItem cur) {
        Intent intent = new Intent(activity, MiniHDAlbumsActivity.class);
        intent.putParcelableArrayListExtra(INTENT_KEY_ROBOT_ALBUMS, albumItemList);
        intent.putExtra(INTENT_KEY_CURRENT_ALBUM, cur);
        activity.startActivity(intent);
    }

    public static void toMobileDetail(Activity activity, String bindPhoneNumber) {
        Intent intent = new Intent(activity, BindPhoneDetailActivity.class);
        intent.putExtra(INTENT_KEY_ROBOT_BIND_PHONENUMBER, bindPhoneNumber);
        activity.startActivity(intent);
    }

    public static void toMobileNumber(Activity activity) {
        Intent intent = new Intent(activity, BindMobileNumberActivity.class);
        activity.startActivity(intent);
    }

    public static void toMobileNumber(Activity activity, String userPhone) {
        Intent intent = new Intent(activity, BindMobileNumberActivity.class);
        intent.putExtra(INTENT_KEY_NEW_PHONE, userPhone);
        activity.startActivity(intent);
    }

    public static void toQQMusicActivity(FragmentActivity activity) {
        Intent intent = new Intent(activity, QQMusicActivity.class);
        activity.startActivity(intent);
    }

    public static void toSystemNetSetting(FragmentActivity activity) {
        activity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));

    }
    public static void toSearchBleActivity(Activity activity, Bundle bundle) {
        Intent intent = new Intent(activity, SearchBleForCodingActivity.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    public static void toMiniAppUpdateActivity(Activity activity){
        Intent intent = new Intent(activity, MiniAppUpdateActivity.class);
        activity.startActivity(intent);
    }

    public static void toMutilVoiceInteract(Activity activity){
        Intent intent = new Intent(activity, MutilVoiceInteractActivity.class);
        activity.startActivity(intent);
    }

    public static void toConmunicationActivity(Activity activity){
        Intent intent = new Intent(activity, AlphaMiniCameraPrivacyActivity.class);
        activity.startActivity(intent);
    }

    public static void toAgreementActivity(Activity activity){
        Intent intent = new Intent(activity, AgreementActivity.class);
        activity.startActivity(intent);
    }

}

package com.ubtechinc.alpha.mini.ui.bluetooth;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.common.ActivityManager;
import com.ubtechinc.alpha.mini.net.CanclePermissionModule;
import com.ubtechinc.alpha.mini.repository.RobotAuthorizeReponsitory;
import com.ubtechinc.alpha.mini.ui.PageRouter;
import com.ubtechinc.alpha.mini.utils.CustomerCallUtils;
import com.ubtechinc.alpha.mini.widget.MaterialDialog;
import com.ubtechinc.bluetooth.UbtBluetoothManager;
import com.ubtechinc.nets.ResponseListener;
import com.ubtechinc.nets.http.ThrowableWrapper;

import static com.ubtechinc.alpha.mini.constants.Constants.ALEARDY_CONNECT_ERROR_CODE;
import static com.ubtechinc.alpha.mini.constants.Constants.BLUETOOTH_SEND_FIAL;
import static com.ubtechinc.alpha.mini.constants.Constants.CONNECT_TIME_OUT_ERROR_CODE;
import static com.ubtechinc.alpha.mini.constants.Constants.CONNECT_WIFI_FAILED_ERROR_CODE;
import static com.ubtechinc.alpha.mini.constants.Constants.GET_CLIENT_ID_ERROR_CODE;
import static com.ubtechinc.alpha.mini.constants.Constants.ON_SERAIL_ERROR_CODE;
import static com.ubtechinc.alpha.mini.constants.Constants.PASSWORD_VALIDATA_ERROR_CODE;
import static com.ubtechinc.alpha.mini.constants.Constants.PING_ERROR_CODE;
import static com.ubtechinc.alpha.mini.constants.Constants.REGISTER_ROBOT_ERROR_CODE;
import static com.ubtechinc.alpha.mini.constants.Constants.TVS_ERROR_CODE;
import static com.ubtechinc.alpha.mini.constants.Constants.TVS_LOGIN_ERROR_CODE;
import static com.ubtechinc.bluetooth.Constants.ALREADY_BADING;

/**
 * @author：wululin
 * @date：2017/11/3 16:34
 * @modifier：ubt
 * @modify_date：2017/11/3 16:34
 * [A brief description]
 * version
 */

public class ShowErrorDialogUtil {

    private static final String TAG = "ShowErrorDialogUtil";

    public static void bleNetworkError(int errorCode, Activity activity) {
        bleNetworkError(errorCode, activity, null);
    }
    /**
     * 蓝牙配网错误处理
     * @param errorCode
     */
    public static void bleNetworkError(int errorCode, Activity activity, IRetry retry){

        if(errorCode == CONNECT_WIFI_FAILED_ERROR_CODE) {
            showRobotWifiErrorDialog(activity);
            // 登录失败后解除绑定
            unBind(UbtBluetoothManager.getInstance().getCurrentDeviceSerial());
        }else if(errorCode==PING_ERROR_CODE
                || errorCode == CONNECT_TIME_OUT_ERROR_CODE
                || errorCode == BLUETOOTH_SEND_FIAL
                ){
            showConnectTimeoutDialog(activity);
        }
        else if(errorCode==GET_CLIENT_ID_ERROR_CODE
                || errorCode == TVS_LOGIN_ERROR_CODE
                || errorCode == REGISTER_ROBOT_ERROR_CODE){
            showPhoneNetworkErrorDialog(activity);
        }else if(errorCode == PASSWORD_VALIDATA_ERROR_CODE){
            showPwdErrorDialog(activity);
        }else if(errorCode == TVS_ERROR_CODE) {
            showTVSErrorDialog(activity,"获取语音服务失败");
            // 登录失败后解除绑定
            unBind(UbtBluetoothManager.getInstance().getCurrentDeviceSerial());
        }else if(errorCode == ALEARDY_CONNECT_ERROR_CODE){
            showDevicesByOtherDialog(activity);
        }else if(errorCode == ON_SERAIL_ERROR_CODE){
            showNoExistErrorDialog(activity);
        } else if(errorCode == ON_SERAIL_ERROR_CODE){
            showNoExistErrorDialog(activity);
        } else if(errorCode == ALREADY_BADING){
            showAlreadyBandingDialog(activity);
        }
    }

    private static void unBind(String dsn) {
        if(dsn == null) {
            return;
        }
        RobotAuthorizeReponsitory.getInstance().throwPermisson(dsn, new ResponseListener<CanclePermissionModule.Response>() {
            @Override
            public void onError(ThrowableWrapper e) {
                Log.d(TAG, " throwPermisson  onError : " + Log.getStackTraceString(e));
            }

            @Override
            public void onSuccess(CanclePermissionModule.Response response) {
                Log.d(TAG, " throwPermisson  onSuccess ");
            }
        });
    }


    private static void showDevicesByOtherDialog(Activity activity) {
        final MaterialDialog aterialDialog = new MaterialDialog(activity);
        aterialDialog.setMessage(R.string.devies_connect_by_other)
                .setNegativeButton(R.string.i_know,new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        aterialDialog.dismiss();
                        finishAllWifiAcitivity();
                    }
                }).setBackgroundResource(R.drawable.dialog_bg_shap).show();

    }

    public  static void showTVSErrorDialog(final Activity activity,String msg){
        final MaterialDialog aterialDialog = new MaterialDialog(activity);
        aterialDialog.setMessage(msg)
                .setNegativeButton(R.string.i_know,new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        aterialDialog.dismiss();
                    }
                }).setBackgroundResource(R.drawable.dialog_bg_shap).show();
    }

    public  static void showNoExistErrorDialog(final Activity activity){
        final MaterialDialog aterialDialog = new MaterialDialog(activity);
        aterialDialog.setMessage(R.string.robot_not_exist)
                .setTitle(R.string.can_not_bind)
                .setNegativeButton(R.string.exit_ble,new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        finishAllWifiAcitivity();
                        aterialDialog.dismiss();
                    }
                }).setBackgroundResource(R.drawable.dialog_bg_shap)
                .setPositiveButton(R.string.contact_customers,new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        aterialDialog.dismiss();
                        finishAllWifiAcitivity();
                        CustomerCallUtils.call(activity);
                    }
                }).show();
    }

    public  static void showAlreadyBandingDialog(final Activity activity){
        final MaterialDialog aterialDialog = new MaterialDialog(activity);
        aterialDialog.setMessage(R.string.robot_has_banding)
                .setTitle(R.string.can_not_bind)
                .setNegativeButton(R.string.i_know,new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        aterialDialog.dismiss();
                    }
                }).setBackgroundResource(R.drawable.dialog_bg_shap).show();
    }

    public  static void showSendFailDialog(final Activity activity){
        final MaterialDialog aterialDialog = new MaterialDialog(activity);
        aterialDialog.setMessage(R.string.bluetooth_fail)
                .setNegativeButton(R.string.i_know,new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        aterialDialog.dismiss();
                    }
                }).setBackgroundResource(R.drawable.dialog_bg_shap).show();
    }

    public  static void showPwdErrorDialog(final Activity activity){
        final MaterialDialog aterialDialog = new MaterialDialog(activity);
        aterialDialog.setMessage(R.string.wifi_pwd_error)
               .setNegativeButton(R.string.i_know,new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                aterialDialog.dismiss();
            }
        }).setBackgroundResource(R.drawable.dialog_bg_shap).show();
    }

    public static void showNoNetorkDialog(final Activity activity, final IRetry retry){
        final MaterialDialog  mNetworkErrorDialog = new MaterialDialog(activity);

        mNetworkErrorDialog.setTitle(R.string.bangding_fialed).setMessage(R.string.phone_network_error_msg)
                .setPositiveButton(R.string.please_try_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mNetworkErrorDialog.dismiss();
                        if(retry != null) {
                            retry.retry();
                        }
                    }
                }).setNegativeButton(R.string.simple_message_cancel,new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                mNetworkErrorDialog.dismiss();
            }
        }).setBackgroundResource(R.drawable.dialog_bg_shap).setCanceledOnTouchOutside(true).show();
    }


    public  static void showPhoneNetworkErrorDialog(final Activity activity) {
            final MaterialDialog  mNetworkErrorDialog = new MaterialDialog(activity);
//        View view = LayoutInflater.from(activity).inflate(R.layout.network_error_dialog,null);
//        TextView iKnowTv = view.findViewById(R.id.i_know_tv);
//        iKnowTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mNetworkErrorDialog.dismiss();
//                PageRouter.toBangdingActivity(activity);
//                finishAllWifiAcitivity();
//            }
//        });
        mNetworkErrorDialog.setMessage(R.string.net_work_error)
                .setNegativeButton(R.string.i_know, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mNetworkErrorDialog.dismiss();
                    }
                })
                .setCanceledOnTouchOutside(true)
                .setBackgroundResource(R.drawable.dialog_bg_shap)
                .show();
    }

    public static void showRobotWifiErrorDialog(final Activity activity){
            final MaterialDialog aterialDialog = new MaterialDialog(activity);

        aterialDialog.setTitle(R.string.bangding_fialed).setMessage(R.string.robot_wifi_unable_msg)
                .setPositiveButton(R.string.change_wifi, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        aterialDialog.dismiss();
                        PageRouter.toChooseWifiActivity(activity);
                        ActivityManager.getInstance().popActivity(ConnectWifiActivity.class.getName());
                    }
                }).setNegativeButton(R.string.simple_message_cancel,new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                aterialDialog.dismiss();
            }
        }).setBackgroundResource(R.drawable.dialog_bg_shap).show();

    }

    public static void showConnectTimeoutDialog(final Activity activity){
        final MaterialDialog aterialDialog = new MaterialDialog(activity);

        aterialDialog.setMessage(R.string.connect_timeout)
                .setNegativeButton(R.string.i_know, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        aterialDialog.dismiss();
                    }
                })
                .setCanceledOnTouchOutside(true)
                .setBackgroundResource(R.drawable.dialog_bg_shap)
                .show();
    }

    public static void finishAllWifiAcitivity(){
        ActivityManager.getInstance().popActivity(ChooseWifiActivity.class.getName());
        ActivityManager.getInstance().popActivity(ConnectWifiActivity.class.getName());
        ActivityManager.getInstance().popActivity(BleAutoConnectActivity.class.getName());
        ActivityManager.getInstance().popActivity(OpenBluetoothActivity.class.getName());
        ActivityManager.getInstance().popActivity(OpenPowerTipActivity.class.getName());
        ActivityManager.getInstance().popActivity(RequestContrRightActivity.class.getName());
        ActivityManager.getInstance().popActivity(SearchBluetoothActivity.class.getName());

    }

    public interface IRetry {
        void retry();
    }

}

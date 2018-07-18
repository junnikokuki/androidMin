package com.ubtechinc.alpha.mini.widget;

import android.content.Context;
import android.widget.Toast;

import com.ubtechinc.alpha.mini.ui.utils.Utils;

public class NToast {
	public static String Mmessage = "";
	// Toast
	private static Toast toast;
	public static void shortToast(Context context, int message) {
		if (context != null) {
			Mmessage = context.getString(message);
			if (null == toast) {
				toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			}else {
				toast.setText(message);
			}
			toast.show();

		}

	}

	public static void shortToast(Context context, String message) {
		if (context != null) {
			Mmessage = message;
			if (null == toast) {
				toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			}else {
				toast.setText(message);
			}
			toast.show();
		}

	}

	public static void longToast(Context context, int message) {
		if (context != null) {
			Mmessage = context.getResources().getString(message);
			if (null == toast) {
				toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
			}else {
				toast.setText(message);
			}
			toast.show();
		}
	}

	public static void longToast(Context context, String message) {
		if (context != null) {
			Mmessage = message;
			if (null == toast) {
				toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
			}else {
				toast.setText(message);
			}
			toast.show();
		}
	}

	/**
	 * 自定义显示Toast时间
	 *
 	 * @param message
	 * @param duration
	 */
	public static void show(Context context, CharSequence message, int duration) {
		if (Utils.isEmpty(message) && context != null) {
			if (null == toast) {
				toast = Toast.makeText(context, message, duration);

			} else {
				toast.setText(message);
			}
			toast.show();
		}
	}

	/**
	 * 自定义显示Toast时间
	 *
 	 * @param message
	 * @param duration
	 */
	public static void show(Context context, int message, int duration) {
		if(context == null) return;
		if (null == toast) {
			toast = Toast.makeText(context, message, duration);

		} else {
			toast.setText(message);
		}
		toast.show();
	}
}

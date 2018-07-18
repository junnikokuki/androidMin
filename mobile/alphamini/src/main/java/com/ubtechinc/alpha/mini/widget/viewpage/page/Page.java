package com.ubtechinc.alpha.mini.widget.viewpage.page;

import android.net.Uri;
import android.view.View;

/**
 * Created by junsheng.chen on 2018/7/7.
 */
public interface Page {

    View getPageView();

    PageData getPageData();

    void onPageResume(PageData data);

    void onPictureTake(Uri uri);
}

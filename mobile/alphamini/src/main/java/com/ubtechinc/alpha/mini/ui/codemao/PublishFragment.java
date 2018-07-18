package com.ubtechinc.alpha.mini.ui.codemao;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.avatar.utils.DensityUtil;
import com.ubtechinc.alpha.mini.ui.codemao.listener.PageResultListener;
import com.ubtechinc.alpha.mini.widget.recyclerview.listener.OnStateClickListener;
import com.ubtechinc.alpha.mini.widget.viewpage.CustomViewpageAdapter;
import com.ubtechinc.alpha.mini.widget.viewpage.page.Page;
import com.ubtechinc.alpha.mini.widget.viewpage.page.PageData;
import com.ubtechinc.alpha.mini.widget.viewpage.page.SelectPage;
import com.ubtechinc.alpha.mini.widget.viewpage.page.UploadPage;
import com.ubtechinc.alpha.mini.widget.viewpage.page.UploadProgressListener;
import com.ubtechinc.alpha.mini.widget.viewstack.ViewStack;

/**
 * Created by junsheng.chen on 2018/7/7.
 */
public class PublishFragment extends DialogFragment implements View.OnClickListener,
        ViewPager.OnPageChangeListener, OnStateClickListener, PageResultListener, UploadProgressListener {

    public static final String TAG = "PublishFragment";
    public static final int TAKE_PICTURE_CODE = 110;

    private Context mContext;
    private ViewPager mPages;
    private ViewStack mStack;
    private TextView mTitle;
    private Page mPreviousPage;

    private Page mUploadPage;
    private Page mSelectedPage;
    private View mLoading;

    private String mContent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mContent = bundle.getString("content");
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mStack = new ViewStack();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_upload_popup_main, container, false);
        mPages = view.findViewById(R.id.pages);
        mTitle = view.findViewById(android.R.id.title);
        mLoading = view.findViewById(R.id.uploading);
        CustomViewpageAdapter adapter = new CustomViewpageAdapter(mContext);

        View uploadView = LayoutInflater.from(mContext).inflate(R.layout.layout_upload_popup_view, null, false);
        View selectView = LayoutInflater.from(mContext).inflate(R.layout.layout_select_syspic, null, false);
        adapter.addItemView(uploadView);
        adapter.addItemView(selectView);

        view.findViewById(R.id.back).setOnClickListener(this);

        mPages.setAdapter(adapter);
        mPages.addOnPageChangeListener(this);

        mUploadPage = new UploadPage(mContext, uploadView, this, mContent, this);
        mSelectedPage = new SelectPage(mContext, selectView, this);
        mStack.push(mUploadPage);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //init fist page
        /*Page firstPage = mStack.peek();

        RecyclerView uploadPicView = firstPage.getPageView().findViewById(R.id.upload_pic);
        uploadPicView.setLayoutManager(
                new GridLayoutManager(mContext, 3, LinearLayoutManager.HORIZONTAL, false));

        BaseItemAdapter adapter = new BaseItemAdapter();
        adapter.register(Image.class, new ImageViewManager(mContext));

        uploadPicView.setAdapter(adapter);

        EmptyListItem emptyListItem = new EmptyListItem();
        final StateViewHelper helper = new StateViewHelper(uploadPicView, emptyListItem);
        emptyListItem.setOnStateClickListener(this);

        helper.show();*/
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        if (dialog != null && window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //DisplayMetrics dm = new DisplayMetrics();
            //getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            //dialog.getWindow().setLayout(DensityUtil.dp2px(mContext, 550), DensityUtil.dp2px(mContext, 320));
        }
    }

    @Override
    public void onStateClick() {
        //turn to next page
        Page currentPage = mStack.peek();
        CustomViewpageAdapter adapter = (CustomViewpageAdapter) mPages.getAdapter();
        int currentIndex = adapter.indexOfView(currentPage.getPageView());

        mPreviousPage = currentPage;
        mStack.push(mSelectedPage);
        mPages.setCurrentItem(currentIndex + 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                Page view = mStack.pop();
                mPreviousPage = view;
                if (mStack.isEmpty()) {
                    dismiss();
                    mStack.clear();
                } else {
                    if (view != null) {
                        CustomViewpageAdapter adapter = (CustomViewpageAdapter) mPages.getAdapter();
                        int index = adapter.indexOfView(view.getPageView());
                        mPages.setCurrentItem(index - 1);
                    }
                }

                break;
        }
    }

    @Override
    public void onPageSelected(int position) {

        mTitle.setText(mPages.getAdapter().getPageTitle(position));
        Page currentPage = mStack.peek();
        if (mPreviousPage != null && currentPage != null) {
            currentPage.onPageResume(mPreviousPage.getPageData());
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void onPictureTake(Uri uri) {
        Page page = mStack.peek();
        if (page != null) {
            page.onPictureTake(uri);
        }
    }

    @Override
    public void onPageResult(PageData data) {
        mPreviousPage = mStack.pop();
        Page currentPage = mStack.peek();
        if (mPreviousPage != null) {
            CustomViewpageAdapter adapter = (CustomViewpageAdapter) mPages.getAdapter();
            int index = adapter.indexOfView(currentPage.getPageView());
            mPages.setCurrentItem(index);
        }
        //mPreviousPage.onPageResume(data);

    }

    @Override
    public void onUploadProgress() {
        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUploadStart() {

    }

    @Override
    public void onUploadSuccess() {
        mLoading.setVisibility(View.GONE);
        ToastUtils.showLongToast("发布成功");
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onUploadFail() {
        mLoading.setVisibility(View.GONE);
    }
}

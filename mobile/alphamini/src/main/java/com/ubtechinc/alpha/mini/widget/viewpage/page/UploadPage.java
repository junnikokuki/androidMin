package com.ubtechinc.alpha.mini.widget.viewpage.page;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.qiniu.android.http.ResponseInfo;
import com.ubtech.utilcode.utils.FileUtils;
import com.ubtech.utilcode.utils.JsonUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.entity.Product;
import com.ubtechinc.alpha.mini.entity.observable.AuthLive;
import com.ubtechinc.alpha.mini.net.UploadProductModule;
import com.ubtechinc.alpha.mini.repository.ProductReponsitory;
import com.ubtechinc.alpha.mini.repository.QiNiuRepository;
import com.ubtechinc.alpha.mini.repository.datasource.IProductCallback;
import com.ubtechinc.alpha.mini.widget.CustomEditText;
import com.ubtechinc.alpha.mini.widget.recyclerview.BaseItemAdapter;
import com.ubtechinc.alpha.mini.widget.recyclerview.helper.StateViewHelper;
import com.ubtechinc.alpha.mini.widget.recyclerview.holder.BaseViewHolder;
import com.ubtechinc.alpha.mini.widget.recyclerview.holder.MultiViewHolder;
import com.ubtechinc.alpha.mini.widget.recyclerview.item.EmptyListItem;
import com.ubtechinc.alpha.mini.widget.recyclerview.item.Image;
import com.ubtechinc.alpha.mini.widget.recyclerview.item.ImageViewManager;
import com.ubtechinc.alpha.mini.widget.recyclerview.listener.OnItemClickListener;
import com.ubtechinc.alpha.mini.widget.recyclerview.listener.OnStateClickListener;
import com.ubtechinc.nets.http.ThrowableWrapper;
import com.ubtechinc.nets.utils.JsonUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;

/**
 * Created by junsheng.chen on 2018/7/7.
 */
public class UploadPage implements Page, View.OnClickListener {

    private View pageView;
    private PageData mData;
    private Button mCommit;
    private CustomEditText mSampleName;
    private CustomEditText mIntroduce;
    private CustomEditText mSampleDetail;
    final StateViewHelper mHelper;
    private RecyclerView mUploadView;
    private Product mProduct;
    private String mContent;
    UploadProgressListener mUploadListener;

    public UploadPage(Context context, View view, final OnStateClickListener onStateClickListener, String content, UploadProgressListener uploadProgressListener) {
        pageView = view;
        mContent = content;
        mUploadListener = uploadProgressListener;

        mCommit = view.findViewById(R.id.commit);
        mSampleName = view.findViewById(R.id.sample_name);
        mIntroduce = view.findViewById(R.id.sample_introduce);
        mSampleDetail = view.findViewById(R.id.sample_detail);
        mUploadHandler = new UploadProgress(this);
        //init first view

        mUploadView = view.findViewById(R.id.upload_pic);
        mUploadView.setLayoutManager(
                new GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false));

        final BaseItemAdapter adapter = new BaseItemAdapter();
        adapter.register(Image.class, new ImageViewManager(context, ImageViewManager.DETAIL));

        adapter.addFootItem(new Image(R.drawable.icon_addto, "footer", 0, Image.FOOTER, Image.IMAGE));
        mUploadView.setAdapter(adapter);


        EmptyListItem emptyListItem = new EmptyListItem();
        mHelper = new StateViewHelper(mUploadView, emptyListItem);
        emptyListItem.setOnStateClickListener(onStateClickListener);

        mHelper.show();

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseViewHolder viewHolder, View v) {

                MultiViewHolder holder = (MultiViewHolder) viewHolder;
                Image image = (Image) viewHolder.getItemData();
                if (image.typeInView == Image.FOOTER) {
                    Log.d("cjslog", "footer click");
                    onStateClickListener.onStateClick();
                }
                switch (v.getId()) {
                    case R.id.delete_up_btn:
                        adapter.removeDataItem(holder.getItemPosition() - adapter.getHeadCount());
                        if (adapter.getDataList().size() == 0) {
                            mHelper.show();
                        }
                        mCommit.setEnabled(mCommitClickable && !mHelper.isShowing());
                        break;
                }
            }
        });

        initListener();
        mProduct = JsonUtils.jsonToBean(content, Product.class);
        mSampleName.setText(mProduct.getOpusName());
        mSampleName.setSelectAllOnFocus(true);

    }

    private boolean mCommitClickable = false;

    private void initListener() {
        mCommit.setOnClickListener(this);
        Observable<CharSequence> nameObservable = RxTextView.textChanges(mSampleName.getEtContent()).skip(1);
        Observable<CharSequence> introduceObservable = RxTextView.textChanges(mIntroduce.getEtContent()).skip(1);
        Observable<CharSequence> detailObservable = RxTextView.textChanges(mSampleDetail.getEtContent()).skip(1);

        Observable.combineLatest(nameObservable, introduceObservable, detailObservable, new Function3<CharSequence, CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean apply(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3) {
                boolean nameValid = !TextUtils.isEmpty(mSampleName.getText());
                boolean introduceValid = !TextUtils.isEmpty(mIntroduce.getText());
                boolean detailValid = !TextUtils.isEmpty(mSampleDetail.getText());

                return nameValid && introduceValid && detailValid;
            }
        }).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean valid) throws Exception {
                mCommitClickable = valid;
                mCommit.setEnabled(valid && !mHelper.isShowing());
            }
        });
    }

    public View getPageView() {
        return pageView;
    }

    @Override
    public PageData getPageData() {
        return mData;
    }

    @Override
    public void onPageResume(PageData data) {
        mData = data;
        SparseArray<Image> selectedList = data.selectedList();
        if (selectedList.size() != 0) {
            mHelper.hide();

            BaseItemAdapter adapter = (BaseItemAdapter) mUploadView.getAdapter();
            List<Object> dataList = adapter.getDataList();
            for (int i = 0; i < selectedList.size(); i ++) {
                Image image = selectedList.get(i);
                boolean add = true;
                for (int j = 0; j < dataList.size(); j ++) {
                    Image dataImg = (Image) dataList.get(j);
                    if (dataImg.getImgPath().equals(image.getImgPath())) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    adapter.addDataItem(selectedList.get(i));
                }
            }

        }
        mCommit.setEnabled(mCommitClickable && !mHelper.isShowing());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                Log.d("cjslog", "commit click");
                wrapAndUpload();
                break;
        }
    }


    private void wrapAndUpload() {

        Observable.create(new ObservableOnSubscribe<UploadProductModule.Files>() {
            @Override
            public void subscribe(final ObservableEmitter<UploadProductModule.Files> e) {
                final SparseArray<Image> images = mData.selectedList();
                QiNiuRepository repository = QiNiuRepository.getInstance();
                File file;
                for (int i = 0; i < images.size(); i ++) {
                    final Image image = images.get(i);
                    file = new File((String) image.getImgPath());
                    repository.uploadFile(file, FileUtils.getFileMD5ToString(file),
                            new QiNiuRepository.UploadFileListener() {
                                @Override
                                public void onProgress(String key, double percent) {
                                    Message message = Message.obtain(mUploadHandler);

                                    message.what = UploadProgress.UPDATE;
                                    message.arg1 = images.size();
                                    message.obj = key + "/" + percent;
                                    //Log.d("cjslog", "arg:" + message.obj);

                                    message.sendToTarget();
                                }

                                @Override
                                public void onSuccess(String key, ResponseInfo info, JSONObject response) {
                                    Log.d("cjslog", "" + "http://pak6wgbx3.bkt.clouddn.com" + " " + key);
                                    try {
                                        String encodedFileName = URLEncoder.encode(key, "utf-8");
                                        String publicUrl = String.format("%s/%s", QiNiuRepository.BASE_URL, encodedFileName);
                                        UploadProductModule.Files file = new UploadProductModule.Files();
                                        file.setFileUrl(publicUrl);
                                        file.setFileName(image.getName());
                                        file.setType(image.getMineType());
                                        file.setOrder(images.indexOfValue(image));
                                        file.setDescription(mSampleDetail.getText());
                                        e.onNext(file);
                                        Message message = Message.obtain(mUploadHandler);
                                        message.what = UploadProgress.SUCCESS;
                                        message.obj = key;
                                        message.sendToTarget();
                                    } catch (UnsupportedEncodingException e1) {
                                        e1.printStackTrace();
                                        e.onError(e1);
                                    }

                                }

                                @Override
                                public void onFail() {
                                    e.onError(new Throwable("upload fail"));
                                }
                            });
                }

                repository.uploadData(mContent.getBytes(), mProduct.getId(), new QiNiuRepository.UploadFileListener() {
                    @Override
                    public void onProgress(String key, double percent) {
                        Message message = Message.obtain(mUploadHandler);

                        message.what = UploadProgress.UPDATE;
                        message.arg1 = images.size();
                        message.obj = key + "/" + percent;
                    }

                    @Override
                    public void onSuccess(String key, ResponseInfo info, JSONObject response) {
                        try {
                            String encodedFileName = URLEncoder.encode(key, "utf-8");
                            String publicUrl = String.format("%s/%s", QiNiuRepository.BASE_URL, encodedFileName);
                            UploadProductModule.Files file = new UploadProductModule.Files();
                            file.setFileUrl(publicUrl);
                            file.setFileName(key);
                            file.setType(UploadProductModule.FILE);
                            file.setOrder(images.size());
                            file.setDescription(mSampleDetail.getText());
                            e.onNext(file);
                            Message message = Message.obtain(mUploadHandler);
                            message.what = UploadProgress.SUCCESS;
                            message.obj = key;
                            message.sendToTarget();
                        } catch (UnsupportedEncodingException e1) {
                            e1.printStackTrace();
                            e.onError(e1);
                        }

                    }

                    @Override
                    public void onFail() {
                        e.onError(new Throwable("upload fail2"));
                    }
                });
            }
        }).subscribe(new Observer<UploadProductModule.Files>() {

            List<UploadProductModule.Files> files;

            @Override
            public void onSubscribe(Disposable d) {
                files = new ArrayList<>();
            }

            @Override
            public void onNext(UploadProductModule.Files value) {
                files.add(value);
                if (files.size() == mData.selectedList().size()) {
                    //upload complete
                    ProductReponsitory.getInstance().uploadProduct(
                            files.get(0).getFileUrl(),
                            mSampleDetail.getText(),
                            files,
                            mSampleName.getText(),
                            mIntroduce.getText(),
                            ProductReponsitory.TYPE_GRAPH,
                            AuthLive.getInstance().getUserId(),
                            new IProductCallback.UploadProductCallback() {
                                @Override
                                public void onUploadSuccess() {
                                    Log.d("cjslog", "success2");
                                }

                                @Override
                                public void onDataNotAvailable(ThrowableWrapper e) {
                                    Log.d("cjslog", "fail:", e);
                                }
                            }

                    );
                }
            }

            @Override
            public void onError(Throwable e) {

                Log.d("cjslog", "e:", e);
            }

            @Override
            public void onComplete() {

            }
        });

    }

    private UploadProgress mUploadHandler;

    private static final class UploadProgress extends Handler {

        public static final int UPDATE = 0;
        public static final int SUCCESS = 1;
        public static final int FAIL = 2;
        private double mPresent = 0;
        private HashMap<String, Double> mProgressMap;
        private WeakReference<UploadPage> mReference;

        public UploadProgress(UploadPage page) {
            mProgressMap = new HashMap<>();
            mReference = new WeakReference<>(page);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    int total = msg.arg1;
                    String data = (String) msg.obj;
                    String[] msgs = data.split("/");
                    double present = Double.valueOf(msgs[1]);
                    //String present = String.format("%.2f", data.split("/")[1]);
                    //float progress = Float.valueOf(present);
                    Log.d("cjslog", "key:" + msgs[0] + " " + present);
                    //(100 / (total + 1)) * (1 / (progress));
                    mPresent = mPresent + (100 / (total + 1)) * present;
                    Log.d("cjslog", "present" + mPresent);
                    mProgressMap.put(msgs[0], present);

                    UploadPage page = mReference.get();
                    if (page != null) {
                        page.mUploadListener.onUploadProgress();
                    }
                    break;

                case SUCCESS:
                    String key = (String) msg.obj;
                    mProgressMap.remove(key);
                    if (mProgressMap.isEmpty()) {
                        UploadPage currentPage = mReference.get();
                        if (currentPage != null) {
                            currentPage.mUploadListener.onUploadSuccess();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onPictureTake(Uri uri) {

    }
}

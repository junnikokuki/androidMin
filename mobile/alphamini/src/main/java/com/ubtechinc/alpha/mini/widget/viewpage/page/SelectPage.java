package com.ubtechinc.alpha.mini.widget.viewpage.page;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.ubtech.utilcode.utils.FileUtils;
import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;
import com.ubtechinc.alpha.mini.ui.codemao.CodeMaoActivity;
import com.ubtechinc.alpha.mini.ui.codemao.listener.PageResultListener;
import com.ubtechinc.alpha.mini.widget.recyclerview.BaseItemAdapter;
import com.ubtechinc.alpha.mini.widget.recyclerview.holder.BaseViewHolder;
import com.ubtechinc.alpha.mini.widget.recyclerview.item.Image;
import com.ubtechinc.alpha.mini.widget.recyclerview.item.ImageViewManager;
import com.ubtechinc.alpha.mini.widget.recyclerview.listener.OnItemClickListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.ubtechinc.alpha.mini.ui.codemao.PublishFragment.TAKE_PICTURE_CODE;


/**
 * Created by junsheng.chen on 2018/7/7.
 */
public class SelectPage implements Page, View.OnClickListener {

    private static final int SPANCOUNT = 7;
    private View pageView;
    private SelectPageData mData;
    private Context mContext;
    private RecyclerView mSysImages;
    private ArrayList<Image> mSelected;
    private PageResultListener mPageResult;
    private String photoPath;
    private Handler mHandler;

    private static final class UiHandler extends Handler {
        private WeakReference<SelectPage> mReference;

        public UiHandler(SelectPage page) {
            mReference = new WeakReference<>(page);
        }

        @Override
        public void handleMessage(Message msg) {
            Image image = (Image) msg.obj;
            SelectPage page = mReference.get();
            if (page != null) {
                BaseItemAdapter adapter = (BaseItemAdapter) page.mSysImages.getAdapter();
                adapter.addDataItem(0, image);
            }
        }
    }

    public SelectPage(Context context, final View view, PageResultListener pageResultListener) {
        pageView = view;
        mContext = context;
        mPageResult = pageResultListener;
        mSelected = new ArrayList<>(8);
        mData = new SelectPageData();
        mHandler = new UiHandler(this);

        mSysImages = view.findViewById(R.id.system_images);
        view.findViewById(R.id.select_complete).setOnClickListener(this);

        mSysImages.setLayoutManager(
                new GridLayoutManager(mContext, SPANCOUNT, LinearLayoutManager.VERTICAL, false));

        final BaseItemAdapter baseItemAdapter = new BaseItemAdapter();
        baseItemAdapter.register(Image.class, new ImageViewManager(context, ImageViewManager.SELECT));

        mSysImages.setAdapter(baseItemAdapter);

        baseItemAdapter.addHeadItem(new Image(R.drawable.icon_camera, "header", 0, Image.HEADER, Image.IMAGE));

        baseItemAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseViewHolder viewHolder, View v) {
                BaseItemAdapter itemAdapter = (BaseItemAdapter) mSysImages.getAdapter();
                if (itemAdapter.getHeadCount() > viewHolder.getItemPosition()) {
                    //header;
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        String path = Environment.getExternalStorageDirectory() +
                                File.separator + Environment.DIRECTORY_DCIM + File.separator;
                        File file = new File(path);
                        if (!file.exists()) {
                            file.mkdir();
                        }
                        String fileName = getPhotoFileName() + ".jpg";
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File photo =  new File(path + fileName);
                        photoPath = photo.getPath();
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                        ((Activity)mContext).startActivityForResult(intent, TAKE_PICTURE_CODE);
                    }

                } else {
                    if (mSelected.size() >= 8) {
                        ToastUtils.showLongToast(R.string.select_pic_max);
                        return;
                    }
                    Image image = (Image) viewHolder.getItemData();
                    if (image.getSelectedIndex() != -1) {
                        List list = itemAdapter.getDataList();
                        int selectIndex = mSelected.indexOf(image);

                        for (int i = selectIndex + 1; i < mSelected.size(); i ++) {
                            Image selectImg = mSelected.get(i);
                            int dataIndex = list.indexOf(selectImg);

                            int selectOrder = selectImg.getSelectedIndex();
                            selectImg.setSelectedIndex(--selectOrder);

                            list.set(dataIndex, selectImg);

                            itemAdapter.notifyItemChanged(dataIndex + itemAdapter.getHeadCount());
                        }

                        mSelected.remove(image);
                        image.setSelectedIndex(-1);
                        itemAdapter.notifyItemChanged(viewHolder.getItemPosition());
                    } else {
                        mSelected.add(image);
                        image.setSelectedIndex(mSelected.indexOf(image) + 1);
                        //markTv.setText(String.valueOf(mSelected.indexOf(image) + 1));
                        itemAdapter.notifyItemChanged(viewHolder.getItemPosition());
                    }
                }
            }
        });


        startLoadSystemImage();
    }

    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "IMG_" + dateFormat.format(date);
    }

    private void startLoadSystemImage() {
        new QueryHandler(mContext.getContentResolver(), this).execute();
    }

    @Override
    public View getPageView() {
        return pageView;
    }

    @Override
    public PageData getPageData() {
        return mData;
    }

    @Override
    public void onPageResume(PageData data) {

    }

    void updateSystemVideo(Cursor cursor) {
        BaseItemAdapter adapter = (BaseItemAdapter) mSysImages.getAdapter();
        if (cursor != null) {
            while (cursor.moveToNext()) {

                // 获取图片的路径
                String path = cursor.getString(
                        cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                //获取图片名称
                String name = cursor.getString(
                        cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                //获取图片时间
                long time = cursor.getLong(
                        cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));

                long duration = cursor.getLong(
                        cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                if (!".downloading".equals(FileUtils.getFileExtension(path))) {
                    //过滤未下载完成的文件
                    Image image = new Image(path, name, time, Image.NORMAL, Image.VIDEO);
                    image.setDuration(duration);
                    adapter.addDataItem(image);

                }


            }
        }
        swap();
    }

    void updateSystemImg(Cursor cursor) {
        BaseItemAdapter adapter = (BaseItemAdapter) mSysImages.getAdapter();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获取图片的路径
                String path = cursor.getString(
                        cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                //获取图片名称
                String name = cursor.getString(
                        cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                //获取图片时间
                long time = cursor.getLong(
                        cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

                if (!".downloading".equals(FileUtils.getFileExtension(path))) {
                    //过滤未下载完成的文件
                    adapter.addDataItem(new Image(path, name, time, Image.NORMAL, Image.IMAGE));

                }
            }
        }
        swap();
    }

    private synchronized void swap() {
        final BaseItemAdapter adapter = (BaseItemAdapter) mSysImages.getAdapter();
        final List<Object> list = adapter.getDataList();


        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for(int i =0;i < list.size() - 1;i++)
                {
                    for(int j = 0;j <  list.size() - 1-i;j++)// j开始等于0，
                    {
                        if(((Image)list.get(j)).getTime() < ((Image)list.get(j + 1)).getTime())
                        {
                    /*int temp = score[j];
                    score[j] = score[j+1];
                    score[j+1] = temp;*/
                            Collections.swap(list, j, j + 1);
                            publishProgress(j, j + 1);
                        }
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adapter.notifyDataSetChanged();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                //adapter.moveDataItem(values[0] + adapter.getHeadCount(), values[1] + adapter.getHeadCount());
            }
        }.execute();


    }

    @Override
    public void onPictureTake(Uri uri) {
        Cursor cursor = null;
        if (uri != null) {

            cursor = mContext.getContentResolver().query(uri, new String[]{
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media._ID},
                    null,
                    null,
                    null);
            if (cursor != null) {

                updateSystemImg(cursor);
                cursor.close();
            }

        } else if (photoPath != null) {
            MediaScannerConnection.scanFile(mContext, new String[]{photoPath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Cursor newFileCursor = mContext.getContentResolver().query(uri, new String[]{
                                    MediaStore.Images.Media.DATA,
                                    MediaStore.Images.Media.DISPLAY_NAME,
                                    MediaStore.Images.Media.DATE_ADDED,
                                    MediaStore.Images.Media._ID},
                            null,
                            null,
                            null);

                    if (newFileCursor != null) {
                        if (newFileCursor.moveToNext()) {  //只有一张照片
                            Message message = Message.obtain(mHandler);
                        /*String path = newFileCursor.getString(
                                newFileCursor.getColumnIndex(MediaStore.Images.Media.DATA));*/
                            //获取图片名称
                            String name = newFileCursor.getString(
                                    newFileCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                            //获取图片时间
                            long time = newFileCursor.getLong(
                                    newFileCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));

                            if (!".downloading".equals(FileUtils.getFileExtension(path))) {
                                //过滤未下载完成的文件
                                message.obj = new Image(path, name, time, Image.NORMAL, Image.IMAGE);
                                message.sendToTarget();
                            }
                        }

                        newFileCursor.close();
                    }
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_complete:
                //wrap data
                wrapAndFinish();
                break;
        }
    }

    private void wrapAndFinish() {
        SparseArray<Image> addList = new SparseArray<>();
        for (int i = 0; i < mSelected.size(); i ++) {
            addList.append(i, mSelected.get(i));
        }

        mData.updateList(addList);

        mPageResult.onPageResult(mData);
    }

    private static final class QueryHandler extends AsyncQueryHandler {

        private static final int IMAGE = 0;
        private static final int VIDEO = 1;

        private WeakReference<SelectPage> mReference;

        public QueryHandler(ContentResolver cr, SelectPage page) {
            super(cr);
            mReference = new WeakReference(page);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);

            SelectPage page = mReference.get();
            if (page != null) {
                switch (token) {
                    case IMAGE:
                        page.updateSystemImg(cursor);
                        break;
                    case VIDEO:
                        page.updateSystemVideo(cursor);
                        break;
                }

            }

            cursor.close();
        }

        public void execute() {
            //image
            startQuery(IMAGE, null, MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.Media.DATA,
                            MediaStore.Images.Media.DISPLAY_NAME,
                            MediaStore.Images.Media.DATE_ADDED,
                            MediaStore.Images.Media._ID},
                    null,
                    null,
                    MediaStore.Images.Media.DATE_ADDED + " DESC");

            //video

            // 视频其他信息的查询条件
            String[] mediaColumns = {MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DATE_ADDED};

            startQuery(VIDEO, null, MediaStore.Video.Media
                            .EXTERNAL_CONTENT_URI,
                    mediaColumns, MediaStore.Video.Media.DURATION + "<60000", null, MediaStore.Video.Media.DATE_ADDED + " DESC");
        }
    }
}

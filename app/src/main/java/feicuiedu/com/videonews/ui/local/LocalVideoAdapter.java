package feicuiedu.com.videonews.ui.local;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地视频列表的适配器。
 */
class LocalVideoAdapter extends CursorAdapter {

    // 用于缓存本地视频缩略图，生成缩略图很耗时，不希望重复去生成
    // TODO: 使用LruCache代替HashMap，避免本地视频过多时内存溢出
    private final HashMap<String, Bitmap> thumbnailCache = new HashMap<>();

    // 用于生成视频缩略图的线程池
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public LocalVideoAdapter(Context context) {
        super(context, null , true);
    }


    @Override public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return new LocalVideoView(context);
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {

        final LocalVideoView localVideoView = (LocalVideoView) view;
        localVideoView.bind(cursor);

        final String filePath = localVideoView.getFilePath();

        if (thumbnailCache.get(filePath) != null) { // 内存中已有此缩略图，直接使用
            localVideoView.setPreview(thumbnailCache.get(filePath));
        } else {
            executorService.submit(new Runnable() {
                @Override public void run() {
                    // 获取视频缩略图，耗时操作，不要在主线程执行
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                    thumbnailCache.put(filePath, bitmap);
                    localVideoView.setPreview(filePath, bitmap);
                }
            });
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        thumbnailCache.clear();
        executorService.shutdown();
    }
}

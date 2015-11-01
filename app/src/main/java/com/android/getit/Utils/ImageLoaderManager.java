package com.android.getit.Utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * Created by shi.ming on 2015/8/6.
 */
public class ImageLoaderManager {
    private static ImageLoader mImageLoader;

    private static ImageLoaderConfiguration mImageLoaderConfiguration;

    private static DisplayImageOptions.Builder mDisplayImageOptionsBuilder;

    public static final ImageLoader getInstance() {
        if (null == mImageLoader) {
            synchronized (ImageLoaderManager.class) {
                if (null == mImageLoader) {
                    mImageLoader = ImageLoader.getInstance();
                    initImageLoader();
                }
            }
        }
        return mImageLoader;
    }
        /**
         * �ڴ��Ƿ����
         */
    private static boolean isMemoryLimited;

    public static final void initImageLoaderConfiguration(Context context) {
        if (null == mImageLoaderConfiguration) {
            final int memoryCache = Utils.getMemoryClass(context);
            isMemoryLimited = memoryCache <= 64;
            final DisplayImageOptions displayImageOptions = getDisplayImageOptionsBuilder()
                    .build();
            mImageLoaderConfiguration = new ImageLoaderConfiguration.Builder(
                    context)
                    .threadPoolSize(isMemoryLimited ? 3 : 5)
                            // default
                    .threadPriority(Thread.NORM_PRIORITY - 1)
                            // default
                    .tasksProcessingOrder(QueueProcessingType.FIFO)
                            // default
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(
                            isMemoryLimited ? new WeakMemoryCache()
                                    : new LruMemoryCache(
                                    1024 * 1024 * memoryCache / 8))
                            // default
                    .diskCache(
                            new UnlimitedDiscCache(StorageUtils
                                    .getCacheDirectory(context)))
                            // default
                    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                            // default
                    .imageDownloader(new BaseImageDownloader(context))
                            // default
                    .defaultDisplayImageOptions(displayImageOptions)
                    .build();
        }

    }
    public static final DisplayImageOptions.Builder getDisplayImageOptionsBuilder() {
        if (null == mDisplayImageOptionsBuilder) {
            synchronized (ImageLoaderManager.class) {
                if (null == mDisplayImageOptionsBuilder) {
                    mDisplayImageOptionsBuilder = new DisplayImageOptions.Builder();
                    mDisplayImageOptionsBuilder
                            .resetViewBeforeLoading(true)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .bitmapConfig(
                                    isMemoryLimited ? Bitmap.Config.RGB_565
                                            : Bitmap.Config.ARGB_8888)
                            .imageScaleType(ImageScaleType.EXACTLY);
                }
            }
        }
        return mDisplayImageOptionsBuilder;
    }

    public static final void initImageLoader() {
        ImageLoaderConfiguration config = getImageLoaderConfiguration();
        mImageLoader.init(config);
    }

    public static final ImageLoaderConfiguration getImageLoaderConfiguration() {
        if (null == mImageLoaderConfiguration) {
            throw new IllegalArgumentException("û�г�ʼ�� ImageLoaderConfiguration");
        } else {
            return mImageLoaderConfiguration;
        }
    }

    public static void destory() {
        mImageLoader.destroy();
    }
}

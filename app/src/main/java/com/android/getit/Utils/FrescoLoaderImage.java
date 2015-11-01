package com.android.getit.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.android.getit.ConfigConstants;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by sminger on 2015/11/1.
 */
public class FrescoLoaderImage {
    private static ImagePipelineConfig mImagePipelineConfig;
    private static final String IMAGE_PIPELINE_CACHE_DIR = "imagepipeline_cache";
    private static Context mContext;

    public FrescoLoaderImage() {

    }

    public static GenericDraweeHierarchy getRoundingGDHierarchy(Resources resources) {
        GenericDraweeHierarchy gdh = new GenericDraweeHierarchyBuilder(resources)
                .setRoundingParams(RoundingParams.asCircle())
                .build();
        return gdh;
    }
    public static GenericDraweeHierarchy getProgressGDHHierarchy(Resources resources) {
        GenericDraweeHierarchy gdh = new GenericDraweeHierarchyBuilder(resources)
                .setProgressBarImage(new ProgressBarDrawable())
                .build();
        return gdh;
    }
    public static void InitLoaderImage(Context context) {
        mContext = context;
        InitFresco(mContext);
    }
    private static void InitFresco(Context context) {
        ProgressiveJpegConfig pjpegConfig = new ProgressiveJpegConfig() {

            @Override
            public int getNextScanNumberToDecode(int scanNumber) {
                Log.d("ssss", "ddddddd  scanNumber :" + scanNumber);
                return scanNumber + 2;
            }

            @Override
            public QualityInfo getQualityInfo(int scanNumber) {
                Log.d("ssss", "dddddddd======getQualityInfo:"+ scanNumber);
                boolean isGoodEnough = (scanNumber >= 5);
                return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
            }
        };
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context)
                .setProgressiveJpegConfig(pjpegConfig);//modify by sminger
        configureCaches(configBuilder, context);
        mImagePipelineConfig = configBuilder.build();

        // Fresco.initialize(this);

        Fresco.initialize(context, mImagePipelineConfig);
        Fresco.getImagePipeline();
    }

    /**
     * Configures disk and memory cache not to exceed common limits
     */
    private static void configureCaches(
            ImagePipelineConfig.Builder configBuilder,
            Context context) {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                ConfigConstants.MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE,                     // Max entries in the cache
                ConfigConstants.MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                Integer.MAX_VALUE,                     // Max length of eviction queue
                Integer.MAX_VALUE);                    // Max cache entry size
        configBuilder
                .setBitmapMemoryCacheParamsSupplier(
                        new Supplier<MemoryCacheParams>() {
                            public MemoryCacheParams get() {
                                return bitmapCacheParams;
                            }
                        })
                .setMainDiskCacheConfig(
                        DiskCacheConfig.newBuilder()
                                .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())
                                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                                .setMaxCacheSize(ConfigConstants.MAX_DISK_CACHE_SIZE)
                                .build());
    }

    public static void loaderProgressively( SimpleDraweeView view, Uri uri) {
        loaderProgressively(view, uri, null);
    }
    public static void loaderProgressively( SimpleDraweeView view, Uri uri,GenericDraweeHierarchy gdh) {
        if (gdh != null) {
            view.setHierarchy(gdh);
        }
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(true)
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                        //.setUri(uri)
                .setOldController(view.getController())

                .build();
        view.setController(controller);

    }

    public static void loaderImage(SimpleDraweeView view, Uri uri){
        loaderImage(view, uri, null);
    }
    public static void loaderImage(SimpleDraweeView view, Uri uri, GenericDraweeHierarchy gdh) {
        loaderProgressively(view, uri, gdh);
    }
}

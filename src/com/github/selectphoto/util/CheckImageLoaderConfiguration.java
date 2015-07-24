package com.github.selectphoto.util;

import java.io.File;
import java.io.IOException;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**    
 * 使用UIL第三方开源库必须做的配置检查，详情参见GitHub上关于UIL的WIKI和使用说明
 */
public class CheckImageLoaderConfiguration {

	public static void checkImageLoaderConfiguration(Context context) throws IOException {
		if(!UniversalImageLoadTool.checkImageLoader()) {
		
			File cacheDir = StorageUtils.getCacheDirectory(context, true);
			int cacheMemMaxSize = 10 * 1024 * 1024;//10MB
			long cacheDiskMaxSize = 50 * 1024 * 1024;//50MB
			
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
			.threadPriority(Thread.NORM_PRIORITY)
			.denyCacheImageMultipleSizesInMemory()
			.memoryCacheSize(cacheMemMaxSize)
			.diskCache(new LruDiscCache(cacheDir, new Md5FileNameGenerator(), cacheDiskMaxSize))
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.build();
			
			// Initialize ImageLoader with configuration.
			ImageLoader.getInstance().init(config);
		}
	}
}

package com.github.selectphoto.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.github.selectphoto.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 通用图片加载程序（UIL）的显示方法工具类
 *
 */
public class UniversalImageLoadTool {

	private static ImageLoader imageLoader = ImageLoader.getInstance();
	
	public static ImageLoader getImageLoader() {
		return imageLoader;
	}
	public static boolean checkImageLoader() {
		return imageLoader.isInited();
	}
	
	/**
	 * 加载显示图片
	 * @param uri
	 * @param imageAware
	 */
	public static void disPlay(String uri, ImageAware imageAware) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.icon_profile_photo_def)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.displayer(new SimpleBitmapDisplayer())
		.build();

		imageLoader.displayImage(uri, imageAware, options);
	}
	
	/**
	 * 加载显示图片
	 * @param uri
	 * @param imageView
	 * @param listener
	 */
	public static void displayImage(String uri, ImageView imageView, ImageLoadingListener listener) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.resetViewBeforeLoading(true)
		.cacheOnDisk(true)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new SimpleBitmapDisplayer())
		.build();

		imageLoader.displayImage(uri, imageView, options, listener);
	}
	
	/**
	 * 清除缓存
	 */
	public static void clear(){
		imageLoader.clearMemoryCache();		
		imageLoader.clearDiskCache();
	}
	
	/**
	 * 恢复加载
	 */
	public static void resume(){
		imageLoader.resume();
	}
	
	/**
	 * 暂停加载
	 */
	public static void pause(){
		imageLoader.pause();
	}
	
	/**
	 * 停止加载
	 */
	public static void stop(){
		imageLoader.stop();
	}
	
	/**
	 * 销毁加载
	 */
	public static void destroy() {
		imageLoader.destroy();
	}
}

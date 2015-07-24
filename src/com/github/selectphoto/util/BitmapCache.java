package com.github.selectphoto.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.widget.ImageView;

import com.github.selectphoto.R;
import com.nostra13.universalimageloader.core.assist.deque.LIFOLinkedBlockingDeque;

/**
 * 图片压缩和缓存类，缓存机制LruCache
 * @author Administrator
 *
 */
public class BitmapCache {
	private Handler mHandler;
	private ThreadPoolExecutor mThreadPoolExecutor;
	private LruCache<String, Bitmap> mImageLruCache;
	
	//应用程序最大可用内存
	int maxMemory = (int) Runtime.getRuntime().maxMemory();
	
	//设置图片缓存大小为maxMemory的1/8
	int cacheSize = maxMemory / 8;
	
	private int mDefaultPhoto = R.drawable.icon_profile_photo_def;

	public BitmapCache() {
		mHandler = new Handler();
		
		mImageLruCache = new LruCache<String, Bitmap>(cacheSize) {
			
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
		
		mThreadPoolExecutor = new ThreadPoolExecutor(2, 4, 3, TimeUnit.SECONDS,
				new LIFOLinkedBlockingDeque<Runnable>());
	}
	
	/**
	 * 将图片存储到LruCache
	 */
	private void addBitmapToLruCache(String key, Bitmap bitmap) {
		if (getBitmapFromLruCache(key) == null && bitmap != null) {
			mImageLruCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache缓存获取图片
	 */
	private Bitmap getBitmapFromLruCache(String key) {
		return mImageLruCache.get(key);
	}

	public void displayBitmap(final ImageView imageView,
			final String sourcePath, final ImageCallback callback) {
		
		if (TextUtils.isEmpty(sourcePath)) {
			return;
		}

		Bitmap bmp = getBitmapFromLruCache(sourcePath);
		if (bmp != null) {
			if (callback != null) {
				callback.imageLoad(imageView, bmp, sourcePath);
			}
		} else {
			imageView.setImageResource(mDefaultPhoto);
			mThreadPoolExecutor.execute(new ThreadPoolTast(sourcePath, imageView, callback));
		}
	}
	
	//对图片原图进行压缩
	private Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 256) && (options.outHeight >> i <= 256)) {
				in = new BufferedInputStream(new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return BitmapUtil.reviewPicRotate(bitmap, path);
	}

	public interface ImageCallback {
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params);
	}
	
	class ThreadPoolTast implements Runnable {
		Bitmap thumb;
		String sourcePath;
		ImageView imageView;
		ImageCallback callback;

		public ThreadPoolTast(String sourcePath, ImageView imageView, ImageCallback callback) {
			this.sourcePath = sourcePath;
			this.imageView = imageView;
			this.callback = callback;
		}
		
		@Override
		public void run() {
			try {
				//对图片原图进行压缩，并添加到LruCache缓存
				thumb = revitionImageSize(sourcePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			addBitmapToLruCache(sourcePath, thumb);
			
			if (callback != null) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						callback.imageLoad(imageView, thumb, sourcePath);
					}
				});
			}
		}
	}
	
	// 外部可以设置默认图片资源ID
	public void setDefaultPhoto(int defaultphoto) {
		mDefaultPhoto = defaultphoto;
	}
}

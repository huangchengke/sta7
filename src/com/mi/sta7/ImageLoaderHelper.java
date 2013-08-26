package com.mi.sta7;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.mi.sta7.ui.MainActivity;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class ImageLoaderHelper {

	public static ImageLoader imageLoader;

	public static File cacheDir = null;
	private static final int MEM_CACHE_SIZE = 3 * 1024 * 1024;
	private static final int DISC_CACHE_SIZE = 10 * 1024 * 1024; // 如用非 External Storage, 限定 10 MB
	private static final int CONN_TIMEOUT = 8 * 1000;
	private static final int READ_TIMEOUT = 20 * 1000;
	private static final int MAX_IMAGE_SIZE = 1024;
	private static final int THREAD_POOL_SIZE = 7;
	/**
	 * Rener 初始化, 其中 Image Loader 由原來 UrlImageViewHelper 換成 Android-Universal-Image-Loader
	 * @see https://github.com/nostra13/Android-Universal-Image-Loader
	 * @param context
	 */
	public static void imageLoaderInit(Context context) {
		imageLoader = ImageLoader.getInstance();
		cacheDir = StorageUtils.getCacheDirectory(context);
		boolean isSDCache =
				cacheDir.getAbsolutePath().matches(Environment.getExternalStorageDirectory() + ".*");
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
			.cacheInMemory()
			.cacheOnDisc()
			.build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
			.discCache(isSDCache ?
					new UnlimitedDiscCache(cacheDir) : cacheDir != null ?
					new TotalSizeLimitedDiscCache(cacheDir, DISC_CACHE_SIZE) : null)
			.defaultDisplayImageOptions(defaultOptions)
			.memoryCache(new UsingFreqLimitedMemoryCache(MEM_CACHE_SIZE))
			.memoryCacheExtraOptions(MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)
			.denyCacheImageMultipleSizesInMemory()
			.threadPoolSize(THREAD_POOL_SIZE)
			.threadPriority(Thread.NORM_PRIORITY - 1)
			.imageDownloader(new URLConnectionImageDownloader(CONN_TIMEOUT, READ_TIMEOUT))
			.build();
		imageLoader.init(config);
	}
}

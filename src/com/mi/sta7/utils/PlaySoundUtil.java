package com.mi.sta7.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

import com.mi.sta7.R;

public class PlaySoundUtil {
	private SoundPool soundPoolDisLike;
	private SoundPool soundPoolLike;
	private SoundPool shakePool;
	public final static int SOUND_EXPLOSION = 1;
	public final int SOUND_YOU_WIN = 2;
	public final int SOUND_YOU_LOSE = 3;
	private static Context context;
	private int flag1, flag2;
	private int music;
	private int music2;
	private int shakeMusic;

	public PlaySoundUtil(Context context) {
		PlaySoundUtil.context = context;
	}

	public void playDisLikeSound() {
		soundPoolDisLike = new SoundPool(10, AudioManager.STREAM_SYSTEM, 10);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质
		music = soundPoolDisLike.load(context, R.raw.xu, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		soundPoolDisLike
				.setOnLoadCompleteListener(new OnLoadCompleteListener() {
					@Override
					public void onLoadComplete(SoundPool soundPool,
							int sampleId, int status) {
						flag2 = soundPool.play(music, getVolumnRatio(), getVolumnRatio(), 0, -1, 1);
						soundPool.unload(flag2);
					}
				});
	}

	public void playLikeSound() {
		soundPoolLike = new SoundPool(10, AudioManager.STREAM_SYSTEM, 10);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质
		music2 = soundPoolLike.load(context, R.raw.coins, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		soundPoolLike.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				Log.i("hck", " playLikeSound");
				flag1 = soundPool.play(music2,getVolumnRatio(), getVolumnRatio(), 0, -1, 1);
				soundPool.unload(flag1);
			}
		});
	}

	public void playShakeSound() {
		shakePool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质
		shakeMusic = shakePool.load(context, R.raw.coins, 1); // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		shakePool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				flag1 = soundPool.play(shakeMusic, getVolumnRatio(), getVolumnRatio(), 0, -1, 1);
				soundPool.unload(flag1);
			}
		});

	}

	public float getVolumnRatio() {
		AudioManager am = (AudioManager) context
				.getSystemService(context.AUDIO_SERVICE);
		float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_RING);
		float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_RING);
		Log.i("hck", audioCurrentVolumn / audioMaxVolumn + "");
		return audioCurrentVolumn / audioMaxVolumn;
	}

	public void stopLike() {
		if (soundPoolLike != null) {
			soundPoolLike.stop(music2);
			soundPoolLike.release();
			soundPoolLike = null;
			System.gc();
		}
	}

	public void stopDisLike() {
		if (soundPoolDisLike != null) {
			soundPoolDisLike.stop(music);
			soundPoolDisLike.release();
			soundPoolDisLike = null;
			System.gc();
		}
	}

	public void stopShake() {
		if (shakePool != null) {
			shakePool.stop(shakeMusic);
			shakePool.release();
			shakePool = null;
			System.gc();
		}
	}
}

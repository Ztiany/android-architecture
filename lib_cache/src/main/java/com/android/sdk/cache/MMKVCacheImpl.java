package com.android.sdk.cache;

import android.content.Context;
import android.util.Log;

import com.tencent.mmkv.MMKV;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ztiany
 * Email: ztiany3@gmail.com
 * Date : 2018-11-01 11:25
 */
public class MMKVCacheImpl implements CacheManager {

    private static final String TAG = MMKVCacheImpl.class.getSimpleName();

    private static final AtomicBoolean FLAT = new AtomicBoolean(false);

    private final MMKV mMmkv;

    public MMKVCacheImpl(Context context, String mmkvId) {
        this(context, mmkvId, false);
    }

    public MMKVCacheImpl(Context context, String mmkvId, boolean multiProcess) {

        if (FLAT.compareAndSet(false, true)) {
            String rootDir = MMKV.initialize(context.getApplicationContext());
            Log.d(TAG, "MMKV initialized and rootDir is: " + rootDir);
        }

        int mode = multiProcess ? MMKV.MULTI_PROCESS_MODE : MMKV.SINGLE_PROCESS_MODE;
        mMmkv = MMKV.mmkvWithID(mmkvId, mode);
    }
    @Override
    public void putEntity(String key, Object entity, long cacheTime) {
        CacheEntityImpl.putEntity(key, entity, cacheTime, this);
    }

    @Override
    public void putEntity(String key, Object entity) {
        CacheEntityImpl.putEntity(key, entity, 0, this);
    }

    @Override
    public <T> T getEntity(String key, Class<T> clazz) {
        return CacheEntityImpl.getEntity(key, clazz, this);
    }

    @Override
    public <T> List<T> getEntities(String key, Class<T> clazz) {
        return CacheEntityImpl.getEntities(key, clazz, this);
    }

    @Override
    public void putString(String key, String value) {
        mMmkv.encode(key, value);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return mMmkv.decodeString(key, defaultValue);
    }

    @Override
    public String getString(String key) {
        return mMmkv.decodeString(key);
    }

    @Override
    public void putLong(String key, long value) {
        mMmkv.encode(key, value);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return mMmkv.decodeLong(key, defaultValue);
    }

    @Override
    public void putInt(String key, int value) {
        mMmkv.encode(key, value);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return mMmkv.decodeInt(key, defaultValue);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        mMmkv.encode(key, value);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return mMmkv.decodeBool(key, defaultValue);
    }

    @Override
    public void remove(String key) {
        mMmkv.removeValueForKey(key);
    }

    @Override
    public void clearAll() {
        mMmkv.clear();
    }

}

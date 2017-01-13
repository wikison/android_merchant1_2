package com.zemult.merchant.view.common;

import android.content.Context;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.GlideModule;
import com.bumptech.glide.request.target.ViewTarget;
import com.zemult.merchant.R;
import com.zemult.merchant.config.Constants;

/**
 * Created by admin on 2016/6/24.
 */
public class MGlideMoudle implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        ViewTarget.setTagId(R.id.glide_tag_id);
        int cacheSize100MegaBytes = 104857600;
        String downloadDirectoryPath = Environment.getExternalStorageDirectory().getPath()+"/"+ Constants.ImageLoad_CACHE_DIR;
        builder.setDiskCache(
                new DiskLruCacheFactory( downloadDirectoryPath, cacheSize100MegaBytes )
        );
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
    }
}

/*
 *  Created by Azamov X ㋡ on 11/21/23, 2:02 AM
 *  Copyright (c) 2023 . All rights reserved.
 *  Last modified 11/21/23, 2:02 AM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

@GlideModule
class GlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.setDefaultRequestOptions(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey(System.currentTimeMillis().toShort())),
        )
    }

    companion object {
        private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        val factory = OkHttpUrlLoader.Factory(okHttpClient)
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            factory,
        )
    }
}

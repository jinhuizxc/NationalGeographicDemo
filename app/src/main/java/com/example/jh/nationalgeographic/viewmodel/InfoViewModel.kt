package com.example.jh.nationalgeographic.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.example.jh.nationalgeographic.data.Resource
import com.example.jh.nationalgeographic.data.model.Detail
import com.example.jh.nationalgeographic.utlis.GlideCacheUtil

/**
 * Created by wheat7 on 2017/9/27.
 */

class InfoViewModel : ViewModel() {

    var mImageCache : MutableLiveData<String> = MutableLiveData<String>()

    fun getImageCache(context: Context) : MutableLiveData<String> {

        mImageCache.value = GlideCacheUtil.getInstance().getCacheSize(context)
        return mImageCache
    }
}
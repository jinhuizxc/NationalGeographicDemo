package com.example.jh.nationalgeographic.ui.adapter

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.jh.nationalgeographic.R
import com.example.jh.nationalgeographic.data.WebRepository
import com.example.jh.nationalgeographic.data.model.Album
import com.example.jh.nationalgeographic.data.model.Item
import com.example.jh.nationalgeographic.databinding.ItemItemBinding
import com.example.jh.nationalgeographic.databinding.ViewEmptyBinding
import com.example.jh.nationalgeographic.databinding.ViewRecyclerLoadingBinding
import com.example.jh.nationalgeographic.ui.activity.DetailActivity

/**
 * Created by wheat7 on 2017/9/13.
 *
 * 关于适配器几个重要的方法：
 * 请求数据时依次执行
 * 1.getItemViewType
 * 2.onCreateViewHolder
 * 3.onBindViewHolder
 *
 * 当用SwipeRefreshLayout再次请求数据刷新的时候方法执行
 * 1.getItemViewType
 * 2.onBindViewHolder ，  onCreateViewHolder 方法不在执行。
 *
 */
class ItemAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mData: MutableList<Album> = ArrayList<Album>()
    private val TYPE_ITEM = 1
    private val TYPE_FOOTER = 2
    //空的layout是为了防止getItemCount()+1后没有数据的时候返回一个空的itemView
    private val TYPE_EMPTY = 3
    private var mContext: Context? = null

    // 因为kotlin中的类定义同时也是构造函数，这个时候是不能进行操作的，
    // 所以kotlin增加了一个新的关键字init用来处理类的初始化问题，
    // init模块中的内容可以直接使用构造函数的参数。
    init {
        mContext = context
    }

    fun getRealCount(): Int {
        return mData.size
    }

    // 请求数据
    fun setData(data: MutableList<Album>) {
        mData = data
        notifyDataSetChanged()
    }

    // 添加数据
    fun addData(newData: MutableList<Album>) {
        mData.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (mData.size > 0) {
            if (position + 1 == itemCount) {
                return TYPE_FOOTER
            } else if (mData.size > 0) {
                return TYPE_ITEM
            }
        }
        return TYPE_EMPTY
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ItemViewHolder) {
            if (mData.size != 0) {
                holder.binding!!.title = mData.get(position).title
                Glide.with(mContext)
                        .load(mData.get(position).url).crossFade()
                        .into(holder.binding?.imgItem)
                holder.binding!!.itemDaily.setOnClickListener { view ->
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener!!.onClick(this, position, view, holder, mData)
                    }
                }

            }
        }
    }

    private var footerViewHolder: FooterViewHolder? = null
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        if (viewType == TYPE_ITEM) {
            val binding = DataBindingUtil
                    .inflate<ItemItemBinding>(LayoutInflater.from(parent?.context), R.layout.item_item,
                            parent, false)
            return ItemViewHolder(binding)
        } else if (viewType == TYPE_FOOTER) {
            val binding = DataBindingUtil
                    .inflate<ViewRecyclerLoadingBinding>(LayoutInflater.from(parent?.context), R.layout.view_recycler_loading,
                            parent, false)
            footerViewHolder = FooterViewHolder(binding)
            return footerViewHolder
        }
        val binding = DataBindingUtil
                .inflate<ViewEmptyBinding>(LayoutInflater.from(parent?.context), R.layout.view_empty,
                        parent, false)

        return EmptyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mData.size + 1
    }

    // EmptyViewHolder
    class EmptyViewHolder(binding: ViewEmptyBinding) : RecyclerView.ViewHolder(binding.root)

    // ItemViewHolder
    class ItemViewHolder(binding: ItemItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var binding: ItemItemBinding? = null
        init {
            this.binding = binding
        }
    }

    // FooterViewHolder
    class FooterViewHolder(binding: ViewRecyclerLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        var binding: ViewRecyclerLoadingBinding? = null

        init {
            this.binding = binding
        }
    }

    // 是否加载更多的时候，更改布局
    fun setIsLoading() {
        footerViewHolder?.binding?.textLoading?.text = "正在加载更多..."
        footerViewHolder?.binding?.progressLoading?.visibility = View.VISIBLE
    }

    fun setOnNoLoadMore() {
        footerViewHolder?.binding?.textLoading?.text = "没有更多了"
        footerViewHolder?.binding?.progressLoading?.visibility = View.GONE
    }

    // 是否加载更多的时候，如果请求失败的方法
    fun setNetError() {
        Log.d("NationalGeographic", "setNetError")
        footerViewHolder?.binding?.textLoading?.text = "加载失败，点击重试"
        footerViewHolder?.binding?.viewLoading?.setOnClickListener({ if (mOnReloadClickListener != null) mOnReloadClickListener!!.onClick() })
        footerViewHolder?.binding?.progressLoading?.visibility = View.GONE
    }


    interface OnReloadClickListener {
        fun onClick()
    }
    //网络问题重新加载时点击回调
    private var mOnReloadClickListener: OnReloadClickListener? = null

    fun setOnReloadClickListener(onReloadClickListener: OnReloadClickListener) {
        mOnReloadClickListener = onReloadClickListener
    }


    // item点击事件的接口
    interface OnItemClickListener {
        fun onClick(adapter: ItemAdapter, position: Int, view: View, itemViewHolder: ItemViewHolder, data: MutableList<Album>)
    }

    //点击事件回调
    private var mOnItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }


}
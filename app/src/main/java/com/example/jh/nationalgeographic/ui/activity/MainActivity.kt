package com.example.jh.nationalgeographic.ui.activity

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.jh.nationalgeographic.R
import com.example.jh.nationalgeographic.data.Resource
import com.example.jh.nationalgeographic.data.model.Album
import com.example.jh.nationalgeographic.data.model.Detail
import com.example.jh.nationalgeographic.data.model.Item
import com.example.jh.nationalgeographic.databinding.ActivityMainBinding
import com.example.jh.nationalgeographic.ui.adapter.ItemAdapter
import com.example.jh.nationalgeographic.ui.listener.LoadMoreRecyclerOnScrollListener
import com.example.jh.nationalgeographic.viewmodel.DetailViewModel
import com.example.jh.nationalgeographic.viewmodel.ItemViewModel

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<ActivityMainBinding>(), SwipeRefreshLayout.OnRefreshListener, ItemAdapter.OnItemClickListener {

    var mItemViewModel: ItemViewModel? = null
    // 适配器
    var mAdapter: ItemAdapter? = ItemAdapter(this)
    private var mCurrentPage: Int = 1

    private val SUCCESS: Int = 1
    private val ERROR: Int = 2
    //private val LOADING : Int = 3

    var mProgressDialog: ProgressDialog? = null

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog?.setMessage("正在加载...")
        /**
         * dialog.setCancelable(false);
         * dialog弹出后会点击屏幕或物理返回键，dialog不消失
         *
         * dialog.setCanceledOnTouchOutside(false);
         * dialog弹出后会点击屏幕，dialog不消失；点击物理返回键dialog消失
         */
        mProgressDialog?.setCanceledOnTouchOutside(false)
        recycler.adapter = mAdapter
        val layoutManager = LinearLayoutManager(this)
        recycler.layoutManager = layoutManager
        // 适配器item的点击事件
        mAdapter?.setOnItemClickListener(this)
        mItemViewModel = ViewModelProviders.of(this).get(ItemViewModel::class.java)

        // 拖动布局才会执行的方法
        swipe_refresh.setOnRefreshListener(this)
        // 加载数据？！
        onRefresh()

        // recycleView 的滑动监听方法
//        recycler.setOnScrollListener(object : RecyclerView.OnScrollListener(){
//            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//            }
//
//            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//            }
//        })

        recycler.setOnScrollListener(object : LoadMoreRecyclerOnScrollListener(layoutManager) {
            override fun onLoadMore(current_page: Int) {
                mCurrentPage = current_page
                getMoreItem()
            }
        })

        var clickTime: Long = 0
        title_main.setOnClickListener({
            if (System.currentTimeMillis() - clickTime > 2000) {
                Toast.makeText(this, "再按一次回到顶部", Toast.LENGTH_SHORT).show()
                clickTime = System.currentTimeMillis()
            } else {
                recycler.smoothScrollToPosition(0)
            }
        })

        // toolbar的右侧按钮点击事件
        ic_more.setOnClickListener({ view ->
            val intent: Intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        })
    }

    private fun getMoreItem() {
        mItemViewModel?.getItem(mCurrentPage)?.observe(this@MainActivity, Observer<Resource<Item?>?> { t ->
            Log.d("NationalGeographic", "onChanged")
            mAdapter?.setIsLoading()
            if (t!!.mStatus == SUCCESS) {
                mAdapter?.addData(t.mData!!.album)
            } else {
                mAdapter?.setNetError()
                mAdapter?.setOnReloadClickListener(object : ItemAdapter.OnReloadClickListener {
                    override fun onClick() {
                        Toast.makeText(this@MainActivity, "加载失败，请检查网络重试", Toast.LENGTH_SHORT).show()
                        getMoreItem()
                    }
                })
            }
        })
    }

    /**
     * 适配器的点击事件
     */
    //在点击的时候进行网络请求，成功后传递数据到DetailActivity，避免了在DetailActivity进行网络请求没有请求到时没有数据填充
    private var mDetailViewModel: DetailViewModel? = null
    override fun onClick(adapter: ItemAdapter, position: Int, view: View, itemViewHolder: ItemAdapter.ItemViewHolder, data: MutableList<Album>) {
        mProgressDialog?.show()
        mDetailViewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        mDetailViewModel!!.getDetail(data.get(position).id!!)!!.observe(this, object : Observer<Resource<Detail?>?> {
            override fun onChanged(t: Resource<Detail?>?) {
                if (t!!.mStatus == SUCCESS) {
                    val intent: Intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtra("DETAIL", t!!.mData!!)
                    this@MainActivity.startActivity(intent)
                    mProgressDialog?.dismiss()
                } else {
                    Toast.makeText(this@MainActivity, "加载失败，请检查网络重试", Toast.LENGTH_SHORT).show()
                    mProgressDialog?.dismiss()
                }
            }
        })
    }

    override fun onRefresh() {
        swipe_refresh.isRefreshing = true
        mItemViewModel?.getItem(1)?.observe(this, Observer<Resource<Item?>?> { t ->
            if (t!!.mStatus == SUCCESS) {
                view_error.visibility = View.GONE
                swipe_refresh.isRefreshing = false
                mAdapter?.setData(t.mData!!.album)
            }
            if (t.mStatus == ERROR) {
                swipe_refresh.isRefreshing = false
                if (mAdapter?.getRealCount() == 0) {
                    view_error.visibility = View.VISIBLE
                    // kotlin——简化点击事件
//                    view_error.setOnClickListener {
//                        Toast.makeText(this@MainActivity, "asdfasdf", Toast.LENGTH_SHORT).show()
//                    }
                    view_error.setOnClickListener(View.OnClickListener {
                        // 请求数据失败，点击error的view再次请求数据
                        onRefresh()
                    })
                }
                Toast.makeText(this@MainActivity, "加载失败，请检查网络重试", Toast.LENGTH_SHORT).show()
            }
        })
    }

    var exitTime: Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show()
            exitTime = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }
    }
}

package com.example.jh.nationalgeographic.ui.adapter

import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.example.jh.nationalgeographic.R
import com.github.chrisbanes.photoview.PhotoView
import com.example.jh.nationalgeographic.data.model.Detail

/**
 * Created by wheat7 on 2017/9/17.
 * https://github.com/wheat7
 */

class DetailPagerAdapter() : PagerAdapter() {

    private var mData: Detail? = null

    // 设置数据
    fun setData(data: Detail) {
        mData = data
        notifyDataSetChanged()
    }

    // 复写父类PagerAdapter 的instantiateItem
    override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val photoView = PhotoView(container?.getContext())
        Glide.with(container?.getContext())
                .load(mData?.picture!!.get(position)
                        .url).into(photoView)

        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        container?.addView(photoView, params)
        photoView.setOnClickListener(View.OnClickListener {
            if (mOnPageClickListener != null) {
                mOnPageClickListener!!.onClick()
            }
        })
        return photoView
    }

    /**
     *  java.lang.UnsupportedOperationException:
     *  Required method destroyItem was not overridden
     *  需要有这个方法！，不然会报异常
     */
    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
//        super.destroyItem(container, position, `object`)
        container!!.removeView(`object` as View)
    }

    // 重写PagerAdapter 的2个方法
    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }
    override fun getCount(): Int {
        if (mData != null) {
            return mData?.picture!!.size
        }
        return 0
    }





    // 图片点击事件的处理
    public interface OnPageClickListener {
        fun onClick()
    }

    private var mOnPageClickListener: OnPageClickListener? = null

    public fun setonPageClickListener(listener: OnPageClickListener) {
        mOnPageClickListener = listener
    }


}

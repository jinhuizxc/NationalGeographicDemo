package com.example.jh.nationalgeographic.ui.activity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import com.example.jh.nationalgeographic.databinding.ActivityCollectionBinding
import com.example.jh.nationalgeographic.R
import com.example.jh.nationalgeographic.data.Collects
import com.example.jh.nationalgeographic.data.model.Detail
import com.example.jh.nationalgeographic.ui.activity.DetailActivity
import com.example.jh.nationalgeographic.ui.adapter.CollectionAdapter
import kotlinx.android.synthetic.main.activity_collection.*


/**
 * Created by wheat7 on 2017/9/26.
 * https://github.com/wheat7
 */

class CollectionActivity : BaseActivity<ActivityCollectionBinding>(),
        CollectionAdapter.OnCollectClickListener,
        CollectionAdapter.OnItemClickListener {

    var mAdapter: CollectionAdapter = CollectionAdapter(this)
    var mData: Detail? = null
    private val REQUEST_INFO = 100

    override val layoutId: Int
        get() = R.layout.activity_collection

    override fun initView(savedInstanceState: Bundle?) {
        mData = this.intent.extras.getSerializable("DETAIL") as Detail;
        if (mData != null) {
            mAdapter.setData(mData!!)
        }
        recycler_collection.adapter = mAdapter
        recycler_collection.layoutManager = LinearLayoutManager(this)
        mAdapter.setOnCollectClickListener(this)
        mAdapter.setOnItemClickListener(this)
        // 返回键
        back_collection.setOnClickListener({
            onBackPressed()
        })
    }

    override fun onItemClick(adapter: CollectionAdapter, position: Int, view: View, collectionViewHolder: CollectionAdapter.CollectionViewHolder, data: Detail) {
        val intent: Intent = Intent(this@CollectionActivity, DetailActivity::class.java)
        intent.putExtra("DETAIL", Collects.getDetail())
        intent.putExtra("POS", position)
        // 带返回值跳转
        this@CollectionActivity.startActivityForResult(intent, REQUEST_INFO)
    }

    override fun onCollectClick(adapter: CollectionAdapter, position: Int, view: View, collectionViewHolder: CollectionAdapter.CollectionViewHolder, data: Detail) {
        // 是否需要取消收藏的判断
        showDialog(data, position);

//        从adapter中remove会出现错乱问题 直接重置data 下个版本解决
//        mAdapter.removeItem(position)

        //属性动画会有item view错乱问题 预留 后续版本解决
//        val animatorSet = AnimatorSet()
//        val transY = ObjectAnimator.ofFloat(view, "translationY", 300f)
//        val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
//        animatorSet.duration = 400
//        animatorSet.interpolator = DecelerateInterpolator()
//        animatorSet.play(transY).with(alpha)
//        animatorSet.start()
//        animatorSet.addListener(object : Animator.AnimatorListener {
//            override fun onAnimationStart(animator: Animator) {
//
//            }
//
//            override fun onAnimationEnd(animator: Animator) {
//                Collects.deleteItem(data.picture!!.get(position).id)
////                mAdapter.removeItem(position)
//                mAdapter.setData(Collects.getDetail())
//                view.animate().alpha(1f)
//                view.animate().translationY(-300f)
//
//                if (mAdapter.itemCount == 0) {
//                    this@CollectionActivity.finish()
//                }
//            }
//
//            override fun onAnimationCancel(animator: Animator) {
//
//            }
//
//            override fun onAnimationRepeat(animator: Animator) {
//
//            }
//        })
    }

    /**
     * 是否需要取消收藏的判断
     */
    private fun showDialog(data: Detail, position: Int) {
        val dialog = AlertDialog.Builder(this).create()
        dialog.show()
        if (dialog.window == null) return
        dialog.window!!.setContentView(R.layout.back)//设置弹出框加载的布局(自定义dialog)
        val cancel = dialog.findViewById<View>(R.id.btn_cancle) as Button
        val sure = dialog.findViewById<View>(R.id.btn_sure) as Button

        cancel.setOnClickListener { dialog.dismiss() }
        sure.setOnClickListener {
            Collects.deleteItem(data.picture!!.get(position).id)
            mAdapter.setData(Collects.getDetail())
            dialog.dismiss()
            // 如果适配器为空则销毁界面
            if (mAdapter.itemCount == 0) {
                this@CollectionActivity.finish()
            }
        }

    }

    // 处理返回结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_INFO && resultCode == Activity.RESULT_OK) {
            // 这里data是要回传的数据，通常思维是要用data，并没有用这个data，
            // 之所以用带返回值跳转是——为了得到当前收藏的项
            mAdapter.setData(Collects.getDetail())
            if (mAdapter.itemCount == 0) {
                this@CollectionActivity.finish()
            }
        }
    }
}
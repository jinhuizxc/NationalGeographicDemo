package com.example.jh.nationalgeographic.ui.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import com.example.jh.nationalgeographic.databinding.ActivitySplashBinding
import com.example.jh.nationalgeographic.R
import com.example.jh.nationalgeographic.data.Collects
import kotlinx.android.synthetic.main.activity_splash.*


/**
 * Created by wheat7 on 2017/9/13.
 *
 * 学习前辈的国家地理项目，用kotlin编写！
 * 2017/10/13 在升级了android studio 后运行测试可以运行！
 */
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_splash

    override fun initView(savedInstanceState: Bundle?) {
        // api 大于22
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }
        val anim = ObjectAnimator.ofFloat(fl_splash, "alpha", 1f, 0f)
        anim.duration = 3000
        anim.interpolator = DecelerateInterpolator()
        anim.startDelay = 1000
        anim.start()
        // kotlin 添加事件的监听——object：
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                val intent: Intent = Intent(this@SplashActivity, MainActivity::class.java)
                this@SplashActivity.startActivity(intent)
                this@SplashActivity.finish()
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
    }
}

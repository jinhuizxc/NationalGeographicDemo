package com.example.jh.nationalgeographic.ui.activity

import android.os.Bundle
import com.example.jh.nationalgeographic.R
import com.example.jh.nationalgeographic.databinding.ActivityAboutBinding
import com.example.jh.nationalgeographic.ui.activity.BaseActivity

import kotlinx.android.synthetic.main.activity_about.*


/**
 * Created by wheat7 on 2017/9/27.
 */
class AboutActivity : BaseActivity<ActivityAboutBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_about

    override fun initView(savedInstanceState: Bundle?) {
        about_back.setOnClickListener({
            onBackPressed()
        })
    }
}
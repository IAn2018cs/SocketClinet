package cn.ian2018.socketclinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cn.ian2018.socketclinet.R

/**
 * Created by chenshuai on 2020/8/14
 */
class ChooseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)
    }

    fun chart(view: View) {
        ChooseChartActivity.start(this)
    }

    fun groupChart(view: View) {
        ChooseGroupActivity.start(this)
    }

    fun createGroup(view: View) {
        CreateGroupActivity.start(this)
    }

    fun joinGroup(view: View) {
        JoinGroupActivity.start(this)
    }

    companion object {

        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, ChooseActivity::class.java)
            context.startActivity(intent)
        }
    }
}
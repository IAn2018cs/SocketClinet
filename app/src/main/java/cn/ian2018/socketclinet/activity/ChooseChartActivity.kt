package cn.ian2018.socketclinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ian2018.socketclinet.R
import cn.ian2018.socketclinet.adapter.UserAdapter
import cn.ian2018.socketclinet.api.E2eeApi
import cn.ian2018.socketclinet.api.bean.UserData
import kotlinx.android.synthetic.main.activity_choose_chart.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 * Created by chenshuai on 2020/8/7
 */
class ChooseChartActivity : AppCompatActivity() {

    private val userAdapter = UserAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_chart)

        userAdapter.setOnItemClick { userData: UserData -> MainActivity.start(this@ChooseChartActivity, userData) }

        recycler.apply {
            layoutManager = LinearLayoutManager(this@ChooseChartActivity)
            adapter = userAdapter
        }

        swLayout.setOnRefreshListener {
            loadData()
        }

        swLayout.isRefreshing = true

        loadData()
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.Main).launch {
            val queryOnline = E2eeApi.queryOnline()
            queryOnline?.data?.let {
                userAdapter.setList(it)
            }
            swLayout.isRefreshing = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, ChooseChartActivity::class.java)
            context.startActivity(starter)
        }
    }
}
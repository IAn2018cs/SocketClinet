package cn.ian2018.socketclinet.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.ian2018.socketclinet.R
import cn.ian2018.socketclinet.adapter.GroupAdapter
import cn.ian2018.socketclinet.api.E2eeApi
import cn.ian2018.socketclinet.api.bean.GroupData
import cn.ian2018.socketclinet.util.SPUtil
import kotlinx.android.synthetic.main.activity_choose_group.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by chenshuai on 2020/8/14
 */
class ChooseGroupActivity : AppCompatActivity() {

    private val groupAdapter = GroupAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_group)

        groupAdapter.setOnItemClick { groupData: GroupData -> MainActivity.start(this@ChooseGroupActivity, groupData) }

        recycler.apply {
            layoutManager = LinearLayoutManager(this@ChooseGroupActivity)
            adapter = groupAdapter
        }

        swLayout.setOnRefreshListener {
            loadData()
        }

        swLayout.isRefreshing = true

        loadData()
    }

    private fun loadData() {
        CoroutineScope(Dispatchers.Main).launch {
            val groupResult = E2eeApi.queryGroup(SPUtil.getId(this@ChooseGroupActivity))
            groupResult?.data?.let {
                groupAdapter.setList(it)
            }
            swLayout.isRefreshing = false
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ChooseGroupActivity::class.java)
            context.startActivity(intent)
        }
    }
}
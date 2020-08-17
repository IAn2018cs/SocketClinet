package cn.ian2018.socketclinet.api

import cn.ian2018.socketclinet.AppContext
import cn.ian2018.socketclinet.api.bean.*
import cn.ian2018.socketclinet.util.Logger
import com.amber.lib.net.Method
import com.amber.lib.net.NetManager
import com.amber.lib.net.Params
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Created by chenshuai on 2020-05-08
 */
object E2eeApi {

    private const val PRODUCE_IP = "111.229.253.137"
    private const val TEST_IP = "192.168.30.157"
    const val IP = PRODUCE_IP
    private const val PRODUCE_URL = "http://$IP/e2ee/"
    private const val TEST_URL = "http://$IP/e2ee/"
    private const val HOST = TEST_URL

    private const val CREATE_GROUP_URL = "${HOST}create.php"
    private const val JOIN_GROUP_URL = "${HOST}join.php"
    private const val QUERY_GROUP_URL = "${HOST}query.php"
    private const val DELETE_GROUP_URL = "${HOST}delete.php"
    private const val QUERY_ONLINE_URL = "${HOST}online.php"


    private fun request(url: String, params: Params? = null, timeout: Long = 10): String {
        return try {
            NetManager.setConnectTimeout(AppContext.context, timeout, TimeUnit.SECONDS)
            NetManager.setReadTimeout(AppContext.context, timeout, TimeUnit.SECONDS)
            NetManager.setWriteTimeout(AppContext.context, timeout, TimeUnit.SECONDS)
            val result = NetManager.getInstance().fastRequestString(
                    AppContext.context,
                    url,
                    Method.GET,
                    null,
                    params
                    //, SecurityController.SIGN_V2 or SecurityController.REQUEST_V2 or SecurityController.RESPONSE_V2
            )
            Logger.d(msg = "request result:${result}")
            result
        } catch (e: Exception) {
            Logger.e(msg = "request error:${e.message}")
            ""
        }
    }

    suspend fun createGroup(owner: String, name: String): CreateGroupResult? {
        return withContext(Dispatchers.IO) {
            createGroupSync(owner, name)
        }
    }

    fun createGroupSync(owner: String, name: String): CreateGroupResult? {
        Logger.d(msg = "owner: $owner, name: $name")
        val result = request(
                CREATE_GROUP_URL,
                Params.create("owner", owner, "name", name)
        )
        return try {
            val createGroupType = object : TypeToken<CreateGroupResult>() {}.type
            val createGroupResult = Gson().fromJson<CreateGroupResult>(result, createGroupType)
            createGroupResult
        } catch (e: Exception) {
            Logger.e(msg = "gson error: ${e.message}")
            null
        }
    }

    suspend fun joinGroup(userId: String, shareCode: String): JoinGroupResult? {
        return withContext(Dispatchers.IO) {
            joinGroupSync(userId, shareCode)
        }
    }

    fun joinGroupSync(userId: String, shareCode: String): JoinGroupResult? {
        val result = request(
                JOIN_GROUP_URL,
                Params.create("userId", userId, "shareCode", shareCode)
        )
        return try {
            val joinGroupType = object : TypeToken<JoinGroupResult>() {}.type
            val joinGroupResult = Gson().fromJson<JoinGroupResult>(result, joinGroupType)
            joinGroupResult
        } catch (e: Exception) {
            Logger.e(msg = "gson error: ${e.message}")
            null
        }
    }

    suspend fun queryGroup(userId: String): QueryGroupResult? {
        return withContext(Dispatchers.IO) {
            queryGroupSync(userId)
        }
    }


    fun queryGroupSync(userId: String): QueryGroupResult? {
        val result = request(
                QUERY_GROUP_URL,
                Params.create("userId", userId)
        )
        return try {
            val queryGroupType = object : TypeToken<QueryGroupResult>() {}.type
            val queryGroupResult = Gson().fromJson<QueryGroupResult>(result, queryGroupType)
            queryGroupResult
        } catch (e: Exception) {
            Logger.e(msg = "gson error: ${e.message}")
            null
        }
    }

    suspend fun deleteGroup(group: Int): DeleteGroupResult? {
        return withContext(Dispatchers.IO) {
            deleteGroupSync(group)
        }
    }

    fun deleteGroupSync(group: Int): DeleteGroupResult? {
        val result = request(
                DELETE_GROUP_URL,
                Params.create("group", group.toString())
        )
        return try {
            val deleteGroupType = object : TypeToken<DeleteGroupResult>() {}.type
            val deleteGroupResult = Gson().fromJson<DeleteGroupResult>(result, deleteGroupType)
            deleteGroupResult
        } catch (e: Exception) {
            Logger.e(msg = "gson error: ${e.message}")
            null
        }
    }

    suspend fun queryOnline(): QueryOnlineResult? {
        return withContext(Dispatchers.IO) {
            queryOnlineSync()
        }
    }

    fun queryOnlineSync(): QueryOnlineResult? {
        val result = request(
                QUERY_ONLINE_URL
        )
        return try {
            val queryOnlineType = object : TypeToken<QueryOnlineResult>() {}.type
            val queryOnlineResult = Gson().fromJson<QueryOnlineResult>(result, queryOnlineType)
            queryOnlineResult
        } catch (e: Exception) {
            Logger.e(msg = "gson error: ${e.message}")
            null
        }
    }
}
package com.anubis.SwissArmyKnife

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import com.alibaba.android.arouter.launcher.ARouter
import com.anubis.SwissArmyKnife.GreenDao.Data
import com.anubis.SwissArmyKnife.R.id.sv_Hint
import com.anubis.SwissArmyKnife.R.id.tv_Hint
import com.anubis.kt_extends.*
import com.anubis.kt_extends.eApp.eAppRestart
import com.anubis.kt_extends.eKeyEvent.eSetKeyDownExit
import com.anubis.kt_extends.eShell.eExecShell
import com.anubis.kt_extends.eTime.eGetCurrentTime
import com.anubis.module_arcfaceft.eArcFaceFTActivity
import com.anubis.module_greendao.eOperationDao
import com.anubis.module_portMSG.ePortMessage
import com.anubis.module_tts.Bean.TTSMode
import com.anubis.module_tts.Bean.VoiceModel
import com.anubis.module_tts.eTTS
import kotlinx.android.synthetic.main.list_edit_item.view.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class MainActivity : Activity() {
    private var APP: app? = null
    private var TTS: eTTS? = null
    private var filePath = ""
    private var file: File? = null
    private var data: Array<String>? = null
    private var Time: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ePermissions.eSetPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA))
        APP = app().get()
        app().get()?.getActivity()!!.add(this)
        TTS = eTTS.initTTS(app().get()!!, app().get()!!.mHandler!!, TTSMode.ONLINE)
        getInfo()
        data = arrayOf("初始化发音", "发音人切换调用", "串口通信1", "数据库插入", "数据库查询", "数据库删除", "动态加载", "AecFaceFT人脸跟踪模块（Intent跳转）", "AecFaceFT人脸跟踪模块（路由转发跳转）", "APP重启","APP重启0", "ROOT权限检测", "执行Shell1", "修改为系统APP1", "正则匹配1", "清除记录")
        init()
    }

    private fun appRestart() {
        for (activity in APP!!.getActivity()!!) {
            activity.finish()
        }
        Handler().postDelayed({
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            startActivity(intent)
        }, 500)
    }

    private fun init() {
        filePath = this.filesDir.path + "SAK_Record.txt"
        file = File(filePath)
        if (file!!.exists()) {
            Handler().post {
                val buf = BufferedReader(FileReader(filePath))
                Hint(buf.readText())
            }
        } else {
            file!!.createNewFile()
        }
        rvList.layoutManager = LinearLayoutManager(this)
        val callback = object : ICallBack {
            override fun CallResult(view: View, itmeID: Int, MSG: String) {
                when (itmeID) {
                    data!!.indexOf("初始化发音") -> TTS!!.setParams().speak("初始化发音调用")
                    data!!.indexOf("发音人切换调用") -> TTS!!.setParams(VoiceModel.EMOTIONAL_MALE).speak("发音人切换,网络优先调用")
                    data!!.indexOf("APP重启") -> Hint("APP重启:${eApp.eAppRestart(this@MainActivity)}")
                    data!!.indexOf("APP重启0") -> Hint("APP重启0:${appRestart()}")

                    data!!.indexOf("串口通信1") -> Hint("串口通讯状态：" + ePortMessage().getInit(this@MainActivity, MSG).MSG())
                    data!!.indexOf("数据库插入") -> Hint("数据库插入：${eOperationDao(this@MainActivity).insertUser(Data("00000", "11111"))}")
                    data!!.indexOf("数据库查询") -> Hint("数据库查询:" + eOperationDao(this@MainActivity).queryAllUser(Data()).size)
                    data!!.indexOf("数据库删除") ->
//                        Hint("数据库操作测试:${OperationDao(this@MainActivity).insertUser(Data("00000","11111")::class.java)}")
                        Hint("数据库删除：${eOperationDao(this@MainActivity).deleteAll(Data("", ""))}")

                    data!!.indexOf("动态加载") -> reflection("com.anubis.SwissArmyKnife.MainActivity")
                    data!!.indexOf("AecFaceFT人脸跟踪模块（Intent跳转）") -> startActivity(Intent(this@MainActivity, Face::class.java))
                    data!!.indexOf("AecFaceFT人脸跟踪模块（路由转发跳转）") -> ARouter.getInstance().build("/face/arcFace").navigation()
                    data!!.indexOf("ROOT权限检测") -> Hint("ROOT权限检测:${eShell.eHaveRoot()}")
                    data!!.indexOf("执行Shell1") -> if (MSG.isNotEmpty()) {
                        Hint("执行Shell:\n" + eExecShell(MSG))
                    }
                    data!!.indexOf("修改为系统APP1") -> {
                        if (MSG.isNotEmpty()) {
                            val shell = "cp -r /data/app/$MSG* /system/priv*"
                            Hint("自定义修改为系统APP:" + eExecShell(shell))
                            Hint("执行Shell:$shell")
                        } else {
                            var shell = " cp -r /data/app/$packageName* /system/priv*"
                            Hint("修改为系统APP:" + eExecShell(shell))
                            Hint("执行Shell:$shell")
                            if (File("/data/app-lib/$packageName-1").exists()) {
                                shell = "mv /data/app-lib/$packageName*/ /system/lib/"
                                Hint("文件夹存在，修改lib数据:" + eExecShell(shell))
                                Hint("执行Shell:$shell")
                            }
                        }
                        Handler().postDelayed({
                            val shell = "chmod -R 755   /system/priv*/$MSG*"
                            Hint("修改文件权限:" + eExecShell(shell))
                            Hint("执行Shell:$shell")
                        }, 2000)
                        Handler().postDelayed({
                            val shell = " rm -rf /data/app/$MSG*"
                            Hint("删除数据遗留:" + eExecShell(shell))
                            Hint("执行Shell:$shell")
                            eShowTip("请重启设备")
                        }, 3500)
                    }
                    data!!.indexOf("正则匹配1") -> Hint("正则匹配：/data/app/$packageName$MSG")
                    data!!.indexOf("清除记录") -> {
                        if (System.currentTimeMillis() - Time > 1000) {
                            Time = System.currentTimeMillis()
                        } else {
                            tv_Hint.text = ""
                            file!!.writeText("")
                            eShowTip("记录已清除")
                        }
                    }
                }
            }
        }
        val myAdapter = MyAdapter(this, data!!, callback)
        rvList.adapter = myAdapter
        eExecShell("mount -o remount,rw rootfs /system/ ")
    }

    private fun Hint(str: String) {
        val Str = "${eGetCurrentTime("MM-dd HH:mm:ss")}： $str\n\n\n"
        eLog(Str, "SAK")
        tv_Hint.append(Str)
        sv_Hint.fullScroll(ScrollView.FOCUS_DOWN)
    }

    class MyAdapter(val mContext: Context, val mDatas: Array<String>, val mCallbacks: ICallBack) : RecyclerView.Adapter<MyAdapter.MyHolder>() {
        var mPosition: Int? = null
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.list_edit_item, parent, false)
            return MyHolder(view)
        }

        override fun getItemCount(): Int {
            return mDatas.size
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            mPosition = position
            holder.setData(mDatas[position])
            holder.itemView.bt_item.setOnClickListener {
                var editContext = ""
                if (it.tag == "1") {
                    editContext = holder.itemView.ed_item.text.toString()
                }
                mCallbacks.CallResult(it, position, editContext)
            }

        }


        inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun setData(data: String) {
                itemView.ed_item.visibility = View.GONE
                if (data.last().toString() == "1") {
                    itemView.ed_item.visibility = View.VISIBLE
                    itemView.bt_item.tag = "1"
                }
                itemView.bt_item.text = data

            }
        }


    }

    fun getInfo() {
        eLog("packageName:$packageName---CPU:${Build.CPU_ABI}")

    }


    fun Context.esExistMainActivity(activity: Class<*>): Boolean {
        val intent = Intent(this, activity)
        val cmpName = intent.resolveActivity(packageManager)
        var flag = false
        if (cmpName != null) { // 说明系统中存在这个activity
            val am: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val taskInfoList = am.getRunningTasks(30) //获取从栈顶开始往下查找的10个activity
            for (taskInfo in taskInfoList) {
                eLog("$taskInfo---" + cmpName)
                if (taskInfo.baseActivity == cmpName) { // 说明它已经启动了
                    flag = true
                    break //跳出循环，优化效率
                }
            }
        }
        return flag
    }

    fun startDetector() {
        val it = Intent(this, eArcFaceFTActivity::class.java)
        ActivityCompat.startActivityForResult(this, it, 3, null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        ePermissions.eSetOnRequestPermissionsResult(this, requestCode, permissions, grantResults)
        if (requestCode != 1) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }

    fun reflection(packName: String) {
        val cls = Class.forName(packName)
        val clsInstance = cls.newInstance()
        val method = cls.getDeclaredMethod("ShowTip", Activity::class.java, String::class.java)
        Hint("获得所有方法:${cls.declaredMethods}--获得方法传入类型：${method.parameterTypes}")
        method.invoke(clsInstance, this@MainActivity, "类动态加载")
    }

    private fun ShowTip(mActivity: Activity, msg: String) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Hint("keyCode:$keyCode")
        eLog("size" + app().get()?.getActivity()!!.size)
        return eSetKeyDownExit(this, keyCode, app().get()?.getActivity(), false, exitHint = "完成退出")
    }

    override fun onDestroy() {
        if (tv_Hint.text.isNotEmpty()) {
            file!!.writeText(tv_Hint.text.toString())
        }
        super.onDestroy()
    }

    interface ICallBack {
        fun CallResult(view: View, numID: Int, MSG: String)
    }

}

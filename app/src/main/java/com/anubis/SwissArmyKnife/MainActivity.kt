package com.anubis.SwissArmyKnife

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.Spinner
import com.alibaba.android.arouter.launcher.ARouter
import com.anubis.SwissArmyKnife.GreenDao.Data
import com.anubis.kt_extends.*
import com.anubis.kt_extends.eKeyEvent.eSetKeyDownExit
import com.anubis.kt_extends.eShell.eExecShell
import com.anubis.kt_extends.eTime.eGetCurrentTime
import com.anubis.module_ewifi.eWiFi
import com.anubis.module_greendao.eGreenDao
import com.anubis.module_portMSG.ePortMSG
import com.anubis.module_tts.Bean.TTSMode
import com.anubis.module_tts.Bean.VoiceModel
import com.anubis.module_tts.eTTS
import com.anubis.module_vncs.eVNC
import com.anubis.utils.util.eToastUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_edit_item.view.*
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class MainActivity : Activity() {
    private var APP: app? = null
    private var TTS: eTTS? = null
    private var filePath = ""
    private var file: File? = null
    private var datas: Array<String>? = null
    private var Time: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ePermissions.eSetPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA))
        APP = app().get()
        app().get()?.getActivity()!!.add(this)
        TTS = eTTS.initTTS(app().get()!!, app().get()!!.mHandler!!, TTSMode.MIX, VoiceModel.MALE)
        datas = arrayOf("sp_bt切换化发音调用", "et_bt串口通信", "bt后台启动_bt后台杀死_bt吐司改变", "btVNC二进制文件执行", "bt数据库插入_bt数据库查询_bt数据库删除", "btAecFaceFT人脸跟踪模块（路由转发跳转）", "bt系统设置权限检测_bt搜索WIFI", "bt创建WIFI热点0_bt创建WIFI热点_bt关闭WIFI热点", "btAPP重启", "et_btROOT权限检测_btShell执行_bt修改为系统APP", "et_bt正则匹配", "bt清除记录")
        init()
    }

    private fun getDigit(str: String): Int {
        var Digit = 0
        for (data in datas!!) {
            if (data.indexOf(str) != -1) {
                Digit = datas!!.indexOf(data)
            }
        }
        return Digit
    }

    var i = 1
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
        val voiceModel = arrayOf(VoiceModel.FEMALE, VoiceModel.MALE, VoiceModel.EMOTIONAL_MALE, VoiceModel.CHILDREN)
        var spID = 0
        val adapter = ArrayAdapter<VoiceModel>(this@MainActivity, android.R.layout.simple_spinner_item, voiceModel)
        val callback = object : ICallBack {
            override fun CallResult(view: View?, itmeID: Int, MSG: String, spinner: Spinner) {
                when (itmeID) {
                    getDigit("初始化发音") -> {
                        spinner.adapter = adapter
                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                spID = position
                            }
                        }
                        when (view?.id) {
                            R.id.bt_item1 -> {
                                TTS = TTS!!.setParams(voiceModel[spID])
                                Handler().postDelayed({ TTS!!.speak("发音人切换发音调用") }, 800)
                            }
                        }
                    }
                    getDigit("VNC") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("VNC二进制文件执行:${eVNC.startVNCs(this@MainActivity)}")
                    }
                    getDigit("后台杀死") -> when (view?.id) {
                        R.id.bt_item1 -> {
                            Hint("后台服务状态：${eApp.eIsServiceRunning(this@MainActivity,MyService::class.java.name)}")
                            Hint("后台启动状态：${startService(Intent(this@MainActivity, MyService::class.java))}")
                        }
                        R.id.bt_item2 -> Hint("后台杀死状态：${eApp.eKillBackgroundProcesses(this@MainActivity, MyService::class.java.name)}")
                        R.id.bt_item3->{
                            eToastUtils.setMsgColor(Color.GREEN)
                            eToastUtils.showShort("Toast测试")
                        }
                    }
                    getDigit("APP重启") -> Hint("APP重启:${eApp.eAppRestart(this@MainActivity)}")
                    getDigit("串口通信") -> Hint("串口通讯状态：" + ePortMSG.MSG(this@MainActivity,if(MSG.isEmpty())"0" else MSG,"/dev/ttyS1"))
//                        Hint("串口通讯状态：" + ePortMSG().getInit(this@MainActivity, MSG ?: "").MSG())
                    getDigit("数据库") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("数据库插入：${eGreenDao(this@MainActivity).insertUser(Data("00000", "11111"))}")
                        R.id.bt_item2 -> Hint("数据库查询:" + eGreenDao(this@MainActivity).queryAllUser(Data()).size)
                        R.id.bt_item3 -> Hint("数据库删除：${eGreenDao(this@MainActivity).deleteAll(Data("", ""))}")
                    }
                    getDigit("系统设置权限检测") -> when (view?.id) {
                        R.id.bt_item1 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Hint("系统设置权限检测：${ePermissions.eSetSystemPermissions(this@MainActivity)}")
                        } else {
                            Hint("安卓版本低于6.0--${Build.VERSION.SDK_INT}")
                        }
                        R.id.bt_item2 -> {
                            Hint("搜索WIFI:")
                            for (wifi in eWiFi.eGetScanWiFi(this@MainActivity)!!) {
                                Hint("SSID:${wifi.SSID}")
                            }
                        }
                    }
                    getDigit("热点") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("创建WIFI热点0:${eWiFi.eCreateWifiHotspot(this@MainActivity)}")
                        R.id.bt_item2 -> Hint("创建WIFI热点:${eWiFi.eCreateWifiHotspot(this@MainActivity)}")
                        R.id.bt_item3 -> Hint("关闭WIFI热点：${eWiFi.eCloseWifiHotspot(this@MainActivity)}")
                    }
                    getDigit("AecFaceFT人脸跟踪模块（路由转发跳转）") -> ARouter.getInstance().build("/face/arcFace").navigation()
                    getDigit("Shell执行") -> when (view?.id) {
                        R.id.bt_item1 -> Hint("ROOT权限检测:${eShell.eHaveRoot()}")
                        R.id.bt_item2 -> Hint("Shell执行：${eShell.eExecShell(MSG)}")
                        R.id.bt_item3 -> {
                            if (MSG.isNotEmpty()) {
                                val shell = "cp -r /datas/app/$MSG* /system/priv*"
                                Hint("自定义修改为系统APP:" + eExecShell(shell))
                                Hint("执行Shell:$shell")
                            } else {
                                var shell = " cp -r /datas/app/$packageName* /system/priv*"
                                Hint("修改为系统APP:" + eExecShell(shell))
                                Hint("执行Shell:$shell")
                                if (File("/datas/app-lib/$packageName-1").exists()) {
                                    shell = "mv /datas/app-lib/$packageName*/ /system/lib/"
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
                                val shell = " rm -rf /datas/app/$MSG*"
                                Hint("删除数据遗留:" + eExecShell(shell))
                                Hint("执行Shell:$shell")
                                eShowTip("请重启设备")
                            }, 3500)
                        }
                    }
                    getDigit("正则匹配") -> Hint("正则匹配：/datas/app/$packageName$MSG")
                    getDigit("清除记录") -> {
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
        val myAdapter = MyAdapter(this, datas!!, callback)
        rvList.adapter = myAdapter
        rvList.setItemViewCacheSize(datas!!.size)
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
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.list_edit_item, parent, false)
            return MyHolder(view)
        }

        override fun getItemCount(): Int {
            return mDatas.size
        }

        //方法执行
        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            mPosition = position
            holder.setData(mDatas[position], position)
            holder.itemView.bt_item1.setOnClickListener {
                val editContext = holder.itemView.et_item1.text.toString()
                mCallbacks.CallResult(it, position, editContext, holder.itemView.sp_item1)
            }
            holder.itemView.bt_item2.setOnClickListener {
                val editContext = holder.itemView.et_item1.text.toString()
                mCallbacks.CallResult(it, position, editContext, holder.itemView.sp_item1)
            }
            holder.itemView.bt_item3.setOnClickListener {
                val editContext = holder.itemView.et_item1.text.toString()
                mCallbacks.CallResult(it, position, editContext, holder.itemView.sp_item1)
            }

        }

        //界面设置 ed_    sp_  bt_x3
        inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun setData(datas: String, position: Int) {
                try {
                    var datas = datas.split("_")
//                    datas=datas.reversed()
                    var btList = ArrayList<String>()
                    for (str in datas) {
                        eLog("split:$str")
                        when (str.substring(0, 2)) {
                            "et" -> itemView.et_item1.visibility = View.VISIBLE
                            "sp" -> {
                                mCallbacks.CallResult(null, position, "", itemView.sp_item1)
                                itemView.sp_item1.visibility = View.VISIBLE
                            }
                            "bt" -> btList.add(str)
                        }
                    }
                    //启动并设置button
                    for (str in btList) {
                        when (btList.indexOf(str)) {
                            0 -> {
                                itemView.bt_item1.visibility = View.VISIBLE
                                itemView.bt_item1.text = eString.eGetNumberPeriod(str, 2, "MAX")
                            }
                            1 -> {
                                itemView.bt_item2.visibility = View.VISIBLE
                                itemView.bt_item2.text = eString.eGetNumberPeriod(str, 2, "MAX")
                            }
                            2 -> {
                                itemView.bt_item3.visibility = View.VISIBLE
                                itemView.bt_item3.text = eString.eGetNumberPeriod(str, 2, "MAX")
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (datas.substring(0, 2) == "bt") {
                        itemView.bt_item1.visibility = View.VISIBLE
                        itemView.bt_item1.text = eString.eGetNumberPeriod(datas, 2, "MAX")
                    }
                }
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        ePermissions.eSetOnRequestPermissionsResult(this, requestCode, permissions, grantResults)
        if (requestCode != 1) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
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
        fun CallResult(view: View?, numID: Int, MSG: String, spinner: Spinner)
    }
}




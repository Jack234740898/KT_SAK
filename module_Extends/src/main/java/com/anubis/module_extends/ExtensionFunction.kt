package com.anubis.kt_extends

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.text.TextUtils.indexOf
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import com.lzy.imagepicker.ImagePicker
import com.lzy.imagepicker.loader.ImageLoader
import com.lzy.imagepicker.view.CropImageView
import com.tencent.bugly.Bugly.enable
import com.tencent.bugly.proguard.p
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.startActivity
import org.json.JSONObject
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.experimental.and
import kotlin.math.log


/**
 * Author  ： AnubisASN   on 2018-07-23 9:12.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * Q Q： 773506352
 * 命名规则定义：
 * Module :  module_'ModuleName'
 * Library :  lib_'LibraryName'
 * Package :  'PackageName'_'Module'
 * Class :  'Mark'_'Function'_'Tier'
 * Layout :  'Module'_'Function'
 * Resource :  'Module'_'ResourceName'_'Mark'
 * /+Id :  'LoayoutName'_'Widget'+FunctionName
 * Router :  /'Module'/'Function'
 * 说明：Toamst扩展函数
 */


fun Context.eShowTip(str: Any, i: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, str.toString(), i).show()
}

/**
 * Log i e扩展函数------------------------------------------------------------------------------------
 */

fun Activity?.eLog(str: Any, TAG: String = "TAG") {
    Log.i(TAG, "${this?.localClassName ?: "eLog"}-：${str.toString()}\n ")
}

fun eLog(str: Any, TAG: String = "TAG") {
    Log.i(TAG, "eLog:${str.toString()}\n ")
}

fun Activity?.eLogE(str: Any, TAG: String = "TAG") {
    Log.e(TAG, "${this?.localClassName ?: "eLogE"}-：${str.toString()}\n ")
}

fun eLogE(str: Any, TAG: String = "TAG") {
    Log.e(TAG, "eLogE:${str.toString()}\n ")
}


/**
 * SharedPreferences数据文件存储扩展-------------------------------------------------------------------
 */
//系统数据文件存储扩展
fun Context.eSetSystemSharedPreferences(key: Any, value: Any): Boolean {
    val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
    val key = key.toString()
    val editor = sharedPreferences.edit()
    when (value) {
        is String -> editor.putString(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
    }
    return editor.commit()
}

//系统数据文件存储读取扩展
fun Context.eGetSystemSharedPreferences(key: String, value: Any = ""): Any {
    val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
    return when (value) {
        is String -> sharedPreferences.getString(key, value).toString()
        is Boolean -> sharedPreferences.getBoolean(key, value)
        is Float -> sharedPreferences.getFloat(key, value)
        is Int -> sharedPreferences.getInt(key, value)
        is Long -> sharedPreferences.getLong(key, value)
        else -> sharedPreferences.getString(key, value as String?)
    }

}

//用户文件数据存储扩展
fun Context.eSetUserPutSharedPreferences(userID: String, key: String, value: Any): Boolean {
    val sharedPreferences = getSharedPreferences(userID, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    when (value) {
        is String -> editor.putString(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
    }
    return editor.commit()
}

//用户文件数据读取扩展
fun Context.eGetUserSharedPreferences(userID: Int, key: String, value: Any = ""): Any {
    val sharedPreferences = getSharedPreferences(userID.toString(), Context.MODE_PRIVATE)
    return when (value) {
        is String -> sharedPreferences.getString(key, value)
        is Boolean -> sharedPreferences.getBoolean(key, value)
        is Float -> sharedPreferences.getFloat(key, value)
        is Int -> sharedPreferences.getInt(key, value)
        is Long -> sharedPreferences.getLong(key, value)
        else -> sharedPreferences.getString(key, value as String?)
    }

}

//首选项数据文件存储扩展
fun Context.eSetDefaultSharedPreferences(key: String, value: Any = ""): Any {
    val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
    val edit = sharedPref.edit()
    return when (value) {
        is String -> sharedPref.getString(key, value)
        is Boolean -> sharedPref.getBoolean(key, value)
        is Float -> sharedPref.getFloat(key, value)
        is Int -> sharedPref.getInt(key, value)
        is Long -> sharedPref.getLong(key, value)
        else -> sharedPref.getString(key, value as String?)
    }
}

//首选项数据文件读取扩展
fun Context.eGetDefaultSharedPreferences(key: String, value: Any = ""): Any {
    var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
    return when (value) {
        is String -> sharedPref.getString(key, value)
        is Boolean -> sharedPref.getBoolean(key, value)
        is Float -> sharedPref.getFloat(key, value)
        is Int -> sharedPref.getInt(key, value)
        is Long -> sharedPref.getLong(key, value)
        else -> sharedPref.getString(key, value as String?)
    }
}


/**
 * Intent Set传递扩展---------------------------------------------------------------------------------
 */
//Intent Get传递扩展
fun Intent.eGetMessage(Sign: String): String = getStringExtra(Sign)


/**
 * Intent Set 捆绑传递扩展----------------------------------------------------------------------------
 */
fun Bundle.eSetMessage(Sign: String, Message: Any = "") = when (Message) {
    is String -> putString(Sign, Message)
    is Int -> putInt(Sign, Message)
    is Float -> putFloat(Sign, Message)
    is Boolean -> putBoolean(Sign, Message)
    is Char -> putChar(Sign, Message)
    is Byte -> putByte(Sign, Message)
    is ArrayList<*> -> putStringArrayList(Sign, Message as ArrayList<String>)
    is IntArray -> putIntArray(Sign, Message)
    else -> {
    }
}


/**
 * String Json解析扩展--------------------------------------------------------------------------------
 */
object eJson {
    //Object Json解析扩展
    fun eGetJsonObject(json: String, resultKey: String) = JSONObject(json).getString(resultKey)

    //Array Json解析扩展
    fun eGetJsonArray(json: String, resultKey: String, i: Int) = JSONObject(json).getJSONArray(resultKey).getJSONObject(i).toString()

    fun eGetJsonArray(json: String, resultKey: String) = JSONObject(json).getJSONArray(resultKey)

}

//音频播放
fun ePlayVoice(context: Context, music: Int, isLoop: Boolean = false) {
    try {
        val mp = MediaPlayer.create(context, music)//重新设置要播放的音频
        mp.isLooping = isLoop
        mp.start()//开始播放
    } catch (e: Exception) {
        e.printStackTrace()//输出异常信息
    }

}


/**
 * App程序扩展类--------------------------------------------------------------------------------------
 */
object eApp {
    //开机自启
    var isSetAutoBoot: Boolean = true
    fun eSetAutoBoot(context: Context, intent: Intent, className: Class<*>) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            eLog("开机启动","SAK")
            isSetAutoBoot=false
            val startServiceIntent = Intent(context,className)
            startServiceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(startServiceIntent)
        }
    }

    //获取所有安装的app
    fun eGetLocalApps(context: Context) = context.packageManager.queryIntentActivities(Intent(Intent.ACTION_MAIN, null), 0)


    //获取APP包名
    fun eGetLocalAppsPackageName(context: Context, appName: String): String? {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = context.packageManager.queryIntentActivities(intent, 0)
        //for循环遍历ResolveInfo对象获取包名和类名
        for (i in 0 until apps.size) {
            val info = apps[i]
            val packageName = info.activityInfo.packageName
            val appsName = info.activityInfo.loadLabel(context.packageManager)
            if (appName == appsName) {
                return packageName
            }
        }
        return null
    }


    //APP重启
    fun eAppRestart(activity: Activity, activityList: ArrayList<Activity>? = null): Boolean {
        try {
            if (activityList != null) {
                for (av in activityList) {
                    av.finish()
                }
            } else {
                activity.finish()
            }
            val LaunchIntent = activity.packageManager.getLaunchIntentForPackage(activity.application.packageName)
            LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(LaunchIntent)
            return true
        } catch (e: Exception) {
            return false
        }

    }

    //    APP包名启动
    fun eAppStart(activity: Activity, packageName: String? = null): Boolean {
        try {
            val LaunchIntent = activity.packageManager.getLaunchIntentForPackage(packageName
                    ?: activity.packageName)
            LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(LaunchIntent)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    //App运行判断
    fun eIsAppRunning(context: Context, packageName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcessInfoList = activityManager.runningAppProcesses ?: return false
        for (processInfo in runningAppProcessInfoList) {
            if (processInfo.processName == packageName && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }

    //Activity运行判断
    fun eActivityWhetherWorked(context: Context, className: String): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val list = am.getRunningTasks(1)
        if (list != null && list.size > 0) {
            val cpn = list[0].topActivity
            if (className == cpn.className) {
                return true
            }
        }
        return false
    }

    //获取当前显示的Activity
    fun eGetShowActivity(context: Context) = context.activityManager.getRunningTasks(1)[0].topActivity

    //判断Activity是否存在任务栈里面
    fun eIsExistMainActivity(context: Context, activity: Class<*>): Boolean {
        val intent = Intent(context, activity)
        val cmpName = intent.resolveActivity(context.packageManager)
        var flag = false
        if (cmpName != null) { // 说明系统中存在这个activity
            val am: ActivityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val taskInfoList = am.getRunningTasks(10) //获取从栈顶开始往下查找的10个activity
            for (taskInfo in taskInfoList) {
                if (taskInfo.baseActivity == cmpName) { // 说明它已经启动了
                    flag = true
                    break //跳出循环，优化效率
                }
            }
        }
        return flag
    }

    //服务运行判断 com.anubis.iva.Service.IVAService
    fun eServiceWhetherWorked(context: Context, className: String): Boolean {
        val myManager = context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningService = myManager.getRunningServices(30) as ArrayList<ActivityManager.RunningServiceInfo>
        for (i in runningService.indices) {
            if (runningService[i].service.className.toString() == className) {
                return true
            }
        }
        return false
    }

    //软件安装判断
    fun eIsAppInstall(packageName: String, mContext: Context): Boolean {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = mContext.packageManager.getPackageInfo(
                    packageName, 0)

        } catch (e: PackageManager.NameNotFoundException) {
            packageInfo = null
            e.printStackTrace()
        }
        return packageInfo != null
    }

    //   首选Summary项动态改变
    //sSummaryDynamicSetting(findPreference("root_screen") as PreferenceScreen)
    //Summary修改
    fun Context.eSummaryModify(group: PreferenceGroup) {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        for (i in 0..group.preferenceCount - 1) {
            val p = group.getPreference(i)
            if (p is PreferenceGroup) {
                eSummaryModify(p)
            } else {
                if ((p is ListPreference) or (p is EditTextPreference)) {
                    val value = sp.all[p.key]
                    p.summary = if (value == null) "" else "$value"
                    p.onPreferenceChangeListener = this as Preference.OnPreferenceChangeListener?
                }
            }
        }
    }
}

/**
 * 正则扩展类-----------------------------------------------------------------------------------------
 */
object eRegular {
    //获取数字
    fun eGetNumber(str: String): Int {
        val regEx = "[^0-9]"
        val p = Pattern.compile(regEx)
        val m = p.matcher(str)
        return (m.replaceAll("").trim { it <= ' ' }).toInt()
    }

    //IP格式判断
    fun eIsIP(ip: String): Boolean {
        val regIP: String = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$"
        return Pattern.compile(regIP).matcher(ip).matches()
    }

    //电话格式判断
    fun eIsPhoneNumber(number: String): Boolean {
        val regExp: String = "^((13[0-9])|(15[^[4,5],\\D])|(16[^6,\\D])|(18[0,1,5-9])|(17[6,7,8]))\\d{8}$"
        val p: Pattern = Pattern.compile(regExp)
        val m: Matcher = p?.matcher(number.toString())
        return m.find()
    }

    //邮编格式判断
    fun eIsZipNO(zipCode: String): Boolean {
        val str = "^[1-9][0-9]{5}$"
        return Pattern.compile(str).matcher(zipCode).matches()
    }

    //邮箱格式判断
    fun eIsEmail(email: String): Boolean {
        if (null == email || "" == email) return false
        val p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")//复杂匹配
        val m = p.matcher(email)
        return m.matches()
    }
}

/**
 * KeyDownExit事件监听扩展类--------------------------------------------------------------------------
 */
object eKeyEvent {
    private var Time: Long = 0
    fun eSetKeyDownExit(activity: Activity, keyCode: Int, activityList: ArrayList<Activity>? = null, systemExit: Boolean = true, hint: String = "再按一次退出", exitHint: String = "APP已退出", ClickTime: Long = 2000): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - Time > ClickTime) {
                activity.eShowTip(hint)
                Time = System.currentTimeMillis()
                false
            } else {
                if (activityList != null) {
                    for (activity in activityList) {
                        activity.finish()
                    }
                }
                activity.eShowTip(exitHint)
                activity.finish()
                if (systemExit) {
                    System.exit(0)
                }
                true
            }
        } else
            false
    }
}


/**
 * 时间扩展类-----------------------------------------------------------------------------------------
 */
object eTime {
    /**
     * 时间差
     * @param endDate 上次更新时间
     * @param nowDate 当前时间
     * @return 天数
     */
    fun eGetTimeDifference(nowDate: Date, endDate: Date, type: String = "hour"): Long {
        val nd = (1000 * 24 * 60 * 60).toLong()
        val nh = (1000 * 60 * 60).toLong()
        val nm = (1000 * 60).toLong()
        val ns = 1000
        // 获得两个时间的毫秒时间差异
        val diff = nowDate.time - endDate.time
        return when (type) {
            "day" -> diff / nd
            "hour" -> diff % nd / nh
            "min" -> diff % nd % nh / nm
            "second" -> diff % nd % nh % nm / ns
            else -> diff % nd / nh
        }
    }

    //获取当前星期
    fun eGetCurrentWeek() = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toString()) {
        "1" -> "星期天"
        "2" -> "星期一"
        "3" -> "星期二"
        "4" -> "星期三"
        "5" -> "星期四"
        "6" -> "星期五"
        "7" -> "星期六"
        else -> "发生错误"
    }

    //获取当前时间   (yyyy-MM-dd HH:mm:ss)
    fun eGetCurrentTime(format: String = "yyyy-MM-dd HH:mm:ss") = SimpleDateFormat(format).format(Date())

    //获取时间戳格式转时间
    fun eGetCuoFormatTime(dateCuo: Long, format: String = "yyyy-MM-dd HH:mm:ss") = SimpleDateFormat(format).format(Date(dateCuo))

}

/**
 * 网络扩展类-----------------------------------------------------------------------------------------
 */
object eNetWork {
    // 判断是否有网
    fun eIsNetworkAvailable(context: Context): Boolean {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // 获取NetworkInfo对象
        val networkInfo = connectivityManager.allNetworkInfo
        if (networkInfo != null && networkInfo.isNotEmpty()) {
            for (i in networkInfo.indices) {
                // 判断当前网络状态是否为连接状态
                if (networkInfo[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
            }
        }
        return false
    }

    //检测网络是否可用
    fun eIsNetworkOnline(): Boolean {
        try {
            val ipProcess = Runtime.getRuntime().exec("ping -c 1 114.114.114.114")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    //计算数据大小
    fun eGetFormatSize(size: Long) = when {
        size < 1 -> "0 K/s"
        size in 1..1023 -> size.toString() + " K/s"
        1024 < size -> (size / 1024).toString() + " M/s"
        else -> "0 K/s"
    }

    // 获取PING延迟
    fun eGetNetDelayTime(): String {
        var delay = String()
        var p: Process? = null
        var output = ""
        try {
            p = Runtime.getRuntime().exec("/system/bin/ping -c 4 " + "www.baidu.com")
            eLog("p$p")
            eLog("inputStream${p.inputStream}")
            eLog("InputStreamReader${InputStreamReader(p.inputStream)}")
            val buf = BufferedReader(InputStreamReader(p!!.inputStream))
            eLog("BufferedReader$buf")
            var str: String? = ""
            while (str != null) {
                str = buf.readLine()
                eLog("str$str")
                output += str
                if (output.contains("avg")) {
                    val i = str.indexOf("/", 20)
                    val j = str.indexOf(".", i)
                    delay = str.substring(i + 1, j)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return delay
    }
}


/**
 * 设备信息扩展类--------------------------------------------------------------------------------------
 */
object eDevice {
    // 获取屏幕密度，宽高
    fun eGetDensityWidthHeight(mContext: Context): Array<Any> {
        val resources = mContext.resources
        val dm = resources.displayMetrics
        val density = dm.density
        val width = dm.widthPixels
        val height = dm.heightPixels
        return arrayOf(density, width, height)
    }

    // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    fun eGetDpToPx(context: Context, dpValue: Float) = (dpValue * context.resources.displayMetrics.density + 0.5f).toInt()

    //获取系统的名称
    fun eGetSysVersionName() = Build.MODEL

    //获取系统的SDK版本
    fun eGetSysSDKVersion() = Build.VERSION.SDK

    //获取系统的无线电固件版本
    fun eGetRadioVersion() = Build.RADIO

    //获得显示
    fun eGetDisPlay() = Build.DISPLAY
}

/**
 * 蓝牙扩展类-----------------------------------------------------------------------------------------
 */
object eBluetooth {
    // 打开蓝牙
    fun eOpenBluetooth(bel_name: String, context: Context) {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            context.eShowTip("该设备不支持蓝牙")
        } else {
            if (mBluetoothAdapter.isEnabled) {
                mBluetoothAdapter.name = bel_name
                eSetDiscoverableTimeout()
            } else {
                mBluetoothAdapter.enable()
                context.eShowTip("已打开蓝牙")
            }
        }
    }

    //关闭蓝牙
    fun eCloseBluetooth(context: Context) {
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter == null) {
            context.eShowTip("该设备不支持蓝牙")
        } else {
            if (!mBluetoothAdapter.isEnabled) {
                mBluetoothAdapter.disable()
                context.eShowTip("已关闭蓝牙")
            }
        }
    }

    //设置开放检测
    fun eSetDiscoverableTimeout(timeout: Int = 0) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        try {
            val setDiscoverableTimeout = BluetoothAdapter::class.java.getMethod("setDiscoverableTimeout", Int::class.javaPrimitiveType)
            setDiscoverableTimeout.isAccessible = true
            val setScanMode = BluetoothAdapter::class.java.getMethod("setScanMode", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            setScanMode.isAccessible = true
            setDiscoverableTimeout.invoke(adapter, timeout)
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, timeout)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


/**
 * Bitmap扩展类--------------------------------------------------------------------------------------
 */
object eBitmap {
    //Bitmap释放
    fun eGcBitmap(bitmap: Bitmap?) {
        var bitmap = bitmap
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle() // 回收图片所占的内存
            bitmap = null
            System.gc() // 提醒系统及时回收
        }
    }

    //Bitmap转Base64工具
    fun eBitmapToBase64(bitmap: Bitmap): String? {
        var baos: ByteArrayOutputStream? = null
        var reslut: String? = null
        try {
            if (bitmap != null) {
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                baos.flush()
                baos.close()
                // 转换为字节数组
                val byteArray = baos.toByteArray()
                // 转换为字符串
                reslut = Base64.encodeToString(byteArray, Base64.DEFAULT)
            } else {
                return null
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                baos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return reslut
    }

    //Base64转Bitmap工具
    fun eBase64ToBitmap(base64String: String): Bitmap {
        val decode = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decode, 0, decode.size)
    }

    //获取预览图
    fun eGetPhoneBitmap(mImageNV21: ByteArray, width: Int, height: Int, rect: Rect = Rect(0, 0, width, height), mCameraID: Int = 1): Bitmap? {
        var mBitmap: Bitmap? = null
        if (mImageNV21 != null) {
            val image = YuvImage(mImageNV21, ImageFormat.NV21, width, height, null)
            val stream = ByteArrayOutputStream()
            image.compressToJpeg(rect, 80, stream)
            val bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
            mBitmap = eRotateMyBitmap(bmp)
            stream.close()
            eGcBitmap(bmp)
        }
        return mBitmap
    }


    //图片旋转
    fun eRotateMyBitmap(bmp: Bitmap, mCameraID: Int = 1): Bitmap {
        //*****旋转一下
        var matrix = Matrix()
        if (mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            matrix.postRotate(270f)
        } else {
            matrix.postRotate(90f)
        }
        return Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
    }
}

/**
 * 字符转换扩展类--------------------------------------------------------------------------------------
 */
object eString {
    //数值段获取
    fun eGetNumberPeriod(str: String, start: Any, end: Any): String {
        val Str = str.trim()
        val Start: Int = when (start) {
            is Int -> start
            is String -> str.indexOf(start) + 1
            else -> {
                0
            }
        }
        val End: Int = when (end) {
            is Int -> end
            is String -> if (end == "MAX") str.length else str.indexOf(end)
            else -> {
                0
            }
        }
        return if (Str.isEmpty()) "" else Str.substring(Start, End).trim()
    }

    //判断是否为中文
    fun eIsChinese(c: Char) = c.toInt() >= 0x4E00 && c.toInt() <= 0x9FA5// 根据字节码判断

    //判断是否有英文
    fun eHasEglish(chars: CharArray): Boolean {
        for (i in chars.indices) {
            if (chars[i].toInt() >= 97 && chars[i].toInt() <= 122) {
                return true
            }
        }
        return false
    }

    //十六进制字符串转字节
    fun eGetHexStringToBytes(hexString: String): ByteArray? {
        var hexString = hexString
        if (hexString == "") {
            return null
        }
        hexString = hexString.toUpperCase()
        val length = hexString.length / 2
        val hexChars = hexString.toCharArray()
        val d = ByteArray(length)
        for (i in 0 until length) {
            val pos = i * 2
            d[i] = (eGetCharToByte(hexChars[pos]).toInt() shl 4 or eGetCharToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return d
    }

    //字符转字节
    fun eGetCharToByte(c: Char) = indexOf("0123456789ABCDEF", c).toByte()

    //字节转字符串
    fun eBytesToHexString(src: ByteArray, lenth: Int): String? {
        val stringBuilder = StringBuilder("")
        if (src.isEmpty()) {
            return null
        }
        for (i in 0 until lenth) {
            val v = src[i] and 0xFF.toByte()
            val hv = Integer.toHexString(v.toInt())
            if (hv.length < 2) {
                stringBuilder.append(0)
            }
            stringBuilder.append(hv)
        }
        return stringBuilder.toString()
    }

    //MD5加密
    fun eEncodeMD5(str: String, digits: Int = 32): String {
        try {
            //获取md5加密对象
            val instance: MessageDigest = MessageDigest.getInstance("MD5")
            //对字符串加密，返回字节数组
            val digest: ByteArray = instance.digest(str.toByteArray())
            val sb: StringBuffer = StringBuffer()
            for (b in digest) {
                //获取低八位有效值
                var i: Int = b.toInt() and 0xff
                //将整数转化为16进制
                var hexString = Integer.toHexString(i)
                if (hexString.length < 2) {
                    //如果是一位的话，补0
                    hexString = "0" + hexString
                }
                sb.append(hexString)
            }
            return if (digits == 32) sb.toString() else sb.toString().substring(8, 24)

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return ""
    }
}

/**
 * 运行权限扩展类--------------------------------------------------------------------------------------
 */
object ePermissions {
    //系统设置修改权限
    fun eSetSystemPermissions(context: Context): Boolean {
        var str=false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:${context.packageName}")
                context.startActivity(intent)
            } else {
                 str=Settings.System.canWrite(context)
            }
        }else{
            str=true
        }
        return str
    }


    //授权判断
    fun eSetPermissions(activity: Activity, permissionsArray: Array<out String>, requestCode: Int = 1): Boolean {
        val permissionsList = ArrayList<String>()
        for (permission in permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission)
            }
        }
        return if (permissionsList.isEmpty()) {
            //未授予的权限为空，表示都授予了
            true
        } else {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(arrayOfNulls<String>(permissionsList.size)), 1)
            false
        }

    }

    //显示授权设置
    fun eSetOnRequestPermissionsResult(activity: Activity, requestCode: Int, permissions: Array<String>, grantResults: IntArray, isPermissionsOKHint: String = "", isPermissionsNoHint: String = "") {
        when (requestCode) {
            1 -> for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    if (isPermissionsOKHint.isEmpty()) {
                        activity.eShowTip(isPermissionsOKHint)
                    }
                } else {
                    if (isPermissionsNoHint.isEmpty()) {
                        activity.eShowTip(isPermissionsNoHint)
                    }
                }
            }
        }
    }
}


/**
 * linux命令扩展类------------------------------------------------------------------------------------
 */
object eShell {
    //判断是否有Root权限
    fun eHaveRoot(): Boolean {
        try {
            Runtime.getRuntime().exec("su").toString()
        } catch (e: Exception) {
            return false
        }
        return true
    }

    //执行命令并且输出结果
    fun eExecShell(shell: String): String {
        var result = ""
        var dos: DataOutputStream? = null
        var dis: DataInputStream? = null
        try {
            val p = Runtime.getRuntime().exec("su")// 经过Root处理的android系统即有su命令
            dos = DataOutputStream(p.outputStream)
            dis = DataInputStream(p.inputStream)
            eLog(shell)
            dos.writeBytes(shell + "\n")
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            var line: String? = ""
            while ((dis.readLine()).apply { line = this } != null) {
                result += line + "\n"
            }
            p.waitFor()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (dos != null) {
                try {
                    dos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (dis != null) {
                try {
                    dis.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    //执行命令但不关注结果输出
    fun eExecShellSilent(shell: String): Int {
        var result = -1;
        var dos: DataOutputStream? = null

        try {
            var p = Runtime.getRuntime().exec("su")
            dos = DataOutputStream(p.outputStream)
            eLog(shell)
            dos.writeBytes(shell + "\n")
            dos.flush()
            dos.writeBytes("exit\n")
            dos.flush()
            p.waitFor()
            result = p.exitValue()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (dos != null) {
                try {
                    dos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return result
    }
}


/**
 * 反射机制动态加载扩展---------------------------------------------------------------------------------
 */
object eReflection {
    ////获取 加载类
    fun eGetClass(packageName: String) = Class.forName(packageName)

    ////获取 实例化类
    fun eGetClassInstance(cls: Class<Any>) = cls.newInstance()

    ////调用方法
    fun eInvokeMethod(cls: Class<Any>, methodName: String) = {
        cls.getDeclaredMethod(methodName, String::class.java)
    }
}





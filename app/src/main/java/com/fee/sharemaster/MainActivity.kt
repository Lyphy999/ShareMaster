package com.fee.sharemaster

import android.content.ComponentName
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.fee.sharemaster.databinding.ActivityMainBinding
import com.fee.sharemaster.wxapi.WXEntryActivity
import common.share.ShareMaster
import common.share.core.AShareData
import common.share.core.AbsEventResultHandler
import java.io.File

class MainActivity : AppCompatActivity(),View.OnClickListener{
    private val TAG = "MainActivity"
    lateinit var activityMainBinding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, " --> onCreate() ")

        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        activityMainBinding.btnWxShare.setOnClickListener(this)
        activityMainBinding.btnSysShare.setOnClickListener(this)
        activityMainBinding.btnAliShare.setOnClickListener(this)
        ShareMaster.initWxSdk(this){
            appId = "wxb4bbf0651d312ab6"
        }

        requestPermissions(arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ),100)
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnWxShare -> {//微信分享
                val resultHandler = object :AbsEventResultHandler(){
                    override fun onEventResp(
                        isEventOk: Boolean,
                        respCode: Int,
                        respSubCode: Int,
                        respMsg: String,
                        theRespAssignId: String
                    ) {

                    }
                }
                resultHandler.eventTransactionId = "分享测试"
                ShareMaster.wxShare(resultHandler){
                    wxEntryActivityClass = WXEntryActivity::class.java
                    shareToScene = AShareData.DataConfigs.SCENE_TO_WX_FAVORITE
                    shareDataType = AShareData.DataConfigs.DATA_TYPE_TEXT
                    shareText = "我想把本段文本给分享出去"
                }
            }
            R.id.btnSysShare ->{//系统原生 的 分享
                ShareMaster.systemShare {
                    shareTitle = "分享到"
                    //分享文本
//                    shareText = "我想把本段文本给分享出去"
//                    shareDataType = AShareData.DATA_TYPE_TEXT
                    //分享链接
//                    shareUrl = "https://www.baidu.com" //分享链接
//                    shareDataType = AShareData.DATA_TYPE_URL
                    //分享图片
                    val rootFile = Environment.getExternalStorageDirectory()
                    val imgFile = File(rootFile, "app_logo.png")
                    val testFile = File(rootFile, "test.text")
                    //有权限了也不能创建文件
//                    if (!testFile.exists()) {
//                        val isSuc = testFile.createNewFile()
//                        if (isSuc) {
//
//                        }
//                    }
                    //android.os.FileUriExposedException: file:///storage/emulated/0/app_logo.png exposed beyond app through ClipData.Item.getUri()
                    //     at android.os.StrictMode.onFileUriExposed(StrictMode.java:2141)
                    //Android7.0 之后 禁止如上
                    //缺点：1、发送方传递的文件路径接收方完全知晓，一目了然，没有安全保障。
                    //2、发送方传递的文件路径接收方可能没有读取权限，导致接收异常。
                    val fileUri = MyFileProvider.getUri(this@MainActivity,imgFile)
                    dataUri = fileUri
                    shareDataType = AShareData.DATA_TYPE_IMAGE
                    mContext = this@MainActivity
                    //分享到的目标：
                    //com.tencent.mm/.ui.tools.ShareImgUI
//                    shareToComponent = ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareImgUI")
//                    shareToComponent = ComponentName("com.tencent.mm","")//只有包名 系统找不到指定的目标App,框架会根据包名找
                }
            }
            R.id.btnAliShare ->{//支付宝的分享

            }
            else -> {
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.e(TAG, " --> onConfigurationChanged()  newConfig = $newConfig")
    }
    override fun onStop() {
        super.onStop()
        Log.i(TAG, " --> onStop() ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, " --> onDestroy() ")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, " --> onResume() ")
    }
}
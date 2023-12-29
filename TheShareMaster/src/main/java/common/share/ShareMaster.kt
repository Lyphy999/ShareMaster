package common.share

import android.annotation.SuppressLint
import android.content.Context
import common.share.core.AbsEventResultHandler
import common.share.core.IEventResultCallback
import common.share.system.SystemShareData
import common.share.wx.AWxShareData
import common.share.wx.WxSdk
import common.share.wx.WxSdkConfig

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * <P>DESC:
 * 分享系统的入口
 * </p>
 * ******************(^_^)***********************
 */
@SuppressLint("StaticFieldLeak")
object ShareMaster {

    private var sAppContext: Context? = null
    fun initWxSdk(context: Context,config: WxSdkConfig.() -> Unit):Boolean {
        sAppContext = context.applicationContext
        val aWxConfig = WxSdkConfig()
        aWxConfig.config()
        return WxSdk.initSdk(context, aWxConfig)
    }

    fun initAlipaySdk() {
    }

    fun initDouYinSdk() {

    }

    private fun test() {
        wxShare {

        }
    }

    fun wxShare(eventResultHandler: AbsEventResultHandler? = null,configData: AWxShareData.() -> Unit): AWxShareData {
        val aWxShareData = AWxShareData()
        aWxShareData.configData()
        aWxShareData.share(null,eventResultHandler)
        return aWxShareData
    }

    fun systemShare(
        evenreCallback: IEventResultCallback? = null,
        configData: SystemShareData.() -> Unit
    ) :SystemShareData{
        val systemShareData = SystemShareData()
        systemShareData.configData()
        systemShareData.share(null,evenreCallback)
        return systemShareData
    }

    fun getContext() = sAppContext

}
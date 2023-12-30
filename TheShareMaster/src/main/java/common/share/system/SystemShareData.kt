package common.share.system

import android.content.ComponentName
import android.net.Uri
import common.share.core.AShareData

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * <P>DESC:
 * 使用 系统自带的 分享功能的 分享数据体
 * </p>
 * ******************(^_^)***********************
 */
/**
 * 可配置的字段:
 * [shareTitle]
 * [shareDataType]
 * [shareText]
 * [mContext]
 */
class SystemShareData: AShareData<SystemShareData>() {
    init {
        theShareBuilderStrategy = SystemShareBuilderStrategy()
    }
    /**
     * 类似于微信中的分享 文本
     */
    var shareText: String = ""

    /**
     * 要分享的数据 Uri
     */
    var dataUri: Uri? = null

    /**
     * 可以指定分享到的目的 App 包、类信息
     * 注：需要包名和全类名: eg.: ComponentName("com.tencent.mm","com.tencent.mm.ui.tools.ShareImgUI")
     */
    var shareToComponent: ComponentName? = null



}
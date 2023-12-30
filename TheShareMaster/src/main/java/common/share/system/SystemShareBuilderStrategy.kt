package common.share.system

import android.content.Context
import android.content.Intent
import common.share.ShareMaster
import common.share.core.AShareData
import common.share.core.IEventResultCallback
import common.share.core.IShareBuilderStrategy

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * <P>DESC:
 * 启用系统自带 的 分享构建策略
 * </p>
 * ******************(^_^)***********************
 */
class SystemShareBuilderStrategy : IShareBuilderStrategy<SystemShareData> {
    override fun buildAndShare(
        theShareData: SystemShareData,
        resultCallback: IEventResultCallback?
    ): Boolean {
        var errorCode: Int = IEventResultCallback.CODE_ERR_COMM
        val sendIntent: Intent? = when (theShareData.shareDataType) {
                AShareData.DATA_TYPE_TEXT -> {
                    if (theShareData.shareText.isBlank()) {
                        errorCode = IEventResultCallback.CODE_SHARE_DATA_ERROR
                        null
                    } else {
                        val sendIntent = Intent(Intent.ACTION_SEND)
                        sendIntent.type = "text/plain"
                        sendIntent.putExtra(Intent.EXTRA_TEXT, theShareData.shareText)
                        sendIntent
                    }
                }
                AShareData.DATA_TYPE_URL ->{
                    if (theShareData.shareUrl.isBlank()) {
                        errorCode = IEventResultCallback.CODE_SHARE_DATA_ERROR
                        null
                    } else {
                        val sendIntent = Intent(Intent.ACTION_SEND)
                        sendIntent.type = "text/plain"
                        sendIntent.putExtra(Intent.EXTRA_TEXT, theShareData.shareUrl)
                        sendIntent
                    }
                }
                AShareData.DATA_TYPE_IMAGE -> {//分享图片
//                    val sendIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
//                    val sendIntent = Intent(Intent.ACTION_SENDTO)
                    val sendIntent = Intent(Intent.ACTION_SEND)
//                    sendIntent.setData(Uri.)
//                    sendIntent.setDataAndType()
                    sendIntent.type = "image/*"
//                    sendIntent.putExtra(Intent.EXTRA_STREAM,Uri)
                    null
                }
                else -> {
                    errorCode = IEventResultCallback.CODE_ERR_UNSUPPORT
                    null
                }
            }
        val isStartOk = if (sendIntent != null) {
            val context: Context? = theShareData.mContext ?: ShareMaster.getContext()
            val shareToComponent = theShareData.shareToComponent
            var targetIntents: ArrayList<Intent>? = null
            if (shareToComponent != null || theShareData.targetAppPackageName.isNotBlank()) {
                var isHasShareToComponent = false
                if (shareToComponent != null) {
                    if (shareToComponent.packageName.isNotBlank() && shareToComponent.className.isNotBlank()) {
                        sendIntent.component = shareToComponent
                        isHasShareToComponent = true
                    }
                }
                if (!isHasShareToComponent) {
                    val targetAppPackageName = shareToComponent?.packageName ?: theShareData.targetAppPackageName
                    val s = context?.packageManager?.queryIntentActivities(sendIntent,0)
                    s?.forEach {info ->
                        if (targetIntents == null) {
                            targetIntents = ArrayList(1)
                        }
                        val activityInfo = info.activityInfo
                        if (activityInfo.packageName == targetAppPackageName) {
                            val targetIntent = Intent(sendIntent)
                            targetIntent.component = null
                            targetIntent.setPackage(activityInfo.packageName)
                            targetIntent.setClassName(activityInfo.packageName, activityInfo.name)
                            targetIntents?.add(targetIntent)
                        }
                    }
                }

            }
            if (context != null) {
                if (targetIntents == null) {
                    context.startActivity(Intent.createChooser(sendIntent,theShareData.shareTitle))
//                context.startActivity(sendIntent) //这个也可以弹出，但样式与上面的不一样
                } else {
                    val choose = Intent.createChooser(targetIntents!![0], theShareData.shareTitle)
                    context.startActivity(choose)
                }
                true
            }
            else{
                errorCode = IEventResultCallback.CODE_ERR_SENT_FAILED
                false
            }
        }
        else{
            false
        }
        if (!isStartOk) {
            resultCallback?.onEventResp(
                false,
                errorCode,
                respSubCode = 0,
                respMsg = IEventResultCallback.respCodeDesc(errorCode)
            )
        }
        return isStartOk
    }

}
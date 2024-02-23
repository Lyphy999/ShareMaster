package common.share.system

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat
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

    private val mGrantMode by lazy(LazyThreadSafetyMode.NONE) {
        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    }

    override fun buildAndShare(
        theShareData: SystemShareData,
        resultCallback: IEventResultCallback?
    ): Boolean {
        var errorCode: Int = IEventResultCallback.CODE_ERR_COMM
        val sendIntent: Intent? = when (theShareData.shareDataType) {
            AShareData.DATA_TYPE_TEXT -> { //分享文本
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

            AShareData.DATA_TYPE_URL -> { //分享链接
                if (theShareData.shareUrl.isBlank()) {
                    errorCode = IEventResultCallback.CODE_SHARE_DATA_ERROR
                    null
                } else {
                    val sendIntent = Intent(Intent.ACTION_SEND)
                    sendIntent.type = "text/plain"
                    sendIntent.putExtra(Intent.EXTRA_TEXT, theShareData.shareUrl)
                    // EXTRA_TITLE 会显示在系统弹框的中间
//                    sendIntent.putExtra(Intent.EXTRA_TITLE, theShareData.shareTitle)
                    sendIntent
                }
            }

            AShareData.DATA_TYPE_IMAGE -> {//分享图片
//                ShareCompat.IntentBuilder(theShareData.mContext!!)
//                    .setType("image/*")
//                    .addStream(theShareData.dataUri!!)
//                    .setChooserTitle(theShareData.shareTitle)
//                    .startChooser()

//                    val sendIntent = Intent(Intent.ACTION_SEND_MULTIPLE) //分享多个
//                    val sendIntent = Intent(Intent.ACTION_SENDTO)
                val sendIntent = Intent(Intent.ACTION_SEND)
                sendIntent.type = "image/*"
//                sendIntent.type = "*/*"
//                sendIntent.setDataAndType(theShareData.dataUri, "image/*")//这个不行
                sendIntent.putExtra(Intent.EXTRA_STREAM, theShareData.dataUri)
//                sendIntent.clipData = ClipData.newRawUri(theShareData.shareTitle,theShareData.dataUri)
                sendIntent.addFlags(mGrantMode)//
                sendIntent
            }

            else -> {
                errorCode = IEventResultCallback.CODE_ERR_UNSUPPORT
                null
            }
        }
        val isStartOk = if (sendIntent != null) {
            val context: Context? = theShareData.mContext ?: ShareMaster.getContext()
            val shareToComponent = theShareData.shareToComponent
            var targetIntents: List<Intent>? = null
            if (shareToComponent != null) {//外部有指定 分享到的目标组件
                val targetAppPackageName = shareToComponent.packageName
                var isHasShareToComponent = false
                if (targetAppPackageName.isNotBlank() && shareToComponent.className.isNotBlank()) {
                    sendIntent.component = shareToComponent //只有当外部指定了 完整目标组件时，才赋值
                    isHasShareToComponent = true
                    context?.grantUriPermission(
                        targetAppPackageName,
                        theShareData.dataUri,
                        mGrantMode
                    )
                }
                if (!isHasShareToComponent) {//目标组件不完整，则通过包名去查找匹配
                    targetIntents = matchTargetIntents(
                        context,
                        targetAppPackageName,
                        sendIntent,
                        theShareData.dataUri
                    )
                }
            } else {
                //目的为让目标App被临时赋予权限
                matchTargetIntents(context, "", sendIntent, theShareData.dataUri)
            }
            if (context != null) {
                val targetIntentSize = targetIntents?.size ?: 0
                if (targetIntentSize < 1) {
                    val chooseIntent = Intent.createChooser(sendIntent, theShareData.shareTitle)
                    chooseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(chooseIntent)
//                context.startActivity(sendIntent) //这个也可以弹出，但样式与上面的不一样
                } else {
                    val choose = Intent.createChooser(targetIntents!![0], theShareData.shareTitle)
                    choose.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    //如果找到了多个匹配的目标组件
                    if (targetIntentSize > 1) {
                        choose.putExtra(
                            Intent.EXTRA_INITIAL_INTENTS,
                            targetIntents!!.toTypedArray()
                        )
                    }
                    context.startActivity(choose)
                }
                true
            } else {
                errorCode = IEventResultCallback.CODE_ERR_SENT_FAILED
                false
            }
        } else {
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


    private fun matchTargetIntents(
        context: Context?,
        targetPackageName: String,
        actionIntent: Intent,
        dataUri: Uri? = null
    ): List<Intent>? {
        val s = context?.packageManager?.queryIntentActivities(actionIntent, 0)
        return s?.filter {
//                it.resolvePackageName //这个 为 null
            val pkgName = it.activityInfo.packageName
            if (dataUri != null) {
                context.grantUriPermission(pkgName, dataUri, mGrantMode)
            }
            pkgName == targetPackageName
        }?.map {
            val activityInfo = it.activityInfo
            val targetIntent = Intent(actionIntent)
            targetIntent.setPackage(activityInfo.packageName)
            targetIntent.setClassName(activityInfo.packageName, activityInfo.name)
            targetIntent
        }
    }
}
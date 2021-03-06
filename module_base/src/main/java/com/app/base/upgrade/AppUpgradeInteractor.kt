package com.app.base.upgrade

import android.app.Dialog
import android.content.Context
import com.android.base.utils.android.XAppUtils
import com.android.base.utils.android.compat.AndroidVersion
import com.android.base.utils.android.views.getColorCompat
import com.android.base.utils.common.otherwise
import com.android.base.utils.common.yes
import com.android.sdk.permission.AutoPermission
import com.android.sdk.upgrade.UpgradeException
import com.android.sdk.upgrade.UpgradeInfo
import com.android.sdk.upgrade.UpgradeInteractor
import com.app.base.AppContext
import com.app.base.R
import com.app.base.config.AppDirectory
import com.app.base.config.AppSettings
import com.app.base.widget.dialog.showConfirmDialog
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import java.lang.ref.WeakReference

/**
 *@author Ztiany
 *      Email: ztiany3@gmail.com
 *      Date : 2019-10-30 11:26
 */
class AppUpgradeInteractor : UpgradeInteractor {

    private var loadingDialogReference: WeakReference<UpgradeLoadingDialog>? = null
    private var upgradeDialogReference: WeakReference<Dialog>? = null

    private val notificationHelper by lazy { NotificationHelper() }

    private val appUpdateRepository by lazy {
        AppUpdateRepository(AppContext.serviceFactory())
    }

    private fun newLoadingDialogIfNeed(context: Context): Dialog {
        loadingDialogReference?.get()?.dismiss()
        val dialog = UpgradeLoadingDialog(context)
        loadingDialogReference = WeakReference(dialog)
        return dialog
    }

    override fun checkUpgrade(): Flowable<UpgradeInfo> {
        return appUpdateRepository.checkNewVersion()
                .map { buildUpgradeInfo(it) }
    }

    private fun buildUpgradeInfo(response: UpgradeResponse): UpgradeInfo {
        return UpgradeInfo(
                isForce = false,
                isNewVersion = false,
                versionName = "",
                downloadUrl = "",
                description = "",
                digitalAbstract = "",
                raw = response
        )
    }

    override fun showUpgradeDialog(context: Context, upgradeInfo: UpgradeInfo, onCancel: () -> Unit, onConfirm: () -> Unit) {
        Timber.d("showUpgradeDialog")
        upgradeDialogReference?.get()?.dismiss()

        val upgradeDialog = showConfirmDialog(context) {
            title = "更新提示"
            message = upgradeInfo.description
            messageColor = context.getColorCompat(R.color.opacity50_black)
            positiveText = "立即更新"
            positiveListener = { onConfirm() }
            if (!upgradeInfo.isForce) {
                negativeListener = {
                    onCancel()
                }
            } else {
                disableNegative()
            }
            cancelable = false
        }

        upgradeDialogReference = WeakReference(upgradeDialog)
    }

    override fun showInstallTipsDialog(context: Context, forceUpgrade: Boolean, onCancel: () -> Unit, onConfirm: () -> Unit) {
        if (!forceUpgrade) {
            return
        }
        showConfirmDialog(context) {
            message = "新版本已经下载完成，请点击“确认”进行安装"
            cancelable = false
            negativeText = null
            negativeListener = {
                it.dismiss()
                onCancel()
            }
            positiveListener = { onConfirm() }
            autoDismiss = false
        }
    }

    override fun showDownloadingFailed(context: Context, forceUpgrade: Boolean, error: UpgradeException, onCancel: () -> Unit, onConfirm: () -> Unit) {
        showConfirmDialog(context) {
            message = forceUpgrade.yes { "下载更新失败，需要重试" } otherwise { "下载更新失败，是否重试？" }
            cancelable = false
            negativeText = forceUpgrade.yes { null } otherwise { "取消" }
            negativeListener = { onCancel() }
            positiveListener = { onConfirm() }
        }
    }

    override fun showDownloadingDialog(context: Context, forceUpgrade: Boolean) {
        Timber.d("showDownloadingDialog")
        notificationHelper.cancelNotification()

        newLoadingDialogIfNeed(context).run {
            setCancelable(false)
            show()
        }
    }

    override fun dismissDownloadingDialog() {
        Timber.d("showDownloadingDialog")
        notificationHelper.cancelNotification()
        loadingDialogReference?.get()?.dismiss()
    }

    override fun onProgress(total: Long, progress: Long) {
        Timber.d("onProgress, total = $total, progress = $progress")
        notificationHelper.notifyProgress(total, progress)
        loadingDialogReference?.get()?.notifyProgress(total, progress)
    }

    override fun installApk(file: File, upgradeInfo: UpgradeInfo) {
        Timber.d("installApk")
        if (AndroidVersion.atLeast(26)) {
            //Android8.0未知来源应用安装权限方案
            AutoPermission.with(AppContext.get())
                    .install()
                    .file(file)
                    .onDenied { Timber.d("installApk onDenied") }
                    .onGranted { Timber.d("installApk onGranted") }
                    .start()
        } else {
            //正常安装
            XAppUtils.installApp(AppContext.get(), file, AppSettings.appFileProviderAuthorities)
        }
    }

    override fun checkApkFile(apkFile: File, digitalAbstract: String) = true

    override fun generateAppDownloadPath(versionName: String): String = AppDirectory.createAppDownloadPath(versionName)

    override fun createHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

}
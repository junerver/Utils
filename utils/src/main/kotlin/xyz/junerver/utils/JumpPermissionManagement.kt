package xyz.junerver.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log

/**
 * Created by hkq325800 on 2017/4/14.
 */
object JumpPermissionManagement {
    /**
     * Build.MANUFACTURER
     */
    private const val MANUFACTURER_HUAWEI = "Huawei" //华为
    private const val MANUFACTURER_MEIZU = "Meizu" //魅族
    private const val MANUFACTURER_XIAOMI = "Xiaomi" //小米
    private const val MANUFACTURER_SONY = "Sony" //索尼
    private const val MANUFACTURER_OPPO = "OPPO"
    private const val MANUFACTURER_LG = "LG"
    private const val MANUFACTURER_VIVO = "vivo"
    private const val MANUFACTURER_SAMSUNG = "samsung" //三星
    private const val MANUFACTURER_LETV = "Letv" //乐视
    private const val MANUFACTURER_ZTE = "ZTE" //中兴
    private const val MANUFACTURER_YULONG = "YuLong" //酷派
    private const val MANUFACTURER_LENOVO = "LENOVO" //联想

    /**
     * 此函数可以自己定义
     * @param activity
     */
    fun goToSetting(activity: Activity) {
        when (Build.MANUFACTURER) {
            MANUFACTURER_HUAWEI -> Huawei(activity)
            MANUFACTURER_MEIZU -> Meizu(activity)
            MANUFACTURER_XIAOMI -> Xiaomi(activity)
            MANUFACTURER_SONY -> Sony(activity)
            MANUFACTURER_OPPO -> OPPO(activity)
            MANUFACTURER_LG -> LG(activity)
            MANUFACTURER_LETV -> Letv(activity)
            else -> {
                ApplicationInfo(activity)
                Log.e("goToSetting", "目前暂不支持此系统")
            }
        }
    }

    fun Huawei(activity: Activity) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", activity.packageName)
        val comp = ComponentName(
            "com.huawei.systemmanager",
            "com.huawei.permissionmanager.ui.MainActivity"
        )
        intent.component = comp
        activity.startActivity(intent)
    }

    fun Meizu(activity: Activity) {
        val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.putExtra("packageName", activity.packageName)
        activity.startActivity(intent)
    }

    fun Xiaomi(activity: Activity) {
//        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
//        ComponentName componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
//        intent.setComponent(componentName);
//        intent.putExtra("extra_pkgname", activity.getPackageName());
//        activity.startActivity(intent);
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            localIntent.data = Uri.fromParts("package", activity.packageName, null)
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.action = Intent.ACTION_VIEW
            localIntent.setClassName(
                "com.android.settings",
                "com.android.setting.InstalledAppDetails"
            )
            localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.packageName)
        }
        activity.startActivity(localIntent)
    }

    fun Sony(activity: Activity) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", activity.packageName)
        val comp = ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity")
        intent.component = comp
        activity.startActivity(intent)
    }

    fun OPPO(activity: Activity) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", activity.packageName)
        val comp = ComponentName(
            "com.color.safecenter",
            "com.color.safecenter.permission.PermissionManagerActivity"
        )
        intent.component = comp
        activity.startActivity(intent)
    }

    fun LG(activity: Activity) {
        val intent = Intent("android.intent.action.MAIN")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", activity.packageName)
        val comp = ComponentName(
            "com.android.settings",
            "com.android.settings.Settings\$AccessLockSummaryActivity"
        )
        intent.component = comp
        activity.startActivity(intent)
    }

    fun Letv(activity: Activity) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", activity.packageName)
        val comp = ComponentName(
            "com.letv.android.letvsafe",
            "com.letv.android.letvsafe.PermissionAndApps"
        )
        intent.component = comp
        activity.startActivity(intent)
    }

    /**
     * 只能打开到自带安全软件
     * @param activity
     */
    fun _360(activity: Activity) {
        val intent = Intent("android.intent.action.MAIN")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("packageName", activity.packageName)
        val comp = ComponentName(
            "com.qihoo360.mobilesafe",
            "com.qihoo360.mobilesafe.ui.index.AppEnterActivity"
        )
        intent.component = comp
        activity.startActivity(intent)
    }

    /**
     * 应用信息界面
     * @param activity
     */
    fun ApplicationInfo(activity: Activity) {
        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
            localIntent.data = Uri.fromParts("package", activity.packageName, null)
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.action = Intent.ACTION_VIEW
            localIntent.setClassName(
                "com.android.settings",
                "com.android.settings.InstalledAppDetails"
            )
            localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.packageName)
        }
        activity.startActivity(localIntent)
    }

    /**
     * 系统设置界面
     * @param activity
     */
    fun SystemConfig(activity: Activity) {
        val intent = Intent(Settings.ACTION_SETTINGS)
        activity.startActivity(intent)
    }
}
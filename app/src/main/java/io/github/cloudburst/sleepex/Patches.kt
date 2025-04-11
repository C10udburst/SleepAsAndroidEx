package io.github.cloudburst.sleepex

import android.content.Context
import android.util.Log
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import androidx.core.content.edit

fun patchAnalytics(cl: ClassLoader) {
    val firebase = cl.loadClass("com.google.firebase.analytics.FirebaseAnalytics")

    XposedBridge.hookAllMethods(firebase, "logEvent", object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            // Early return
            Log.d("SleepEx", "FirebaseAnalytics: ${param?.args?.toString()})")
            param?.result = null
        }
    });
}

fun patchTrialFilter(cl: ClassLoader): Any? = try {
    var trialFilter = cl.loadClass("com.urbandroid.sleep.TrialFilter")

    var isTrialField = trialFilter.getDeclaredField("isTrial")
    isTrialField.isAccessible = true

    XposedBridge.hookAllMethods(trialFilter, "refresh", object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            isTrialField.setBoolean(param?.thisObject, false)
        }
    })
} catch (e: Exception) {
    Log.e("SleepEx", "Failed to patch trial filter", e)
}

fun patchNagging(cl: ClassLoader) : Any? = try {
    var settings = cl.loadClass("com.urbandroid.sleep.service.Settings")
    var naggingMethods = listOf(
        "shouldAskForRatingPlayStore",
        "isTimeToAskAnalytics",
        "isTimeToAskAds"
    )

    var settingsToChange = mapOf(
        "ads_opt_out" to true,
        "analytics_opt_out" to true,
    )
    var context = settings.getDeclaredField("context")
    context.isAccessible = true

    for (method in naggingMethods) {
        XposedBridge.hookAllMethods(settings, method, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                param?.result = false
            }
        })
    }

    XposedBridge.hookAllConstructors(settings, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            var ctx = context.get(param?.thisObject) as? Context ?: return
            var sharedPreferences = ctx.getSharedPreferences("${ctx.packageName}_preferences", Context.MODE_PRIVATE)
            sharedPreferences.edit() {
                for (setting in settingsToChange) {
                    putBoolean(setting.key, setting.value)
                }
            }
        }
    })

} catch (e: Exception) {
    Log.e("SleepEx", "Failed to patch out nagging", e)
}
package io.github.cloudburst.sleepex;

import android.text.format.DateFormat;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.luckypray.dexkit.DexKitBridge;
import java.util.Date;

import static io.github.cloudburst.sleepex.PatchesKt.*;



public final class Module implements IXposedHookLoadPackage {

    private static final String TAG = "SleepAsAndroidEx";

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.urbandroid.sleep")) return;

        Log.d(TAG, "Module build date: " + DateFormat.format("yyyy-MM-dd HH:mm:ss", new Date(BuildConfig.BUILD_DATE)));

        var cl = lpparam.classLoader;
        System.loadLibrary("dexkit");
        try (DexKitBridge bridge = DexKitBridge.create(lpparam.appInfo.sourceDir)) {
            if (bridge == null) {
                Log.e(TAG, "Failed to create DexKitBridge");
                return;
            }
            patchAnalytics(cl);
            patchTrialFilter(cl);
            patchNagging(cl);
        } catch (Exception e) {
            Log.e(TAG, "Failed to find method", e);
        }
    }

}

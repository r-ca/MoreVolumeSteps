package one.nem.morevolumesteps;

import android.media.AudioManager;

import java.util.Arrays;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        // get Audio Service
        try {
            final Class<?> classAudioService = XposedHelpers.findClass("com.android.server.audio.AudioService", loadPackageParam.classLoader);
            if (classAudioService != null) {
                XposedBridge.log("Found AudioService class: " + classAudioService.getName());
            }

            XposedHelpers.findAndHookMethod(classAudioService, "createStreamStates", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    int[] maxStreamVolume = (int[])
                    XposedHelpers.getStaticObjectField(classAudioService, "MAX_STREAM_VOLUME");
                    XposedBridge.log("Current: " + Arrays.toString(maxStreamVolume));
                    XposedBridge.log("Attempting to overwrite...");
                    try {
                        maxStreamVolume[AudioManager.STREAM_MUSIC] = 60;
                    }
                    catch (Throwable t) {
                        XposedBridge.log("FAILED: " + t.getMessage());
                    }
                    XposedBridge.log("Overwritten:" + Arrays.toString(maxStreamVolume));
                }
            });
        }
        catch (Throwable t) {
            XposedBridge.log("ERROR: " + t.getMessage());
        }
    }
}

package slimchat.android.service;

import android.app.Service;
import android.os.PowerManager;

/**
 * Created by feng on 14-9-27.
 */
public class BroadCastXX {

    /**
     * Acquires a partial wake lock for this client

    private void acquireWakeLock() {
        if (wakelock == null) {
            PowerManager pm = (PowerManager) service
                    .getSystemService(Service.POWER_SERVICE);
            wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    wakeLockTag);
        }
        wakelock.acquire();

    }

    private void releaseWakeLock() {
        if(wakelock != null && wakelock.isHeld()){
            wakelock.release();
        }
    }
     */
}

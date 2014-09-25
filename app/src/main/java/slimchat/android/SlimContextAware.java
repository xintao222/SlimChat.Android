package slimchat.android;

import android.content.Context;

/**
 * Created by feng on 14-9-24.
 */
public class SlimContextAware {

    protected Context appContext = null;

    /**
     * Init application context
     *
     * @param appContext application context
     */
    public void init(Context appContext) {
        this.appContext = appContext;
    }

    public void ensureContext() throws Exception {
        if(appContext == null) {
            throw new Exception("appContext not init");
        }
    }

}

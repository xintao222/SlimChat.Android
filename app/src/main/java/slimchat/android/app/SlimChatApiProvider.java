package slimchat.android.app;

import java.util.HashMap;
import java.util.Map;

import slimchat.android.SlimApiProvider;
import slimchat.android.SlimChat;

/**
 * Created by feng on 14-9-24.
 */
public class SlimChatApiProvider implements SlimApiProvider {

    /**
     * Default API URL
     */
    static final String DEFAULT_API_URL = "http://slimpp.io/api.php/v1";

    /**
     * Default AUTH URL
     */
    static final String DEFAULT_AUTH_URL = "http://slimpp.io/login";

    /**
     * API URL
     */
    private String apiURL = DEFAULT_API_URL;

    /**
     * AUTH URL
     */
    private String authURL = DEFAULT_AUTH_URL;

    /**
     * ALL API
     */
    private Map<String, SlimApi> apiMap = new HashMap<String, SlimApi>();

    public SlimChatApiProvider() {
        apiMap.put("online", new SlimApi(Method.POST, apiURL+"/online"));
        apiMap.put("message", new SlimApi(Method.POST, apiURL+"/message"));
        apiMap.put("presence", new SlimApi(Method.POST, apiURL+"/presence"));
        apiMap.put("status", new SlimApi(Method.POST, apiURL+"/status"));
        apiMap.put("buddies", new SlimApi(Method.GET, apiURL+"/buddies"));
        apiMap.put("offline", new SlimApi(Method.POST, apiURL+"/offline"));
    }

    @Override
    public SlimApi authApi() {
        return new SlimApi(Method.POST, authURL + "?client=android");
    }

    @Override
    public SlimApi serviceApi(String action) {
        return apiMap.get(action);
    }
}

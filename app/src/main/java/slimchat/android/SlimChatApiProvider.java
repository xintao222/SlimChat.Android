package slimchat.android;

/**
 * Created by feng on 14-9-24.
 */
public interface SlimChatApiProvider {

    /**
     * REST API Method
     */
    public enum Method {
        GET,
        POST,
        PUT,
        DELETE,
    }

    /**
     * API built with method, url
     */
    public class SlimApi {

        final Method method;

        final String url;

        public SlimApi(Method method, String url) {
            this.method = method;
            this.url = url;
        }

        public Method getMethod() {
            return method;
        }

        public String getUrl() {
            return url;
        }

    }

    /**
     * Authentication API
     *
     * @return authentication API
     */
    public SlimApi authApi();

    /**
     * Service API
     *
     * @param action action name
     * @return service api
     */
    public SlimApi serviceApi(String action);
}

/**
 *
 * The MIT License (MIT)
 * Copyright (c) 2014 <slimpp.io>

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package slimchat.android.core;

/**
 * SlimChat REST API.
 *
 * @author feng.lee@slimpp.io
 */
public final class SlimApi {

    //REST Method
    final Method method;

    //REST URL
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
     * REST API Provider
     */
    public interface Provider {

        /**
         * Authentication API
         *
         * @return authentication API
         */
        SlimApi authApi();

        /**
         * Service API
         *
         * @param action action name
         * @return service api
         */
        SlimApi serviceApi(String action);

    }

}

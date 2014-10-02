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
package slimchat.android.model;

import android.net.Uri;

/**
 * Slim URIs
 * <p/>
 * Created by feng on 14-10-2.
 */
public class SlimUris {

    public final static String SCHEME = "slimpp";

    public static Uri userUri(String id) {
        return buildUri(Type.USER, id);
    }

    public static Uri roomUri(String id) {
        return buildUri(Type.ROOM, id);
    }

    public static Uri buildUri(Type type, String id) {
        return new Uri.Builder().scheme(SCHEME).encodedOpaquePart(type.name().toLowerCase() + ":" + id).build();
    }

    public static Type parseType(Uri uri) {
        return Type.valueOf(parseToken(uri, 0).toUpperCase());
    }

    public static String parseId(Uri uri) {
        return parseToken(uri, 1);
    }

    public static String parseToken(Uri uri, int index) {
        String part = uri.getEncodedSchemeSpecificPart();
        String[] tokens = part.split(":");
        if (tokens.length != 2) {
            throw new InvalidUriException(uri);
        }
        if (!isValidType(tokens[0])) {
            throw new InvalidUriException(uri);
        }
        return tokens[index];
    }

    public static boolean isRoomUri(Uri uri) {
        return parseType(uri) == Type.ROOM;
    }

    public static boolean isUserUri(Uri uri) {
        return parseType(uri) == Type.USER;
    }

    private static boolean isValidType(String token) {
        if (Type.USER.name().toLowerCase().equals(token)) return true;
        if (Type.ROOM.name().toLowerCase().equals(token)) return true;
        return false;
    }

    public static enum Type {
        USER,
        ROOM
    }

    public static class InvalidUriException extends RuntimeException {
        public InvalidUriException(Uri uri) {
            super("Invalid URI: " + uri.toString());
        }
    }

}

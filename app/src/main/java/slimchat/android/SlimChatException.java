package slimchat.android;

/**
 * Created by feng on 14-9-28.
 */
public class SlimChatException extends  Exception {

    public SlimChatException() {
        super();
    }

    public SlimChatException(String detailMessage) {
        super(detailMessage);
    }

    public SlimChatException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public SlimChatException(Throwable throwable) {
        super(throwable);
    }

}

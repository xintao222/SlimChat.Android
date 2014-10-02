package slimchat.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by feng on 14-9-23.
 */
public class SlimDbOpenHelper  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static SlimDbOpenHelper instance;

    /*
    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK +" TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InviteMessgeDao.TABLE_NAME + " ("
            + InviteMessgeDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InviteMessgeDao.COLUMN_NAME_FROM + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_REASON + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_TIME + " TEXT); ";

*/

    private SlimDbOpenHelper(Context context) {
        super(context, "users", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}

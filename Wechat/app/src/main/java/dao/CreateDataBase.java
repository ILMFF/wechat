package dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static Xmpp.AppTemp.MyUserName;

/**
 * Created by Administrator on 2017/2/24.
 */

public class CreateDataBase extends SQLiteOpenHelper {


    //创建聊天记录表
    public static final String Create_ChatRecord = "create table IF NOT EXISTS WeChatRecord_"+MyUserName+"(" +
            "  SenderName varchar(20)," +
            "  SenderNickName varchar(30)," +
            "  SenderIcon MEDIUMTEXT," +
            "  Content text," +
            "  CreateTime text," +
            "  Belong varchar(70)," +
            "  ContentFrom varchar(50)," +
            "  ChatRoomOrNot varchar(50)," +
            "  MemberSender varchar(50)" +
            ")";



    //创建未读消息表,
    // 针对情况：即是收到了消息但是用户并没有去访问,然后直接退出。所以应该先用一个表来进行保存未读信息，
    // 为了下次用户再次进入时，能再次加载用户没访问的信息

    public static final String Create_NewsRecord = "create table IF NOT EXISTS NewsRecord_"+MyUserName+"(" +
            "  SenderName varchar(20)," +
            "  SenderNickName varchar(30)," +
            "  SenderIcon MEDIUMTEXT," +
            "  Content text," +
            "  CreateTime text," +
            "  ContentFrom varchar(50)," +
            "  ChatRoomOrNot varchar(50)," +
            "  MemberSender varchar(50)" +
            ")";


    //创建群聊房间表
    //针对情况：由于聊天室是即时的一旦用户下线，用户就会退出群聊，但是房间依然还在，所以用本地表记录该用户下线
    //之前曾是哪个群聊里面的会员，一旦上线让其自动重新加入到该群聊。
    //已经退出群聊也要在群聊表里面进行相应的删除。


    public static final String Create_ChatRooms = "create table IF NOT EXISTS ChatRooms_"+MyUserName+"(" +
            "  RoomName varchar(30)," +
            "  RoomNickName varchar(30)" +
            ")";





    public CreateDataBase(Context context, String name) {
        super(context, name , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
            Log.e("创建数据库","当前名字:   "+MyUserName);
            db.execSQL(Create_ChatRecord);  //创建用户对应的聊天记录表
            db.execSQL(Create_NewsRecord);  //创建未读消息表
            db.execSQL(Create_ChatRooms);  //创建用户的群聊表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

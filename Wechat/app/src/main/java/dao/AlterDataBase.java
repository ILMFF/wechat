package dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Xmpp.AppTemp;
import Xmpp.GetRealName;
import dao.bean.ChatRecord;
import dao.bean.NewsRecord;
import weixin.bean.MessageBean;

import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MyUserName;

//create table IF NOT EXISTS WeChatRecord_"+MyUserName+"(" +
//        "  SenderName varchar(20)," +
//        "  SenderNickName varchar(30)," +
//        "  SenderIcon blob," +
//        "  Content varchar(300)," +
//        "  CreateTime varchar(50)," +
//        "  Belong varchar(70)," +
//        "  ContentFrom varchar(50)" +
//        ")";

public class AlterDataBase {

    SQLiteDatabase sqLiteDatabase;

    public AlterDataBase(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }


    public void InsertRecord(ChatRecord chatRecord){



        sqLiteDatabase.execSQL("insert into WeChatRecord_" + MyUserName +"(SenderName,SenderNickName,SenderIcon," +
                "Content,CreateTime,Belong,ContentFrom,ChatRoomOrNot,MemberSender) values("+"\'"+chatRecord.SenderName+"\'"+
                ",\'"+chatRecord.SenderNickName+"\'"+
                ",\'"+chatRecord.SenderIcon+"\'"+
                ",\'"+chatRecord.Content+"\'"+
                ",\'"+chatRecord.CreateTime+"\'"+
                ",\'"+chatRecord.Belong+"\'"+
                ",\'"+chatRecord.ContentFrom+"\'"+
                ",\'"+chatRecord.ChatRoomOrNot+"\'"+
                ",\'"+chatRecord.MemberSender+"\'"+")");


    }

    public void InsertNewsRecord(NewsRecord chatRecord){  //保存未读消息


        sqLiteDatabase.execSQL("insert into NewsRecord_" + MyUserName +"(SenderName,SenderNickName,SenderIcon," +
                "Content,CreateTime,ContentFrom,ChatRoomOrNot,MemberSender) values("+"\'"+chatRecord.SenderName+"\'"+
                ",\'"+chatRecord.SenderNickName+"\'"+
                ",\'"+chatRecord.SenderIcon+"\'"+
                ",\'"+chatRecord.Content+"\'"+
                ",\'"+chatRecord.CreateTime+"\'"+
                ",\'"+chatRecord.ContentFrom+"\'"+
                ",\'"+chatRecord.ChatRoom_ornot+"\'"+
                ",\'"+chatRecord.MemberSender+"\'"+")");


        Log.e("唯独消息的插入",chatRecord.SenderName+"      "+chatRecord.Content+"   "+chatRecord.ChatRoom_ornot);


    }





    public void DeleteRecord(String belong){  //当好友删除时，将用户与该好友相关的聊天记录全部清除

        sqLiteDatabase.execSQL("delete from  WeChatRecord_" + MyUserName +" where Belong = "+"\'"+belong+"\'");


    }

    public Cursor QueryAllRecord(){  //查询所有聊天记录，在进行一一筛选

        Cursor cursor = sqLiteDatabase.rawQuery("select * from WeChatRecord_" + MyUserName, null);
//        Log.e("聊天记录数量",cursor.getCount()+"");
        return cursor;

    }

    public int QueryOneRecord(String CreateTime){
        //查询是否消息重复添加进聊天记录表
        Cursor cursor = sqLiteDatabase.rawQuery("select * from WeChatRecord_" + MyUserName+" where CreateTime = ?",new String[]{CreateTime});

        return cursor.getCount();

    }

    public List<MessageBean> QueryTargetRecord(String date){

        List<MessageBean> list_message = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from WeChatRecord_" + MyUserName,null);

        if (cursor.moveToFirst()) {
            do {

                String CreateTime = cursor.getString(cursor.getColumnIndex("CreateTime"));
                Log.e("时间对比",CreateTime+"     :     "+date);
                if (CreateTime.contains(date)){
                    // 04-18  02:09:40
                    //1-9
                    Log.e("进入了","1111111111111");

                    String Belong = cursor.getString(cursor.getColumnIndex("Belong"));
                    String Content = cursor.getString(cursor.getColumnIndex("Content"));
                    String SenderName = cursor.getString(cursor.getColumnIndex("SenderName"));
                    String SenderNickName = cursor.getString(cursor.getColumnIndex("SenderNickName"));
                    String SenderIcon = cursor.getString(cursor.getColumnIndex("SenderIcon"));
                    String ContentFrom = cursor.getString(cursor.getColumnIndex("ContentFrom"));
                    String ChatRoomOrNot = cursor.getString(cursor.getColumnIndex("ChatRoomOrNot"));
                    String MemberSender = cursor.getString(cursor.getColumnIndex("MemberSender"));

                    byte[] bitmapArray;
                    bitmapArray = Base64.decode(SenderIcon, Base64.DEFAULT);
//                Log.e("bitmapArray",bitmapArray.length+"");
                    Bitmap sendericons = BitmapFactory.decodeByteArray(bitmapArray, 0,
                            bitmapArray.length);
                    Log.e("获取信息",Content);

                    String str = AppTemp.ChatTarget + "/" + MyUserName;
                    Log.e("name", AppTemp.ChatTarget + MyUserName + "           " + Belong + "  " + Content + "    ");
//                Log.e("对比",Belong.equals(str)+"   "+str.length()+"   "+Belong.length());
                    if (Belong.equals(str) == true) {  //找到对应人的聊天记录
//                    Log.e("进入添加","--------------------------"+SenderName+"              "+ContentFrom);
                        list_message.add(new MessageBean(SenderName, Content, CreateTime, ContentFrom, SenderNickName, sendericons,ChatRoomOrNot,MemberSender));
                    }

                }

            }while (cursor.moveToNext());
        }


        return list_message;
    }




    public Cursor GetAllNewsRecord(){  //把所有没有阅读过的聊天信息都重新加载进界面显示

        Log.e("当前用户",MyUserName);
        Cursor cursor = sqLiteDatabase.rawQuery("select * from NewsRecord_" + MyUserName, null);

        return cursor;
    }


    public void DeleteOneNewsRecord(String Sender ,String Createtime , String Content){  //把阅读过的消息删除掉

        sqLiteDatabase.execSQL("delete from NewsRecord_"+MyUserName+" where SenderName = ? and CreateTime = ? and Content = ?",
        new String[]{Sender,Createtime,Content});

    }

    public void DeleteAllRecord(){

        sqLiteDatabase.execSQL("delete from NewsRecord_"+MyUserName);
        sqLiteDatabase.execSQL("delete from WeChatRecord_"+MyUserName);
    }

    public void DeleteOneRecord(String Sender){

        sqLiteDatabase.execSQL("delete from NewsRecord_"+MyUserName+" where SenderName = ? ",new String[]{Sender});
        sqLiteDatabase.execSQL("delete from WeChatRecord_"+MyUserName+" where SenderName = ? ",new String[]{Sender});
    }

    //插入已经加入的群聊
    public void InsertChatRooms(String RoomNames,String RoomNickName){

        sqLiteDatabase.execSQL("insert into ChatRooms_"+MyUserName+"(RoomName,RoomNickName)" +
                " values("+"\'"+RoomNames+"\'"+",\'"+RoomNickName+"\'"+")");

        /*
        *  sqLiteDatabase.execSQL("insert into NewsRecord_" + MyUserName +"(SenderName,SenderNickName,SenderIcon," +
                "Content,CreateTime,ContentFrom) values("+"\'"+chatRecord.SenderName+"\'"+
                ",\'"+chatRecord.SenderNickName+"\'"+
                ",\'"+chatRecord.SenderIcon+"\'"+
                ",\'"+chatRecord.Content+"\'"+
                ",\'"+chatRecord.CreateTime+"\'"+
                ",\'"+chatRecord.ContentFrom+"\')");
        * */

    }

    public Cursor GetQueryChatRooms(){

        Cursor cursor = sqLiteDatabase.rawQuery("select * from ChatRooms_" + MyUserName, null);
        return cursor;
    }

    public void DeleteChatRooms(String RoomName){

        sqLiteDatabase.execSQL("delete from ChatRooms_" + MyUserName +" where RoomName = ? ", new String[]{RoomName});
    }

}

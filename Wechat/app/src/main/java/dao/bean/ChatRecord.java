package dao.bean;

/**
 * Created by Administrator on 2017/3/7.
 */

public class ChatRecord {
//    "  SenderName varchar(20)," +
//            "  SenderNickName varchar(30)," +
//            "  SenderIcon blob," +
//            "  Content varchar(300)," +
//            "  CreateTime varchar(50)," +
//            "  Belong varchar(70)," +
//            "  ContentFrom varchar(50)" +
//            ")";


    public String SenderName;
    public String SenderNickName;
    public String SenderIcon;
    public String Content;
    public String CreateTime;
    public String Belong;
    public String ContentFrom;
    public String ChatRoomOrNot;
    public String MemberSender;
    public int InformationNum = 0;

    public ChatRecord(String senderName, String senderNickName, String senderIcon, String content
            , String createTime, String belong, String contentFrom, String ChatRoomOrNot, String MemberSender) {
        SenderName = senderName;
        SenderNickName = senderNickName;
        SenderIcon = senderIcon;
        Content = content;
        CreateTime = createTime;
        Belong = belong;
        ContentFrom = contentFrom;
        this.ChatRoomOrNot = ChatRoomOrNot;
        this.MemberSender = MemberSender;
    }

    public ChatRecord(String senderName, String senderNickName, String senderIcon, String content
            , String createTime, String belong, String contentFrom, int informationNum, String ChatRoomOrNot, String MemberSender) {
        SenderName = senderName;
        SenderNickName = senderNickName;
        SenderIcon = senderIcon;
        Content = content;
        CreateTime = createTime;
        Belong = belong;
        ContentFrom = contentFrom;
        InformationNum = informationNum;
        this.ChatRoomOrNot = ChatRoomOrNot;
        this.MemberSender = MemberSender;
    }
}

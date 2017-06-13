package dao.bean;

/**
 * Created by Administrator on 2017/3/8.
 */

public class NewsRecord {


    public String SenderName;
    public String SenderNickName;
    public String SenderIcon;
    public String Content;
    public String CreateTime;
    public String ContentFrom;
    public String ChatRoom_ornot; //是否是群聊信息
    public String MemberSender;  //具体是群的哪位成员发送



    public NewsRecord(String senderName, String senderNickName, String senderIcon, String content, String createTime, String contentFrom, String chatRoom_ornot, String memberSender) {
        SenderName = senderName;
        SenderNickName = senderNickName;
        SenderIcon = senderIcon;
        Content = content;
        CreateTime = createTime;
        ContentFrom = contentFrom;
        ChatRoom_ornot = chatRoom_ornot;
        MemberSender = memberSender;
    }
}

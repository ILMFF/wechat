package teacher.davisstore.com.wechat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.packet.VCard;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import Xmpp.GetBitmapToString;
import Xmpp.GetTime;
import Xmpp.XmppTool;
import app.dinus.com.loadingdrawable.LoadingView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dao.AlterDataBase;
import dao.CreateDataBase;
import dao.bean.NewsRecord;
import weixin.Chat_Activity;
import weixin.bean.Online_Message;

import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.dataBase;
import static Xmpp.AppTemp.onlinemsg_list;
import static Xmpp.GetBitmapToString.getBitmapToString;
import static Xmpp.GetRealName.getRealName;

/**
 * Created by Administrator on 2017/2/21.
 */

public class Login_main extends Activity {

    @BindView(R.id.username_xmpp)
    EditText usernameXmpp;
    @BindView(R.id.password_xmpp)
    EditText passwordXmpp;
    @BindView(R.id.water_bottle_view)
    LoadingView waterBottleView;
    String username;
    public static String password;
    private Intent regist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_main);
        ButterKnife.bind(this);


        //检查是否是通过新注册登录的账户
        regist = getIntent();
        if (regist.getStringExtra("re_username") != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    NewRegister_Login(regist);
                }
            }).start();

        }


    }

    private void NewRegister_Login(Intent intent) {

        String re_username = intent.getStringExtra("re_username");
        String re_password = intent.getStringExtra("re_password");
        String name = intent.getStringExtra("name");
        byte[] usericons = intent.getByteArrayExtra("usericon");
        String encodeImage = StringUtils.encodeBase64(usericons);


        try {
            XmppTool.getConnection().login(re_username, re_password);

            if (XmppTool.getConnection().isConnected()) {
                MyUserName = getRealName(con.getUser());
                Log.e("当前MyUserName",con.getUser()+" "+MyUserName+"   "+re_username);
                dataBase = new CreateDataBase(Login_main.this,re_username);
                dataBase.getWritableDatabase();
                Log.e("1111111","000000000000000");
                VCard card = new VCard();
                card.setNickName(name);
                card.setEncodedImage(encodeImage);
                card.setAvatar(usericons);
                card.setProperty("PHOTO", encodeImage);

                card.save(con);
                Presence presence = new Presence(Presence.Type.available);
                con.sendPacket(presence); //设置为上线状态
                Intent intents = new Intent(Login_main.this, BaseActivity.class);
                intents.putExtra("UserName", re_username);
                intents.putExtra("NickName", name );
                startActivity(intents);
                finish();
            } else {
                Toast.makeText(Login_main.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }


    @OnClick({R.id.login_btn, R.id.regist_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:

                username = usernameXmpp.getText().toString();
                password = passwordXmpp.getText().toString();
                if (username.equals("") == false && password.equals("") == false && username != null  && password != null) {


                    waterBottleView.setVisibility(View.VISIBLE);
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Chat_Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); //隐藏软键盘


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                          /*      if (con!=null && con.isConnected() == false) {
                                    con.connect();
                                }*/
                                XmppTool.getConnection().login(username, password);
                                Log.e("Connection station", XmppTool.getConnection().isConnected() + "");
                                Log.e("user",con.getUser()+"   "+username);

                                if (XmppTool.getConnection().isConnected()) {
                                    //创建聊天记录的数据库,一个号对应一个聊天表
                                    MyUserName = getRealName(con.getUser());
                                    /*CreateDataBase dataBase = new CreateDataBase(Login_main.this,username);
                                    dataBase.getWritableDatabase();*/
                                    dataBase = new CreateDataBase(Login_main.this,username);
                                    dataBase.getWritableDatabase();

                                    AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());

                                    //获取离线消息
                                    OfflineMessageManager offlineManager = new OfflineMessageManager(con);
                                    Iterator<Message> it = offlineManager.getMessages();
                                    VCard card = new VCard();
                                    while (it.hasNext()) {
                                        Message message = it.next();

                                        Log.e("离线消息:", " Received from 【" + message.getFrom() + "】 message: " + message.getBody()+"    "+getRealName(message.getFrom()));
                                        if (message.getFrom().contains("@conference") == false) { //不是群聊邀请
                                            ProviderManager.getInstance().addIQProvider("vCard", "vcard-temp", new org.jivesoftware.smackx.provider.VCardProvider());
                                            try {
                                                card.load(XmppTool.getConnection(),getRealName(message.getFrom())+"@"+con.getServiceName());
                                            } catch (XMPPException e) {
                                                e.printStackTrace();
                                            }
                                            byte[] avatar = card.getAvatar();
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
//                                        onlinemsg_list.add(new Online_Message(message.getFrom(), message.getBody(), (String)message.getProperty("time"),card.getNickName(),bitmap));


                                            //接收到离线消息同时，插入未读消息表
                                            String SenderIcon = getBitmapToString(bitmap); //转成String类型再放到数据库中保存

                                            alterDataBase.InsertNewsRecord(new NewsRecord(getRealName(message.getFrom()), card.getNickName()
                                                    , SenderIcon, message.getBody(), (String) message.getProperty("time"), "IN", "NO", ""));
                                        }else {
                                            alterDataBase.InsertChatRooms(message.getFrom(),MyNickName);

                                        }

                                    }
                                    offlineManager.deleteMessages();
                                    //将状态设置成在线
                                    Presence presence = new Presence(Presence.Type.available);
                                    con.sendPacket(presence);

                                    card.load(XmppTool.getConnection(),username+"@"+con.getServiceName());
//                                    Log.e("当前nickname",card.getNickName()+ "    "+username+"@"+con.getServiceName() );
                                    Intent intent = new Intent(Login_main.this, BaseActivity.class);
                                    intent.putExtra("UserName", username);
                                    intent.putExtra("NickName",  card.getNickName());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Login_main.this, "登录失败", Toast.LENGTH_SHORT).show();
                                }
                            } catch (XMPPException e) {

//                                Toast.makeText(Login_main.this, "手机号码或密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).start();
                }else {

                    Toast.makeText(Login_main.this, "手机号码或密码不能为空", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.regist_btn:
                Intent intent = new Intent(Login_main.this, Register_main.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}

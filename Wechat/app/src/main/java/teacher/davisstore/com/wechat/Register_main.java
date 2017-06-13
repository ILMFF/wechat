package teacher.davisstore.com.wechat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Xmpp.XmppTool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import weixin.bean.MessageBean;

import static Xmpp.AppTemp.friendlist;

/**
 * Created by Administrator on 2017/2/21.
 */

public class Register_main extends Activity {

    @BindView(R.id.reusername_xmpp)
    EditText reusernameXmpp;
    @BindView(R.id.repassword_xmpp)
    EditText repasswordXmpp;
    @BindView(R.id.name_xmpp)
    EditText nameXmpp;
    @BindView(R.id.regist_select_photo)
    ImageView registSelectPhoto;
    @BindView(R.id.repassword_again_xmpp)
    EditText repasswordAgainXmpp;

    private String re_username;
    private String re_password;
    private Bitmap usericon;
    private String imagePath = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_main);
        ButterKnife.bind(this);


    }

    private String register(String re_username, String re_password) {


        Registration registration = new Registration();
        registration.setType(IQ.Type.SET);
        registration.setTo(XmppTool.getConnection().getServiceName());
        registration.setUsername(re_username);
        registration.setPassword(re_password);


        registration.addAttribute("android", "geolo_createUser_android");// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！


        PacketFilter filter = new AndFilter(new PacketIDFilter(
                registration.getPacketID()), new PacketTypeFilter(IQ.class));
        PacketCollector collector = XmppTool.getConnection()
                .createPacketCollector(filter);
        XmppTool.getConnection().sendPacket(registration);  //把注册的XML请求包发送到服务器，让服务器操作数据库进行用户创建

        IQ result = (IQ) collector.nextResult(SmackConfiguration    //返回的结果
                .getPacketReplyTimeout());
        // Stop queuing results
        collector.cancel();// 停止请求results（是否成功的结果）

        if (result == null) {
            Log.e("register", "服务器无响应");
            return "服务器无响应";
        } else if (result.getType() == IQ.Type.RESULT) {
            Log.e("register", "注册成功");
            return "注册成功";
        } else if (result.getType() == IQ.Type.ERROR) {

            if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {  //返回错误结果码
                Log.e("register", "账号已存在");
                return "账号已存在";
            } else {
                Log.e("register", "注册失败");
                return "注册失败";
            }

        }

        return "";
    }


    @OnClick({R.id.register_btn, R.id.regist_btn_back, R.id.regist_select_photo})
    public void onClick(View v) {
//        Toast.makeText(Register_main.this,"1111111",Toast.LENGTH_SHORT).show();

        switch (v.getId()) {

            case R.id.register_btn:

                re_username = reusernameXmpp.getText().toString();
                re_password = repasswordXmpp.getText().toString();

                if (re_username != null && re_password != null && re_username.length() > 0 && re_password.length() > 0) {

                    if (nameXmpp.getText().toString() != null && nameXmpp.getText().toString().length() > 0) {

                        if (imagePath != null) {

                            if (isPhoneNumberValid(re_username)) {

                                if (repasswordAgainXmpp.getText().toString().equals(repasswordXmpp.getText().toString())) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            final String name = nameXmpp.getText().toString();

                                            String result = register(re_username, re_password);
                                            switch (result) {

                                                case "注册成功":
                                                    //                        Toast.makeText(Register_main.this,"注册成功",Toast.LENGTH_SHORT).show();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            final AlertDialog.Builder deletefriend_builder = new AlertDialog.Builder(Register_main.this);
                                                            deletefriend_builder.setMessage("注册成功").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    byte[] usericon_bytes = BitmapToBytes(usericon);
                                                                    Intent intent = new Intent(Register_main.this, Login_main.class);
                                                                    intent.putExtra("re_username", re_username);
                                                                    intent.putExtra("re_password", re_password);
                                                                    intent.putExtra("name", name);
                                                                    intent.putExtra("usericon", usericon_bytes);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            });
                                                            AlertDialog deletefriend_dialog = deletefriend_builder.create();
                                                            deletefriend_dialog.show();

                                                        }
                                                    });



                                                    break;

                                                case "服务器无响应":
                                                    //                        Toast.makeText(Register_main.this,"服务器无响应",Toast.LENGTH_SHORT).show();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            final AlertDialog.Builder deletefriend_builder = new AlertDialog.Builder(Register_main.this);
                                                            deletefriend_builder.setMessage("服务器无响应").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    reusernameXmpp.setText("");
                                                                    repasswordXmpp.setText("");
                                                                    repasswordAgainXmpp.setText("");
                                                                }
                                                            });
                                                            AlertDialog deletefriend_dialog = deletefriend_builder.create();
                                                            deletefriend_dialog.show();

                                                        }
                                                    });
                                                    break;

                                                case "账号已存在":
                                                    //                            Toast.makeText(Register_main.this, "账号已存在", Toast.LENGTH_SHORT).show();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            final AlertDialog.Builder deletefriend_builder = new AlertDialog.Builder(Register_main.this);
                                                            deletefriend_builder.setMessage("账号已存在").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    reusernameXmpp.setText("");
                                                                    repasswordXmpp.setText("");
                                                                    repasswordAgainXmpp.setText("");
                                                                }
                                                            });
                                                            AlertDialog deletefriend_dialog = deletefriend_builder.create();
                                                            deletefriend_dialog.show();

                                                        }
                                                    });

                                                    break;

                                                case "注册失败":
                                                    //                        Toast.makeText(Register_main.this,"注册失败",Toast.LENGTH_SHORT).show();
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {

                                                            final AlertDialog.Builder deletefriend_builder = new AlertDialog.Builder(Register_main.this);
                                                            deletefriend_builder.setMessage("注册失败").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    reusernameXmpp.setText("");
                                                                    repasswordXmpp.setText("");
                                                                    repasswordAgainXmpp.setText("");
                                                                }
                                                            });
                                                            AlertDialog deletefriend_dialog = deletefriend_builder.create();
                                                            deletefriend_dialog.show();

                                                        }
                                                    });
                                                    break;
                                            }

                                        }
                                    }).start();

                                }else {
                                    Toast.makeText(Register_main.this, "密码和确认密码不一致", Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                Toast.makeText(Register_main.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Register_main.this, "头像不能为空", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Register_main.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(Register_main.this, "手机号码和密码不能为空", Toast.LENGTH_SHORT).show();

                }

                break;

            case R.id.regist_select_photo:
                Intent intent_image = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent_image, 1);
                break;


            case R.id.regist_btn_back:

                Intent intent = new Intent(Register_main.this, Login_main.class);
                startActivity(intent);
                finish();

                break;

            default:
                break;

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            //获取图片路径
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            imagePath = c.getString(columnIndex);
            usericon = BitmapFactory.decodeFile(imagePath);
            registSelectPhoto.setImageBitmap(usericon);

        }


    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    public static byte[] BitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


}

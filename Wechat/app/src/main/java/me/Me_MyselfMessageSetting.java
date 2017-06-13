package me;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.MyIcon;
import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MySex;
import static Xmpp.AppTemp.MySignature;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;
import static teacher.davisstore.com.wechat.Register_main.BitmapToBytes;

/**
 * Created by Administrator on 2017/4/16 0016.
 */

public class Me_MyselfMessageSetting extends Activity {

    @BindView(R.id.myself_now_icon)
    ImageView myselfNowIcon;
    @BindView(R.id.myself_now_nickname)
    TextView myselfNowNickname;
    @BindView(R.id.myself_now_sex)
    TextView myselfNowSex;
    @BindView(R.id.myself_now_signature)
    TextView myselfNowSignature;
    VCard card = new VCard();
    String encodeImage = null;
    byte[] usericon_bytes = new byte[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mefragment_myselfsetting);
        ButterKnife.bind(this);
        init();
    }

    private void init() {

        myselfNowIcon.setImageBitmap(MyIcon);
        myselfNowNickname.setText(MyNickName);

        try {
            card.load(con, MyUserName + "@" + con.getServiceName());
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        myselfNowSex.setText(MySex);
        myselfNowSignature.setText(MySignature);

    }

    @OnClick({R.id.myself_icon_setting, R.id.myself_nickname_setting, R.id.myself_sex_setting, R.id.myself_signature_setting, R.id.me_myself_back_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.myself_icon_setting:  //设置头像
                Intent intent_image = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent_image, 1);
                break;
            case R.id.myself_nickname_setting://设置昵称
                Intent intent_nickname = new Intent(this,Me_NickName_Setting.class);
                startActivityForResult(intent_nickname,2);
                break;
            case R.id.myself_sex_setting://设置性别
                String[] single_list = {"男", "女"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("性别");
                builder.setSingleChoiceItems(single_list, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String[] single_list = {"男", "女"};
                        MySex = single_list[which];
                        myselfNowSex.setText(MySex);
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

                break;
            case R.id.myself_signature_setting://设置个性签名
                Intent intent_signature = new Intent(this,Me_Signature_Setting.class);
                startActivityForResult(intent_signature,3);

                break;

            case R.id.me_myself_back_btn://返回键

                if (encodeImage == null ){
                    //重新覆盖数据库表一遍
                    byte[] avatar = card.getAvatar();
                    usericon_bytes = BitmapToBytes(MyIcon);
                    encodeImage = StringUtils.encodeBase64(usericon_bytes);

                    card.setEncodedImage(encodeImage);
                    card.setAvatar(avatar);
                    card.setProperty("PHOTO", encodeImage);
                    card.setNickName(MyNickName);
                    card.setFirstName(MySex);
                    card.setLastName(MySignature);
                }
                try {
                    card.save(con);
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String encodeImage = null;
        byte[] usericon_bytes = new byte[0];
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            //获取图片路径
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            Bitmap usericon = BitmapFactory.decodeFile(imagePath);
            myselfNowIcon.setImageBitmap(usericon);
            usericon_bytes = BitmapToBytes(usericon);
            encodeImage = StringUtils.encodeBase64(usericon_bytes);
            MyIcon = usericon;


        }else if (requestCode == 2 && resultCode == RESULT_OK ){  //修改NickName

            myselfNowNickname.setText(MyNickName);
        }else if (requestCode == 3 && resultCode == RESULT_OK ){
            myselfNowSignature.setText(MySignature);
        }

        if (encodeImage != null && encodeImage.length()>0 && requestCode == 1 ) { //把新的修改数据覆盖数据库表
            card.setEncodedImage(encodeImage);
            card.setAvatar(usericon_bytes);
            card.setProperty("PHOTO",encodeImage);
            card.setNickName(MyNickName);
            card.setFirstName(MySex);
            card.setLastName(MySignature);
        }

    }


}

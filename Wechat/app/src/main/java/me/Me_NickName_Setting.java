package me;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;

/**
 * Created by Administrator on 2017/4/16 0016.
 */

public class Me_NickName_Setting extends Activity {
    @BindView(R.id.me_edit_nickname)
    EditText meEditNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mefragment_nickname);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.me_nickname_back_btn, R.id.me_nickname_save})
    public void onClick(View view)  {
        switch (view.getId()) {
            case R.id.me_nickname_back_btn:
                finish();
                break;
            case R.id.me_nickname_save:
                if (meEditNickname.getText().toString().length()>0 && meEditNickname.getText().toString()!=null) {
                    MyNickName = meEditNickname.getText().toString();
                    setResult(RESULT_OK);
                    finish();
                }else {
                    Toast.makeText(Me_NickName_Setting.this,"昵称不能为空",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}

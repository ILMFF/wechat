package me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.packet.VCard;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.MyIcon;
import static Xmpp.AppTemp.MyNickName;
import static Xmpp.AppTemp.MyUserName;
import static Xmpp.AppTemp.con;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/2/11.
 */

public class MeFragment_main extends Fragment {

    @BindView(R.id.user_icon)
    ImageView userIcon;
    @BindView(R.id.user_name)
    TextView userName;
    public static Context BaseActivity_context = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflate = LayoutInflater.from(getActivity()).inflate(R.layout.mefragment_main, null);
        ButterKnife.bind(this, inflate);
        BaseActivity_context = getActivity();

        userIcon.setImageBitmap(MyIcon);
        userName.setText(MyNickName);

        return inflate;
    }




    @OnClick({R.id.me_mymessage_setting, R.id.me_setting,R.id.me_myfavorite})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.me_mymessage_setting:
                Intent intent = new Intent(getActivity(),Me_MyselfMessageSetting.class);
                startActivityForResult(intent,1);
                break;
            case R.id.me_setting:
                Intent systemintent = new Intent(getActivity(),Me_SystemSetting.class);
                startActivity(systemintent);
                break;

            case R.id.me_myfavorite:
                Intent favorite_intent = new Intent(getActivity(),MeFragment_favorite.class);
                startActivity(favorite_intent);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        userIcon.setImageBitmap(MyIcon);
        userName.setText(MyNickName);

    }
}

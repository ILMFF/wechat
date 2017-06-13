package weixin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import teacher.davisstore.com.wechat.R;
import weixin.adapter.Chat_BackgroundAdapter;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class Chat_BackgroundSelect extends Activity {

    @BindView(R.id.background_image)
    GridView backgroundImage;

    int background_image[] ={R.mipmap.translation_background1,R.mipmap.translation_background2
    ,R.mipmap.translation_background3,R.mipmap.translation_background4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_backgroundselect);
        ButterKnife.bind(this);


        backgroundImage.setAdapter(new Chat_BackgroundAdapter(this,background_image));


    }

    @OnClick(R.id.background_back_btn)
    public void onClick() {
        finish();
    }
}

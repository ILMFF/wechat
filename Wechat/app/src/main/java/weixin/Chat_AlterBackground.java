package weixin;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.chat_background;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class Chat_AlterBackground extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_alterbackground);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.chat_background_back_btn, R.id.friend_alter_background, R.id.friend_alter_systembackground})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_background_back_btn:
                finish();
                break;
            case R.id.friend_alter_background:  //从相册选择背景
                Intent intent_image = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent_image, 1);
                break;
            case R.id.friend_alter_systembackground: //系统默认提供几个背景图
//                Toast.makeText(Chat_AlterBackground.this,"ddddddddddd",Toast.LENGTH_SHORT).show();
                Intent intent_trandition = new Intent(Chat_AlterBackground.this,Chat_BackgroundSelect.class);
                startActivity(intent_trandition);
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
            String imagePath = c.getString(columnIndex);
            chat_background = BitmapFactory.decodeFile(imagePath);

        }


    }
}

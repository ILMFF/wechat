package Xmpp;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Administrator on 2017/3/8.
 */

public class GetBitmapToString {

    public static String getBitmapToString(Bitmap bitmap){


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        String SenderIcon = Base64.encodeToString(appicon, Base64.DEFAULT);

        return SenderIcon;

    }

}

package Xmpp;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/3/17.
 */

public class GetInOfBelong {

    private static String str;

    public  static String getInOfBelong(String belong){

//        Log.e("belong匹配",belong);

        Pattern pattern = Pattern.compile("(.+)/.+");

        Matcher matcher = pattern.matcher(belong);
        if (matcher.find())
        {
            str = matcher.group(1);
        }
        return str;

    }

}

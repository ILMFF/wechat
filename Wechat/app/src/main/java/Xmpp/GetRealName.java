package Xmpp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/2/28.
 */

public class GetRealName {
    //test1@wechat-server/Spark
    //把服务器名后缀去除
    public static String getRealName(String name) {
        String str = null;
        Pattern pattern = Pattern.compile("(.+)@(.*)");
        Matcher matcher = pattern.matcher(name);

        if (matcher.find()) {
            str = matcher.group(1);
        }
        return str;

    }

}

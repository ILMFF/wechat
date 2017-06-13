package Xmpp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/23.
 */

public class GetTime {
    private static SimpleDateFormat formatBuilder;

    public static String getDate(String format) {
        formatBuilder = new SimpleDateFormat(format);
        return formatBuilder.format(new Date());
    }

    public static String getDate() {
        return getDate("MM-dd  hh:mm:ss");
    }
}

package weixin.thread;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dao.AlterDataBase;

import static Xmpp.AppTemp.RecordNameSize;
import static Xmpp.AppTemp.dataBase;
import static Xmpp.AppTemp.onlinemsg_list;
import static Xmpp.GetRealName.getRealName;

/**
 * Created by GuHuiLing on 2017/5/19.
 */

public class AsyncDisplayNum extends AsyncTask<Void, Void, Boolean> {




    @Override
    protected Boolean doInBackground(Void... params) {

        List<String> namelist = new ArrayList();
        boolean flag = true;
        //计算有多少个发来未读消息的人
        for (int i = 0; i < onlinemsg_list.size(); i++) {

            for (int j = 0; j < namelist.size() ; j++) {

                if (namelist.get(j).equals(getRealName(onlinemsg_list.get(i).name))){

                    flag = false;
                    break;
                }

            }

            if (flag){
                namelist.add(getRealName(onlinemsg_list.get(i).name));
            }
            flag = true;
        }

   /*     Log.e("未读消息人",namelist.size()+"");
        for (int i = 0; i <namelist.size() ; i++) {

            Log.e("namelist",namelist.get(i));

        }*/

        //计算有多少个聊天记录的人
        AlterDataBase alter = new AlterDataBase(dataBase.getReadableDatabase());
        Cursor cursor = alter.QueryAllRecord();
        List<String> RecordName = new ArrayList();
        boolean bool = true;
        if (cursor.moveToFirst()){

            do {

                String senderName = cursor.getString(cursor.getColumnIndex("SenderName"));
                for (int i = 0; i <RecordName.size() ; i++) {
//                    Log.e("比较",senderName+"             "+RecordName.get(i));
                    if (senderName.equals(RecordName.get(i))){
                        bool = false;
                        break;
                    }

                }
                if (bool){
//                    Log.e("比较11111",senderName);
                    RecordName.add(senderName) ;
                }

                bool = true;

            }while (cursor.moveToNext());

        }
        cursor.close();

     /*   Log.e("聊天记录人： ",RecordName.size()+"");

        for (int i = 0; i <RecordName.size() ; i++) {

            Log.e("Record_Name111111",RecordName.get(i));

        }*/

        //计算有未读消息人和聊天记录人的合集,重复的就不在添加,得到的结果为显示总数
        boolean temp = true;
        for (int i = 0; i <namelist.size() ; i++) {

            for (int j = 0; j <RecordName.size() ; j++) {

                if (RecordName.get(j).equals(namelist.get(i))) {
                    temp = false;
                    break;
                }
            }
            if (temp){

                RecordName.add(namelist.get(i));

            }
            temp = true;

        }
    /*    for (int i = 0; i <RecordName.size() ; i++) {

            Log.e("RecordName",RecordName.get(i));

        }*/


//        Log.e("应该显示的聊天列表记录数量","             sum: "+RecordName.size());

        RecordNameSize = RecordName.size();
        return true;
    }


    @Override
    protected void onPostExecute(Boolean bool) {
        cancel(true);

    }


}

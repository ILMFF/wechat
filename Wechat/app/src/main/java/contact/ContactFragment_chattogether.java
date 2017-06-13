package contact;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import Xmpp.GetRealName;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import contact.adpter.ChatRoomAdapter;
import dao.AlterDataBase;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.RoomName_List;
import static Xmpp.AppTemp.dataBase;
import static Xmpp.GetRealName.getRealName;


/**
 * Created by Administrator on 2017/5/3 0003.
 */

public class ContactFragment_chattogether extends Activity {

    @BindView(R.id.contact_chatroom_list)
    ListView contactChatroomList;
    private AlterDataBase alterDataBase;
    List<String> chatroom_list;
    public static ChatRoomAdapter roomAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactfragment_chattogether);
        ButterKnife.bind(this);
        init();

    }

    private void init() {

    /*    chatroom_list = new ArrayList<>();
        alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
        Cursor cursor = alterDataBase.GetQueryChatRooms();

        if (cursor.moveToFirst()){

            do {

                String roomName = cursor.getString(cursor.getColumnIndex("RoomName"));

                if (roomName != null && roomName.length()>0){
                    chatroom_list.add(getRealName(roomName));
                    Log.e("已加入的聊天室",roomName);
                }

            }while (cursor.moveToNext());
        }*/

        roomAdapter = new ChatRoomAdapter(this);
        contactChatroomList.setAdapter(roomAdapter);
    }

    @OnClick(R.id.contact_chatroom_back_btn)
    public void onClick() {
        finish();
    }
}

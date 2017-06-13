package weixin;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dao.AlterDataBase;
import teacher.davisstore.com.wechat.R;
import weixin.adapter.Chat_CheckRecordListAdapter;
import weixin.bean.MessageBean;

import static Xmpp.AppTemp.dataBase;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class Chat_CheckChatRecord extends Activity {

    @BindView(R.id.date_select_layout)
    RelativeLayout dateSelectLayout;
    @BindView(R.id.chatrecord_list)
    ListView chatrecordList;
    private int year;
    private int month;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_dateselect);
        ButterKnife.bind(this);


    }

    @OnClick({R.id.chat_dateselect_back_btn, R.id.date_select_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_dateselect_back_btn:
                finish();
                break;
            case R.id.date_select_layout:
                DatePickerDialog dpd = new DatePickerDialog(Chat_CheckChatRecord.this, Datelistener, year, month, day);
                dpd.show();//显示DatePickerDialog组件

                break;
        }
    }

    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
        /**params：view：该事件关联的组件
         * params：myyear：当前选择的年
         * params：monthOfYear：当前选择的月
         * params：dayOfMonth：当前选择的日
         */
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {

            //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
            year = myyear;
            month = monthOfYear + 1;
            day = dayOfMonth;
            String date_month = null;
            String date_day = null;
            //更新日期
            if (month < 10) {
                date_month = "0" + month;
            } else {
                date_month = month + "";
            }
            if (day < 10) {
                date_day = "0" + day;
            } else {
                date_day = day + "";
            }
            String checkdate = date_month + "-" + date_day;
            Log.e("当前选择的日期", month + "-" + day);
            Log.e("当前选择的日期", date_month + "-" + date_day);
            AlterDataBase alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());
            List<MessageBean> messageBeen = alterDataBase.QueryTargetRecord(checkdate);

            dateSelectLayout.setVisibility(View.GONE);
            chatrecordList.setVisibility(View.VISIBLE);
            Chat_CheckRecordListAdapter adapter = new Chat_CheckRecordListAdapter(messageBeen,Chat_CheckChatRecord.this);
            chatrecordList.setAdapter(adapter);
        }

    };
}

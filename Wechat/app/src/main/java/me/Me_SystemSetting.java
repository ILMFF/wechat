package me;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dao.AlterDataBase;
import teacher.davisstore.com.wechat.BaseActivity;
import teacher.davisstore.com.wechat.Login_main;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.con;
import static Xmpp.AppTemp.dataBase;
import static me.MeFragment_main.BaseActivity_context;



public class Me_SystemSetting extends Activity {

    private AlterDataBase alterDataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mefragment_system_setting);
        ButterKnife.bind(this);
        alterDataBase = new AlterDataBase(dataBase.getReadableDatabase());

    }


    @OnClick({R.id.system_setting_back_btn, R.id.system_clear_chatrecord, R.id.system_logout, R.id.system_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.system_setting_back_btn:
                finish();
                break;
            case R.id.system_clear_chatrecord:  //清空所有聊天记录

                final AlertDialog.Builder delete_builder = new AlertDialog.Builder(this);
                delete_builder.setMessage("确定要清除本用户所有聊天记录吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alterDataBase.DeleteAllRecord();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();  //关闭对话框
                    }
                });
                AlertDialog delete_dialog = delete_builder.create();
                delete_dialog.show();
                break;


            case R.id.system_logout: //注销
                final AlertDialog.Builder logout_builder = new AlertDialog.Builder(this);
                logout_builder.setMessage("确定要注销并切换账号？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent login_intent = new Intent(Me_SystemSetting.this, Login_main.class);
                        startActivity(login_intent);
                        finish();
                        ((Activity)BaseActivity_context).finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();  //关闭对话框
                    }
                });
                AlertDialog logout_dialog = logout_builder.create();
                logout_dialog.show();

                break;
            case R.id.system_exit://退出
                final AlertDialog.Builder exit_builder = new AlertDialog.Builder(this);
                exit_builder.setMessage("确定要退出？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        ((Activity)BaseActivity_context).finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();  //关闭对话框
                    }
                });
                AlertDialog exit_dialog = exit_builder.create();
                exit_dialog.show();

                break;
        }
    }
}

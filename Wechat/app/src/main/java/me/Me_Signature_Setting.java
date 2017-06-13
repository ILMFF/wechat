package me;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import teacher.davisstore.com.wechat.R;

import static Xmpp.AppTemp.MySignature;

/**
 * Created by Administrator on 2017/4/16 0016.
 */

public class Me_Signature_Setting extends Activity {

    @BindView(R.id.me_edit_signature)
    EditText meEditSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mefragment_signature);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.me_signature_back_btn, R.id.me_signature_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.me_signature_back_btn:
                finish();
                break;
            case R.id.me_signature_save:
                if (meEditSignature.getText().toString().length()>0 && meEditSignature.getText().toString()!=null) {
                    MySignature = meEditSignature.getText().toString();
                    setResult(RESULT_OK);
                    finish();
                }else {
                    MySignature = "未填写";
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }
}

package com.twd.twdsettings.universal;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.twdsettings.R;
import com.twd.twdsettings.bean.InputItem;

public class UniversalActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private LinearLayout LL_input;
    private LinearLayout LL_language;
    private TextView tv_input;
    private TextView tv_language;
    private TextView tv_inputCurrent;
    private TextView tv_languageCurrent;

    private ImageView arrow_input;
    private ImageView arrow_language;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universal);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();

        String selectedInputMethodId = Settings.Secure.getString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD);
        if (selectedInputMethodId.contains("sogou")){
            tv_inputCurrent.setText("搜狗输入法");
        } else if (selectedInputMethodId.contains("inputmethod.pinyin")) {
            tv_inputCurrent.setText("谷歌拼音输入法");
        } else if (selectedInputMethodId.contains("inputmethod.latin")) {
            tv_inputCurrent.setText("Android键盘(AOSP)");
        }

    }

    private void initView(){
        LL_input = findViewById(R.id.universal_LL_input);
        LL_language = findViewById(R.id.universal_LL_language);
        tv_input = findViewById(R.id.universal_tv_input);
        tv_language = findViewById(R.id.universal_tv_language);
        tv_inputCurrent = findViewById(R.id.universal_tv_inputcurrent);
        tv_languageCurrent = findViewById(R.id.universal_tv_languagecurrent);
        arrow_input = findViewById(R.id.arrow_input);
        arrow_language = findViewById(R.id.arrow_language);

        LL_input.setFocusable(true);
        LL_input.setFocusableInTouchMode(true);
        LL_language.setFocusable(true);
        LL_language.setFocusableInTouchMode(true);

        LL_input.setOnFocusChangeListener(this);
        LL_language.setOnFocusChangeListener(this);

        LL_input.setOnClickListener(this);
        LL_language.setOnClickListener(this);

        LL_input.requestFocus();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v.getId() == R.id.universal_LL_input){
            intent = new Intent(this,UniversalInputActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
           changeItem(v);
        }else {
            resetItem(v);
        }
    }

    private void changeItem(View view){
        view.setBackgroundResource(R.drawable.bg_sel);
        if (view.getId() == R.id.universal_LL_input){
            tv_input.setTextColor(getResources().getColor(R.color.sel_blue));
            tv_inputCurrent.setTextColor(getResources().getColor(R.color.sel_blue));
            arrow_input.setImageResource(R.drawable.arrow_sel);
        } else if (view.getId() == R.id.universal_LL_language) {
            tv_language.setTextColor(getResources().getColor(R.color.sel_blue));
            tv_languageCurrent.setTextColor(getResources().getColor(R.color.sel_blue));
            arrow_language.setImageResource(R.drawable.arrow_sel);
        }
    }

    private void resetItem(View view){
        view.setBackgroundColor(getResources().getColor(R.color.background_color));
        if (view.getId() == R.id.universal_LL_input){
            tv_input.setTextColor(getResources().getColor(R.color.white));
            tv_inputCurrent.setTextColor(getResources().getColor(R.color.white));
            arrow_input.setImageResource(R.drawable.arrow);
        } else if (view.getId() == R.id.universal_LL_language) {
            tv_language.setTextColor(getResources().getColor(R.color.white));
            tv_languageCurrent.setTextColor(getResources().getColor(R.color.white));
            arrow_language.setImageResource(R.drawable.arrow);
        }
    }
}

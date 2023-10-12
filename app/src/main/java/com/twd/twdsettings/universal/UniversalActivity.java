package com.twd.twdsettings.universal;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.twdsettings.R;
import com.twd.twdsettings.SystemPropertiesUtils;
import com.twd.twdsettings.bean.InputItem;

import java.util.Locale;

public class UniversalActivity extends AppCompatActivity implements  View.OnClickListener {

    private LinearLayout LL_input;
    private LinearLayout LL_language;
    private TextView tv_input;
    private TextView tv_language;
    private TextView tv_inputCurrent;
    private TextView tv_languageCurrent;

    private ImageView arrow_input;
    private ImageView arrow_language;
    String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        switch (theme_code){
            case "0": //冰激蓝
                this.setTheme(R.style.Theme_IceBlue);
                break;
            case "1": //木棉白
                this.setTheme(R.style.Theme_KapokWhite);
                break;
            case "2": //星空蓝
                this.setTheme(R.style.Theme_StarBlue);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universal);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        //当前输入法
        String selectedInputMethodId = Settings.Secure.getString(getContentResolver(),Settings.Secure.DEFAULT_INPUT_METHOD);
        if (selectedInputMethodId.contains("sogou")){
            tv_inputCurrent.setText(getString(R.string.inputMethod_value_sougou));
        } else if (selectedInputMethodId.contains("inputmethod.pinyin")) {
            tv_inputCurrent.setText(getString(R.string.inputMethod_value_google));
        } else if (selectedInputMethodId.contains("inputmethod.latin")) {
            tv_inputCurrent.setText(getString(R.string.inputMethod_value_Aosp));
        }

        //当前语言
        Locale currentLocale = getResources().getConfiguration().locale;
        Configuration configuration = new Configuration();
        configuration.setLocale(currentLocale);
        String name = configuration.locale.getDisplayName(currentLocale);
        String currentLanguage = name+"_"+currentLocale.getLanguage()+"_"+currentLocale.getCountry();
        Log.i("Language", "onResume: currentLanguage = " + currentLanguage);

        if (currentLanguage.contains("zh_CN")){
            tv_languageCurrent.setText("简体中文");
        } else if (currentLanguage.contains("zh_TW")) {
            tv_languageCurrent.setText("繁體中文");
        } else  if(currentLanguage.equals("English_en_")){
            tv_languageCurrent.setText("English");
        } else if (currentLanguage.contains("ja_JP")) {
            tv_languageCurrent.setText("日本語");
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
        }else {
            intent = new Intent(this,UniversalLanguageActivity.class);
            startActivity(intent);
        }
    }
}

package com.twd.twdsettings.universal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.twd.twdsettings.R;
import com.twd.twdsettings.SystemPropertiesUtils;
import com.twd.twdsettings.bean.LanguageBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UniversalLanguageActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final String TAG = UniversalLanguageActivity.class.getName();

    private ListView language_listView;
    List<LanguageBean> languageBeans = new ArrayList<>();
    private final Context context = this;

    LanguageItemAdapter languageItemAdapter ;
    String theme_code = SystemPropertiesUtils.getPropertyColor("persist.sys.background_blue","0");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_universal_language);
        initView();
        Locale currentLocale = getResources().getConfiguration().locale;
        String currentLanguageCode = currentLocale.getLanguage()+"_"+currentLocale.getCountry();
        String currentLanguageName = currentLocale.getDisplayName();
        Log.i(TAG, "onCreate: currentLanguageCode = " + currentLanguageCode + "Name = " + currentLanguageName);
    }

    private void initView(){
        language_listView = findViewById(R.id.universal_language_listView);

        //TODO:检测当前系统支持的所有语言
        Locale[] availableLocales = Locale.getAvailableLocales();
        List<String> languageList = new ArrayList<>();

        //获取当前的语言code
        Locale currentLocale = getResources().getConfiguration().locale;
        String currentLanguageCode = currentLocale.getLanguage()+"_"+currentLocale.getCountry();

        for (Locale locale : availableLocales) {
            String language =locale.getDisplayName(locale)+"_"+locale.getLanguage()+"_"+locale.getCountry();
            String languageCode = locale.getLanguage()+"_"+locale.getCountry();
            if (!language.isEmpty() && !languageList.contains(language)) {
                languageList.add(language);
            }
            LanguageBean languageBean = null;
            Map<String,String> languageMap = new HashMap<>();
            languageMap.put("zh_CN","简体中文"); //简体中文
            languageMap.put("zh_TW","繁體中文"); //繁体中文
            languageMap.put("en_","English"); //英语
            languageMap.put("ja_JP","日本語"); //日语
            languageMap.put("ko_KR","한국어"); //韩语-韩国
            languageMap.put("th_TH","ไทย (ไทย)"); //泰语-泰国
            languageMap.put("hi_IN","हिन्दी (भारत)"); //印地语-印度
            languageMap.put("fr_FR","français (France)"); //法语-法国
            languageMap.put("de_DE","Deutsch (Deutschland)"); //德语-德国
            languageMap.put("it_IT","italiano (Italia)"); //意大利语-意大利
            languageMap.put("ru_RU","русский (Россия)"); //俄语-俄罗斯
            languageMap.put("es_ES","español (España)"); //西班牙语-西班牙
            languageMap.put("pt_PT","português (Portugal)"); //葡萄牙语-葡萄牙
            languageMap.put("ar_SA","العربية (المملكة العربية السعودية)");//阿拉伯语-沙特阿拉伯
            languageMap.put("fa_IR","فارسی (ایران)");//波斯语-伊朗
            languageMap.put("tr_TR","Türkçe (Türkiye)");//土耳其语-土耳其

            List<String> supportedLanguages = Arrays.asList("zh_CN","zh_TW","en_","ja_JP"
                                                    ,"ko_KR","th_TH","hi_IN","fr_FR","de_DE","it_IT"
                                                    ,"ru_RU","es_ES","pt_PT","ar_SA","fa_IR","tr_TR");
            for (String language_sup : supportedLanguages){
                if (language_sup.equals(languageCode)){
                    String languageName = languageMap.get(language_sup);
                    languageBean = new LanguageBean(languageName,languageCode,false);
                    if (languageCode.equals(currentLanguageCode)){
                        languageBean.setSelect(true);
                    }
                    languageBeans.add(languageBean);
                }
            }
        }
        //添加语言
        for (String language : languageList) {
            System.out.println(language);
        }
        languageItemAdapter = new LanguageItemAdapter(context,languageBeans);
        language_listView.setAdapter(languageItemAdapter);

        language_listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                languageItemAdapter.setFocusedItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        language_listView.setOnItemClickListener(this::onItemClick);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //创建提示框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.dialog_changeLanguage_title));
        builder.setMessage(getResources().getString(R.string.dialog_changeLanguage_message));

        builder.setPositiveButton(getResources().getString(R.string.dialog_changeLanguage_btConfirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               //进行切换和重启
                LanguageBean languageBean = languageBeans.get(position);
                String indexLanguage = languageBean.getLanguageCode(); // en_US ; zh_CN ; zh_TW
                showToast(indexLanguage);
                /*切换语言方法*/
                if (indexLanguage.equals("zh_CN") ){
                    changeSystemLanguage(Locale.SIMPLIFIED_CHINESE);
                } else if (indexLanguage.equals("zh_TW") ) {
                    changeSystemLanguage(Locale.TRADITIONAL_CHINESE);
                } else if (indexLanguage.equals("en_")) {
                    changeSystemLanguage(Locale.ENGLISH);
                } else if (indexLanguage.equals("ja_JP")) {
                    changeSystemLanguage(Locale.JAPAN);
                } else if (indexLanguage.equals("ko_KR")) {
                    changeSystemLanguage(Locale.KOREA);
                } else if (indexLanguage.equals("th_TH")) {
                    changeSystemLanguage(new Locale("th","TH"));
                } else if (indexLanguage.equals("hi_IN")) {
                    changeSystemLanguage(new Locale("hi","IN"));
                } else if (indexLanguage.equals("fr_FR")) {
                    changeSystemLanguage(Locale.FRANCE);
                } else if (indexLanguage.equals("de_DE")) {
                    changeSystemLanguage(Locale.GERMANY);
                } else if (indexLanguage.equals("it_IT")) {
                    changeSystemLanguage(Locale.ITALY);
                } else if (indexLanguage.equals("ru_RU")) {
                    changeSystemLanguage(new Locale("ru","RU"));
                } else if (indexLanguage.equals("es_ES")) {
                    changeSystemLanguage(new Locale("es","ES"));
                } else if (indexLanguage.equals("pt_PT")) {
                    changeSystemLanguage(new Locale("pt","PT"));
                } else if (indexLanguage.equals("ar_SA")) {
                    changeSystemLanguage(new Locale("ar","SA"));
                }

                // 关闭当前应用程序
                finish();

                // 重新启动应用程序
                Intent restartIntent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(restartIntent);

            }
        });

        builder.setNegativeButton(getResources().getString(R.string.dialog_changeLanguage_btCancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    /*
     * 修改系统语言的方法
     * 需要设置系统app获取系统权限才能执行*/
    public void changeSystemLanguage(Locale locale){
        if (locale != null){
            try {
                Object objIActMag;
                Class clzIActMag = Class.forName("android.app.IActivityManager");
                Class clzActMagNative = Class
                        .forName("android.app.ActivityManagerNative");
                //amn = ActivityManagerNative.getDefault();
                Method mtdActMagNative$getDefault = clzActMagNative
                        .getDeclaredMethod("getDefault");
                objIActMag = mtdActMagNative$getDefault.invoke(clzActMagNative);
                // objIActMag = amn.getConfiguration();
                Method mtdIActMag$getConfiguration = clzIActMag
                        .getDeclaredMethod("getConfiguration");
                Configuration config = (Configuration) mtdIActMag$getConfiguration
                        .invoke(objIActMag);
                // set the locale to the new value
                config.locale = locale;
                //持久化  config.userSetLocale = true;
                Class clzConfig = Class
                        .forName("android.content.res.Configuration");
                java.lang.reflect.Field userSetLocale = clzConfig
                        .getField("userSetLocale");
                userSetLocale.set(config, true);
                // 此处需要声明权限:android.permission.CHANGE_CONFIGURATION
                // 会重新调用 onCreate();
                Class[] clzParams = {Configuration.class};
                // objIActMag.updateConfiguration(config);
                Method mtdIActMag$updateConfiguration = clzIActMag
                        .getDeclaredMethod("updateConfiguration", clzParams);
                mtdIActMag$updateConfiguration.invoke(objIActMag, config);
                BackupManager.dataChanged("com.android.providers.settings");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class LanguageItemAdapter extends ArrayAdapter<LanguageBean> {

        private LayoutInflater inflater;

        boolean isSelected;

        private int focusedItem = 0;
        public LanguageItemAdapter(@NonNull Context context, List<LanguageBean> languageBeans) {
            super(context, 0,languageBeans);
            inflater = LayoutInflater.from(context);
        }

        public void setFocusedItem(int position){
            focusedItem = position;
            notifyDataSetChanged();
        }

        @SuppressLint("ResourceType")
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null){
                //加载自定义item的布局
                itemView = inflater.inflate(R.layout.universal_language_list_item,parent,false);
            }

            //绑定自定义布局中的控件
            TextView languageNameTV = itemView.findViewById(R.id.languageName);
            ImageView languageSelectIV = itemView.findViewById(R.id.iv_languageSelect);
            int bgResIdSel = 0; int bgResIdUnSel = 0;
            int nameTvSel = 0; int nameTvUnSel = 0;
            int IVSelectSel = 0; int IVSelectUnSel = 0;
            switch (theme_code){
                case "0":
                    bgResIdSel = R.color.customWhite; bgResIdUnSel = Color.TRANSPARENT;
                    nameTvSel = R.color.sel_blue; nameTvUnSel = R.color.customWhite;
                    IVSelectSel = R.drawable.input_selected_iceblue_sel;IVSelectUnSel = R.drawable.input_selected_iceblue_unsel;
                    break;
                case "1":
                    bgResIdSel = R.drawable.red_border; bgResIdUnSel = R.drawable.black_border;
                    nameTvSel = R.color.text_red_new; nameTvUnSel = R.color.black;
                    IVSelectSel = R.drawable.input_selected_kapokwhite_sel;IVSelectUnSel = R.drawable.input_selected_kapokwhite_unsel;
                    break;
                case "2":
                    bgResIdSel = R.color.text_red_new;bgResIdUnSel = Color.TRANSPARENT;
                    nameTvSel = R.color.customWhite;nameTvUnSel = R.color.customWhite;
                    IVSelectSel = R.drawable.input_selected_iceblue_unsel;IVSelectUnSel = R.drawable.input_selected_iceblue_unsel;
                    break;
            }
            //给控件赋值
            LanguageBean languageBean = getItem(position);
            if (languageBean != null){
                languageNameTV.setText(languageBean.getLanguageName());
                isSelected = languageBean.isSelect();
                if (isSelected){
                    languageSelectIV.setImageResource(IVSelectSel);
                }else {
                    languageSelectIV.setImageResource(R.drawable.unselected);
                }
            }

            //设置聚焦效果
            if (position == focusedItem){
                itemView.setBackgroundResource(bgResIdSel);
                languageNameTV.setTextColor(ContextCompat.getColor(context,nameTvSel));
                if (languageBean.isSelect()){
                    languageSelectIV.setImageResource(IVSelectSel);
                }else {
                    languageSelectIV.setImageResource(R.drawable.unselected);
                }
            }else {
                itemView.setBackgroundResource(bgResIdUnSel);
                languageNameTV.setTextColor(ContextCompat.getColor(context,nameTvUnSel));
                if (languageBean.isSelect()){
                    languageSelectIV.setImageResource(IVSelectUnSel);
                }else {
                    languageSelectIV.setImageResource(R.drawable.unselected);
                }
            }
            return itemView;
        }
    }

    private void showToast(String text) {
        LayoutInflater inflater = getLayoutInflater();
        View customToastView = inflater.inflate(R.layout.custom_toast, null);
        TextView textView = customToastView.findViewById(R.id.toast_text);
        textView.setText(text);
        Toast customToast = new Toast(getApplicationContext());
        customToast.setView(customToastView);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.show();
    }
}
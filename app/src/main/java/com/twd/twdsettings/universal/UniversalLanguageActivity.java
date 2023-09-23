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

import com.twd.twdsettings.R;
import com.twd.twdsettings.SystemPropertiesUtils;
import com.twd.twdsettings.bean.LanguageBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
           // String language = locale.getDisplayLanguage(locale)+"_"+locale.getLanguage()+"_"+locale.getCountry()+"_"+locale.getDisplayName();
            String language =locale.getDisplayName(locale)+"_"+locale.getLanguage()+"_"+locale.getCountry();
            String languageCode = locale.getLanguage()+"_"+locale.getCountry();
            if (!language.isEmpty() && !languageList.contains(language)) {
                languageList.add(language);
            }
            LanguageBean languageBean = null;
            if (language.contains("zh_CN")){
                languageBean = new LanguageBean("简体中文",languageCode,false);
                if (languageCode.equals(currentLanguageCode)){
                    languageBean.setSelect(true);
                }
                languageBeans.add(languageBean);
            } else if (language.contains("zh_TW")) {
                languageBean = new LanguageBean("繁體中文",languageCode,false);
                if (languageCode.equals(currentLanguageCode)){
                    languageBean.setSelect(true);
                }
                languageBeans.add(languageBean);
            } else if (language.equals("English (United States)_en_US")) {
                languageBean = new LanguageBean("English",languageCode,false);
                if (languageCode.equals(currentLanguageCode)){
                    languageBean.setSelect(true);
                }
                languageBeans.add(languageBean);
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
                // changeSystemLanguage(indexLanguage);
                // 创建一个立即触发的定时任务，用于重新启动应用程序
               /* Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, pendingIntent);
                }*/

                // 退出当前活动
//                finish();

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
    public void changeSystemLanguage(String language) { // "zh_CN"
        if (language != null) {
            try {
                //final IActivityManager am = ActivityManagerNative.getDefault();
                Class classActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
                Method getDefault = classActivityManagerNative.getDeclaredMethod("getDefault");
                Object objIActivityManager = getDefault.invoke(classActivityManagerNative);

                //final Configuration config = am.getConfiguration();
                Class classIActivityManager = Class.forName("android.app.IActivityManager");
                Method getConfiguration = classIActivityManager.getDeclaredMethod("getConfiguration");
                Configuration config = (Configuration) getConfiguration.invoke(objIActivityManager);

                //config.setLocales(locales);
                config.setLocale(new Locale(language));

                //config.userSetLocale = true;
                Class clzConfig = Class
                        .forName("android.content.res.Configuration");
                java.lang.reflect.Field userSetLocale = clzConfig
                        .getField("userSetLocale");
                userSetLocale.set(config, true);

                //am.updatePersistentConfiguration(config);
                Class[] clzParams = {Configuration.class};
                Method updatePersistentConfiguration =
                        classIActivityManager.getDeclaredMethod("updatePersistentConfiguration", clzParams);
                updatePersistentConfiguration.invoke(objIActivityManager, config);

                BackupManager.dataChanged("com.android.providers.settings");
            } catch (Exception exception) {
                exception.printStackTrace();
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
}
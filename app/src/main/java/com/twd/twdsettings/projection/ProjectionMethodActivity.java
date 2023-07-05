package com.twd.twdsettings.projection;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.twd.twdsettings.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;

/**
 * 投影方式 二级菜单
 * 四个方式单选 选中项显示在上层菜单中
 */
public class ProjectionMethodActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProjectionActivity";
    private static final String PATH_CONTROL_MIPI = "/sys/ir/control_mipi";
    private static final String PATH_DEV_PRO_INFO = "/dev/pro_info";
    private static final int VALUE_POSITIVE_DRESS = 0;
    private static final int VALUE_DRESSING_REAR = 2;
    private static final int VALUE_HOISTING_FRONT = 3;
    private static final int VALUE_HOISTING_REAR = 1;

    private RelativeLayout pos_pos;//正装正投   //0
    private RelativeLayout pos_neg;//正装背投   //2
    private RelativeLayout neg_pos;//吊装正投   //3
    private RelativeLayout neg_neg;//吊装背投   //1

    private RelativeLayout background;

    private ImageView sel_pos_pos;
    private ImageView sel_pos_neg;
    private ImageView sel_neg_pos;
    private ImageView sel_neg_neg;

    private TextView tv_pos_pos;
    private TextView tv_pos_neg;
    private TextView tv_neg_pos;
    private TextView tv_neg_neg;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String POS_POS = "pos_pos";
    private static final String POS_NEG = "pos_neg";
    private static final String NEG_POS = "neg_pos";
    private static final String NEG_NEG = "neg_neg";

    //项是否被勾选的标记
    public static boolean pos_pos_check;
    public static boolean pos_neg_check;
    public static boolean neg_pos_check;
    public static boolean neg_neg_check;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projection_method);
        initView();
    }
    /*页面初始化*/
    private void initView(){
        pos_pos = (RelativeLayout) findViewById(R.id.pro_pos_pos);
        pos_neg = (RelativeLayout) findViewById(R.id.pro_pos_neg);
        neg_pos = (RelativeLayout) findViewById(R.id.pro_neg_pos);
        neg_neg = (RelativeLayout) findViewById(R.id.pro_neg_neg);
        background = (RelativeLayout) findViewById(R.id.background);

        tv_pos_pos = (TextView) findViewById(R.id.tv_pos_pos);
        tv_pos_neg = (TextView) findViewById(R.id.tv_pos_neg);
        tv_neg_pos = (TextView) findViewById(R.id.tv_neg_pos);
        tv_neg_neg = (TextView) findViewById(R.id.tv_neg_neg);

        sel_pos_pos = (ImageView) findViewById(R.id.sel_pos_pos);
        //     sel_pos_pos.setImageResource(R.drawable.selected);
        sel_pos_neg = (ImageView) findViewById(R.id.sel_pos_neg);
        sel_neg_pos = (ImageView) findViewById(R.id.sel_neg_pos);
        sel_neg_neg = (ImageView) findViewById(R.id.sel_neg_neg);

        int mode = readProjectionValue(PATH_DEV_PRO_INFO);
        Log.i("yangxin","mode="+mode);
        if (mode == VALUE_POSITIVE_DRESS) {
            sel_pos_pos.setImageResource(R.drawable.selected);
        } else if (mode == VALUE_DRESSING_REAR) {
            sel_pos_neg.setImageResource(R.drawable.selected);
        } else if (mode == VALUE_HOISTING_FRONT) {
            sel_neg_pos.setImageResource(R.drawable.selected);
        } else if (mode == VALUE_HOISTING_REAR) {
            sel_neg_neg.setImageResource(R.drawable.selected);
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        boolean pos_pos_isChecked = prefs.getBoolean(POS_POS,false);
        if (pos_pos_isChecked) sel_pos_pos.setImageResource(R.drawable.selected); else sel_pos_pos.setImageResource(R.drawable.unselected);
        boolean pos_neg_isChecked = prefs.getBoolean(POS_NEG,false);
        if (pos_neg_isChecked) sel_pos_neg.setImageResource(R.drawable.selected); else sel_pos_neg.setImageResource(R.drawable.unselected);
        boolean neg_pos_isChecked = prefs.getBoolean(NEG_POS,false);
        if (neg_pos_isChecked) sel_neg_pos.setImageResource(R.drawable.selected); else sel_neg_pos.setImageResource(R.drawable.unselected);
        boolean neg_neg_isChecked = prefs.getBoolean(NEG_NEG,false);
        if (neg_neg_isChecked) sel_neg_neg.setImageResource(R.drawable.selected); else sel_neg_neg.setImageResource(R.drawable.unselected);

        /* 设置监听 */
        pos_pos.setOnClickListener(this);
        pos_neg.setOnClickListener(this);
        neg_pos.setOnClickListener(this);
        neg_neg.setOnClickListener(this);

        pos_pos.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.test_red);
                    background.setBackgroundResource(R.drawable.bg_pos_pos);
                } else {
                    v.setBackgroundResource(R.drawable.test);
                }
            }
        });
        pos_neg.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.test_red);
                    background.setBackgroundResource(R.drawable.bg_pos_neg);
                } else {
                    v.setBackgroundResource(R.drawable.test);
                }
            }
        });
        neg_pos.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.test_red);
                    background.setBackgroundResource(R.drawable.bg_neg_pos);
                } else {
                    v.setBackgroundResource(R.drawable.test);
                }
            }
        });
        neg_neg.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.test_red);
                    background.setBackgroundResource(R.drawable.bg_neg_neg);
                } else {
                    v.setBackgroundResource(R.drawable.test);
                }
            }
        });

    }

    /**
     * 保存选中状态
     * @param items
     */
    private void setSelect(boolean[] items){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(POS_POS,items[0]);
        editor.putBoolean(POS_NEG,items[1]);
        editor.putBoolean(NEG_POS,items[2]);
        editor.putBoolean(NEG_NEG,items[3]);
        editor.apply();
    }

    /* 点击事件 */
    @Override
    public void onClick(View v) {
        boolean[] isChecked = new boolean[4];
        int id = v.getId();
        if (id == R.id.pro_pos_pos) {
            setSelImageSource(R.drawable.selected, R.drawable.unselected, R.drawable.unselected, R.drawable.unselected);
            isChecked[0] = true;
            isChecked[1] = false;
            isChecked[2] = false;
            isChecked[3] = false;
            setSelect(isChecked);
            setItemsBoolean(true, false, false, false);
            setProjectionMode(VALUE_POSITIVE_DRESS);
        } else if (id == R.id.pro_pos_neg) {
            setSelImageSource(R.drawable.unselected, R.drawable.selected, R.drawable.unselected, R.drawable.unselected);
            isChecked[0] = false;
            isChecked[1] = true;
            isChecked[2] = false;
            isChecked[3] = false;
            setSelect(isChecked);
            setItemsBoolean(false, true, false, false);
            setProjectionMode(VALUE_DRESSING_REAR);
        } else if (id == R.id.pro_neg_pos) {
            setSelImageSource(R.drawable.unselected, R.drawable.unselected, R.drawable.selected, R.drawable.unselected);
            isChecked[0] = false;
            isChecked[1] = false;
            isChecked[2] = true;
            isChecked[3] = false;
            setSelect(isChecked);
            setItemsBoolean(false, false, true, false);
            setProjectionMode(VALUE_HOISTING_FRONT);
        } else if (id == R.id.pro_neg_neg) {
            setSelImageSource(R.drawable.unselected, R.drawable.unselected, R.drawable.unselected, R.drawable.selected);
            isChecked[0] = false;
            isChecked[1] = false;
            isChecked[2] = false;
            isChecked[3] = true;
            setSelect(isChecked);
            setItemsBoolean(false, false, false, true);
            setProjectionMode(VALUE_HOISTING_REAR);
        }
    }

    public static void setProjectionMode(int mode) {

        writeFile(PATH_CONTROL_MIPI, String.valueOf(mode));
        writeFile(PATH_DEV_PRO_INFO, String.valueOf(mode));
    }

    private static int readProjectionValue(String path) {
        File file = new File(path);
        if (file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                int read = reader.read();
                Log.d(TAG, "read " + path + ": " + read);
                if (read != -1) {
                    return read - '0';
                }
            } catch (Exception e) {
                Log.e(TAG, "Read " + path + ": error", e);
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        } else {
            Log.w(TAG, path + " is not exist");
        }
        Log.i(TAG, "read " + path + ": defalut 0");
        return 0;
    }

    private static boolean writeFile(String path, String content) {
        boolean flag = true;
        FileOutputStream out = null;
        PrintStream p = null;
        File file = new File(path);
        if (file.exists()) {
            try {
                out = new FileOutputStream(path);
                p = new PrintStream(out);
                p.print(content);
                Log.i(TAG, "Write " + path + ": " + content);
            } catch (Exception e) {
                flag = false;
                Log.e(TAG, "Write " + path + ": error", e);
                e.printStackTrace();
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                if (p != null) {
                    try {
                        p.close();
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
            }
        } else {
            Log.w(TAG, path + " is not exist");
        }
        return flag;
    }


    /**
     * 设置被选中时的选中图标
     * @param pos_pos
     * @param pos_neg
     * @param neg_pos
     * @param neg_neg
     */
    private void setSelImageSource(int pos_pos,int pos_neg,int neg_pos,int neg_neg){
        sel_pos_pos.setImageResource(pos_pos);
        sel_pos_neg.setImageResource(pos_neg);
        sel_neg_pos.setImageResource(neg_pos);
        sel_neg_neg.setImageResource(neg_neg);
    }

    /**
     * 设置标记FLag
     * @param pos_pos
     * @param pos_neg
     * @param neg_pos
     * @param neg_neg
     */
    private void setItemsBoolean(boolean pos_pos,boolean pos_neg,boolean neg_pos,boolean neg_neg){
        pos_pos_check = pos_pos;
        pos_neg_check = pos_neg;
        neg_pos_check = neg_pos;
        neg_neg_check = neg_neg;
    }
}

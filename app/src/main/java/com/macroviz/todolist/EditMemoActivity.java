package com.macroviz.todolist;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditMemoActivity extends AppCompatActivity implements View.OnClickListener {
    //新增備忘跟編輯備忘都在這Activity中執行
    TextView txtTitle;
    EditText edt_memo;
    Button btn_ok, btn_back;
    Spinner sp_color;
    String new_memo, currentTime;
    Bundle bundle;
    String[] colors;
    SpinnerAdapter spinnerAdapter;
    int index;
    String selected_color;//紀錄目前所選的顏色
    private DbAdapter dbAdapter;
    ArrayList<ItemData> color_list = null;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);
        initView();//另外獨立findViewById
        dbAdapter = new DbAdapter(this);//要加這行才有辦法執行DbAdapter
        bundle = this.getIntent().getExtras();
        //判斷目前是否為編輯狀態
        if(bundle.getString("type").equals("edit")){
            txtTitle.setText("編輯便條");
            index = bundle.getInt("item_id");
            Cursor cursor = dbAdapter.queryById(index);
            edt_memo.setText(cursor.getString(2));
            Log.i("color=",cursor.getString(4));
            for (int i = 0; i < spinnerAdapter.getCount(); i++){
                if(color_list.get(i).code.equals(cursor.getString(4))) {
                    sp_color.setSelection(i);

                }
            }

        }
    }
    private void initView(){
        txtTitle = findViewById(R.id.txtTitle);
        edt_memo = findViewById(R.id.edtMemo);
        edt_memo.setOnClickListener(this);
        sp_color = findViewById(R.id.sp_colors);
        colors =  getResources().getStringArray(R.array.colors);
        //spinnerAdapter = new SpinnerAdapter(this,colors);
        Log.i("color=",String.valueOf(colors));
        LinearLayout container = new LinearLayout(this);
        color_list = new ArrayList<ItemData>();

        color_list.add(new ItemData("SALMON","#FA8072"));
        color_list.add(new ItemData("ORANGERED","#FF4500"));
        color_list.add(new ItemData("LIGHTSALMON","#FFA07A"));
        color_list.add(new ItemData("INDIANRED","#CD5C5C"));
        color_list.add(new ItemData("CHOCOLATE","#D2691E"));
        spinnerAdapter = new SpinnerAdapter(this,color_list);
        sp_color.setAdapter(spinnerAdapter);
        sp_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //取的選取的顏色代碼
                selected_color = color_list.get(position).code;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_ok = findViewById(R.id.btn_ok);
        btn_back = findViewById(R.id.btn_back);
        btn_ok.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edtMemo:
                if(bundle.getString("type").equals("add")) edt_memo.setText("");
                break;

            case R.id.btn_ok:
                //取得edit資料

                new_memo = edt_memo.getText().toString();
                Log.i("memo=",new_memo);
                String currentTime = df.format(new Date(System.currentTimeMillis()));
                if(bundle.getString("type").equals("edit")){
                    try{
                        //更新資料庫中的資料
                        dbAdapter.updateMemo(index, currentTime, new_memo,null, selected_color);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        //回到ShowActivity
                        Intent i = new Intent(this, MainActivity.class);
                        startActivity(i);
                    }
                }else {

                    currentTime = df.format(new Date(System.currentTimeMillis()));
                    try {
                        //呼叫adapter的方法處理新增
                        dbAdapter.createMemo(currentTime, new_memo, null, selected_color);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //回到列表
                        Intent i = new Intent(this, MainActivity.class);
                        startActivity(i);
                    }
                }
                break;
            case R.id.btn_back:
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
        }
    }
}

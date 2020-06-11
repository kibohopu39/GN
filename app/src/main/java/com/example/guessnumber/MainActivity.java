package com.example.guessnumber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private Button n1,n2,n3,n4,n5,n6,n7,n8,n9,n0,del,clr, guess,replay;
    private TextView log,input1,input2,input3,input4;
    private String answer;
    private int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        n1=findViewById(R.id.num1);
        n2=findViewById(R.id.num2);
        n3=findViewById(R.id.num3);
        n4=findViewById(R.id.num4);
        n5=findViewById(R.id.num5);
        n6=findViewById(R.id.num6);
        n7=findViewById(R.id.num7);
        n8=findViewById(R.id.num8);
        n9=findViewById(R.id.num9);
        n0=findViewById(R.id.num0);
        del=findViewById(R.id.delete);
        clr=findViewById(R.id.clear);
        guess=findViewById(R.id.guess);
        replay=findViewById(R.id.replay);
        input1=findViewById(R.id.input1);
        input2=findViewById(R.id.input2);
        input3=findViewById(R.id.input3);
        input4=findViewById(R.id.input4);
        log=findViewById(R.id.log);
        initGame();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }




    //時間戳記來增加金錢,即今天開啟本遊戲就+多少錢
    //本難度沒有提示,因此不會花到金錢
    //答對可以獲得獎勵
    //達到猜題次數上限卻沒有猜到,就是挑戰失敗
    //可以調整難度
    //增加難度就會增加提示按鈕,該按鈕可以顯示出數字但最多顯示一次

    private void initGame(){
        //遊戲初始化

        answer=createAnswer();//創建答案

        log.setText("");//把猜的紀錄刪除
        count=0;//把猜的次數規零
        clear(null);//把最後一次的輸入清空
        MainApp.guesstimes=8;//把次數設定好


    }



    //首先要解決我輸入到哪一格了,才能去判斷我按的數字要放到哪個input中
    public int whichinput(){
        int index=0;
        //依序判斷每個input裡有沒有值,分別重左到右
        int input[]={input1.length(),input2.length(),input3.length(),input4.length()};
        for (int hasNumber:input
             ) {
            if (hasNumber==0){
                index++;
            }
        }
        //index==1--->input1沒數字(第一圈就停)
        //index==2--->input1,2有數字(第二圈停)
        //index==3--->input1,2,3沒數字(第三圈停)
        //index==4--->input1,2,3,4沒數字(第四圈停)
        //index==0--->都有數字(第五圈停,都沒進入判斷所以是0)
        return index;//
    }

    //輸入數字
    public void input(View v){
        int i=whichinput();//要在哪一格設定數字
        int n=0;
        //確定按了哪個按鈕
        int btn[]={n0.getId(),n1.getId(),n2.getId(),n3.getId(),n4.getId(),n5.getId(),n6.getId(),n7.getId(),n8.getId(),n9.getId()};
        for (n=0;n<10;n++){
            if (v.getId()==btn[n]){
                break;
            }
        }
        setInputnumber(n,i);
    }
    // 把數字設置到Input中
    public void setInputnumber(int whatNumber,int whichInputText){
        //先判斷是不是有重複輸入,即按的數字必須與input中的不一樣才可以印出
        CharSequence num=""+whatNumber;
        String InputNumber[]={"0","1","2","3","4","5","6","7","8","9"};
        if (!input1.getText().equals(num) & !input2.getText().equals(num) & !input3.getText().equals(num)){
            switch (whichInputText){
                case 4:
                    input1.setText(InputNumber[whatNumber]);
                    break;
                case 3:
                    input2.setText(InputNumber[whatNumber]);
                    break;
                case 2:
                    input3.setText(InputNumber[whatNumber]);
                    break;
                case 1:
                    input4.setText(InputNumber[whatNumber]);
                    break;
                case 0:
                    break;
            }
        }
    }

    //做出答案
    public String createAnswer(){
        ArrayList pool=new ArrayList<>();
        for (int n=0;n<10;n++){
            pool.add(n);
        }
        Collections.shuffle(pool);//四個數字要不一樣
        StringBuffer sb=new StringBuffer();
        for (int i=0;i<4;i++){
            sb.append(pool.get(i));
        }
        return sb.toString();
    }


    public void clear(View view) {//清空
        input1.setText("");
        input2.setText("");
        input3.setText("");
        input4.setText("");
    }

    public void guess(View view) {//確定送出答案
        //檢查是否都輸入了
        if (whichinput()>0){
            return;
        }
        //把input裡的四個數字組合起來傳給check對答案
        String strInput=(String) input1.getText()+input2.getText()+input3.getText()+input4.getText();
        String result=check(strInput);
        CharSequence temp=log.getText();//取得上一次輸入的
        log.setText(strInput + " => " + result + "\n" + temp);//上一次輸入的紀錄放下面
        clear(null);//清空上一次輸入的

        //如果答對,跳出獲勝自定義的Snackbar
        if (result.equals("4A0B")){

        }


    }
    // 確定送出答案後要檢查
    private String check(String guess){
        int A,B;A=B=0;
        for(int i=0;i<answer.length();i++){
            if (answer.charAt(i)==guess.charAt(i)){
                A++;
            }else if (answer.indexOf(guess.charAt(i))>0){
                B++;
            }
        }
        return A+"A"+B+"B";
    }

    public void doreplay(View view) {//重玩
        initGame();
    }

    public void delete(View view) {//由右到左刪除一位數字
        //首先要確定目前要刪誰
        int i= whichinput();
        switch (i){
            case 4://都沒數字
                break;
            case 3:
                input1.setText("");
                break;
            case 2:
                input2.setText("");
                break;
            case 1:
                input3.setText("");
                break;
            case 0://都有數字
                input4.setText("");
                break;
        }
    }
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }
    long lastExitTime=0;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                // 判断2次点击事件时间
                if ((System.currentTimeMillis() - lastExitTime) > 2000) {
                    Toast.makeText(MainActivity.this, "再按一次退出遊戲",Toast.LENGTH_SHORT).show();
                    lastExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}

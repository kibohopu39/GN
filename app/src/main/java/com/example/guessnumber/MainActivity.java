package com.example.guessnumber;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    private ArrayList arrlist;
    private Button n1,n2,n3,n4,n5,n6,n7,n8,n9,n0,del,clr, guess,replay;
    private TextView log,input1,input2,input3,input4,degree,point,guesstimes,repeat;
    private String answer,result;
    private int count=0,repeatNum=0,temp=0,x=0;//temp是用來判斷數字重複累計,x是多少組重複
    private SharedPreferences sp ;
    private SharedPreferences.Editor editor ;
    //跟 MyService 做繫結
    private MyService myService;
    private boolean isBind;
    private ServiceConnection mConnection=new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            //假如有連到,操作 iBinder
            MyService.LocalBinder binder=(MyService.LocalBinder)iBinder;
            myService=binder.getService();
            isBind=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind=false;
        }
    };

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
        degree=findViewById(R.id.degree);
        point=findViewById(R.id.point);
        guesstimes=findViewById(R.id.guesstimes);
        repeat=findViewById(R.id.repeat);

        sp = getSharedPreferences("config",MODE_PRIVATE);
        editor = sp.edit();
        //放答案跟輸入的陣列
        arrlist = new ArrayList();
        createHelp(MainApp.guessplayingStage,false);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //啟動型先
        playBackMusic();
        //繫結Service
        Intent intent=new Intent(this,MyService.class);
        bindService(intent,mConnection,BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        pauseBackMusic();
        super.onPause();

    }

    @Override
    protected void onStop() {
        pauseBackMusic();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //解除繫結
        if (isBind){
            unbindService(mConnection);
        }
        Intent intent=new Intent(this,MyService.class);
        stopService(intent);
        super.onDestroy();
    }

    //玩法
    //猜數字目前有兩種難度,未來持續增加
    //猜對可以獲得分數
    //分數再關閉遊戲後會儲存
    //達到猜題次數上限卻沒有猜到,就是挑戰失敗

    private void initGame(int stage){
        //遊戲初始化
        arrlist.clear();
        temp=0;
        x=0;
        log.setText("");//把猜的紀錄刪除
        count=0;//把猜的次數規零
        clear(null);//把最後一次的輸入清空
        switch (stage){
            case 1:
                answer=createAnswer(false);//創建答案
                degree.setText("難度: 初級");
                repeat.setText("有"+0+"組數字重複");
                MainApp.guesstimes=12;//把次數設定好
                break;
            case 2:
                answer=createAnswer(true);//創建答案
                degree.setText("難度: 中級");
                repeatNum=checkRepeat(answer);
                repeat.setText("有"+repeatNum+"組數字重複");
                if(repeatNum==1) {//一組重複
                    MainApp.guesstimes = 12 + repeatNum * 6;
                }else{//兩組重複
                    MainApp.guesstimes = 12;
                }
                break;
        }
        guesstimes.setText(count+"/"+MainApp.guesstimes);
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
    public void setInputnumber(int whatNumber,int whichInputText){
        CharSequence num=""+whatNumber;
        String InputNumber[]={"0","1","2","3","4","5","6","7","8","9"};
            //先判斷是不是有重複輸入,即按的數字必須與input中的不一樣才可以印出
        if(MainApp.guessplayingStage==1){
            if (!input1.getText().equals(num) & !input2.getText().equals(num) & !input3.getText().equals(num)) {
                myService.playkeydown_sound();
                switch (whichInputText) {
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
        }else{
            myService.playkeydown_sound();
            switch (whichInputText) {
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

    public String createAnswer(boolean canRepeat) {
        StringBuffer sb = new StringBuffer();
        ArrayList pool = new ArrayList<>();
        for (int n = 0; n < 10; n++) {
            pool.add(n);
        }
        if (!canRepeat) {//如果數字不能重複
            Collections.shuffle(pool);//四個數字要不一樣
            for (int i = 0; i < 4; i++) {
                sb.append(pool.get(i));
            }

        }else{//如果要重複
            for (int i = 0; i < 4; i++){
                if (i>2){//3
                    if (sb.charAt(0)!=sb.charAt(1) & sb.charAt(1)!=sb.charAt(2) & sb.charAt(0)!=sb.charAt(2)) {
                        //第四位,且前三個都不一樣
                        sb.append(sb.charAt(0));//TODO 直接指定第一個重複
                    }else{//如果前面有重複
                        int j=(int) (Math.random()*10);//(0~1)*10=0~9,做出四個
                        sb.append(pool.get(j));
                    }
                }else{//0,1,2
                    int j=(int) (Math.random()*10);//(0~1)*10=0~9,做出四個
                    sb.append(pool.get(j));
                }
            }
            Log.v("wei","wei:"+sb.toString());
        }
        return sb.toString();
    }
    // 把數字設置到Input中

    private int checkRepeat(String answer){
        for (int i=0;i<4;i++){
            arrlist.add(answer.charAt(i));
        }
        for (Object str:arrlist
             ) {
            if (Collections.frequency(arrlist, str)>1 & Collections.frequency(arrlist, str)<4) {
                temp++;//重複時累計,排除重複四次的
            }else if(Collections.frequency(arrlist, str)==4){
                temp=2;
                break;
            }
        }
        //再來看累計,4就是2組重複,3,2就是1組數字重複,0就是都不重複
        if (temp==4){
            x+=2;
        }else if (temp==3 | temp==2){
            x++;
        }
        return x;
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
        result=check(strInput);
        CharSequence temp=log.getText();//取得上一次輸入的
        count++;
        log.setText(strInput + " => " + result + "\n" + temp);//上一次輸入的紀錄放下面
        guesstimes.setText(count+"/"+MainApp.guesstimes);
        clear(null);//清空上一次輸入的

        //如果答對,跳出獲勝
        if (result.equals("4A0B")){
            Integer tempInt= (MainApp.guessplayingStage*20+(MainApp.guesstimes-count)*(MainApp.guesstimes-count)-1);
            String tempStr=String.valueOf(tempInt);
            point.setText(tempStr);
            if(MainApp.guessplayingStage==2){//關卡過兩關後，遊戲結束統計分數
                int Highscore=sp.getInt("score",0);
                if(Highscore<tempInt){//如果這次分數比紀錄中的高
                    editor.putInt("score",tempInt);//紀錄分數
                    editor.commit();
                }
                gameCloseDialog("恭喜您完成所有難度!\n"+
                        "本次總分為:"+tempInt+"分\n"+
                        "紀錄最高分:"+Highscore+"分\n");
            }else{
                MainApp.guessplayingStage++;
                createDialog(true,"恭喜你過了這層難度!");
            }
        }else if (count==MainApp.guesstimes){
            createDialog(false,"正確答案是:"+ "\n"+answer);
        }
    }
    // 確定送出答案後要檢查
    private String check(String guess){
        int A,B,C;A=B=C=0;
        ArrayList tempa=new ArrayList();
        for (int i = 0; i < answer.length(); i++) {
            tempa.add(guess.charAt(i));
            if(answer.charAt(i)==guess.charAt(i)){
                A++;
            }else if(answer.indexOf(guess.charAt(i))>-1){
                B++;
            }
        }
        if(temp!=0){//假設有重複
            for (int i = 0; i < answer.length(); i++){
                if(guess.indexOf(answer.charAt(i))>-1 & Collections.frequency(arrlist,answer.charAt(i))<=Collections.frequency(tempa,answer.charAt(i))&answer.charAt(i)!=guess.charAt(i)){
                    //一一看答案數字有沒有出現在回答測試數字中,,對應位置不是測試數字
                    C++;
                }
                if(answer.indexOf(guess.charAt(i))>-1 & Collections.frequency(arrlist,guess.charAt(i))>Collections.frequency(tempa,guess.charAt(i))&answer.charAt(i)!=guess.charAt(i)){
                    //反過來問
                    C++;
                }
            }
            B=C;
        }
        return A+"A"+B+"B";
    }
    //顯示獲勝或失敗的視窗
    private void createDialog(boolean Win,String mesg){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(Win?"獲勝":"失敗");
        builder.setMessage(mesg);
        builder.setCancelable(false);
        builder.setPositiveButton(Win?"下個難度":"重玩", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myService.playkeydown_sound();
                initGame(MainApp.guessplayingStage);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    //遊戲結束視窗
    private void gameCloseDialog(String mesg){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("遊戲結束");
        builder.setMessage(mesg);
        builder.setCancelable(false);
        builder.setNegativeButton("重新一局", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myService.playkeydown_sound();
                //遊戲重新開始
                MainApp.guessplayingStage=1;
                point.setText("");
                initGame(MainApp.guessplayingStage);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    //顯示遊戲說明視窗,遊戲開始會跳出一次,中間玩的時候也可以自己呼叫它
    private void createHelp(final int stage, final boolean isplaying){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setPositiveButton("我瞭解了!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myService.playkeydown_sound();
                if (!isplaying){//不是正在玩被叫出來
                    initGame(MainApp.guessplayingStage);
                }
            }
        });
        switch (stage){
            case 1:
                builder.setTitle("初級難度說明");
                builder.setMessage("遊戲目標為猜中四個號碼\nA代表數字與位置皆正確\n" +
                        "B代表數字正確位置不正確\n" +
                        "此難度下數字不重複");
                break;
            case 2:
                builder.setTitle("中級難度說明");
                builder.setMessage("遊戲目標為猜中四個號碼\nA代表數字與位置皆正確\n" +
                        "B代表數字正確位置不正確\n" +
                        "此難度下數字有重複");
                break;
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void help(View view) {//點擊獲得說明
        myService.playkeydown_sound();
        createHelp(MainApp.guessplayingStage,true);
    }
    public void doreplay(View view) {//重玩
        myService.playkeydown_sound();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setMessage("確定要重新遊玩這個難度嗎?");
        builder.setPositiveButton("重玩這個難度", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initGame(MainApp.guessplayingStage);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void delete(View view) {//由右到左刪除一位數字
        myService.playkeydown_sound();
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

    //播放音樂
    public void playBackMusic(){
        Intent intent=new Intent(this,MyService.class);
        intent.putExtra("ACTION","start");
        startService(intent);
    }
    //暫停音樂
    public void pauseBackMusic(){
        Intent intent=new Intent(this,MyService.class);
        intent.putExtra("ACTION","pause");
        startService(intent);
    }
}

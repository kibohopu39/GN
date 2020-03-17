package com.example.guessnumber;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Collections;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private Button n1,n2,n3,n4,n5,n6,n7,n8,n9,n0,del,clr, guess,replay;
    private input_handler IH;
    private TextView log,input1,input2,input3,input4;

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


        IH=new input_handler();
        n1.setOnClickListener(IH);
        n2.setOnClickListener(IH);
        n3.setOnClickListener(IH);
        n4.setOnClickListener(IH);
        n5.setOnClickListener(IH);
        n6.setOnClickListener(IH);
        n7.setOnClickListener(IH);
        n8.setOnClickListener(IH);
        n9.setOnClickListener(IH);
        n0.setOnClickListener(IH);




        //initNewGame();
    }

    private class input_handler implements View.OnClickListener {

        @Override
        public void onClick(View v) {




  



          }
    }
    private String answer;
    public void initNewGame(){//起新局
        answer=createAnswer();
        input1.setText("");
        log.setText("");
    }

    private LinkedList<Integer> number;
    private int challange;
    private String createAnswer(){//建立答案
        for(int i=0;i<10;i++) {//產出10個數字放入list
            number.add(i);
        }
        Collections.shuffle(number);//打亂順序
        StringBuffer sb=new StringBuffer();//建立最後要丟出的答案
        for(int j=0;j<challange;j++){
            sb.append(number.get(j));//取?個數字出來放到sb
        }
        return sb.toString();
    }
    private String checkguess(String guess){//鑑定猜測
        int A=0,B=0;
        for (int i=0; i<answer.length(); i++) {
            if (answer.charAt(i) == guess.charAt(i)) {
                A++;
            }else if(answer.indexOf(guess.charAt(i)) != -1) {
                B++;
            }
        }
        return A+"A"+B+"B";
    }

    public void doreplay(View view) {//重啟新局
    }

    public void guess(View view) {//送出答案
        String strInput=input1.getText().toString();
        String result=checkguess(strInput);
        log.append(strInput+"---------"+result+"\n");
        input1.setText("");
    }

    public void clear(View view) {//清空
    }

    public void input(View view) {

    }
}

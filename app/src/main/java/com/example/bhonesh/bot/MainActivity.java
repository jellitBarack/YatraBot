package com.example.bhonesh.bot;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
//import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;
import com.example.bhonesh.bot.ChatMessage;
import com.example.bhonesh.bot.ChatMessageAdapter;
import com.example.bhonesh.bot.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private Button mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    public Bot bot;
    public static Chat chat;
    private ChatMessageAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        copyingfiletophone();

        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/hari";
        AIMLProcessor.extension =  new PCAIMLProcessorExtension();
        //Assign the AIML files to bot for processing
        bot = new Bot("Hari", MagicStrings.root_path, "chat");
        chat = new Chat(bot);



        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = mEditTextMessage.getText().toString();
                String response = chat.multisentenceRespond(message.trim());
                Log.e("responce is ",response);
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                response=processBotResponse(response);
                Log.d("res",response);
                sendMessage(message);
                mimicOtherMessage(response);
                mEditTextMessage.setText("");
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });



    }



    private void sendMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    public static boolean isSDCARDAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)? true :false;
    }
    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


    public String processBotResponse(String result) {

     /*   if(result.contains("map"))
        {
            startActivity(new Intent(MainActivity.this,MapsActivity.class));
        }*/

        if (result.contains("<oob>")) {

            //  OOBProcessor oob = new OOBProcessor(this);
            Log.e("first was ",result);
            String copy=result;
            int i=result.indexOf("<oob>");
            int j=result.indexOf("</oob>");
            Log.e("j is",""+j);
            result=result.substring(0,i)+result.substring(j+6,result.length());
            copy=copy.substring(i,copy.length());
            i=copy.indexOf('>',6);
            j=copy.indexOf('<',i);
            String tof=copy.substring(6,i);
            Log.e("find",tof);
            String toser=copy.substring(i+1,j);


            try {
                if(tof.equals("search")) {
                    toser=toser.replaceAll(" ","+");
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.in/search?q="+toser.replaceAll(" ","+")));
                    startActivity(browserIntent);
                    //Todo: add url here
                }else if(tof.equals("map")){
                    Intent intent=new Intent(MainActivity.this,Display.class);
                    intent.putExtra("query","0");
                    intent.putExtra("text",toser.replaceAll(" ","+"));
                    Toast.makeText(MainActivity.this,"map ",Toast.LENGTH_LONG).show();
                    startActivity(intent);

                }else if(tof.equals("map1")){

                    Intent intent=new Intent(MainActivity.this,Display.class);
                    intent.putExtra("query","1");
                    intent.putExtra("text",toser);
                    Toast.makeText(MainActivity.this,"map ",Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }else if(tof.equals("map2")){
                    Intent intent=new Intent(MainActivity.this,Display.class);
                    intent.putExtra("query","2");
                    intent.putExtra("text","abc");
                    Toast.makeText(MainActivity.this,"map ",Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
                else if(tof.equals("dial")){
                    if(android.text.TextUtils.isDigitsOnly(toser)) {
                        Uri number = Uri.parse("tel:" + toser);
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                        startActivity(callIntent);
                    }else{
                        result="sure";
                    }

                }else if(tof.equals("open")){
                    if(toser.equals("map")){
                        Intent intent=new Intent(MainActivity.this,Display.class);
                        intent.putExtra("query","2");
                        intent.putExtra("text","abc");
                        Toast.makeText(MainActivity.this,"map ",Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                    else if(toser.equals("rail help")){
                        Log.e("call","yes");
                        Intent intent=new Intent(MainActivity.this,RailDisplay.class);
                        // intent.putExtra("type",toser);

                        startActivity(intent);
                    }
                    else if(toser.contains("rail")){
                        Log.e("call","yes");
                        Intent intent=new Intent(MainActivity.this,RailDisplay.class);
                        // intent.putExtra("type",toser);

                        startActivity(intent);
                    }
                    else if(toser.equals("PNR")){
                        Log.e("call","yes");
                        Intent intent=new Intent(MainActivity.this,pnr_check.class);
                        // intent.putExtra("type",toser);

                        startActivity(intent);
                    }
                    else if(toser.equals("TBS")){
                        Log.e("call","yes");
                        Intent intent=new Intent(MainActivity.this,train_between.class);
                        // intent.putExtra("type",toser);

                        startActivity(intent);
                    }
                    else if(toser.equals("ROT")){
                        Log.e("call","yes");
                        Intent intent=new Intent(MainActivity.this,train_route.class);
                        // intent.putExtra("type",toser);

                        startActivity(intent);
                    }
                    else if(toser.equals("FAR")){
                        Log.e("call","yes");
                        Intent intent=new Intent(MainActivity.this,fare_inquiry.class);
                        // intent.putExtra("type",toser);

                        startActivity(intent);
                    }
                    else if(toser.equals("ISA")){
                        Log.e("call","yes");
                        Intent intent=new Intent(MainActivity.this,seat_enquiry.class);
                        // intent.putExtra("type",toser);

                        startActivity(intent);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //result = removeTags(result);
            //mimicOtherMessage(result);
        }
        return result;
    }
    private String removeTags(String string) {
        Pattern REMOVE_TAGS = Pattern.compile("<.+?>");

        if (string == null || string.length() == 0) {
            return string;
        }
        Matcher m = REMOVE_TAGS.matcher(string);
        return m.replaceAll("");
    }






    public void init()
    {
        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (Button) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);
    }


    public void copyingfiletophone()
    {
        //checking SD card availablility
        boolean a = isSDCARDAvailable();
        //receiving the assets from the app directory
        AssetManager assets = getResources().getAssets();
        //assets.open();
        File jayDir = new File(Environment.getExternalStorageDirectory().toString() + "/hari/bots/Hari");

        Log.e("hi",Environment.getDataDirectory().toString());
        boolean b = jayDir.mkdirs();
        if (jayDir.exists()) {
            //Reading the file
            try {
                for (String dir : assets.list("Hari")) {
                    File subdir = new File(jayDir.getPath() + "/" + dir);
                    boolean subdir_check = subdir.mkdirs();
                    Log.e("sun=bdir",subdir_check+" ");
                    for (String file : assets.list("Hari/" + dir)) {
                        File f = new File(jayDir.getPath() + "/" + dir + "/" + file);
                        if (f.exists()) {
                            continue;
                        }
                        InputStream in = null;
                        OutputStream out = null;

                        in = assets.open("Hari/" + dir + "/" + file);
                        Log.e("main path ","Hari/" + dir + "/" + file);
                        out = new FileOutputStream(jayDir.getPath() + "/" + dir + "/" + file);
                        //copy file from assets to the mobile's SD card or any secondary memory
                        copyFile(in, out);
                        in.close();
                        in = null;
                        out.flush();
                        out.close();
                        out = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

package com.virzz.tools.bilibilihelper;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity  {

    public static String TAG = "VIRINK";

    public static String bilibili_download_path = "";
    public static String save_path = "";

    private static Context context;

    private ListView lv;
    private ProgressBar pb;
    private static ArrayList<String> list = new ArrayList<>();
    private static LinkedHashMap<String,Map<String,List<String>>> fileList = new LinkedHashMap<String,Map<String,List<String>>>();
    private static File current_dir;
    private static ArrayAdapter<String> adapter;

//    private static FFmpeg ffmpeg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
//        ffmpeg = FFmpeg.getInstance(context);

//        loadFFMpegBinary();
        bilibili_download_path = getSDPath() + "/Android/data/tv.danmaku.bili/download/";
        save_path = getSDPath() + "/Movies/";

        lv = (ListView) findViewById(R.id.llvv);
        pb = (ProgressBar) findViewById(R.id.ppbb);

        // Set Adapter
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        lv.setAdapter(adapter);

        // TODO: Get Sdcard file list for bilibili download videos
        showDownloadFiles(bilibili_download_path);

        // Set On Item Click Listener
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int id, long arg3) {
                    String name = adapter.getItem(id);
                    String path = (String) fileList.get(name).keySet().toArray()[0];
                    List<String> blvs = fileList.get(name).get(path);
                    // TODO: Move the videos to save path
                    for (String blv : blvs) {
//                        CopySdcardFile(path + "/" + blv, save_path + "/" + name +blv+ ".flv");
                        Log.i(TAG,save_path + "/" + name +blv+ ".flv");
                    }
                    // blv to blv.ts
//                        for (String blv: blvs){
//                            String cmd = "-i "+path+"/"+blv+" -c copy -bsf:v h264_mp4toannexb -f mpegts "+save_path+"/"+blv+".ts";
//                            blv_path.add(blv+".ts");
//                            Log.i(TAG, "ffmpeg "+cmd);
//                            execFFmpegBinary(cmd.split(" "));
//                        }
                    //blv.ts to mp4
//                        blvs.size();
//                        Log.i(TAG,blvs.toString());
                    // ffmpeg -i "concat:0.ts|1.ts" -c copy -bsf:a aac_adtstoasc -movflags +faststart name.mp4
//                        execFFmpegBinary(command);
//                }
            }
        });

        for (String key : fileList.keySet()) {
            adapter.add(key);
        }
        adapter.notifyDataSetChanged();
    }

//    private void loadFFMpegBinary() {
//        try {
//            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
//                @Override
//                public void onFailure() {
//                    Log.i(TAG,"onFailure");
//                }
//            });
//        } catch (FFmpegNotSupportedException e) {
//            Log.e(TAG,"FFmpegNotSupportedException");
//        }
//    }
//
//    private void execFFmpegBinary(final String[] command) {
//        try {
//            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
//                @Override
//                public void onFailure(String s) {
//                    Log.i(TAG,"FAILED with output : "+s);
//                }
//
//                @Override
//                public void onSuccess(String s) {
//                    Log.i(TAG,"SUCCESS with output : "+s);
//                }
//
//                @Override
//                public void onProgress(String s) {
//                    Log.d(TAG, "Started command : ffmpeg "+command);
//                }
//
//                @Override
//                public void onStart() {
//                    Log.d(TAG, "Started command : ffmpeg " + command);
//                }
//
//                @Override
//                public void onFinish() {
//                    Log.d(TAG, "Finished command : ffmpeg "+command);
////                    ffmpeg.
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            // do nothing for now
//        }
//    }

    //文件拷贝
    // 要复制的目录下的所有非子目录(文件夹)文件拷贝
    public int CopySdcardFile(String fromFile, String toFile)
    {
        try
        {
            InputStream fosfrom = new FileInputStream(fromFile);
            OutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0)
            {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return 0;

        } catch (Exception ex)
        {
            return -1;
        }
    }

    /** 使用JSONObject */
    private static String[] parseJsonObject(String jsonData, boolean fanju) {
        String [] name = new String[2];
        try
        {
            JSONObject jsonObj = new JSONObject(jsonData);
            String title = jsonObj.getString("title");
            String tag = jsonObj.getString("type_tag");;
            if ( fanju ) {
                String index = jsonObj.getJSONObject("ep").getString("index");
                String index_title = jsonObj.getJSONObject("ep").getString("index_title");
                name[0] = title+"_"+index+"_"+index_title;
            } else {
                String part = jsonObj.getJSONObject("page_data").getString("part");
                name[0] = title + "_" + part;
            }
            name[1] = tag;
        }
        catch (Exception e)
        {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }
        return name;
    }

    /**  获取 Bilibili 缓存视频目录 */
    public static void showDownloadFiles(String path) {
        adapter.clear();
        current_dir = new File(path);
        // 如果是目录
        if (current_dir.isDirectory()) {
            File[] files = current_dir.listFiles();
            if (files != null){
                for (File file : files) {
                    String fileName = file.getName();
                    boolean sp = Pattern.compile("^[0-9]+$").matcher(fileName).matches();
                    boolean fj = Pattern.compile("^s_[0-9]+$").matcher(fileName).matches();
                    if (file.isDirectory()) {
                        if (sp){
                            analyseVideos(file.getAbsolutePath());
                        }else if(fj) {
                            analyseFanjus(file.getAbsolutePath());
                        }
                    }
                }
            }
        }
    }

    private static void analyseVideos(String path){
        Log.i(TAG,path);
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null){
            for (File f : files) {
                String entryJson = "";
                String p = f.getAbsolutePath();
                entryJson = readFile( p + "/entry.json");
                String [] r = parseJsonObject(entryJson,false);
                Log.i(TAG,r[0]+"  "+r[1]);
                fileList.put(r[0],analyseEntryVideos(p+"/"+r[1]));
            }
        }
    }

    private static void analyseFanjus(String path){
        Log.i(TAG, path);
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null){
            for (File f : files) {
                String entryJson = "";
                String p = f.getAbsolutePath();
                entryJson = readFile( p + "/entry.json");
                String [] r = parseJsonObject(entryJson,false);
                Log.i(TAG,r[0]+"  "+r[1]);
                fileList.put(r[0],analyseEntryVideos(p+"/"+r[1]));
            }
        }
    }

    private static HashMap analyseEntryVideos(String path){
        Log.i(TAG,path);
        HashMap thm = new HashMap<String, List<String>>();
        List blvList = new ArrayList();
        try {
            File file = new File(path);
            File[] files = file.listFiles();
            if (files != null){
                for (File f : files) {
                    String fileName = f.getName();
                    Log.i(TAG,fileName);
                    boolean blv = Pattern.compile("^[0-9]+.blv$").matcher(fileName).matches();
                    if (blv){
                        blvList.add(fileName);
                    }
                }
            }
        } catch (Exception e){
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }
        thm.put(path,blvList);
        return thm;
    }

    private static String readFile(String path) {
        String res = "";
        try{
            FileInputStream fis = new FileInputStream(path);
            byte [] buffer = new byte[fis.available()];
            fis.read(buffer);
            res = ""+new String(buffer);
            fis.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return res;
    }

    /** 获取SD卡根目录路径 */
    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if(sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();
    }

}

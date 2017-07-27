package com.example.heminghang.hmhwhiteboard.drawings;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.heminghang.hmhwhiteboard.drawings.brush.Brush;
import com.example.heminghang.hmhwhiteboard.drawings.brush.CircleBrush;
import com.example.heminghang.hmhwhiteboard.drawings.brush.PenBrush;
import com.example.heminghang.hmhwhiteboard.R;
import com.example.heminghang.hmhwhiteboard.drawings.brush.RectBrush;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

@TargetApi(3)
public class DrawingActivity extends Activity implements View.OnTouchListener{
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private DrawingSurface drawingSurface;
    private DrawingPath currentDrawingPath;
    private Paint currentPaint;
    private Socket persistentSocket = null;

    private Button redoBtn;
    private Button undoBtn;

    private Brush currentBrush;

    private LinearLayout colorPanel;

    private File APP_FILE_PATH = new File("/sdcard/TutorialForAndroidDrawings");
	public static String TAG = "TcpClientActivity";
    
    public static String serverAddr = "";
    private int serverPort = 12345;
    private String message = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setCurrentPaint();
        currentBrush = new PenBrush();
        
        drawingSurface = (DrawingSurface) findViewById(R.id.drawingSurface);
        drawingSurface.setOnTouchListener(this);
        drawingSurface.previewPath = new DrawingPath();
        drawingSurface.previewPath.path = new Path();
        drawingSurface.previewPath.paint = getPreviewPaint();

        colorPanel = (LinearLayout)findViewById(R.id.colorPanelLayout);

        redoBtn = (Button) findViewById(R.id.redoBtn);
        undoBtn = (Button) findViewById(R.id.undoBtn);

        redoBtn.setEnabled(false);
        undoBtn.setEnabled(false);
    }

    private void setCurrentPaint(){
        currentPaint = new Paint();
        currentPaint.setDither(true);
        currentPaint.setColor(Color.BLACK);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(3);

    }

    private Paint getPreviewPaint(){
        final Paint previewPaint = new Paint();
        previewPaint.setColor(0xFFC1C1C1);
        previewPaint.setStyle(Paint.Style.STROKE);
        previewPaint.setStrokeJoin(Paint.Join.ROUND);
        previewPaint.setStrokeCap(Paint.Cap.ROUND);
        previewPaint.setStrokeWidth(3);
        return previewPaint;
    }

    //sends data to the pc socket
    public void send(String msg) {

        try {
            message = new String(msg);
            Log.v(TAG, "got server ip");
            Socket s = new Socket(serverAddr, serverPort);
            Log.v(TAG, "got the socket");
            BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");

            osw.write(msg);
            osw.close();

        } catch (UnknownHostException e) {
            Log.v(TAG, e.toString());
        } catch (NumberFormatException e) {
            Log.v(TAG, e.toString());
        } catch (IOException e) {
            Log.v(TAG, e.toString());
        }
    }

    /*public void send(String msg) {

        try {
            // build connection
            if (persistentSocket == null)
                persistentSocket = new Socket(serverAddr, serverPort);

            // get the output stream and set the format of string
            BufferedOutputStream bos = new BufferedOutputStream(persistentSocket.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(bos, "US-ASCII");

            // send message to the server
            osw.write(msg+"\n");
        } catch (UnknownHostException e) {
            Log.v(TAG, e.toString());
        } catch (NumberFormatException e) {
            Log.v(TAG, e.toString());
        } catch (IOException e) {
            Log.v(TAG, e.toString());
        }
    }*/

    public void sendFlushMessage() {
        send("0.0,0.0,0,none,flush");
    }

    public void sendClearMessage() {
        send("0.0,0.0,0,none,clear");
    }

    public void sendRedoMessage() {
        send("0.0,0.0,0,none,redo");
    }

    public void sendUndoMessage() {
        send("0.0,0.0,0,none,undo");
    }

    public void sendScreenSizeInformation(int width, int height) {
        send(width + "," + height + ",0,none,resize");
    }

    // 处理绘画事件
    public boolean onTouch(View view, MotionEvent motionEvent) {
        String event = "none"; // 初始化将要传给服务器的参数event

        // 处理屏幕点按事件
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            drawingSurface.isDrawing = true;

            currentDrawingPath = new DrawingPath();
            currentDrawingPath.paint = currentPaint;
            currentDrawingPath.path = new Path();

            currentBrush.mouseDown(currentDrawingPath.path, motionEvent.getX(), motionEvent.getY());
            currentBrush.mouseDown(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY());

            event = "down";

        // 处理松开点按事件
        }else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
            drawingSurface.isDrawing = true;

            if (currentBrush instanceof PenBrush)
                currentBrush.mouseMove( currentDrawingPath.path, motionEvent.getX(), motionEvent.getY() );

            currentBrush.mouseMove(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY());

            event = "move";

        // 处理手指滑动事件
        }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
            currentBrush.mouseUp(drawingSurface.previewPath.path, motionEvent.getX(), motionEvent.getY());
            drawingSurface.previewPath.path = new Path();
            drawingSurface.addDrawingPath(currentDrawingPath);

            currentBrush.mouseUp( currentDrawingPath.path, motionEvent.getX(), motionEvent.getY() );

            undoBtn.setEnabled(true);
            redoBtn.setEnabled(false);

            event = "up";

        }

        // 获取当前客户端使用的画笔颜色
        int color = currentPaint.getColor();

        // 获取当前刻画段使用的画刷种类
        String brushType = "none";
        if (currentBrush instanceof RectBrush)
            brushType = "rect";
        else if (currentBrush instanceof CircleBrush)
            brushType = "circle";
        else
            brushType = "pen";

        // 将事件发生的坐标、颜色、画刷种类、事件种类发送给服务器
        send(Float.toString(motionEvent.getX()) + "," + Float.toString(motionEvent.getY()) + "," + color + "," + brushType + "," + event);
        if (brushType.equals("rect") || brushType.equals("circle")) {
            sendFlushMessage();
        }
        return true;
    }

    public void onClick(View view){
        int viewId = view.getId();

        // process color choosen event
        if (viewId == R.id.colorBlackBtn || viewId == R.id.colorBlueBtn || viewId == R.id.colorGreenBtn || viewId == R.id.colorMagenBtn || viewId == R.id.colorRedBtn) {
            int color = Color.RED;
            if (viewId == R.id.colorBlackBtn) color = Color.BLACK;
            else if (viewId == R.id.colorMagenBtn) color = Color.MAGENTA;
            else if (viewId == R.id.colorGreenBtn) color = Color.GREEN;
            else if (viewId == R.id.colorBlueBtn) color = Color.BLUE;
            else { color = Color.RED; }

            currentPaint = new Paint();
            currentPaint.setDither(true);
            currentPaint.setColor(color);
            currentPaint.setStyle(Paint.Style.STROKE);
            currentPaint.setStrokeJoin(Paint.Join.ROUND);
            currentPaint.setStrokeCap(Paint.Cap.ROUND);
            currentPaint.setStrokeWidth(3);

            colorPanel.setVisibility(View.INVISIBLE);
        }
        else {
            switch (viewId) {
                case R.id.colorBtn:
                    if (colorPanel.getVisibility() == View.INVISIBLE)
                        colorPanel.setVisibility(View.VISIBLE);
                    else
                        colorPanel.setVisibility(View.INVISIBLE);
                    break;

                case R.id.undoBtn:
                    sendUndoMessage();
                    drawingSurface.undo();
                    if (drawingSurface.hasMoreUndo() == false) {
                        undoBtn.setEnabled(false);
                    }
                    redoBtn.setEnabled(true);
                    break;

                case R.id.redoBtn:
                    sendRedoMessage();
                    drawingSurface.redo();
                    if (drawingSurface.hasMoreRedo() == false) {
                        redoBtn.setEnabled(false);
                    }

                    undoBtn.setEnabled(true);
                    break;

                // 处理保存控件触发事件
                case R.id.saveBtn:
                    // 检查是否有向存储器写的权限
                    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        // 如果没有，首先弹出一个申请权限的对话框，请求权限
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else {
                        // 如果有，调用保存的接口函数
                        saveToStorage();
                    }
                    break;

                case R.id.circleBtn:
                    currentBrush = new CircleBrush();
                    break;

                case R.id.pathBtn:
                    currentBrush = new PenBrush();
                    break;

                case R.id.clearBtn:
                    // clear the android's surface first
                    drawingSurface.clearScreen();

                    // let the server to clear its surface
                    sendClearMessage();
                    break;

                case R.id.rectBtn:
                    currentBrush = new RectBrush();
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveToStorage();
                }
                break;
        }
    }

    private void saveToStorage() {
        // 调用MediaStore保存文件，文件名随机，并且获取保存后的文件名
        String saved = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                drawingSurface.getBitmap(),
                UUID.randomUUID().toString() + ".png",
                "drawing");

        Toast saveToast;

        if (null != saved) {
            // 如果saved为null，表示保存失败，弹框提示
            saveToast = Toast.makeText(getApplicationContext(), "Whiteboard snapshort saved!", Toast.LENGTH_SHORT);
        } else {
            // 如果saved不为null，表示保存成功，弹框提示
            saveToast = Toast.makeText(getApplicationContext(), "Whiteboard snapshort failed!", Toast.LENGTH_SHORT);
        }

        saveToast.show();
    }


    private class ExportBitmapToFile extends AsyncTask<Intent,Void,Boolean> {
        private Context mContext;
        private Handler mHandler;
        private Bitmap nBitmap;

        public ExportBitmapToFile(Context context,Handler handler,Bitmap bitmap) {
            mContext = context;
            nBitmap = bitmap;
            mHandler = handler;
        }

        @Override
        protected Boolean doInBackground(Intent... arg0) {
            try {
                if (!APP_FILE_PATH.exists()) {
                    APP_FILE_PATH.mkdirs();
                }

                final FileOutputStream out = new FileOutputStream(new File(APP_FILE_PATH + "/myAwesomeDrawing.png"));
                nBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                out.flush();
                out.close();
                return true;
            }catch (Exception e) {
                e.printStackTrace();
            }
            //mHandler.post(completeRunnable);
            return false;
        }


        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if ( bool ){
                mHandler.sendEmptyMessage(1);
            }
        }
    }
}

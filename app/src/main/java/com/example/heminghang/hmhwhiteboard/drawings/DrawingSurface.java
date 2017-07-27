package com.example.heminghang.hmhwhiteboard.drawings;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawingSurface extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "DrawingSurface";
    
    private Boolean _run;
    protected DrawThread thread;
    private Bitmap mBitmap;
    public boolean isDrawing = true;
    public DrawingPath previewPath;
    private SurfaceHolder mHolder;

    private CommandManager commandManager;

    public DrawingSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();
        mHolder.addCallback(this);

        commandManager = new CommandManager();
        thread = new DrawThread();
    }

    private Handler previewDoneHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            isDrawing = false;
        }
    };

    class DrawThread extends  Thread{
        private SurfaceHolder mSurfaceHolder;

        // constructor
        public DrawThread() {
            mSurfaceHolder = DrawingSurface.this.mHolder;
        }

        public void setRunning(boolean run) {
            _run = run;
        }

        @Override
        public void run() {
            Canvas canvas = null;
            while (_run){
                if(isDrawing == true){
                    try{
                        canvas = mSurfaceHolder.lockCanvas(null);
                        if (canvas == null) {
                            continue;
                        }
                        if(mBitmap == null){
                            mBitmap =  Bitmap.createBitmap (1, 1, Bitmap.Config.ARGB_8888);
                            Log.d(TAG, "run: create the bitmap");
                        }
                        final Canvas tmpCanvas = new Canvas (mBitmap);

                        // make every canvas to white
                        tmpCanvas.drawColor(Color.WHITE);
                        canvas.drawColor(Color.WHITE);

                        commandManager.executeAll(tmpCanvas,previewDoneHandler);
                        previewPath.draw(tmpCanvas);
                        
                        canvas.drawBitmap (mBitmap, 0,  0, null);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    finally {
                        if (canvas != null) {
                            mSurfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }

            }
        }
    }

    public void clearScreen() {
        Canvas canvas = null;
        try {
            canvas = mHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.SRC);

            commandManager.clear();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                mHolder.unlockCanvasAndPost(canvas);
            }
        }
    }


    public void addDrawingPath (DrawingPath drawingPath){
        commandManager.addCommand(drawingPath);
    }

    public boolean hasMoreRedo(){
        return commandManager.hasMoreRedo();
    }

    public void redo(){
        isDrawing = true;
        commandManager.redo();
    }

    public void undo(){
        isDrawing = true;
        commandManager.undo();
    }

    public boolean hasMoreUndo(){
        return commandManager.hasMoreUndo();
    }

    public Bitmap getBitmap(){
        return mBitmap;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,  int height) {
        mBitmap =  Bitmap.createBitmap (width, height, Bitmap.Config.ARGB_8888);;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

}

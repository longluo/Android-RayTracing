package me.longluo.raytracing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final String TAG = "RayTracer";

    private TextView mTvRaysPerSecond;

    private TextView mTvFpsPerSecond;

    private Bitmap mImage;

    private Bitmap mLightProbe;

    private Bitmap mBackground;

    private LinearLayout mLLRoot;

    private RaytraceTask mRayTracingThread;

    private int animationSpeed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTvRaysPerSecond = findViewById(R.id.tv_rays_per_second);

        mTvFpsPerSecond = findViewById(R.id.tv_fps_per_second);

        mLLRoot = findViewById(R.id.root_main);
        mLLRoot.setOnTouchListener(new RaytracerTouchHandler());

        CompoundButton checkboxReflections = findViewById(R.id.cb_reflections);
        checkboxReflections.setChecked(true);
        checkboxReflections.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LibRayTracer.getInstance().setReflectionsEnabled(isChecked);
                mRayTracingThread.ClearStats = true;
            }
        });

        CompoundButton checkboxLightprobe = findViewById(R.id.cb_light_probe);
        checkboxLightprobe.setChecked(true);
        checkboxLightprobe.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LibRayTracer.getInstance().setLightprobeEnabled(isChecked);
                mRayTracingThread.ClearStats = true;
            }
        });

        CompoundButton checkboxInterlacing = findViewById(R.id.cb_interlacing);

        checkboxInterlacing.setChecked(true);
        checkboxInterlacing.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LibRayTracer.getInstance().setInterlacingEnabled(isChecked);
                mRayTracingThread.ClearStats = true;
            }
        });

        SeekBar seekBarSpeed = findViewById(R.id.sb_speed);
        seekBarSpeed.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                animationSpeed = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarSpeed.setProgress(8);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mLightProbe == null) {
            mLightProbe = BitmapFactory.decodeResource(getResources(), R.drawable.light_probe);
            mLightProbe.setHasAlpha(false);
        }

        if (mBackground == null) {
            mBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);
            mBackground.setHasAlpha(false);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            return;
        }

        if (mLLRoot.getWidth() == 0 || mLLRoot.getHeight() == 0) {
            return;
        }

        if (mImage == null) {
            mImage = Bitmap.createBitmap(mLLRoot.getWidth(), mLLRoot.getHeight(), Bitmap.Config.ARGB_8888);
            mImage.setHasAlpha(false);
        }

        mRayTracingThread = new RaytraceTask();
        mRayTracingThread.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        mRayTracingThread = null;
    }

    class RaytracerTouchHandler implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent ev) {
            final int action = ev.getActionMasked();

            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                final int actionIndex = ev.getActionIndex();
                final int pointerID = ev.getPointerId(actionIndex);
                final float x = ev.getX(actionIndex);
                final float y = ev.getY(actionIndex);

                // Query the native code to see if this touch intersects a sphere.
                final int sphereID = LibRayTracer.getInstance().traceTouch(x, y);
                if (sphereID == -1) {
                    return false;
                }

                // Add this touch to the list.
                TouchTracker thisTouch = new TouchTracker();
                thisTouch.setPointerID(pointerID);
                thisTouch.setSphereID(sphereID);
                thisTouch.setX(x);
                thisTouch.setY(y);
                mRayTracingThread.touches.add(thisTouch);
                return true;
            }

            if (action == MotionEvent.ACTION_MOVE) {
                // ACTION_MOVE events are batched, so we have to iterate over the pointers.
                for (int i = 0; i < ev.getPointerCount(); i++) {
                    // Find the matching touch.
                    final int pointerID = ev.getPointerId(i);
                    final int touchListIndex = getTouchListIndex(pointerID);

                    if (touchListIndex == -1) {
                        continue;
                    }

                    TouchTracker touch = mRayTracingThread.touches.get(touchListIndex);
                    // Update its position.
                    touch.setX(ev.getX(i));
                    touch.setY(ev.getY(i));
                }

                return true;
            }

            if (action == MotionEvent.ACTION_POINTER_UP) {
                final int actionIndex = ev.getActionIndex();
                final int pointerID = ev.getPointerId(actionIndex);

                // Find the matching touch and remove it.
                final int touchListIndex = getTouchListIndex(pointerID);
                if (touchListIndex == -1) {
                    return false;
                }

                mRayTracingThread.touches.remove(touchListIndex);
                return true;
            }

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                mRayTracingThread.touches.clear();
                return true;
            }

            Log.e(TAG, "Unhandled touch action " + action);
            return false;
        }

        /* Returns the index of the elem in raytraceThread.touches
           that matches pointerID, or -1 if no match is found. */
        private int getTouchListIndex(int pointerID) {
            for (int i = 0; i < mRayTracingThread.touches.size(); i++) {
                TouchTracker thisTouch = mRayTracingThread.touches.get(i);
                if (pointerID == thisTouch.getPointerID()) {
                    return i;
                }
            }

            return -1;
        }

		/* Returns true if there is an elem in raytraceThread.touches
		   that matches sphereID. */
		/*private boolean checkSphereExists(int sphereID) {
		    for(int i=0; i < raytraceThread.touches.size(); i++) {
				TouchTracker thisTouch = raytraceThread.touches.get(i);
				if(sphereID == thisTouch.pointerID)
					return true;
			}
			return false;
		}*/
    }

    class RaytraceTask extends AsyncTask<Void, Integer, Bitmap> {
        private boolean ClearStats = false;
        private long startTime;
        private long numRays;
        private int numFrames;

        ArrayList<TouchTracker> touches = new ArrayList<TouchTracker>();

        public RaytraceTask() {
            ClearStats();
        }

        public void ClearStats() {
            mTvRaysPerSecond.setText("--- x10^6 Viewing Rays/Second");
            mTvFpsPerSecond.setText("--- Frames/Second");
            ResetStats();
            ClearStats = false;
        }

        public void ResetStats() {
            startTime = System.currentTimeMillis();
            numRays = 0;
            numFrames = 0;
        }

        // Do ray tracing in background
        @Override
        protected Bitmap doInBackground(Void... params) {
            LibRayTracer.getInstance().initialize(mImage);
            LibRayTracer.getInstance().passLightProbe(mLightProbe);
            LibRayTracer.getInstance().passBackground(mBackground);

            long lastUpdateTime = System.currentTimeMillis();

            // Continue as long as owned by the MainActivity.
            while (MainActivity.this.mRayTracingThread == this) {
                for (int i = 0; i < touches.size(); i++) {
                    TouchTracker touch = touches.get(i);
                    LibRayTracer.getInstance().moveTouch(touch.getX(), touch.getY(), touch.getSphereID());
                }
                long timeElapsed = System.currentTimeMillis() - lastUpdateTime;
                lastUpdateTime = System.currentTimeMillis();
                int thisNumRays = LibRayTracer.getInstance().rayTrace(mImage, animationSpeed * timeElapsed);
                publishProgress(thisNumRays);
            }

            return mImage;
        }

        @SuppressWarnings("deprecation")
        protected void onProgressUpdate(Integer... progress) {
            if (mImage != null) {
                Drawable d = new BitmapDrawable(getResources(), mImage);
                mLLRoot.setBackgroundDrawable(d);
            }
            numRays += progress[0];
            numFrames++;
            final float secondsElapsed = (System.currentTimeMillis() - startTime) / 1000.0f;
            if (ClearStats) {
                ClearStats();
            } else if (secondsElapsed > 1.0f) {
                float RaysPerSecond = numRays / secondsElapsed;
                float FramesPerSecond = numFrames / secondsElapsed;
                mTvRaysPerSecond.setText(String.format("%.2f", RaysPerSecond / 1000000) + "x10^6 Viewing Rays/Second");
                mTvFpsPerSecond.setText(String.format("%.2f", FramesPerSecond) + " Frames/Second");
                ResetStats();
            }
        }
    }
}

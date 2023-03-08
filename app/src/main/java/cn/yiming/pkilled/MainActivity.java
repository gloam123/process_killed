package cn.yiming.pkilled;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import cn.yiming.pkilled.process.ProcessConnections;

public class MainActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();
    private Runnable mOnTick;
    private Spinner mSpBindFlags;

    // see array.xml process_bind_flags
    Integer mBindFlags[] = {
            Context.BIND_AUTO_CREATE,
            Context.BIND_DEBUG_UNBIND,
            Context.BIND_NOT_FOREGROUND,
            Context.BIND_ABOVE_CLIENT,
            Context.BIND_ALLOW_OOM_MANAGEMENT,
            Context.BIND_WAIVE_PRIORITY,
            Context.BIND_IMPORTANT,
            Context.BIND_ADJUST_WITH_ACTIVITY,
            Context.BIND_NOT_PERCEPTIBLE,
            Context.BIND_INCLUDE_CAPABILITIES
    };

    class TickRunnable implements Runnable {
        @Override
        public void run() {
            updateTvStatus(findViewById(R.id.tv_important_status));
            updateTvStatus(findViewById(R.id.tv_waive_status));
            updateTvStatus(findViewById(R.id.tv_misc_status));

            mHandler.postDelayed(mOnTick, 100);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_start_process).setOnClickListener(v -> {
            startProcess();
        });

        mSpBindFlags = findViewById(R.id.sp_misc_flags);
        mSpBindFlags.setSelection(7);
    }

    private void startProcess() {
        ProcessConnections.startImportantProcess(this, (service) -> {
            TextView tv = findViewById(R.id.tv_important_status);
            tv.setTag(service);
            updateTvStatus(tv);
        });

        ProcessConnections.startWaiveProcess(this, service -> {
            TextView tv = findViewById(R.id.tv_waive_status);
            tv.setTag(service);
            updateTvStatus(tv);
        });

        Spinner sp = findViewById(R.id.sp_misc_flags);
        int flags = mBindFlags[sp.getSelectedItemPosition()];
        ProcessConnections.startMiscProcess(this, service -> {
            TextView tv = findViewById(R.id.tv_misc_status);
            tv.setTag(service);
            updateTvStatus(tv);
        }, flags);

        if (mOnTick == null) {
            mOnTick = new TickRunnable();
            mHandler.post(mOnTick);
        }
    }

    private void updateTvStatus(TextView tv) {
        Object tag = tv.getTag();
        boolean connected = tag != null ? true : false;
        int color = ContextCompat.getColor(this, connected ? R.color.connected_color : R.color.disconnected_color);
        int drawableId = connected ? R.mipmap.outline_link_black_48dp : R.mipmap.outline_link_off_black_48dp;

        // settings text and icon color.
        TextViewCompat.setCompoundDrawableTintList(tv, ColorStateList.valueOf(color));
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableId, 0);
        tv.setTextColor(color);

        if (connected) {
            // update text
            ProcessConnections.RemoteService service = (ProcessConnections.RemoteService)tag;
            tv.setText(service.getData());
        }
    }
}


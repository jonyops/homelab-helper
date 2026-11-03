package local.homelab.helper;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Open once after installing: Android does not deliver broadcasts to an app that has never
 * been opened. The button plays the chime so you can check the volume.
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad, pad, pad);
        LinearLayout frame = new LinearLayout(this);
        frame.setFitsSystemWindows(true);
        frame.addView(root, new LinearLayout.LayoutParams(-1, -1));

        TextView info = new TextView(this);
        info.setTextSize(17);
        info.setText("Homelab Helper is ready. Home Assistant uses it to do things on this phone, "
                + "like playing the chores chime when you finish your chores. Sounds follow your "
                + "notification volume and stay quiet when the phone is on silent.\n\n"
                + "You can close this app now.");
        root.addView(info);

        Button test = new Button(this);
        test.setText("Play the chime");
        test.setOnClickListener(v -> Chime.play(this, Chime.DEFAULT, () -> {}));
        root.addView(test);

        setContentView(frame);
    }
}

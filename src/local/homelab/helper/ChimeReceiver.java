package local.homelab.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Receives local.homelab.helper.CHIME (extra "sound", default chores_done) and plays it. */
public class ChimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent intent) {
        PendingResult pending = goAsync(); // keeps the process alive until the sound ends
        Chime.play(ctx.getApplicationContext(), intent.getStringExtra("sound"), pending::finish);
    }
}

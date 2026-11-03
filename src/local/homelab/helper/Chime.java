package local.homelab.helper;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

/** Plays one sound from assets/<name>.mp3 on the notification stream (so silent mode silences it). */
final class Chime {
    static final String DEFAULT = "chores_done";

    private Chime() {}

    /** Returns false if the sound does not exist. onDone runs once playback ends or fails. */
    static boolean play(Context ctx, String name, Runnable onDone) {
        if (name == null || !name.matches("[a-z0-9_]+")) name = DEFAULT;
        MediaPlayer mp = new MediaPlayer();
        try (AssetFileDescriptor fd = ctx.getAssets().openFd("chimes/" + name + ".mp3")) {
            mp.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            mp.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mp.setOnCompletionListener(p -> { p.release(); onDone.run(); });
            mp.setOnErrorListener((p, w, e) -> { p.release(); onDone.run(); return true; });
            mp.prepare();
            mp.start();
            return true;
        } catch (Exception e) {
            mp.release();
            onDone.run();
            return false;
        }
    }
}

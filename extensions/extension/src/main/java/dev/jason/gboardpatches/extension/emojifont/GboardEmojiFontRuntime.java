package dev.jason.gboardpatches.extension.emojifont;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.atomic.AtomicReference;

public final class GboardEmojiFontRuntime {
    private static final String TAG = "GboardEmojiFont";
    private static final int PRIMARY_LABEL_VIEW_ID = 0x7f0b0651;

    private static final AtomicReference<Typeface> CACHED_TYPEFACE = new AtomicReference<Typeface>(null);
    private static volatile boolean cacheInitialized = false;

    private GboardEmojiFontRuntime() {
    }

    public static Typeface getCustomEmojiTypefaceOrNull(Context context) {
        if (context == null) return null;
        if (cacheInitialized) {
            return CACHED_TYPEFACE.get();
        }
        synchronized (CACHED_TYPEFACE) {
            if (cacheInitialized) {
                return CACHED_TYPEFACE.get();
            }
            Typeface loaded = null;
            try {
                SharedPreferences prefs = GboardEmojiFontSettings.preferences(context);
                if (GboardEmojiFontSettings.readEnabled(prefs)) {
                    File fontFile = GboardEmojiFontSettings.getFontFile(context);
                    if (fontFile != null && fontFile.exists() && fontFile.length() > 0) {
                        loaded = Typeface.createFromFile(fontFile);
                    }
                }
            } catch (Throwable throwable) {
                Log.w(TAG, "Failed to load custom emoji typeface", throwable);
            }
            CACHED_TYPEFACE.set(loaded);
            cacheInitialized = true;
            return loaded;
        }
    }

    public static void invalidateCache() {
        synchronized (CACHED_TYPEFACE) {
            CACHED_TYPEFACE.set(null);
            cacheInitialized = false;
        }
    }

    public static boolean saveCustomEmojiFont(Context context, byte[] data, String fontName) {
        if (context == null || data == null || data.length == 0) {
            return false;
        }
        try {
            File fontFile = GboardEmojiFontSettings.getFontFile(context);
            if (fontFile == null) return false;

            File parent = fontFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            File tempFile = new File(fontFile.getAbsolutePath() + ".tmp");
            FileOutputStream fos = new FileOutputStream(tempFile);
            try {
                fos.write(data);
                fos.flush();
            } finally {
                fos.close();
            }

            // Verify typeface can be parsed before activating
            Typeface testTypeface = Typeface.createFromFile(tempFile);
            if (testTypeface == null) {
                tempFile.delete();
                return false;
            }

            if (fontFile.exists()) {
                fontFile.delete();
            }
            if (!tempFile.renameTo(fontFile)) {
                tempFile.delete();
                return false;
            }

            SharedPreferences prefs = GboardEmojiFontSettings.preferences(context);
            GboardEmojiFontSettings.writeSettings(prefs, true, fontName, data.length);
            invalidateCache();
            return true;
        } catch (Throwable throwable) {
            Log.w(TAG, "Failed to save custom emoji font", throwable);
            return false;
        }
    }

    public static boolean deleteCustomEmojiFont(Context context) {
        if (context == null) return false;
        try {
            File fontFile = GboardEmojiFontSettings.getFontFile(context);
            if (fontFile != null && fontFile.exists()) {
                fontFile.delete();
            }
            SharedPreferences prefs = GboardEmojiFontSettings.preferences(context);
            GboardEmojiFontSettings.writeSettings(prefs, false, "", 0L);
            invalidateCache();
            return true;
        } catch (Throwable throwable) {
            Log.w(TAG, "Failed to delete custom emoji font", throwable);
            return false;
        }
    }

    public static void applyToTextView(TextView textView) {
        if (textView == null) return;
        try {
            Typeface custom = getCustomEmojiTypefaceOrNull(textView.getContext());
            if (custom != null) {
                textView.setTypeface(custom);
            }
        } catch (Throwable throwable) {
            Log.w(TAG, "Failed to apply custom emoji typeface to TextView", throwable);
        }
    }

    public static void applyToPaint(Paint paint, Context context) {
        if (paint == null || context == null) return;
        try {
            Typeface custom = getCustomEmojiTypefaceOrNull(context);
            if (custom != null) {
                paint.setTypeface(custom);
            }
        } catch (Throwable throwable) {
            Log.w(TAG, "Failed to apply custom emoji typeface to Paint", throwable);
        }
    }

    public static void afterSoftKeyBound(Object receiver, Object metadata) {
        if (!(receiver instanceof View)) return;
        View root = (View) receiver;
        try {
            Typeface custom = getCustomEmojiTypefaceOrNull(root.getContext());
            if (custom == null) return;

            View labelView = root.findViewById(PRIMARY_LABEL_VIEW_ID);
            if (labelView instanceof TextView) {
                TextView textView = (TextView) labelView;
                CharSequence text = textView.getText();
                if (isEmojiOrSymbol(text) || isEmojiOrSymbol(textView.getContentDescription())) {
                    textView.setTypeface(custom);
                }
            }
        } catch (Throwable throwable) {
            // Never disrupt keyboard key binding
        }
    }

    public static boolean isEmojiOrSymbol(CharSequence text) {
        if (text == null || text.length() == 0) return false;
        int length = text.length();
        for (int i = 0; i < length; ) {
            int codePoint = Character.codePointAt(text, i);
            i += Character.charCount(codePoint);
            if (isEmojiCodePoint(codePoint)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmojiCodePoint(int codePoint) {
        return (codePoint >= 0x1F000 && codePoint <= 0x1FAFF) // Emoticons, Pictographs, Symbols
                || (codePoint >= 0x2600 && codePoint <= 0x27BF) // Miscellaneous Symbols, Dingbats
                || (codePoint >= 0xFE00 && codePoint <= 0xFE0F) // Variation Selectors
                || (codePoint >= 0x1F900 && codePoint <= 0x1F9FF) // Supplemental Symbols
                || (codePoint >= 0x2300 && codePoint <= 0x23FF); // Misc Technical
    }
}

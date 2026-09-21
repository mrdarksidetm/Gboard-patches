package dev.jason.gboardpatches.extension.emojifont;

import org.junit.Assert;
import org.junit.Test;

public class GboardEmojiFontRuntimeTest {

    @Test
    public void testIsEmojiOrSymbolDetection() {
        // Common emojis
        Assert.assertTrue(GboardEmojiFontRuntime.isEmojiOrSymbol("😀"));
        Assert.assertTrue(GboardEmojiFontRuntime.isEmojiOrSymbol("🔥"));
        Assert.assertTrue(GboardEmojiFontRuntime.isEmojiOrSymbol("🚀"));
        Assert.assertTrue(GboardEmojiFontRuntime.isEmojiOrSymbol("❤️"));
        Assert.assertTrue(GboardEmojiFontRuntime.isEmojiOrSymbol("🎉"));
        Assert.assertTrue(GboardEmojiFontRuntime.isEmojiOrSymbol("🦄"));

        // Text with emoji
        Assert.assertTrue(GboardEmojiFontRuntime.isEmojiOrSymbol("Hello 😀"));

        // Non-emoji text
        Assert.assertFalse(GboardEmojiFontRuntime.isEmojiOrSymbol("abc"));
        Assert.assertFalse(GboardEmojiFontRuntime.isEmojiOrSymbol("123"));
        Assert.assertFalse(GboardEmojiFontRuntime.isEmojiOrSymbol("Hello world"));
        Assert.assertFalse(GboardEmojiFontRuntime.isEmojiOrSymbol(""));
        Assert.assertFalse(GboardEmojiFontRuntime.isEmojiOrSymbol(null));
    }

    @Test
    public void testDefaultSettingsConstants() {
        Assert.assertEquals("gboard_patches_emoji_font", GboardEmojiFontSettings.PREF_NAME);
        Assert.assertEquals("pref_custom_emoji_font_enabled", GboardEmojiFontSettings.PREF_KEY_ENABLED);
        Assert.assertEquals("pref_custom_emoji_font_name", GboardEmojiFontSettings.PREF_KEY_FONT_NAME);
        Assert.assertEquals("pref_custom_emoji_font_size", GboardEmojiFontSettings.PREF_KEY_FONT_SIZE);
        Assert.assertEquals("custom_emoji_font.ttf", GboardEmojiFontSettings.FONT_FILE_NAME);
        Assert.assertFalse(GboardEmojiFontSettings.DEFAULT_ENABLED);
    }
}

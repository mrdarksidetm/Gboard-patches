package dev.jason.gboardpatches.extension.advancedvoice;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class GboardAdvancedVoiceUniversalLocaleTest {

    @Test
    public void universalSupportedLocaleSetContainsEveryLocaleAndLanguageTag() {
        Set<Object> stock = new HashSet<Object>();
        stock.add(Locale.US);

        GboardAdvancedVoice1803Runtime.UniversalSupportedLocaleSet set =
                new GboardAdvancedVoice1803Runtime.UniversalSupportedLocaleSet(stock);

        Locale[] testLocales = new Locale[] {
                Locale.US,
                Locale.UK,
                Locale.forLanguageTag("zh-TW"),
                Locale.forLanguageTag("zh-CN"),
                Locale.forLanguageTag("es-ES"),
                Locale.forLanguageTag("es-US"),
                Locale.forLanguageTag("fr-FR"),
                Locale.forLanguageTag("de-DE"),
                Locale.forLanguageTag("ja-JP"),
                Locale.forLanguageTag("hi-IN"),
                Locale.forLanguageTag("ru-RU"),
                Locale.forLanguageTag("pt-BR"),
                Locale.forLanguageTag("ar-SA")
        };

        for (Locale locale : testLocales) {
            Assert.assertTrue("Universal set must contain locale " + locale, set.contains(locale));
        }

        String[] testTags = new String[] {
                "en-US", "zh-TW", "es-ES", "fr-FR", "de-DE", "hi-IN", "ja-JP", "ru-RU"
        };
        for (String tag : testTags) {
            Assert.assertTrue("Universal set must contain language tag " + tag, set.contains(tag));
        }

        Assert.assertTrue(set.containsAll(Collections.singletonList(Locale.forLanguageTag("es-ES"))));
        Assert.assertTrue(set.contains(Locale.US));
        Assert.assertTrue(set.contains(Locale.forLanguageTag("en-IN")));
        Assert.assertTrue(set.contains("en-US"));
        Assert.assertTrue(set.contains("en-IN"));
        Assert.assertFalse(set.isEmpty());
        Assert.assertTrue(set.size() >= 3);
    }

    @Test
    public void includeExactZhTwSupportedLocaleReturnsUniversalSetWhenEnabled() {
        GboardAdvancedVoice1803RuntimeSettings.clearEnabledOverrideForTest();
        try {
            GboardAdvancedVoice1803RuntimeSettings.setEnabledOverrideForTest(true);
            Set<Object> stock = new HashSet<Object>();
            stock.add(Locale.US);

            Object result = GboardAdvancedVoice1803Runtime.includeExactZhTwSupportedLocale(stock);
            Assert.assertTrue(result instanceof GboardAdvancedVoice1803Runtime.UniversalSupportedLocaleSet);

            @SuppressWarnings("unchecked")
            Set<Object> resultSet = (Set<Object>) result;
            Assert.assertTrue(resultSet.contains(Locale.forLanguageTag("es-ES")));
            Assert.assertTrue(resultSet.contains(Locale.forLanguageTag("hi-IN")));
            Assert.assertTrue(resultSet.contains(Locale.forLanguageTag("fr-FR")));
            Assert.assertTrue(resultSet.contains(Locale.US));
        } finally {
            GboardAdvancedVoice1803RuntimeSettings.clearEnabledOverrideForTest();
        }
    }

    @Test
    public void after1803NativeSplitReadinessReturnsTrueWhenEnabled() {
        GboardAdvancedVoice1803RuntimeSettings.clearEnabledOverrideForTest();
        try {
            GboardAdvancedVoice1803RuntimeSettings.setEnabledOverrideForTest(true);
            Assert.assertTrue(GboardAdvancedVoice1803Runtime.after1803NativeSplitReadiness(false));
            Assert.assertTrue(GboardAdvancedVoice1803Runtime.after1803NativeSplitReadiness(true));

            GboardAdvancedVoice1803RuntimeSettings.setEnabledOverrideForTest(false);
            Assert.assertFalse(GboardAdvancedVoice1803Runtime.after1803NativeSplitReadiness(false));
            Assert.assertTrue(GboardAdvancedVoice1803Runtime.after1803NativeSplitReadiness(true));
        } finally {
            GboardAdvancedVoice1803RuntimeSettings.clearEnabledOverrideForTest();
        }
    }
}

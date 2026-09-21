package dev.jason.gboardpatches.extension.rambler;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import dev.jason.gboardpatches.extension.advancedvoice.GboardAdvancedVoice1803RuntimeSettings;
import dev.jason.gboardpatches.extension.advancedvoice.GboardAdvancedVoiceSettings;

public final class GboardRambler1803OfficialSelectionRuntimeTest {
    @After
    public void tearDown() {
        GboardRambler1803OfficialSelectionRuntime.resetForTests();
        GboardAdvancedVoice1803RuntimeSettings.clearEnabledOverrideForTest();
    }

    @Test
    public void ramblerEnabledPersistentlyForcesAgenticDictation() {
        GboardAdvancedVoice1803RuntimeSettings.setEnabledOverrideForTest(true);
        GboardAdvancedVoice1803RuntimeSettings.setBackendOverrideForTest(
                GboardAdvancedVoiceSettings.BACKEND_RAMBLER);
        GboardRambler1803OfficialSelectionRuntime.updateOfficialSelection(false);
        GboardRambler1803OfficialSelectionRuntime.enterDefaultSelectionSuppression();

        Assert.assertTrue(
                GboardRambler1803OfficialSelectionRuntime.shouldEnableAgenticDictation());
    }

    @Test
    public void officialSelectorStateControlsAgenticCapabilityOutsideSettings() {
        GboardAdvancedVoice1803RuntimeSettings.setBackendOverrideForTest(
                GboardAdvancedVoiceSettings.BACKEND_ADVANCED);
        GboardRambler1803OfficialSelectionRuntime.updateOfficialSelection(false);
        Assert.assertFalse(
                GboardRambler1803OfficialSelectionRuntime.shouldEnableAgenticDictation());

        GboardRambler1803OfficialSelectionRuntime.updateOfficialSelection(true);
        Assert.assertTrue(
                GboardRambler1803OfficialSelectionRuntime.shouldEnableAgenticDictation());
    }

    @Test
    public void voiceSettingsScopeTemporarilyExposesBothOfficialChoices() {
        GboardAdvancedVoice1803RuntimeSettings.setBackendOverrideForTest(
                GboardAdvancedVoiceSettings.BACKEND_ADVANCED);
        GboardRambler1803OfficialSelectionRuntime.updateOfficialSelection(false);
        GboardRambler1803OfficialSelectionRuntime.enterVoiceSettingsScope();
        GboardRambler1803OfficialSelectionRuntime.enterVoiceSettingsScope();

        Assert.assertTrue(
                GboardRambler1803OfficialSelectionRuntime.shouldEnableAgenticDictation());
        GboardRambler1803OfficialSelectionRuntime.exitVoiceSettingsScope();
        Assert.assertTrue(
                GboardRambler1803OfficialSelectionRuntime.shouldEnableAgenticDictation());
        GboardRambler1803OfficialSelectionRuntime.exitVoiceSettingsScope();
        Assert.assertFalse(
                GboardRambler1803OfficialSelectionRuntime.shouldEnableAgenticDictation());
    }

    @Test
    public void defaultSelectionSuppressionWinsAndBalancedExitRestoresOfficialState() {
        GboardAdvancedVoice1803RuntimeSettings.setBackendOverrideForTest(
                GboardAdvancedVoiceSettings.BACKEND_ADVANCED);
        GboardRambler1803OfficialSelectionRuntime.updateOfficialSelection(true);
        GboardRambler1803OfficialSelectionRuntime.enterVoiceSettingsScope();
        GboardRambler1803OfficialSelectionRuntime.enterDefaultSelectionSuppression();
        GboardRambler1803OfficialSelectionRuntime.enterDefaultSelectionSuppression();

        Assert.assertFalse(
                GboardRambler1803OfficialSelectionRuntime.shouldEnableAgenticDictation());
        GboardRambler1803OfficialSelectionRuntime.exitDefaultSelectionSuppression();
        Assert.assertFalse(
                GboardRambler1803OfficialSelectionRuntime.shouldEnableAgenticDictation());
        GboardRambler1803OfficialSelectionRuntime.exitDefaultSelectionSuppression();
        Assert.assertTrue(
                GboardRambler1803OfficialSelectionRuntime.shouldEnableAgenticDictation());

        GboardRambler1803OfficialSelectionRuntime.exitVoiceSettingsScope();
        Assert.assertTrue(
                GboardRambler1803OfficialSelectionRuntime.shouldEnableAgenticDictation());
    }
}

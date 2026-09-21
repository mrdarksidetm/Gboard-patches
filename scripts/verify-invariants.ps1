# Scripts to verify critical fork invariants for mrdarksidetm/Gboard-patches
# Ensures that upstream merges never silently remove or overwrite our features or credits.

[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"

Write-Host "==> Verifying Fork Invariants..." -ForegroundColor Cyan

$root = Resolve-Path (Join-Path $PSScriptRoot "..")

function Assert-Condition($condition, $message) {
    if (-not $condition) {
        Write-Error "INVARIANT FAILURE: $message"
        exit 1
    }
    Write-Host "  [OK] $message" -ForegroundColor Green
}

# --- Invariant 1: Custom Emoji Font Feature ---
Write-Host "`n1. Checking Custom Emoji Font Feature..." -ForegroundColor Yellow
$catalogJsonPath = Join-Path $root "patches/src/main/resources/gboard/gboard-port-product-catalog.json"
Assert-Condition (Test-Path $catalogJsonPath) "Catalog JSON exists"
$catalogContent = Get-Content -Raw $catalogJsonPath
Assert-Condition ($catalogContent -match '"feature_id"\s*:\s*"custom_emoji_font"') "Catalog contains custom_emoji_font"

$catalogShaPath = Join-Path $root "patches/src/main/resources/gboard/gboard-port-product-catalog.sha256"
Assert-Condition (Test-Path $catalogShaPath) "Catalog SHA-256 file exists"
$expectedSha = (Get-Content -Raw $catalogShaPath).Trim()
$actualSha = (Get-FileHash -LiteralPath $catalogJsonPath -Algorithm SHA256).Hash.ToLowerInvariant()
Assert-Condition ($expectedSha -eq $actualSha) "Catalog SHA-256 hash matches"

$emojiRuntimePath = Join-Path $root "extensions/extension/src/main/java/dev/jason/gboardpatches/extension/emojifont/GboardEmojiFontRuntime.java"
Assert-Condition (Test-Path $emojiRuntimePath) "GboardEmojiFontRuntime.java exists"

$emojiPatchPath = Join-Path $root "patches/src/main/kotlin/dev/jason/gboardpatches/patches/gboard/features/emojifont/GboardEmojiFontPatch.kt"
Assert-Condition (Test-Path $emojiPatchPath) "GboardEmojiFontPatch.kt exists"

$runtimeAbiPath = Join-Path $root "patches/src/main/kotlin/dev/jason/gboardpatches/patches/gboard/shared/runtimeabi/RuntimeAbi.kt"
$runtimeAbiContent = Get-Content -Raw $runtimeAbiPath
Assert-Condition ($runtimeAbiContent -match "EMOJI_FONT_RUNTIME_AFTER_SOFT_KEY_BOUND") "RuntimeAbi contains EMOJI_FONT_RUNTIME_AFTER_SOFT_KEY_BOUND"

$settingsXmlPath = Join-Path $root "extensions/extension/src/main/settings-text/gboard_settings_text.xml"
$settingsXmlContent = Get-Content -Raw $settingsXmlPath
Assert-Condition ($settingsXmlContent -match "gboard_patches_emoji_font_title") "Settings XML contains emoji font entry"

# --- Invariant 2: Voice & Rambler for English US + India ---
Write-Host "`n2. Checking Voice & Rambler Configuration..." -ForegroundColor Yellow
$voiceSettingsPath = Join-Path $root "extensions/extension/src/main/java/dev/jason/gboardpatches/extension/advancedvoice/GboardAdvancedVoiceSettings.java"
$voiceSettingsContent = Get-Content -Raw $voiceSettingsPath
Assert-Condition ($voiceSettingsContent -match 'DEFAULT_BACKEND\s*=\s*BACKEND_RAMBLER') "DEFAULT_BACKEND is BACKEND_RAMBLER"

$voiceRuntimeSettingsPath = Join-Path $root "extensions/extension/src/main/java/dev/jason/gboardpatches/extension/advancedvoice/GboardAdvancedVoice1803RuntimeSettings.java"
$voiceRuntimeSettingsContent = Get-Content -Raw $voiceRuntimeSettingsPath
Assert-Condition ($voiceRuntimeSettingsContent -match 'public static boolean isRamblerEnabled\(\)') "isRamblerEnabled() is defined"
Assert-Condition ($voiceRuntimeSettingsContent -match 'BACKEND_RAMBLER\.equals\(backend\)') "isRamblerEnabled() checks BACKEND_RAMBLER"

$ramblerSelectorRuntimePath = Join-Path $root "extensions/extension/src/main/java/dev/jason/gboardpatches/extension/rambler/GboardRambler1803OfficialSelectionRuntime.java"
$ramblerSelectorContent = Get-Content -Raw $ramblerSelectorRuntimePath
Assert-Condition ($ramblerSelectorContent -match 'GboardAdvancedVoice1803RuntimeSettings\.isRamblerEnabled\(\)') "Rambler selection checks isRamblerEnabled()"

$voiceRuntimePath = Join-Path $root "extensions/extension/src/main/java/dev/jason/gboardpatches/extension/advancedvoice/GboardAdvancedVoice1803Runtime.java"
$voiceRuntimeContent = Get-Content -Raw $voiceRuntimePath
Assert-Condition ($voiceRuntimeContent -match 'UniversalSupportedLocaleSet') "UniversalSupportedLocaleSet exists"
Assert-Condition ($voiceRuntimeContent -match 'Locale\.forLanguageTag\("en-IN"\)') "en-IN locale explicitly supported"
Assert-Condition ($voiceRuntimeContent -match 'Locale\.US') "en-US locale explicitly supported"

# --- Invariant 3: AI Writing Tools Persistence ---
Write-Host "`n3. Checking AI Writing Tools..." -ForegroundColor Yellow
$writingToolsRuntimePath = Join-Path $root "extensions/extension/src/main/java/dev/jason/gboardpatches/extension/writingtools/GboardAiWritingToolsRuntime.java"
$writingToolsContent = Get-Content -Raw $writingToolsRuntimePath
Assert-Condition ($writingToolsContent -match 'FLAG_CONFIG_PROOFREAD') "Writing tools handles proofread flag"
Assert-Condition ($writingToolsContent -match 'FLAG_WRITING_TOOLS') "Writing tools handles writing_tools flag"
Assert-Condition ($writingToolsContent -match 'ALL_LANGUAGES_ALLOWLIST_VALUE') "Writing tools includes universal language tags allowlist"

# --- Invariant 4: Attribution & Credits ---
Write-Host "`n4. Checking Attribution & Credits..." -ForegroundColor Yellow
$constantsPath = Join-Path $root "patches/src/main/kotlin/dev/jason/gboardpatches/patches/shared/Constants.kt"
$constantsContent = Get-Content -Raw $constantsPath
Assert-Condition ($constantsContent -match 'github\.com/mrdarksidetm') "Constants.kt attributes author to mrdarksidetm"
Assert-Condition ($constantsContent -match 'github\.com/jasonwu1994') "Constants.kt credits upstream jasonwu1994"

Write-Host "`n==> ALL INVARIANTS PASSED SUCCESSFULLY!" -ForegroundColor Green
exit 0

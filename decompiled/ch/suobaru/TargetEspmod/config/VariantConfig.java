/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.fabricmc.loader.api.FabricLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ch.suobaru.TargetEspmod.config;

import ch.suobaru.TargetEspmod.Variant;
import ch.suobaru.TargetEspmod.config.EffectSettings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VariantConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"targetespmod");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("targetespmod.json");
    private static final int CURRENT_SCHEMA_VERSION = 2;
    public static final int DEFAULT_COLOR = -10522177;
    private static Variant appliedVariant = Variant.OFF;
    private static boolean hurtEnabled = true;
    private static int colorArgb = -10522177;
    private static final EnumMap<Variant, EffectSettings> SETTINGS = VariantConfig.defaultSettings();

    private VariantConfig() {
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH, new LinkOption[0])) {
            VariantConfig.save();
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH);){
            ConfigData data = (ConfigData)GSON.fromJson((Reader)reader, ConfigData.class);
            if (data == null) {
                VariantConfig.resetDefaults();
                VariantConfig.save();
                return;
            }
            appliedVariant = Variant.fromId(data.variant);
            hurtEnabled = data.hurt == null || data.hurt != false;
            colorArgb = VariantConfig.normalizeColor(data.color == null ? -10522177 : data.color);
            SETTINGS.clear();
            SETTINGS.putAll(VariantConfig.readSettings(data.settings));
            if (data.schemaVersion == null || data.schemaVersion < 2 || VariantConfig.settingsNeedRewrite(data.settings)) {
                VariantConfig.save();
            }
        }
        catch (IOException | RuntimeException exception) {
            LOGGER.error("Could not read {}. Defaults will be used.", (Object)CONFIG_PATH, (Object)exception);
            VariantConfig.backupBrokenConfig();
            VariantConfig.resetDefaults();
            VariantConfig.save();
        }
    }

    private static void resetDefaults() {
        appliedVariant = Variant.OFF;
        hurtEnabled = true;
        colorArgb = -10522177;
        SETTINGS.clear();
        SETTINGS.putAll(VariantConfig.defaultSettings());
    }

    public static Variant getAppliedVariant() {
        return appliedVariant;
    }

    public static boolean isHurtEnabled() {
        return hurtEnabled;
    }

    public static int getColorArgb() {
        return colorArgb;
    }

    public static EffectSettings getSettings(Variant variant) {
        Variant safeVariant = variant == null ? Variant.OFF : variant;
        return SETTINGS.getOrDefault((Object)safeVariant, EffectSettings.defaultsFor(safeVariant));
    }

    public static Map<Variant, EffectSettings> getAllSettings() {
        return Map.copyOf(SETTINGS);
    }

    public static EnumMap<Variant, EffectSettings> copySettings() {
        return new EnumMap<Variant, EffectSettings>(SETTINGS);
    }

    public static void applyAndSave(Variant variant, boolean hurt, int color, Map<Variant, EffectSettings> settings) {
        appliedVariant = variant == null ? Variant.OFF : variant;
        hurtEnabled = hurt;
        colorArgb = VariantConfig.normalizeColor(color);
        EnumMap<Variant, EffectSettings> normalizedSettings = VariantConfig.normalizeSettings(settings);
        SETTINGS.clear();
        SETTINGS.putAll(normalizedSettings);
        VariantConfig.save();
    }

    public static void applyAndSave(Variant variant, boolean hurt, int color) {
        VariantConfig.applyAndSave(variant, hurt, color, SETTINGS);
    }

    public static boolean saveSettings(Variant variant, EffectSettings settings) {
        Variant safeVariant;
        Variant variant2 = safeVariant = variant == null ? Variant.OFF : variant;
        if (!EffectSettings.hasSettings(safeVariant)) {
            return false;
        }
        SETTINGS.put(safeVariant, EffectSettings.sanitize(safeVariant, settings));
        return VariantConfig.save();
    }

    private static int normalizeColor(int color) {
        return 0xFF000000 | color & 0xFFFFFF;
    }

    private static EnumMap<Variant, EffectSettings> defaultSettings() {
        EnumMap<Variant, EffectSettings> settings = new EnumMap<Variant, EffectSettings>(Variant.class);
        for (Variant variant : Variant.values()) {
            settings.put(variant, EffectSettings.defaultsFor(variant));
        }
        return settings;
    }

    private static EnumMap<Variant, EffectSettings> normalizeSettings(Map<Variant, EffectSettings> source) {
        EnumMap<Variant, EffectSettings> normalized = VariantConfig.defaultSettings();
        if (source == null) {
            return normalized;
        }
        for (Variant variant : Variant.values()) {
            EffectSettings candidate = source.get((Object)variant);
            if (candidate == null) continue;
            normalized.put(variant, EffectSettings.sanitize(variant, candidate));
        }
        return normalized;
    }

    private static EnumMap<Variant, EffectSettings> readSettings(Map<String, SettingsData> source) {
        EnumMap<Variant, EffectSettings> settings = VariantConfig.defaultSettings();
        if (source == null) {
            return settings;
        }
        for (Variant variant : Variant.values()) {
            SettingsData data = VariantConfig.findSettingsData(source, variant.id());
            if (data == null) continue;
            settings.put(variant, data.toSettings(variant));
        }
        return settings;
    }

    private static SettingsData findSettingsData(Map<String, SettingsData> source, String id) {
        SettingsData exact = source.get(id);
        if (exact != null) {
            return exact;
        }
        for (Map.Entry<String, SettingsData> entry : source.entrySet()) {
            if (entry.getKey() == null || !entry.getKey().equalsIgnoreCase(id)) continue;
            return entry.getValue();
        }
        return null;
    }

    private static boolean settingsNeedRewrite(Map<String, SettingsData> source) {
        if (source == null) {
            return true;
        }
        for (Variant variant : Variant.values()) {
            if (!EffectSettings.hasSettings(variant) || source.containsKey(variant.id())) continue;
            return true;
        }
        return false;
    }

    private static boolean save() {
        Path temporaryPath = CONFIG_PATH.resolveSibling(String.valueOf(CONFIG_PATH.getFileName()) + ".tmp");
        try {
            Files.createDirectories(CONFIG_PATH.getParent(), new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(temporaryPath, new OpenOption[0]);){
                GSON.toJson((Object)new ConfigData(appliedVariant.id(), hurtEnabled, colorArgb, SETTINGS), (Appendable)writer);
            }
            try {
                Files.move(temporaryPath, CONFIG_PATH, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporaryPath, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        }
        catch (IOException exception) {
            LOGGER.error("Could not save {}.", (Object)CONFIG_PATH, (Object)exception);
            try {
                Files.deleteIfExists(temporaryPath);
            }
            catch (IOException cleanupException) {
                LOGGER.debug("Could not remove temporary config {}.", (Object)temporaryPath, (Object)cleanupException);
            }
            return false;
        }
    }

    private static void backupBrokenConfig() {
        Path brokenPath = CONFIG_PATH.resolveSibling(String.valueOf(CONFIG_PATH.getFileName()) + ".broken");
        try {
            Files.move(CONFIG_PATH, brokenPath, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.warn("Moved invalid config to {}.", (Object)brokenPath);
        }
        catch (IOException backupException) {
            LOGGER.warn("Could not back up invalid config {}.", (Object)CONFIG_PATH, (Object)backupException);
        }
    }

    private static final class ConfigData {
        private Integer schemaVersion = 2;
        private String variant = Variant.OFF.id();
        private Boolean hurt = true;
        private Integer color = -10522177;
        private Map<String, SettingsData> settings;

        private ConfigData() {
        }

        private ConfigData(String variant, boolean hurt, int color, Map<Variant, EffectSettings> settings) {
            this.variant = variant;
            this.hurt = hurt;
            this.color = color;
            this.settings = new LinkedHashMap<String, SettingsData>();
            for (Variant effectVariant : Variant.values()) {
                if (!EffectSettings.hasSettings(effectVariant)) continue;
                EffectSettings value = settings.getOrDefault((Object)effectVariant, EffectSettings.defaultsFor(effectVariant));
                this.settings.put(effectVariant.id(), new SettingsData(value));
            }
        }
    }

    private static final class SettingsData {
        private Float size;
        private Float speed;
        private Float radius;
        private Float waveHeight;
        private Integer amount;
        private Integer trailLength;

        private SettingsData() {
        }

        private SettingsData(EffectSettings settings) {
            this.size = Float.valueOf(settings.size());
            this.speed = Float.valueOf(settings.speed());
            this.radius = Float.valueOf(settings.radius());
            this.waveHeight = Float.valueOf(settings.waveHeight());
            this.amount = settings.amount();
            this.trailLength = settings.trailLength();
        }

        private EffectSettings toSettings(Variant variant) {
            return EffectSettings.fromRaw(variant, this.size, this.speed, this.radius, this.waveHeight, this.amount, this.trailLength);
        }
    }
}


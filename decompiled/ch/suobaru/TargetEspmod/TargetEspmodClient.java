/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
 *  net.fabricmc.fabric.api.event.player.AttackEntityCallback
 *  net.minecraft.class_1269
 *  net.minecraft.class_1937
 *  net.minecraft.class_2960
 *  net.minecraft.class_304
 *  net.minecraft.class_304$class_11900
 *  net.minecraft.class_310
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_437
 *  net.minecraft.class_638
 */
package ch.suobaru.TargetEspmod;

import ch.suobaru.TargetEspmod.config.VariantConfig;
import ch.suobaru.TargetEspmod.gui.VariantSelectorScreen;
import ch.suobaru.TargetEspmod.target.TargetManager;
import ch.suobaru.TargetEspmod.utility.render.TargetRenderLayers;
import ch.suobaru.TargetEspmod.visual.TargetESP;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.class_1269;
import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_437;
import net.minecraft.class_638;

public final class TargetEspmodClient
implements ClientModInitializer {
    public static final String MOD_ID = "targetespmod";
    public static final TargetManager TARGET_MANAGER = new TargetManager();
    public static final TargetESP TARGET_ESP = new TargetESP(TARGET_MANAGER);
    private static final class_304.class_11900 KEY_CATEGORY = class_304.class_11900.method_74698((class_2960)TargetEspmodClient.id("controls"));
    private static final class_304 OPEN_SCREEN_KEY = KeyBindingHelper.registerKeyBinding((class_304)new class_304("key.targetespmod.open", class_3675.class_307.field_1668, 73, KEY_CATEGORY));
    private class_638 lastWorld;

    public static class_2960 id(String path) {
        return class_2960.method_60655((String)MOD_ID, (String)path);
    }

    public void onInitializeClient() {
        VariantConfig.load();
        TargetRenderLayers.initialize();
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            class_310 client = class_310.method_1551();
            if (world.method_8608() && player == client.field_1724 && !player.method_7325()) {
                TARGET_MANAGER.setCurrentTarget(entity);
            }
            return class_1269.field_5811;
        });
        WorldRenderEvents.END_EXTRACTION.register(TARGET_ESP::extract);
        WorldRenderEvents.END_MAIN.register(TARGET_ESP::draw);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_SCREEN_KEY.method_1436()) {
                if (client.field_1755 != null) continue;
                client.method_1507((class_437)new VariantSelectorScreen());
            }
            if (client.field_1687 != this.lastWorld) {
                this.lastWorld = client.field_1687;
                TARGET_MANAGER.reset();
                TARGET_ESP.resetTransientState();
            }
            if (client.field_1724 == null || client.field_1687 == null) {
                TARGET_MANAGER.reset();
            } else if (TARGET_MANAGER.tick((class_1937)client.field_1687)) {
                TARGET_ESP.resetTransientState();
            }
        });
    }
}


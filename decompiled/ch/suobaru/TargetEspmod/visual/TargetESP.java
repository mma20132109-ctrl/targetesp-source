/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext
 *  net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext
 *  net.minecraft.class_12075
 *  net.minecraft.class_1309
 *  net.minecraft.class_156
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_7833
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package ch.suobaru.TargetEspmod.visual;

import ch.suobaru.TargetEspmod.TargetEspmodClient;
import ch.suobaru.TargetEspmod.Variant;
import ch.suobaru.TargetEspmod.config.EffectSettings;
import ch.suobaru.TargetEspmod.config.VariantConfig;
import ch.suobaru.TargetEspmod.target.TargetManager;
import ch.suobaru.TargetEspmod.utility.animation.Animation;
import ch.suobaru.TargetEspmod.utility.animation.Easing;
import ch.suobaru.TargetEspmod.utility.render.CrystalRenderer;
import ch.suobaru.TargetEspmod.utility.render.DrawUtility;
import ch.suobaru.TargetEspmod.utility.render.TargetRenderLayers;
import ch.suobaru.TargetEspmod.visual.CubeParticle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.class_12075;
import net.minecraft.class_1309;
import net.minecraft.class_156;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class TargetESP {
    private static final RenderStateDataKey<Frame> FRAME_KEY = RenderStateDataKey.create(() -> "targetespmod:frame");
    private static final class_2960 IMAGE_TEXTURE = TargetEspmodClient.id("textures/image.png");
    private static final class_2960 IMAGE_2_TEXTURE = TargetEspmodClient.id("textures/image_2.png");
    private static final class_2960 IMAGE_3_TEXTURE = TargetEspmodClient.id("textures/image_3.png");
    private static final class_2960 BLOOM_TEXTURE = TargetEspmodClient.id("textures/bloom.png");
    private static final class_2960 CUBE_BLOOM_TEXTURE = TargetEspmodClient.id("textures/wonderful_bloom.png");
    private static final class_2960 TRIANGLE_TEXTURE = TargetEspmodClient.id("textures/vega_trianglestipple.png");
    private static final int HURT_COLOR = -64765;
    static final long CUBE_ATTACH_LIFE_MS = 560L;
    static final long CUBE_FADE_LIFE_MS = 320L;
    private static final float CUBE_SPAWN_INTERVAL_SECONDS = 0.022f;
    private static final int RING_SEGMENTS = 96;
    private static final double IMAGE_PHASE_PERIOD = 4.4;
    private static final double GHOSTS_PHASE_PERIOD = 30.0;
    private static final double GHOSTS_2_PHASE_PERIOD = 18.0;
    private static final double TRIANGLE_PHASE_PERIOD = Math.PI * 6;
    private static final double RING_PHASE_PERIOD = 2.0;
    private static final double CRYSTALS_PHASE_PERIOD = 20.0;
    private final TargetManager targetManager;
    private final Animation visibility = new Animation(300L, 0.0f, Easing.FIGMA_EASE_IN_OUT);
    private final ArrayList<CubeParticle> cubeParticles = new ArrayList();
    private final double[] effectPhases = new double[Variant.values().length];
    private class_1309 trackedEntity;
    private RenderTarget lastTarget;
    private Variant lastVariant = Variant.OFF;
    private float cubeSpawnAccumulator;
    private long lastFrameTimeMs;

    public TargetESP(TargetManager targetManager) {
        this.targetManager = targetManager;
    }

    public void resetTransientState() {
        this.visibility.reset(0.0f);
        this.trackedEntity = null;
        this.lastTarget = null;
        this.lastVariant = Variant.OFF;
        this.cubeSpawnAccumulator = 0.0f;
        this.lastFrameTimeMs = 0L;
        Arrays.fill(this.effectPhases, 0.0);
        this.cubeParticles.clear();
    }

    public void extract(WorldExtractionContext context) {
        RenderTarget renderTarget;
        RenderTarget activeTarget;
        long nowMs = class_156.method_658();
        float deltaSeconds = this.frameDelta(nowMs);
        float tickProgress = context.tickCounter().method_60637(false);
        class_1309 activeEntity = this.targetManager.getLivingTarget();
        if (activeEntity != null) {
            this.trackedEntity = activeEntity;
        }
        class_1309 renderEntity = activeEntity != null ? activeEntity : this.trackedEntity;
        RenderTarget trackedTarget = TargetESP.isTrackable(renderEntity, context) ? TargetESP.snapshot(renderEntity, tickProgress) : null;
        RenderTarget renderTarget2 = activeTarget = activeEntity == null ? null : trackedTarget;
        if (trackedTarget != null) {
            this.lastTarget = trackedTarget;
        }
        float visible = this.visibility.update(activeTarget != null);
        Variant variant = VariantConfig.getAppliedVariant();
        EffectSettings settings = VariantConfig.getSettings(variant);
        double effectPhase = this.advanceEffectPhase(variant, deltaSeconds, settings.speed());
        if (this.lastVariant == Variant.CUBES && variant != Variant.CUBES) {
            this.cubeParticles.clear();
            this.cubeSpawnAccumulator = 0.0f;
        }
        List<CubeParticle.Visual> cubeVisuals = variant == Variant.CUBES ? this.updateCubeParticles(activeTarget, deltaSeconds, nowMs, settings) : List.of();
        RenderTarget renderTarget3 = renderTarget = trackedTarget != null ? trackedTarget : this.lastTarget;
        if (activeTarget == null && visible <= 0.001f && cubeVisuals.isEmpty()) {
            this.trackedEntity = null;
            this.lastTarget = null;
            renderTarget = null;
        }
        Frame frame = renderTarget == null && cubeVisuals.isEmpty() ? null : new Frame(variant, renderTarget, visible, 0.5f + visible * 0.5f, effectPhase, settings, cubeVisuals);
        context.worldState().setData(FRAME_KEY, (Object)frame);
        this.lastVariant = variant;
    }

    public void draw(WorldRenderContext context) {
        Frame frame = (Frame)context.worldState().getData(FRAME_KEY);
        class_4587 matrices = context.matrices();
        if (frame == null || matrices == null) {
            return;
        }
        class_12075 camera = context.worldState().field_63082;
        class_4597 consumers = context.consumers();
        switch (frame.variant()) {
            case IMAGE: 
            case IMAGE_2: 
            case IMAGE_3: {
                this.drawImage(matrices, consumers, camera, frame);
                break;
            }
            case GHOSTS: {
                this.drawGhosts(matrices, consumers, camera, frame);
                break;
            }
            case GHOSTS_2: {
                this.drawGhosts2(matrices, consumers, camera, frame);
                break;
            }
            case CUBES: {
                this.drawCubes(matrices, consumers, camera, frame);
                break;
            }
            case TRIANGLE: {
                this.drawTriangle(matrices, consumers, camera, frame);
                break;
            }
            case RING: {
                this.drawRing(matrices, consumers, camera, frame);
                break;
            }
            case CRYSTALS: {
                this.drawCrystals(matrices, consumers, camera, frame);
                break;
            }
        }
    }

    private float frameDelta(long nowMs) {
        if (this.lastFrameTimeMs == 0L) {
            this.lastFrameTimeMs = nowMs;
            return 0.0f;
        }
        float delta = class_3532.method_15363((float)((float)(nowMs - this.lastFrameTimeMs) / 1000.0f), (float)0.0f, (float)0.1f);
        this.lastFrameTimeMs = nowMs;
        return delta;
    }

    private double advanceEffectPhase(Variant variant, float deltaSeconds, double speed) {
        int phaseIndex = variant.ordinal();
        double phase = this.effectPhases[phaseIndex];
        double period = TargetESP.phasePeriod(variant);
        if (period <= 0.0) {
            return phase;
        }
        if ((phase = (phase + (double)deltaSeconds * speed) % period) < 0.0) {
            phase += period;
        }
        this.effectPhases[phaseIndex] = phase;
        return phase;
    }

    private static double phasePeriod(Variant variant) {
        return switch (variant) {
            case Variant.IMAGE, Variant.IMAGE_2, Variant.IMAGE_3 -> 4.4;
            case Variant.GHOSTS -> 30.0;
            case Variant.GHOSTS_2 -> 18.0;
            case Variant.TRIANGLE -> Math.PI * 6;
            case Variant.RING -> 2.0;
            case Variant.CRYSTALS -> 20.0;
            default -> 0.0;
        };
    }

    private List<CubeParticle.Visual> updateCubeParticles(RenderTarget activeTarget, float deltaSeconds, long nowMs, EffectSettings settings) {
        int particleLimit = Math.max(0, settings.amount());
        int excessParticles = this.cubeParticles.size() - particleLimit;
        if (excessParticles > 0) {
            this.cubeParticles.subList(0, excessParticles).clear();
        }
        if (activeTarget != null) {
            this.cubeSpawnAccumulator = Math.min(this.cubeSpawnAccumulator + deltaSeconds, 0.044f);
            ThreadLocalRandom random = ThreadLocalRandom.current();
            while (this.cubeParticles.size() < particleLimit && this.cubeSpawnAccumulator >= 0.022f) {
                this.cubeSpawnAccumulator -= 0.022f;
                double angle = random.nextDouble(Math.PI * 2);
                double x = Math.cos(angle) * (double)settings.radius();
                double y = 0.02 + random.nextDouble() * 0.1;
                double z = Math.sin(angle) * (double)settings.radius();
                this.cubeParticles.add(new CubeParticle(activeTarget, x, y, z, nowMs));
            }
            if (this.cubeParticles.size() >= particleLimit) {
                this.cubeSpawnAccumulator = 0.0f;
            }
        } else {
            this.cubeSpawnAccumulator = 0.0f;
        }
        ArrayList<CubeParticle.Visual> visuals = new ArrayList<CubeParticle.Visual>(this.cubeParticles.size());
        for (int index = this.cubeParticles.size() - 1; index >= 0; --index) {
            CubeParticle particle = this.cubeParticles.get(index);
            particle.update(deltaSeconds, nowMs, activeTarget, settings.speed());
            if (particle.shouldRemove(nowMs)) {
                this.cubeParticles.remove(index);
                continue;
            }
            CubeParticle.Visual visual = particle.snapshot(nowMs, settings.size());
            if (visual == null) continue;
            visuals.add(visual);
        }
        return List.copyOf(visuals);
    }

    private void drawImage(class_4587 matrices, class_4597 consumers, class_12075 camera, Frame frame) {
        RenderTarget target = frame.target();
        if (target == null || frame.visibility() <= 0.001f) {
            return;
        }
        class_243 center = target.position().method_1031(0.0, (double)((target.height() + 0.4f) * 0.5f), 0.0);
        float distanceScale = (float)Math.max(0.1, center.method_1022(camera.field_63078) * 0.007);
        float halfSize = frame.settings().size() * 12.0f * frame.scale() * distanceScale * 0.5f;
        int color = TargetESP.displayColor(target, frame.visibility());
        class_2960 texture = switch (frame.variant()) {
            case Variant.IMAGE_2 -> IMAGE_2_TEXTURE;
            case Variant.IMAGE_3 -> IMAGE_3_TEXTURE;
            default -> IMAGE_TEXTURE;
        };
        class_4588 output = consumers.method_73477(TargetRenderLayers.texturedOverlay(texture));
        TargetESP.appendBillboard(matrices, output, camera, center, halfSize, color, TargetESP.imageRotation(frame.effectPhase()));
    }

    private void drawGhosts(class_4587 matrices, class_4597 consumers, class_12075 camera, Frame frame) {
        RenderTarget target = frame.target();
        if (target == null || frame.visibility() <= 0.001f) {
            return;
        }
        class_4588 output = consumers.method_73477(TargetRenderLayers.textured(BLOOM_TEXTURE));
        int color = TargetESP.displayColor(target, frame.visibility());
        int amount = Math.max(1, frame.settings().amount());
        float size = frame.settings().size();
        float radius = target.width() * frame.settings().radius() * Math.max(0.5f, 1.2f - 0.5f * frame.visibility());
        double orbitDegrees = frame.effectPhase() * 60.0;
        double waveDegrees = frame.effectPhase() * 12.0;
        for (int index = 0; index < amount; ++index) {
            double degrees = 360.0 * (double)index / (double)amount;
            double theta = Math.toRadians(degrees + orbitDegrees);
            float pulse = 0.5f + 0.5f * class_3532.method_15374((double)((float)Math.toRadians(degrees * 3.0)));
            float coreSize = (0.13f + 0.12f * pulse) * size;
            float glowSize = (0.7f + 0.25f * pulse) * size;
            class_243 position = target.position().method_1031(Math.sin(theta) * (double)radius, (double)(target.height() / 1.5f) + (double)(target.height() / 3.0f) * Math.sin(Math.toRadians(degrees / 2.0 + waveDegrees)), Math.cos(theta) * (double)radius);
            TargetESP.appendBillboard(matrices, output, camera, position, glowSize * 0.5f, TargetESP.multAlpha(color, 0.06f), 0.0f);
            TargetESP.appendBillboard(matrices, output, camera, position, coreSize * 0.5f, color, 0.0f);
        }
    }

    private void drawGhosts2(class_4587 matrices, class_4597 consumers, class_12075 camera, Frame frame) {
        RenderTarget target = frame.target();
        if (target == null || frame.visibility() <= 0.001f) {
            return;
        }
        EffectSettings settings = frame.settings();
        int arms = class_3532.method_15340((int)settings.amount(), (int)1, (int)3);
        int trailLength = Math.max(1, settings.trailLength());
        float baseSize = settings.size();
        float radius = settings.radius();
        float waveHeight = settings.waveHeight();
        float centerHeight = Math.max(waveHeight + 0.05f, target.height() * 0.55f);
        double ageSpeedTicks = frame.effectPhase() * 20.0;
        int baseColor = TargetESP.displayColor(target, frame.visibility());
        class_4588 output = consumers.method_73477(TargetRenderLayers.textured(CUBE_BLOOM_TEXTURE));
        for (int arm = 0; arm < arms; ++arm) {
            double armOffset = 360.0 * (double)arm / (double)arms;
            for (int sample = 0; sample <= trailLength; ++sample) {
                float offset = (float)sample / (float)trailLength;
                double thetaDegrees = ((double)sample / 1.5 + ageSpeedTicks) * 8.0 + armOffset;
                double theta = Math.toRadians(thetaDegrees);
                double waveDegrees = (ageSpeedTicks + (double)(sample * (arm + 1))) * 3.0;
                class_243 position = target.position().method_1031(Math.cos(theta) * (double)radius, (double)centerHeight + Math.sin(Math.toRadians(waveDegrees)) * (double)waveHeight, Math.sin(theta) * (double)radius);
                float halfSize = Math.max(baseSize * offset, baseSize * 0.3f);
                TargetESP.appendBillboard(matrices, output, camera, position, halfSize, TargetESP.multAlpha(baseColor, offset), 0.0f);
            }
        }
    }

    private void drawCrystals(class_4587 matrices, class_4597 consumers, class_12075 camera, Frame frame) {
        RenderTarget target = frame.target();
        if (target == null || frame.visibility() <= 0.001f) {
            return;
        }
        int color = TargetESP.displayColor(target, frame.visibility());
        EffectSettings settings = frame.settings();
        int crystalCount = Math.max(1, settings.amount());
        float size = settings.size();
        float radius = target.width() * settings.radius() * (1.2f - 0.5f * frame.visibility());
        class_243 targetCenter = target.position().method_1031(0.0, (double)(target.height() * 0.5f), 0.0);
        class_243[] positions = new class_243[crystalCount];
        class_4588 crystalOutput = consumers.method_73477(TargetRenderLayers.COLORED_TRIANGLES);
        for (int index = 0; index < positions.length; ++index) {
            class_243 crystalPosition;
            double degrees = (double)index * (360.0 / (double)positions.length) + frame.effectPhase() * 18.0;
            double theta = Math.toRadians(degrees);
            positions[index] = crystalPosition = target.position().method_1031(Math.sin(theta) * (double)radius, 0.1 + (double)target.height() * Math.abs(Math.sin(theta)), Math.cos(theta) * (double)radius);
            Vector3f direction = targetCenter.method_1020(crystalPosition).method_46409();
            if (direction.lengthSquared() < 1.0E-6f) {
                direction.set(0.0f, 1.0f, 0.0f);
            } else {
                direction.normalize();
            }
            Quaternionf rotation = new Quaternionf().rotationTo((Vector3fc)new Vector3f(0.0f, 1.0f, 0.0f), (Vector3fc)direction);
            matrices.method_22903();
            matrices.method_22904(crystalPosition.field_1352 - camera.field_63078.field_1352, crystalPosition.field_1351 - camera.field_63078.field_1351, crystalPosition.field_1350 - camera.field_63078.field_1350);
            matrices.method_22907((Quaternionfc)rotation);
            CrystalRenderer.emit(matrices, crystalOutput, 0.1f * size, color);
            matrices.method_22909();
        }
        class_4588 glowOutput = consumers.method_73477(TargetRenderLayers.textured(BLOOM_TEXTURE));
        int glowColor = TargetESP.multAlpha(color, 0.2f);
        for (class_243 position : positions) {
            TargetESP.appendBillboard(matrices, glowOutput, camera, position, 0.5f * size, glowColor, 0.0f);
        }
    }

    private void drawCubes(class_4587 matrices, class_4597 consumers, class_12075 camera, Frame frame) {
        if (frame.cubes().isEmpty()) {
            return;
        }
        int baseColor = TargetESP.displayColor(frame.target(), 1.0f);
        class_4588 faces = consumers.method_73477(TargetRenderLayers.COLORED_QUADS);
        for (CubeParticle.Visual visual : frame.cubes()) {
            int color = TargetESP.multAlpha(baseColor, visual.alpha());
            CubeParticle.emitFaces(visual, matrices, camera.field_63078, faces, color);
        }
        class_4588 lines = consumers.method_73477(TargetRenderLayers.COLORED_LINES);
        for (CubeParticle.Visual visual : frame.cubes()) {
            int color = TargetESP.multAlpha(baseColor, visual.alpha());
            CubeParticle.emitLines(visual, matrices, camera.field_63078, lines, color);
        }
        class_4588 class_45882 = consumers.method_73477(TargetRenderLayers.textured(CUBE_BLOOM_TEXTURE));
        for (CubeParticle.Visual visual : frame.cubes()) {
            int color = TargetESP.multAlpha(baseColor, visual.alpha());
            CubeParticle.emitBloom(visual, matrices, camera, class_45882, color);
        }
    }

    private void drawTriangle(class_4587 matrices, class_4597 consumers, class_12075 camera, Frame frame) {
        RenderTarget target = frame.target();
        if (target == null || frame.visibility() <= 0.001f) {
            return;
        }
        double phase = frame.effectPhase();
        float angle = class_3532.method_15363((float)((float)((Math.sin(phase / 0.15) + 1.0) * 15.0)), (float)0.0f, (float)30.0f);
        float pulse = (float)Math.clamp((Math.sin(phase / 0.5) + 1.0) * 0.1 + 0.8, 0.8, 1.0);
        float rotation = (float)(-((double)angle - 15.0) + (Math.sin(phase) + 1.0) * 180.0);
        float halfSize = Math.max(target.height(), target.width()) * frame.settings().size() * pulse * frame.visibility() * 0.5f;
        class_243 center = target.position().method_1031(0.0, (double)(target.height() * 0.5f + 0.1f), 0.0);
        class_4588 output = consumers.method_73477(TargetRenderLayers.textured(TRIANGLE_TEXTURE));
        TargetESP.appendBillboard(matrices, output, camera, center, halfSize, TargetESP.displayColor(target, frame.visibility()), rotation);
    }

    private void drawRing(class_4587 matrices, class_4597 consumers, class_12075 camera, Frame frame) {
        RenderTarget target = frame.target();
        if (target == null || frame.visibility() <= 0.001f) {
            return;
        }
        double elapsed = frame.effectPhase();
        boolean descending = elapsed > 1.0;
        double progress = elapsed;
        progress = descending ? progress - 1.0 : 1.0 - progress;
        progress = progress < 0.5 ? 2.0 * progress * progress : 1.0 - Math.pow(-2.0 * progress + 2.0, 2.0) * 0.5;
        double trail = (double)target.height() / 1.2 * (progress > 0.5 ? 1.0 - progress : progress) * (descending ? -1.0 : 1.0);
        int color = TargetESP.displayColor(target, frame.visibility());
        int strong = TargetESP.multAlpha(color, 0.88235295f);
        int transparent = TargetESP.multAlpha(color, 0.003921569f);
        float radius = frame.settings().radius();
        matrices.method_22903();
        matrices.method_22904(target.position().field_1352 - camera.field_63078.field_1352, target.position().field_1351 - camera.field_63078.field_1351, target.position().field_1350 - camera.field_63078.field_1350);
        class_4587.class_4665 entry = matrices.method_23760();
        Matrix4f matrix = entry.method_23761();
        float ringY = (float)((double)target.height() * progress);
        class_4588 strip = consumers.method_73477(TargetRenderLayers.COLORED_TRIANGLE_STRIP);
        for (int segment = 0; segment <= 96; ++segment) {
            double theta = Math.PI * 2 * (double)segment / 96.0;
            float x = (float)Math.cos(theta) * radius;
            float z = (float)Math.sin(theta) * radius;
            strip.method_22918((Matrix4fc)matrix, x, ringY, z).method_39415(strong);
            strip.method_22918((Matrix4fc)matrix, x, ringY + (float)trail, z).method_39415(transparent);
        }
        class_4588 lines = consumers.method_73477(TargetRenderLayers.COLORED_LINES);
        for (int segment = 0; segment < 96; ++segment) {
            double theta1 = Math.PI * 2 * (double)segment / 96.0;
            double theta2 = Math.PI * 2 * (double)(segment + 1) / 96.0;
            float x1 = (float)Math.cos(theta1) * radius;
            float z1 = (float)Math.sin(theta1) * radius;
            float x2 = (float)Math.cos(theta2) * radius;
            float z2 = (float)Math.sin(theta2) * radius;
            float dx = x2 - x1;
            float dz = z2 - z1;
            float inverseLength = class_3532.method_48119((float)(dx * dx + dz * dz));
            lines.method_22918((Matrix4fc)matrix, x1, ringY, z1).method_39415(color).method_60831(entry, dx *= inverseLength, 0.0f, dz *= inverseLength).method_75298(1.5f);
            lines.method_22918((Matrix4fc)matrix, x2, ringY, z2).method_39415(color).method_60831(entry, dx, 0.0f, dz).method_75298(1.5f);
        }
        matrices.method_22909();
    }

    private static void appendBillboard(class_4587 matrices, class_4588 output, class_12075 camera, class_243 worldPosition, float halfSize, int color, float rotation) {
        if (color >>> 24 == 0 || halfSize <= 0.0f) {
            return;
        }
        matrices.method_22903();
        matrices.method_22904(worldPosition.field_1352 - camera.field_63078.field_1352, worldPosition.field_1351 - camera.field_63078.field_1351, worldPosition.field_1350 - camera.field_63078.field_1350);
        matrices.method_22907((Quaternionfc)camera.field_63081);
        if (rotation != 0.0f) {
            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(rotation));
        }
        DrawUtility.drawImage(matrices, output, -halfSize, -halfSize, 0.0f, halfSize * 2.0f, halfSize * 2.0f, color);
        matrices.method_22909();
    }

    private static float imageRotation(double effectPhase) {
        double legPosition = effectPhase / 2.2;
        long leg = (long)Math.floor(legPosition);
        float progress = (float)(legPosition - (double)leg);
        float eased = (float)((1.0 - Math.cos((double)progress * Math.PI)) * 0.5);
        float from = (leg & 1L) == 0L ? -280.0f : 280.0f;
        float to = -from;
        return class_3532.method_16439((float)eased, (float)from, (float)to);
    }

    private static RenderTarget snapshot(class_1309 entity, float tickProgress) {
        class_243 position = new class_243(class_3532.method_16436((double)tickProgress, (double)entity.field_6038, (double)entity.method_23317()), class_3532.method_16436((double)tickProgress, (double)entity.field_5971, (double)entity.method_23318()), class_3532.method_16436((double)tickProgress, (double)entity.field_5989, (double)entity.method_23321()));
        float hurtProgress = 0.0f;
        if (VariantConfig.isHurtEnabled()) {
            float hurtTicks = class_3532.method_15363((float)((float)entity.field_6235 - tickProgress), (float)0.0f, (float)10.0f);
            float linear = hurtTicks / 10.0f;
            hurtProgress = linear * linear * (3.0f - 2.0f * linear);
        }
        return new RenderTarget(entity.method_5628(), position, entity.method_17681(), entity.method_17682(), hurtProgress);
    }

    private static boolean isTrackable(class_1309 entity, WorldExtractionContext context) {
        return entity != null && entity.method_73183() == context.world() && TargetManager.isEligibleTarget(entity);
    }

    private static int displayColor(RenderTarget target, float alphaMultiplier) {
        float hurt = target == null ? 0.0f : target.hurtProgress();
        return TargetESP.multAlpha(TargetESP.overCol(VariantConfig.getColorArgb(), -64765, hurt), alphaMultiplier);
    }

    static int multAlpha(int color, float multiplier) {
        int alpha = class_3532.method_15340((int)Math.round((float)(color >>> 24) * multiplier), (int)0, (int)255);
        return alpha << 24 | color & 0xFFFFFF;
    }

    static int overCol(int first, int second, float factor) {
        factor = class_3532.method_15363((float)factor, (float)0.0f, (float)1.0f);
        int red = Math.round((float)(first >> 16 & 0xFF) + (float)((second >> 16 & 0xFF) - (first >> 16 & 0xFF)) * factor);
        int green = Math.round((float)(first >> 8 & 0xFF) + (float)((second >> 8 & 0xFF) - (first >> 8 & 0xFF)) * factor);
        int blue = Math.round((float)(first & 0xFF) + (float)((second & 0xFF) - (first & 0xFF)) * factor);
        int alpha = Math.round((float)(first >>> 24) + (float)((second >>> 24) - (first >>> 24)) * factor);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    record RenderTarget(int entityId, class_243 position, float width, float height, float hurtProgress) {
    }

    private record Frame(Variant variant, RenderTarget target, float visibility, float scale, double effectPhase, EffectSettings settings, List<CubeParticle.Visual> cubes) {
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_12075
 *  net.minecraft.class_243
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_7833
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ch.suobaru.TargetEspmod.visual;

import ch.suobaru.TargetEspmod.visual.TargetESP;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_12075;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;

final class CubeParticle {
    private static final byte[][] EDGES = new byte[][]{{-1, -1, -1, 1, -1, -1}, {1, -1, -1, 1, -1, 1}, {1, -1, 1, -1, -1, 1}, {-1, -1, 1, -1, -1, -1}, {-1, 1, -1, 1, 1, -1}, {1, 1, -1, 1, 1, 1}, {1, 1, 1, -1, 1, 1}, {-1, 1, 1, -1, 1, -1}, {-1, -1, -1, -1, 1, -1}, {1, -1, -1, 1, 1, -1}, {1, -1, 1, 1, 1, 1}, {-1, -1, 1, -1, 1, 1}};
    private final int targetId;
    private final long createdAtMs;
    private double x;
    private double y;
    private double z;
    private class_243 anchorPos;
    private float targetHeight;
    private boolean fading;
    private long fadeStartedAtMs;
    private float fadeStartAlpha;
    private class_243 frozenWorldPos = class_243.field_1353;
    private float velocityX;
    private float velocityY;
    private float velocityZ;
    private float rotationX;
    private float rotationY;
    private float rotationZ;
    private final float rotationSpeedX;
    private final float rotationSpeedY;
    private final float rotationSpeedZ;

    CubeParticle(TargetESP.RenderTarget target, double x, double y, double z, long nowMs) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        this.targetId = target.entityId();
        this.anchorPos = target.position();
        this.targetHeight = target.height();
        this.x = x;
        this.y = y;
        this.z = z;
        this.createdAtMs = nowMs;
        this.rotationX = random.nextFloat() * 360.0f;
        this.rotationY = random.nextFloat() * 360.0f;
        this.rotationZ = random.nextFloat() * 360.0f;
        this.rotationSpeedX = 1.4f + random.nextFloat() * 3.4f;
        this.rotationSpeedY = 1.4f + random.nextFloat() * 3.4f;
        this.rotationSpeedZ = 1.4f + random.nextFloat() * 3.4f;
        this.velocityX = (random.nextFloat() - 0.5f) * 0.0022f;
        this.velocityY = 0.031f + random.nextFloat() * 0.02f;
        this.velocityZ = (random.nextFloat() - 0.5f) * 0.0022f;
    }

    void update(float deltaSeconds, long nowMs, TargetESP.RenderTarget currentTarget, double riseSpeed) {
        boolean targetLost;
        float step = deltaSeconds * 60.0f;
        this.rotationX = CubeParticle.wrapDegrees(this.rotationX + this.rotationSpeedX * step);
        this.rotationY = CubeParticle.wrapDegrees(this.rotationY + this.rotationSpeedY * step);
        this.rotationZ = CubeParticle.wrapDegrees(this.rotationZ + this.rotationSpeedZ * step);
        if (this.fading) {
            return;
        }
        if (currentTarget != null && currentTarget.entityId() == this.targetId) {
            this.anchorPos = currentTarget.position();
            this.targetHeight = currentTarget.height();
        }
        float motionStep = step * (float)riseSpeed;
        this.x += (double)(this.velocityX * motionStep);
        this.y += (double)(this.velocityY * motionStep);
        this.z += (double)(this.velocityZ * motionStep);
        this.velocityX *= (float)Math.pow(0.992f, motionStep);
        this.velocityZ *= (float)Math.pow(0.992f, motionStep);
        this.velocityY *= (float)Math.pow(0.989f, motionStep);
        double shoulderHeight = Math.max(2.2, (double)this.targetHeight * 1.85);
        if (this.y >= shoulderHeight) {
            this.y = shoulderHeight;
            this.beginFade(nowMs);
            return;
        }
        boolean bl = targetLost = currentTarget == null || currentTarget.entityId() != this.targetId;
        if (targetLost || nowMs - this.createdAtMs >= 560L) {
            this.beginFade(nowMs);
        }
    }

    boolean shouldRemove(long nowMs) {
        return this.fading && nowMs - this.fadeStartedAtMs >= 320L;
    }

    Visual snapshot(long nowMs, double sizeMultiplier) {
        float alpha = this.alpha(nowMs);
        if (alpha <= 0.001f) {
            return null;
        }
        float fadeProgress = this.fading ? class_3532.method_15363((float)((float)(nowMs - this.fadeStartedAtMs) / 320.0f), (float)0.0f, (float)1.0f) : 0.0f;
        class_243 position = this.fading ? this.frozenWorldPos : this.anchorPos.method_1031(this.x, this.y, this.z);
        return new Visual(position, this.rotationX, this.rotationY, this.rotationZ, 0.12f * (float)sizeMultiplier * class_3532.method_16439((float)fadeProgress, (float)1.0f, (float)0.45f), 0.95f * (float)sizeMultiplier * class_3532.method_16439((float)fadeProgress, (float)1.0f, (float)0.55f), alpha);
    }

    static void emitFaces(Visual visual, class_4587 matrices, class_243 cameraPos, class_4588 output, int color) {
        matrices.method_22903();
        CubeParticle.applyCubeTransform(visual, matrices, cameraPos);
        CubeParticle.appendFaces(output, matrices.method_23760().method_23761(), TargetESP.multAlpha(color, 0.16f));
        matrices.method_22909();
    }

    static void emitLines(Visual visual, class_4587 matrices, class_243 cameraPos, class_4588 output, int color) {
        matrices.method_22903();
        CubeParticle.applyCubeTransform(visual, matrices, cameraPos);
        class_4587.class_4665 entry = matrices.method_23760();
        CubeParticle.appendEdges(output, entry, entry.method_23761(), TargetESP.multAlpha(color, 0.7f));
        matrices.method_22909();
    }

    static void emitBloom(Visual visual, class_4587 matrices, class_12075 camera, class_4588 output, int color) {
        int bloomColor = TargetESP.multAlpha(color, 0.15f);
        if (bloomColor >>> 24 == 0) {
            return;
        }
        matrices.method_22903();
        matrices.method_22904(visual.position().field_1352 - camera.field_63078.field_1352, visual.position().field_1351 - camera.field_63078.field_1351, visual.position().field_1350 - camera.field_63078.field_1350);
        matrices.method_22907((Quaternionfc)camera.field_63081);
        matrices.method_22905(visual.glowScale(), visual.glowScale(), visual.glowScale());
        Matrix4f matrix = matrices.method_23760().method_23761();
        output.method_22918((Matrix4fc)matrix, -0.5f, 0.5f, 0.0f).method_22913(0.0f, 1.0f).method_39415(bloomColor);
        output.method_22918((Matrix4fc)matrix, 0.5f, 0.5f, 0.0f).method_22913(1.0f, 1.0f).method_39415(bloomColor);
        output.method_22918((Matrix4fc)matrix, 0.5f, -0.5f, 0.0f).method_22913(1.0f, 0.0f).method_39415(bloomColor);
        output.method_22918((Matrix4fc)matrix, -0.5f, -0.5f, 0.0f).method_22913(0.0f, 0.0f).method_39415(bloomColor);
        matrices.method_22909();
    }

    private void beginFade(long nowMs) {
        if (this.fading) {
            return;
        }
        this.frozenWorldPos = this.anchorPos.method_1031(this.x, this.y, this.z);
        this.fadeStartAlpha = this.attachedAlpha(nowMs);
        this.fadeStartedAtMs = nowMs;
        this.fading = true;
    }

    private static void applyCubeTransform(Visual visual, class_4587 matrices, class_243 cameraPos) {
        matrices.method_22904(visual.position().field_1352 - cameraPos.field_1352, visual.position().field_1351 - cameraPos.field_1351, visual.position().field_1350 - cameraPos.field_1350);
        matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(visual.rotationX()));
        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(visual.rotationY()));
        matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(visual.rotationZ()));
        matrices.method_22905(visual.cubeScale(), visual.cubeScale(), visual.cubeScale());
    }

    private float alpha(long nowMs) {
        if (!this.fading) {
            return this.attachedAlpha(nowMs);
        }
        float fade = 1.0f - class_3532.method_15363((float)((float)(nowMs - this.fadeStartedAtMs) / 320.0f), (float)0.0f, (float)1.0f);
        return this.fadeStartAlpha * fade;
    }

    private float attachedAlpha(long nowMs) {
        float fadeIn = class_3532.method_15363((float)((float)(nowMs - this.createdAtMs) / 140.0f), (float)0.0f, (float)1.0f);
        float preFade = 1.0f - class_3532.method_15363((float)((float)(nowMs - this.createdAtMs - 440L) / 120.0f), (float)0.0f, (float)0.35f);
        return fadeIn * preFade;
    }

    private static void appendFaces(class_4588 output, Matrix4f matrix, int color) {
        float min = -0.5f;
        float max = 0.5f;
        CubeParticle.quad(output, matrix, min, min, min, max, min, min, max, min, max, min, min, max, color);
        CubeParticle.quad(output, matrix, min, max, min, min, max, max, max, max, max, max, max, min, color);
        CubeParticle.quad(output, matrix, min, min, min, min, max, min, max, max, min, max, min, min, color);
        CubeParticle.quad(output, matrix, min, min, max, max, min, max, max, max, max, min, max, max, color);
        CubeParticle.quad(output, matrix, min, min, min, min, min, max, min, max, max, min, max, min, color);
        CubeParticle.quad(output, matrix, max, min, min, max, max, min, max, max, max, max, min, max, color);
    }

    private static void quad(class_4588 output, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int color) {
        output.method_22918((Matrix4fc)matrix, x1, y1, z1).method_39415(color);
        output.method_22918((Matrix4fc)matrix, x2, y2, z2).method_39415(color);
        output.method_22918((Matrix4fc)matrix, x3, y3, z3).method_39415(color);
        output.method_22918((Matrix4fc)matrix, x4, y4, z4).method_39415(color);
    }

    private static void appendEdges(class_4588 output, class_4587.class_4665 entry, Matrix4f matrix, int color) {
        for (byte[] edge : EDGES) {
            float x1 = (float)edge[0] * 0.5f;
            float y1 = (float)edge[1] * 0.5f;
            float z1 = (float)edge[2] * 0.5f;
            float x2 = (float)edge[3] * 0.5f;
            float y2 = (float)edge[4] * 0.5f;
            float z2 = (float)edge[5] * 0.5f;
            float dx = x2 - x1;
            float dy = y2 - y1;
            float dz = z2 - z1;
            float inverseLength = class_3532.method_48119((float)(dx * dx + dy * dy + dz * dz));
            output.method_22918((Matrix4fc)matrix, x1, y1, z1).method_39415(color).method_60831(entry, dx *= inverseLength, dy *= inverseLength, dz *= inverseLength).method_75298(1.5f);
            output.method_22918((Matrix4fc)matrix, x2, y2, z2).method_39415(color).method_60831(entry, dx, dy, dz).method_75298(1.5f);
        }
    }

    private static float wrapDegrees(float value) {
        return (value %= 360.0f) < 0.0f ? value + 360.0f : value;
    }

    record Visual(class_243 position, float rotationX, float rotationY, float rotationZ, float cubeScale, float glowScale, float alpha) {
    }
}


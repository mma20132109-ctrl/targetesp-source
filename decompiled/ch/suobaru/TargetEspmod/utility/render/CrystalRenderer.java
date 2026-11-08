/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 */
package ch.suobaru.TargetEspmod.utility.render;

import net.minecraft.class_4587;
import net.minecraft.class_4588;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;

public final class CrystalRenderer {
    private static final Vector3f[] VERTICES = new Vector3f[]{new Vector3f(0.0f, 1.5f, 0.0f), new Vector3f(0.0f, -1.5f, 0.0f), new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(-1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, 0.0f, -1.0f)};
    private static final int[][] FACES = new int[][]{{0, 2, 4}, {0, 4, 3}, {0, 3, 5}, {0, 5, 2}, {1, 4, 2}, {1, 3, 4}, {1, 5, 3}, {1, 2, 5}};
    private static final float[] FACE_BRIGHTNESS = new float[]{1.0f, 0.8f, 0.6f, 0.9f, 0.7f, 0.5f, 0.4f, 0.6f};

    private CrystalRenderer() {
    }

    public static void emit(class_4587 matrices, class_4588 output, float size, int color) {
        matrices.method_22903();
        matrices.method_22905(size, size, size);
        Matrix4f matrix = matrices.method_23760().method_23761();
        for (int index = 0; index < FACES.length; ++index) {
            int[] face = FACES[index];
            int shadedColor = CrystalRenderer.applyBrightness(color, FACE_BRIGHTNESS[index]);
            for (int vertexIndex : face) {
                Vector3f vertex = VERTICES[vertexIndex];
                output.method_22918((Matrix4fc)matrix, vertex.x, vertex.y, vertex.z).method_39415(shadedColor);
            }
        }
        matrices.method_22909();
    }

    private static int applyBrightness(int color, float brightness) {
        int alpha = color >> 24 & 0xFF;
        int red = Math.clamp((long)Math.round((float)(color >> 16 & 0xFF) * brightness), 0, 255);
        int green = Math.clamp((long)Math.round((float)(color >> 8 & 0xFF) * brightness), 0, 255);
        int blue = Math.clamp((long)Math.round((float)(color & 0xFF) * brightness), 0, 255);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }
}


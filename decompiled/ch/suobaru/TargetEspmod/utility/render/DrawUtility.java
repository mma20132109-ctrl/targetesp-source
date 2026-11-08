/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ch.suobaru.TargetEspmod.utility.render;

import net.minecraft.class_4587;
import net.minecraft.class_4588;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class DrawUtility {
    private DrawUtility() {
    }

    public static void drawImage(class_4587 matrices, class_4588 output, float x, float y, float z, float width, float height, int color) {
        Matrix4f matrix = matrices.method_23760().method_23761();
        output.method_22918((Matrix4fc)matrix, x, y + height, z).method_22913(0.0f, 1.0f).method_39415(color);
        output.method_22918((Matrix4fc)matrix, x + width, y + height, z).method_22913(1.0f, 1.0f).method_39415(color);
        output.method_22918((Matrix4fc)matrix, x + width, y, z).method_22913(1.0f, 0.0f).method_39415(color);
        output.method_22918((Matrix4fc)matrix, x, y, z).method_22913(0.0f, 0.0f).method_39415(color);
    }
}


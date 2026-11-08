/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_10799
 *  net.minecraft.class_12247
 *  net.minecraft.class_156
 *  net.minecraft.class_1921
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 */
package ch.suobaru.TargetEspmod.utility.render;

import ch.suobaru.TargetEspmod.TargetEspmodClient;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Function;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_156;
import net.minecraft.class_1921;
import net.minecraft.class_290;
import net.minecraft.class_2960;

public final class TargetRenderLayers {
    private static final RenderPipeline TEXTURED_PIPELINE = TargetRenderLayers.registerTextured("textured_additive", DepthTestFunction.LEQUAL_DEPTH_TEST);
    private static final RenderPipeline TEXTURED_OVERLAY_PIPELINE = TargetRenderLayers.registerTextured("textured_overlay", DepthTestFunction.NO_DEPTH_TEST);
    private static final RenderPipeline QUADS_PIPELINE = TargetRenderLayers.registerColored("colored_quads", VertexFormat.class_5596.field_27382);
    private static final RenderPipeline TRIANGLES_PIPELINE = TargetRenderLayers.registerColored("colored_triangles", VertexFormat.class_5596.field_27379);
    private static final RenderPipeline TRIANGLE_STRIP_PIPELINE = TargetRenderLayers.registerColored("colored_triangle_strip", VertexFormat.class_5596.field_27380);
    private static final RenderPipeline LINES_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56859}).withLocation(TargetEspmodClient.id("pipeline/additive_lines")).withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).build());
    public static final class_1921 COLORED_QUADS = TargetRenderLayers.layer("colored_quads", QUADS_PIPELINE);
    public static final class_1921 COLORED_TRIANGLES = TargetRenderLayers.layer("colored_triangles", TRIANGLES_PIPELINE);
    public static final class_1921 COLORED_TRIANGLE_STRIP = TargetRenderLayers.layer("colored_triangle_strip", TRIANGLE_STRIP_PIPELINE);
    public static final class_1921 COLORED_LINES = TargetRenderLayers.layer("colored_lines", LINES_PIPELINE);
    private static final Function<class_2960, class_1921> TEXTURED_LAYERS = class_156.method_34866(texture -> class_1921.method_75940((String)("targetespmod_textured_additive_" + String.valueOf(texture)), (class_12247)class_12247.method_75927((RenderPipeline)TEXTURED_PIPELINE).method_75934("Sampler0", texture).method_75938()));
    private static final Function<class_2960, class_1921> TEXTURED_OVERLAY_LAYERS = class_156.method_34866(texture -> class_1921.method_75940((String)("targetespmod_textured_overlay_" + String.valueOf(texture)), (class_12247)class_12247.method_75927((RenderPipeline)TEXTURED_OVERLAY_PIPELINE).method_75934("Sampler0", texture).method_75938()));

    private TargetRenderLayers() {
    }

    public static void initialize() {
    }

    public static class_1921 textured(class_2960 texture) {
        return TEXTURED_LAYERS.apply(texture);
    }

    public static class_1921 texturedOverlay(class_2960 texture) {
        return TEXTURED_OVERLAY_LAYERS.apply(texture);
    }

    private static class_1921 layer(String name, RenderPipeline pipeline) {
        return class_1921.method_75940((String)("targetespmod_" + name), (class_12247)class_12247.method_75927((RenderPipeline)pipeline).method_75938());
    }

    private static RenderPipeline registerTextured(String name, DepthTestFunction depthTestFunction) {
        return class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(TargetEspmodClient.id("pipeline/" + name)).withVertexShader("core/position_tex_color").withFragmentShader("core/position_tex_color").withSampler("Sampler0").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(depthTestFunction).withDepthWrite(false).withCull(false).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27382).build());
    }

    private static RenderPipeline registerColored(String name, VertexFormat.class_5596 drawMode) {
        return class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_60125}).withLocation(TargetEspmodClient.id("pipeline/" + name)).withVertexShader("core/position_color").withFragmentShader("core/position_color").withBlend(BlendFunction.LIGHTNING).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(false).withCull(false).withVertexFormat(class_290.field_1576, drawMode).build());
    }
}


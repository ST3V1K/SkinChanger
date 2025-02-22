package me.edoren.skin_changer.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

public class CustomSkinTexture extends DynamicTexture {

    private final ResourceLocation location;

    public CustomSkinTexture(ResourceLocation location, NativeImage image) {
        super(image);
        this.location = location;
    }

    public ResourceLocation getLocation() {
        return this.location;
    }
}

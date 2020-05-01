package me.edoren.skin_changer.client;

import com.mojang.authlib.GameProfile;
import me.edoren.skin_changer.client.api.ISkin;
import me.edoren.skin_changer.client.api.SkinLoaderService;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class ClientController {
    private static ClientController singleInstance = null;

    final Map<ByteBuffer, CustomSkinTexture> textures = new WeakHashMap<>();

    public static ClientController GetInstance() {
        if (singleInstance == null)
            singleInstance = new ClientController();

        return singleInstance;
    }

    private ClientController() {
    }

    public void initialize() {
        MinecraftForge.EVENT_BUS.addListener(this::onClientTickEvent);
    }

    public ResourceLocation getLocationCape(GameProfile profile) {
        ISkin cape = SkinLoaderService.GetInstance().getCape(profile);
        if (cape != null && cape.isDataReady())
            return getOrCreateTexture(cape.getData(), cape).getLocation();
        return null;
    }

    public ResourceLocation getLocationSkin(GameProfile profile) {
        ISkin skin = SkinLoaderService.GetInstance().getSkin(profile);
        if (skin != null && skin.isDataReady())
            return getOrCreateTexture(skin.getData(), skin).getLocation();
        return null;
    }

    public String getSkinType(GameProfile profile) {
        ResourceLocation location = getLocationSkin(profile);
        if (location != null) {
            ISkin skin = SkinLoaderService.GetInstance().getSkin(profile);
            if (skin != null && skin.isDataReady())
                return skin.getSkinType();
        }
        return null;
    }

    public CustomSkinTexture getOrCreateTexture(ByteBuffer data, ISkin skin) {
        if (!textures.containsKey(data)) {
            CustomSkinTexture texture = new CustomSkinTexture(generateRandomLocation(), data);
            Minecraft.getInstance().getTextureManager().func_229263_a_(texture.getLocation(), texture);
            textures.put(data, texture);

            if (skin != null) {
                skin.setRemovalListener(s -> {
                    if (data == s.getData()) {
                        Minecraft.getInstance().execute(() -> {
                            Minecraft.getInstance().getTextureManager().deleteTexture(texture.getLocation());
                            textures.remove(data);
                        });
                    }
                });
            }
        }
        return textures.get(data);
    }

    private void onClientTickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            World world = Minecraft.getInstance().world;
            if (world != null) {
                for (PlayerEntity player : world.getPlayers()) {
                    SkinLoaderService.GetInstance().getSkin(player.getGameProfile());
                    SkinLoaderService.GetInstance().getCape(player.getGameProfile());
                }
            }
        }
    }

    private ResourceLocation generateRandomLocation() {
        return new ResourceLocation("skin_changer", String.format("textures/generated/%s", UUID.randomUUID().toString()));
    }
}

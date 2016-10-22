/*******************************************************************************
 * Created by Himmelt on 2016/10/11.
 * Copyright (c) 2015-2016. Himmelt All rights reserved.
 * https://opensource.org/licenses/MIT
 ******************************************************************************/

package org.soraworld.chatbubbles;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

public class ChatBubblesResourcePack implements IResourcePack {
    public static final Set defaultResourceDomains = ImmutableSet.of("chatbubbles");
    private ChatBubblesDefaultResourcePack defaultResourcePack;
    private MotionTrackerDefaultResourcePack fileResourcePack;
    private ChatBubblesDefaultResourcePack folderResourcePack;

    public ChatBubblesResourcePack() {
        List defaultResourcePacks = (List)ReflectionUtils.getPrivateFieldByType(Minecraft.func_71410_x(), Minecraft.class, List.class, 1);
        DefaultResourcePack realDefaultResourcePack = (DefaultResourcePack)((DefaultResourcePack)defaultResourcePacks.get(0));
        File fileAssets = (File)ReflectionUtils.getPrivateFieldByType(realDefaultResourcePack, DefaultResourcePack.class, File.class, 0);
        this.defaultResourcePack = new ChatBubblesDefaultResourcePack(fileAssets);
        this.fileResourcePack = new MotionTrackerDefaultResourcePack(Minecraft.func_71410_x().field_71412_D);
        this.folderResourcePack = new ChatBubblesDefaultResourcePack(Minecraft.func_71410_x().field_71412_D);
    }

    public Set func_110587_b() {
        return defaultResourceDomains;
    }

    public InputStream func_110590_a(ResourceLocation var1) throws IOException {
        InputStream e;
        try {
            e = this.defaultResourcePack.getInputStream(var1);
            return e;
        } catch (IOException var5) {
            try {
                e = this.fileResourcePack.func_110590_a(var1);
                return e;
            } catch (IOException var4) {
                try {
                    e = this.folderResourcePack.func_110590_a(var1);
                    return e;
                } catch (IOException var3) {
                    throw var3;
                }
            }
        }
    }

    public boolean func_110589_b(ResourceLocation var1) {
        return this.defaultResourcePack.resourceExists(var1) || this.fileResourcePack.func_110589_b(var1) || this.folderResourcePack.func_110589_b(var1);
    }

    public IMetadataSection func_135058_a(IMetadataSerializer var1, String var2) throws IOException {
        return null;
    }

    public BufferedImage func_110586_a() throws IOException {
        return null;
    }

    public String func_130077_b() {
        return "ChatBubbles";
    }

    @Override
    public InputStream getInputStream(ResourceLocation p_110590_1_) throws IOException {
        return null;
    }

    @Override
    public boolean resourceExists(ResourceLocation p_110589_1_) {
        return false;
    }

    @Override
    public Set getResourceDomains() {
        return null;
    }

    @Override
    public IMetadataSection getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_) throws IOException {
        return null;
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return null;
    }

    @Override
    public String getPackName() {
        return null;
    }
}

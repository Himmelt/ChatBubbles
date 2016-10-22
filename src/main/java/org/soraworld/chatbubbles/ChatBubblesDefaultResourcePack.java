/*******************************************************************************
 * Created by Himmelt on 2016/10/11.
 * Copyright (c) 2015-2016. Himmelt All rights reserved.
 * https://opensource.org/licenses/MIT
 ******************************************************************************/

package org.soraworld.chatbubbles;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.util.Map;
import java.util.Set;

public class ChatBubblesDefaultResourcePack {
    public static final Set defaultResourceDomains = ImmutableSet.of("chatbubbles");
    private final Map mapResourceFiles = Maps.newHashMap();
    private final File fileAssets;
    private static final String __OBFID = "CL_00001076";

    public ChatBubblesDefaultResourcePack(File par1File) {
        this.fileAssets = par1File;
        this.readAssetsDir(this.fileAssets);
    }

    public InputStream getInputStream(ResourceLocation par1ResourceLocation) throws IOException {
        InputStream var2 = this.getResourceStream(par1ResourceLocation);
        if(var2 != null) {
            return var2;
        } else {
            File var3 = (File)this.mapResourceFiles.get(par1ResourceLocation.toString());
            if(var3 != null) {
                return new FileInputStream(var3);
            } else {
                throw new FileNotFoundException(par1ResourceLocation.func_110623_a());
            }
        }
    }

    private InputStream getResourceStream(ResourceLocation par1ResourceLocation) {
        return DefaultResourcePack.class.getResourceAsStream("/assets/chatbubbles/" + par1ResourceLocation.func_110623_a());
    }

    public void addResourceFile(String par1Str, File par2File) {
        this.mapResourceFiles.put((new ResourceLocation(par1Str)).toString(), par2File);
    }

    public boolean resourceExists(ResourceLocation par1ResourceLocation) {
        return this.getResourceStream(par1ResourceLocation) != null || this.mapResourceFiles.containsKey(par1ResourceLocation.toString());
    }

    public Set getResourceDomains() {
        return defaultResourceDomains;
    }

    public void readAssetsDir(File par1File) {
        if(par1File.isDirectory()) {
            File[] var2 = par1File.listFiles();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                File var5 = var2[var4];
                this.readAssetsDir(var5);
            }
        } else {
            this.addResourceFile(getRelativeName(this.fileAssets, par1File), par1File);
        }

    }

    protected static String getRelativeName(File par0File, File par1File) {
        return par0File.toURI().relativize(par1File.toURI()).getPath();
    }
}

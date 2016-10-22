/*******************************************************************************
 * Created by Himmelt on 2016/10/11.
 * Copyright (c) 2015-2016. Himmelt All rights reserved.
 * https://opensource.org/licenses/MIT
 ******************************************************************************/

package org.soraworld.chatbubbles;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(
        modid = "chatbubbles",
        version = "1.7.10",
        name = "ChatBubbles"
)
public class ChatBubblesMod {
    ChatBubbles chatBubbles;
    public static final String NAME = "ChatBubbles";
    public static final String MODID = "chatbubbles";
    public static final String VERSION = "1.7.2";
    private boolean isEnabled = true;
    @Mod.Instance("ChatBubblesMod")
    public static ChatBubblesMod instance;
    @SidedProxy(
            clientSide = "org.soraworld.chatbubbles.ClientProxy",
            serverSide = "org.soraworld.chatbubbles.CommonProxy"
    )
    public static CommonProxy proxy;

    public ChatBubblesMod() {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        this.chatBubbles = new ChatBubbles();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientChatEvent(ClientChatReceivedEvent event) {
        /* func_150260_c */
        this.chatBubbles.clientString(event.message.getFormattedText());
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if(event.type == RenderGameOverlayEvent.ElementType.ALL) {
            /* func_71410_x */
            this.chatBubbles.onTickInGame(Minecraft.getMinecraft());
        }

    }

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if(event.entity instanceof EntityPlayer) {
            double var4 = event.entity.field_70142_S + (event.entity.field_70165_t - event.entity.field_70142_S) * (double)event.partialRenderTick;
            double var6 = event.entity.field_70137_T + (event.entity.field_70163_u - event.entity.field_70137_T) * (double)event.partialRenderTick;
            double var8 = event.entity.field_70136_U + (event.entity.field_70161_v - event.entity.field_70136_U) * (double)event.partialRenderTick;
            EntityLivingBase var10001 = (EntityLivingBase)event.entity;
            /* RenderManager.field_78727_a */
            RenderManager var10003 = RenderManager.field_78727_a;
            double var10002 = var4 - RenderManager.field_78725_b;
            RenderManager var10004 = RenderManager.field_78727_a;
            double var81 = var6 - RenderManager.field_78726_c;
            RenderManager var10005 = RenderManager.field_78727_a;
            this.chatBubbles.renderPlayerChatBubbles.func_77033_b(var10001, var10002, var81, var8 - RenderManager.field_78723_d);
        }

    }

    private static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException var2) {
            return false;
        }
    }
}

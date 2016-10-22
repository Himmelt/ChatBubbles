/*******************************************************************************
 * Created by Himmelt on 2016/10/11.
 * Copyright (c) 2015-2016. Himmelt All rights reserved.
 * https://opensource.org/licenses/MIT
 ******************************************************************************/

package org.soraworld.chatbubbles;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Iterator;

public class RenderPlayerChatBubbles extends RenderPlayer {
    ArrayList<ChatBubbleMessage> relevantMessages;
    float r = 0.0F;
    float g = 0.0F;
    float b = 0.0F;
    int lineWidth = 4;

    public RenderPlayerChatBubbles() {
        System.out.println("**Chat Bubble Renderer Initialized**");
        this.relevantMessages = new ArrayList();
    }

    private String scrubCodes(String string) {
        string = string.replaceAll("(\\xA7.)", "");
        return string;
    }

    protected void func_77033_b(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6) {
        if(par1EntityLivingBase != this.field_76990_c.field_78734_h) {
            try {
                String e = this.scrubCodes(((EntityPlayer)par1EntityLivingBase).func_70005_c_());
                this.r = (128.0F + (float)(e.charAt(0) % 32 * 4)) / 256.0F;
                if(e.length() > 1) {
                    this.g = (128.0F + (float)(e.charAt(1) % 32 * 4)) / 256.0F;
                }

                if(e.length() > 2) {
                    this.b = (128.0F + (float)(e.charAt(2) % 32 * 4)) / 256.0F;
                }

                int currentTime = ChatBubbles.instance.game.field_71456_v.func_73834_c();
                this.relevantMessages = ChatBubbles.instance.getMessagesByAuthor(e);
                int lines = 2;

                String[] messageLines;
                for(Iterator i$ = this.relevantMessages.iterator(); i$.hasNext(); lines = lines + messageLines.length + 1) {
                    ChatBubbleMessage message = (ChatBubbleMessage)i$.next();
                    messageLines = message.getMessageLines();
                    float remainingTime = (float)(ChatBubbles.instance.MESSAGELIFETIME - (currentTime - message.getUpdatedCounter()));
                    this.renderMessage(lines, messageLines, remainingTime, (EntityPlayer)par1EntityLivingBase, par2, par4, par6);
                }
            } catch (Exception var15) {
                System.out.println("***Exception in bubbleRenderer***: " + var15);
            }
        }

    }

    protected void renderMessage(int lines, String[] message, float remainingTime, EntityPlayer par1EntityPlayer, double par2, double par4, double par6) {
        double var10 = par1EntityPlayer.func_70068_e(this.field_76990_c.field_78734_h);
        float var12 = par1EntityPlayer.func_70093_af()?32.0F:64.0F;
        int brightness = 15728880;
        int brightMod = brightness % 65536;
        int brightDiv = brightness / 65536;
        OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)brightMod / 1.0F, (float)brightDiv / 1.0F);
        if(var10 <= (double)(var12 * var12)) {
            FontRenderer fontRenderer = this.func_76983_a();
            float var13 = 1.6F;
            float var14 = 0.016666668F * var13;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)par2 + 0.0F, (float)par4 + 2.3F, (float)par6);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-this.field_76990_c.field_78735_i, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.field_76990_c.field_78732_j, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(-var14, -var14, var14);
            GL11.glDisable(2896);
            GL11.glDisable(2912);
            GL11.glDepthMask(false);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            int maxWidth = 8;

            int left;
            for(left = message.length - 1; left >= 0; --left) {
                int right = fontRenderer.func_78256_a(message[left]) / 2;
                if(right > maxWidth) {
                    maxWidth = right;
                }
            }

            left = -maxWidth + 1;
            Tessellator var15 = Tessellator.field_78398_a;
            int top = -(lines + message.length - 1) * 9 + 2;
            int bottom = -lines * 9 + 6;
            float alphaFadeFactor = 1.0F;
            if(remainingTime < 20.0F) {
                alphaFadeFactor = remainingTime / 20.0F;
                alphaFadeFactor *= alphaFadeFactor;
            }

            GL11.glEnable('耷');
            GL11.glPolygonOffset(1.0F, 5.0F);
            GL11.glEnable(3553);
            GL11.glDisable(2929);
            this.drawBubble(1.0F, 1.0F, 1.0F, Math.min(alphaFadeFactor, 0.25F), (float)top, (float)bottom, (float)left, (float)maxWidth, var15, 5.0F);
            GL11.glPolygonOffset(1.0F, 3.0F);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            this.drawBubble(this.r, this.g, this.b, alphaFadeFactor, (float)top, (float)bottom, (float)left, (float)maxWidth, var15, 3.0F);

            for(int t = message.length - 1; t >= 0; --t) {
                GL11.glPolygonOffset(1.0F, 1.0F);
                GL11.glDisable(2929);
                int var16 = -lines * 9;
                alphaFadeFactor = Math.max(alphaFadeFactor, 0.016F);
                int textFade = (int)(alphaFadeFactor * 255.0F);
                int textFadeBG = Math.min(textFade, 127) << 24;
                textFade <<= 24;
                fontRenderer.func_78276_b(message[t], -maxWidth, var16, textFadeBG);
                GL11.glEnable(2929);
                GL11.glDepthMask(true);
                fontRenderer.func_78276_b(message[t], -maxWidth, var16, textFade);
                ++lines;
            }

            GL11.glDisable('耷');
            GL11.glEnable(2912);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }

    }

    private void drawBubble(float r, float g, float b, float a, float top, float bottom, float left, float right, Tessellator var15, float offset) {
        this.img("images/chatBubble.png");
        var15.func_78382_b();
        var15.func_78369_a(r, g, b, a);
        var15.func_78374_a((double)left, (double)top, 0.0D, 0.0625D, 0.125D);
        var15.func_78374_a((double)left, (double)bottom, 0.0D, 0.0625D, 0.875D);
        var15.func_78374_a((double)right, (double)bottom, 0.0D, 0.9375D, 0.875D);
        var15.func_78374_a((double)right, (double)top, 0.0D, 0.9375D, 0.125D);
        var15.func_78381_a();
        var15.func_78382_b();
        var15.func_78369_a(r, g, b, a);
        var15.func_78374_a((double)(left - (float)this.lineWidth), (double)top, 0.0D, 0.0D, 0.125D);
        var15.func_78374_a((double)(left - (float)this.lineWidth), (double)bottom, 0.0D, 0.0D, 0.875D);
        var15.func_78374_a((double)left, (double)bottom, 0.0D, 0.0625D, 0.875D);
        var15.func_78374_a((double)left, (double)top, 0.0D, 0.0625D, 0.125D);
        var15.func_78381_a();
        var15.func_78382_b();
        var15.func_78369_a(r, g, b, a);
        var15.func_78374_a((double)right, (double)top, 0.0D, 0.9375D, 0.125D);
        var15.func_78374_a((double)right, (double)bottom, 0.0D, 0.9375D, 0.875D);
        var15.func_78374_a((double)(right + (float)this.lineWidth), (double)bottom, 0.0D, 1.0D, 0.875D);
        var15.func_78374_a((double)(right + (float)this.lineWidth), (double)top, 0.0D, 1.0D, 0.125D);
        var15.func_78381_a();
        var15.func_78382_b();
        var15.func_78369_a(r, g, b, a);
        var15.func_78374_a((double)left, (double)(top - (float)this.lineWidth), 0.0D, 0.0625D, 0.0D);
        var15.func_78374_a((double)left, (double)top, 0.0D, 0.0625D, 0.125D);
        var15.func_78374_a((double)right, (double)top, 0.0D, 0.9375D, 0.125D);
        var15.func_78374_a((double)right, (double)(top - (float)this.lineWidth), 0.0D, 0.9375D, 0.0D);
        var15.func_78381_a();
        var15.func_78382_b();
        var15.func_78369_a(r, g, b, a);
        var15.func_78374_a((double)left, (double)bottom, 0.0D, 0.0625D, 0.875D);
        var15.func_78374_a((double)left, (double)(bottom + (float)this.lineWidth), 0.0D, 0.0625D, 1.0D);
        var15.func_78374_a(-2.0D, (double)(bottom + (float)this.lineWidth), 0.0D, 0.5D, 1.0D);
        var15.func_78374_a(-2.0D, (double)bottom, 0.0D, 0.5D, 0.875D);
        var15.func_78381_a();
        var15.func_78382_b();
        var15.func_78369_a(r, g, b, a);
        var15.func_78374_a(-2.0D, (double)bottom, 0.0D, 0.5D, 0.875D);
        var15.func_78374_a(-2.0D, (double)(bottom + (float)this.lineWidth), 0.0D, 0.5D, 1.0D);
        var15.func_78374_a(6.0D, (double)(bottom + (float)this.lineWidth), 0.0D, 0.625D, 1.0D);
        var15.func_78374_a(6.0D, (double)bottom, 0.0D, 0.625D, 0.875D);
        var15.func_78381_a();
        var15.func_78382_b();
        var15.func_78369_a(r, g, b, a);
        var15.func_78374_a(6.0D, (double)bottom, 0.0D, 0.625D, 0.875D);
        var15.func_78374_a(6.0D, (double)(bottom + (float)this.lineWidth), 0.0D, 0.625D, 1.0D);
        var15.func_78374_a((double)right, (double)(bottom + (float)this.lineWidth), 0.0D, 0.9375D, 1.0D);
        var15.func_78374_a((double)right, (double)bottom, 0.0D, 0.9375D, 0.875D);
        var15.func_78381_a();
        var15.func_78382_b();
        var15.func_78369_a(r, g, b, a);
        var15.func_78374_a((double)(left - (float)this.lineWidth), (double)(top - (float)this.lineWidth), 0.0D, 0.0D, 0.0D);
        var15.func_78374_a((double)(left - (float)this.lineWidth), (double)top, 0.0D, 0.0D, 0.125D);
        var15.func_78374_a((double)left, (double)top, 0.0D, 0.0625D, 0.125D);
        var15.func_78374_a((double)left, (double)(top - (float)this.lineWidth), 0.0D, 0.0625D, 0.0D);
        var15.func_78381_a();
        var15.func_78382_b();
        var15.func_78369_a(r, g, b, a);
        var15.func_78374_a((double)right, (double)(top - (float)this.lineWidth), 0.0D, 0.9375D, 0.0D);
        var15.func_78374_a((double)right, (double)top, 0.0D, 0.9375D, 0.125D);
        var15.func_78374_a((double)(right + (float)this.lineWidth), (double)top, 0.0D, 1.0D, 0.125D);
        var15.func_78374_a((double)(right + (float)this.lineWidth), (double)(top - (float)this.lineWidth), 0.0D, 1.0D, 0.0D);
        var15.func_78381_a();
        var15.func_78382_b();
        var15.func_78369_a(r, g, b, a);
        var15.func_78374_a((double)(left - (float)this.lineWidth), (double)bottom, 0.0D, 0.0D, 0.875D);
        var15.func_78374_a((double)(left - (float)this.lineWidth), (double)(bottom + (float)this.lineWidth), 0.0D, 0.0D, 1.0D);
        var15.func_78374_a((double)left, (double)(bottom + (float)this.lineWidth), 0.0D, 0.0625D, 1.0D);
        var15.func_78374_a((double)left, (double)bottom, 0.0D, 0.0625D, 0.875D);
        var15.func_78381_a();
        var15.func_78382_b();
        var15.func_78369_a(r, g, b, a);
        var15.func_78374_a((double)right, (double)bottom, 0.0D, 0.9375D, 0.875D);
        var15.func_78374_a((double)right, (double)(bottom + (float)this.lineWidth), 0.0D, 0.9375D, 1.0D);
        var15.func_78374_a((double)(right + (float)this.lineWidth), (double)(bottom + (float)this.lineWidth), 0.0D, 1.0D, 1.0D);
        var15.func_78374_a((double)(right + (float)this.lineWidth), (double)bottom, 0.0D, 1.0D, 0.875D);
        var15.func_78381_a();
        GL11.glPolygonOffset(1.0F, offset - 1.0F);
        this.img("images/chatBubbleTail.png");
        var15.func_78382_b();
        var15.func_78369_a(r, g, b, a);
        var15.func_78374_a(-2.0D, (double)(bottom + (float)this.lineWidth), 0.0D, 0.0D, 0.0D);
        var15.func_78374_a(-2.0D, (double)(bottom + (float)this.lineWidth + 8.0F), 0.0D, 0.0D, 1.0D);
        var15.func_78374_a(6.0D, (double)(bottom + (float)this.lineWidth + 8.0F), 0.0D, 1.0D, 1.0D);
        var15.func_78374_a(6.0D, (double)(bottom + (float)this.lineWidth), 0.0D, 1.0D, 0.0D);
        var15.func_78381_a();
    }

    public void img(String paramStr) {
        this.field_76990_c.field_78724_e.func_110577_a(new ResourceLocation("chatbubbles", paramStr));
    }
}

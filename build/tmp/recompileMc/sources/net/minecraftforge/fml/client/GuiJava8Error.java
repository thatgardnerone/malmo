/*
 * Minecraft Forge
 * Copyright (c) 2016.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Java8VersionException;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class GuiJava8Error extends GuiErrorScreen
{
    private Java8VersionException java8VersionException;
    public GuiJava8Error(Java8VersionException java8VersionException)
    {
        super(null,null);
        this.java8VersionException = java8VersionException;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    @Override
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, 50, this.height - 38, this.width/2 -55, 20, I18n.format("fml.button.visitjavadownloads")));
        if (java8VersionException.getMods().isEmpty())
        {
            this.buttonList.add(new GuiButton(3, this.width / 2 + 5, this.height - 38, this.width / 2 - 55, 20, I18n.format("fml.button.continue")));
        }
        else
        {
            this.buttonList.add(new GuiButton(2, this.width / 2 + 5, this.height - 38, this.width / 2 - 55, 20, I18n.format("menu.quit")));
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 1)
        {
            try
            {
                Desktop.getDesktop().browse(new URI("http://www.oracle.com/technetwork/java/javase/downloads/index.html"));
            }
            catch (Exception e)
            {
                FMLLog.log(Level.ERROR, e, "Problem launching browser");
            }
        }
        else if (button.id == 2)
        {
            FMLCommonHandler.instance().exitJava(1,true);
        }
        else if (button.id == 3)
        {
            FMLClientHandler.instance().showGuiScreen(null);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        int offset = 25;
        if (!java8VersionException.getMods().isEmpty())
        {
            this.drawCenteredString(this.fontRenderer, I18n.format("fml.messages.java8problem", TextFormatting.RED, TextFormatting.BOLD, TextFormatting.RESET), this.width / 2, offset, 0xFFFFFF);
        }
        else
        {
            this.drawCenteredString(this.fontRenderer, I18n.format("fml.messages.java8recommended", TextFormatting.RED, TextFormatting.BOLD, TextFormatting.RESET), this.width / 2, offset, 0xFFFFFF);
        }
        offset+=15;
        this.drawCenteredString(this.fontRenderer, I18n.format("fml.messages.javaversion", System.getProperty("java.version").split("\\.")[1], System.getProperty("java.version")), this.width / 2, offset, 0xFFFFFF);
        offset += 10;
        if (!java8VersionException.getMods().isEmpty())
        {
            this.drawCenteredString(this.fontRenderer, I18n.format("fml.messages.upgradejavaorremove", TextFormatting.RED,TextFormatting.BOLD, TextFormatting.RESET), this.width / 2, offset, 0xFFFFFF);
            offset += 15;
            this.drawCenteredString(this.fontRenderer, I18n.format("fml.messages.modslistedbelow", I18n.format("fml.messages.requirejava8")), this.width / 2, offset, 0xFFFFFF);
            offset += 10;
            this.drawCenteredString(this.fontRenderer, I18n.format("fml.messages.countbadandgood", java8VersionException.getMods().size(), Loader.instance().getActiveModList().size()), this.width / 2, offset, 0xFFFFFF);
            offset += 5;
            for (ModContainer mc : java8VersionException.getMods())
            {
                offset += 10;
                this.drawCenteredString(this.fontRenderer, String.format("%s (%s)", mc.getName(), mc.getModId()), this.width / 2, offset, 0xEEEEEE);
            }
        }
        else
        {
            String text = I18n.format("fml.messages.upgradejava", TextFormatting.RED,TextFormatting.BOLD, TextFormatting.RESET).replaceAll("\\\\n", "\n");
            List<String> lines = Lists.newArrayList();
            for (String line : text.split("\n"))
            {
                lines.addAll(this.fontRenderer.listFormattedStringToWidth(line, this.width - this.fontRenderer.FONT_HEIGHT * 4));
            }

            int maxWidth = 0;
            for (String line : lines)
            {
                maxWidth = Math.max(maxWidth, this.fontRenderer.getStringWidth(line));
            }

            for (String line : lines)
            {
                this.drawString(this.fontRenderer, line, (this.width - maxWidth) / 2, offset, 0xFFFFFF);
                offset += this.fontRenderer.FONT_HEIGHT + 2;
            }

            offset += 15;
        }
        // super.super
        for (int i = 0; i < this.buttonList.size(); ++i)
        {
            ((GuiButton)this.buttonList.get(i)).drawButton(this.mc, mouseX, mouseY);
        }
    }
}
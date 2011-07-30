package com.captiveimagination.game.GText;


import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import com.jme.image.Texture;
import com.jme.renderer.Renderer;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
*
* @author Victor Porof, blue_veek@yahoo.com
*/
public class GFont {

 private int type = BufferedImage.TYPE_INT_ARGB;
 private Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();
 private BufferedImage tempImage;
 private LinkedList<TextureState> charList;
 private int totalCharSet = 256;
 private int fontWidth = 1;
 private int fontHeight = 1;
 private int[] tWidths;
 private int tHeights;
 private int tAscent;
 private int tDescent;

 public GFont(Font font) {
  tempImage = new BufferedImage(fontWidth, fontHeight, type);
  Graphics2D g = (Graphics2D) tempImage.getGraphics();
  g.setFont(font);

  this.tAscent = g.getFontMetrics().getAscent();
  this.tDescent = g.getFontMetrics().getDescent();

  this.charList = new LinkedList<TextureState>();

  this.tWidths = new int[totalCharSet];
  this.tHeights = tAscent + tDescent;

  for (int i = 0; i < totalCharSet; i++) {
   this.fontWidth = g.getFontMetrics().charWidth((char) i);
   this.fontHeight = g.getFontMetrics().getHeight();
   if (fontWidth < 1) {
    fontWidth = 1;
   }
   tWidths[i] = fontWidth;

   int size = font.getSize();
   float posX = font.getSize() / 2f - fontWidth / 2f;
   float posY = tAscent - tDescent;
   BufferedImage bImage = new BufferedImage(size, size, type);
   Graphics2D gt = (Graphics2D) bImage.getGraphics();
   gt.setFont(font);
   gt.drawString(String.valueOf((char) i), posX, posY);

   TextureState cTextureState = renderer.createTextureState();
   Texture cTexure = TextureManager.loadTexture(bImage,
     Texture.MinificationFilter.Trilinear,
     Texture.MagnificationFilter.Bilinear, true);
   cTextureState.setTexture(cTexure);

   charList.add(cTextureState);
  }
 }

 public TextureState getChar(int charCode) {
  return charList.get(charCode);
 }

 public float getTextAscent() {
  return tAscent;
 }

 public float getTextDescent() {
  return tDescent;
 }

 public int[] getMetricsWidths() {
  return tWidths;
 }

 public int getMetricsHeights() {
  return tHeights;
 }
}
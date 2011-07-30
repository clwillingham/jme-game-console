package com.captiveimagination.game.GText;

import java.util.LinkedList;

import com.jme.renderer.ColorRGBA;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;

/**
*
* @author Victor Porof, blue_veek@yahoo.com
*/
public class GText extends GBaseElement {

 /**
  *
  */
 private static final long serialVersionUID = 1L;
 public GFont gFont;
 private String text = "";
 private ColorRGBA fill;
 private LinkedList<Quad> charQuads = new LinkedList<Quad>();
 private float size;
 private float kerneling;
 private float scale;
 private float spacing;

 public GText(GFont gFont, float size, float kerneling) {
  this.gFont = gFont;
  this.size = size;
  this.kerneling = kerneling;

  BlendState bs = renderer.createBlendState();
  bs.setBlendEnabled(true);
  bs.setSourceFunction(SourceFunction.SourceAlpha);
  bs.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
  bs.setEnabled(true);
  setRenderState(bs);

  setText(text);
 }

 private void construct() {
  scale = size / gFont.getMetricsHeights();
  spacing = 0;
  for (int i = 0; i < charQuads.size(); i++) {
   if (i < text.length()) {
    float positionX = spacing * scale
      + gFont.getMetricsWidths()[text.charAt(i)] * scale / 2f;
    float positionY = gFont.getTextDescent() * scale;
    charQuads.get(i).getLocalTranslation().setX(positionX);
    charQuads.get(i).getLocalTranslation().setY(positionY);

    float sizeX = size;
    float sizeY = size;
    charQuads.get(i).getLocalScale().setX(sizeX);
    charQuads.get(i).getLocalScale().setY(sizeY);

    if (fill != null) {
     charQuads.get(i).setSolidColor(fill);
    }
    attachChild(charQuads.get(i));

    charQuads.get(i).setRenderState(gFont.getChar(text.charAt(i)));
    spacing += gFont.getMetricsWidths()[text.charAt(i)] + kerneling;
   }
  }
  for (int i = charQuads.size(); i < text.length(); i++) {
   Quad quad = new Quad(String.valueOf(text.charAt(i)), 1f, 1f);

   float positionX = spacing * scale
     + gFont.getMetricsWidths()[text.charAt(i)] * scale / 2f;
   float positionY = gFont.getTextDescent() * scale;
   quad.getLocalTranslation().setX(positionX);
   quad.getLocalTranslation().setY(positionY);

   float sizeX = size;
   float sizeY = size;
   quad.getLocalScale().setX(sizeX);
   quad.getLocalScale().setY(sizeY);

   if (fill != null) {
    quad.setSolidColor(fill);
   }
   attachChild(quad);

   quad.setRenderState(gFont.getChar(text.charAt(i)));
   spacing += gFont.getMetricsWidths()[text.charAt(i)] + kerneling;

   charQuads.add(quad);
  }
  for (int j = text.length(); j < charQuads.size(); j++) {
   detachChild(charQuads.get(j));
  }
  for (int j = text.length(); j < charQuads.size(); j++) {
   charQuads.remove(j);
  }

  this.width = spacing * scale;
  this.height = size;
  updateRenderState();
 }

 public void setText(Object text) {
  this.text = String.valueOf(text);
  construct();
 }

 public String getText() {
  return text;
 }

 public void setFill(ColorRGBA fill) {
  this.fill = fill;
  construct();
 }

 public ColorRGBA getFill() {
  return fill;
 }
}

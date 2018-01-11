// Copyright 2009-2018 Information & Computational Sciences, JHI. All rights
// reserved. Use is subject to the accompanying licence terms.

package tablet.gui.ribbon;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.gui.*;

import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;

import scri.commons.gui.*;

public class BandAdjust extends JRibbonBand implements ChangeListener
{
	private WinMain winMain;

	public static JRibbonComponent zoomSliderComponent;
	private static JSlider zoomSlider;
	public static JRibbonComponent variantSliderComponent;
	private JSlider variantSlider;

	private static final int ZOOM_MIN = 1;
	private static final int ZOOM_DEF = 7;
	private static final int ZOOM_MAX = 25;

	BandAdjust(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.BandAdjust.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		// Determine the initial zoom level for the reads canvas
		int zoom = Prefs.visReadsZoomLevel;
		if (zoom < ZOOM_MIN || zoom > ZOOM_MAX)
			zoom = ZOOM_DEF;

		zoomSlider = new JSlider(ZOOM_MIN, ZOOM_MAX, zoom);
		zoomSlider.addChangeListener(this);

		zoomSliderComponent = new JRibbonComponent(
			RibbonController.getIcon("ZOOM16", 16),
			RB.getString("gui.ribbon.BandAdjust.zoom"),
			zoomSlider);
		zoomSliderComponent.setRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.BandAdjust.zoom.tooltip",
			Tablet.winKey, Tablet.winKey),
			RB.getString("gui.ribbon.BandAdjust.zoom.richtip")));
		zoomSliderComponent.setKeyTip("Z");


		// Determine the initial zoom level for the variant highlighting
		int vLevel = Prefs.visVariantAlpha;
		if (vLevel < 0 || vLevel > 200)
			vLevel = 0;

		variantSlider = new JSlider(0, 200, vLevel);
		variantSlider.addChangeListener(this);

		variantSliderComponent = new JRibbonComponent(
			RibbonController.getIcon("VARIANT16", 16),
			RB.getString("gui.ribbon.BandAdjust.variants"),
			variantSlider);
		variantSliderComponent.setRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.BandAdjust.variants.tooltip"),
			RB.getString("gui.ribbon.BandAdjust.variants.richtip")));
		variantSliderComponent.setKeyTip("V");

		addRibbonComponent(zoomSliderComponent);
		addRibbonComponent(variantSliderComponent);
	}

	public static void zoomIn(int amount)
	{
		if (zoomSlider.getValue()+amount < ZOOM_MAX)
			zoomSlider.setValue(zoomSlider.getValue()+amount);
		else
			zoomSlider.setValue(ZOOM_MAX);
	}

	public static void zoomOut(int amount)
	{
		if (zoomSlider.getValue()-amount > ZOOM_MIN)
			zoomSlider.setValue(zoomSlider.getValue()-amount);
		else
			zoomSlider.setValue(ZOOM_MIN);
	}

	static void zoomReset()
	{
		zoomSlider.setValue(ZOOM_DEF);
	}

	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == zoomSlider)
		{
			Prefs.visReadsZoomLevel = zoomSlider.getValue();

			winMain.getAssemblyPanel().getController().doZoom();
			winMain.getAssemblyPanel().repaint();
		}

		else if (e.getSource() == variantSlider)
		{
			Prefs.visVariantAlpha = variantSlider.getValue();

			winMain.getAssemblyPanel().forceRedraw();
			winMain.getAssemblyPanel().repaint();
		}
	}

	public static void setZoom(int zoom)
	{
		Prefs.visReadsZoomLevel = zoom;
		zoomSlider.setValue(zoom);
	}
}
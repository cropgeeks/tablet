package tablet.gui.ribbon;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.gui.*;

import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;

import scri.commons.gui.*;

public class HomeAdjustBand extends JRibbonBand implements ChangeListener
{
	private WinMain winMain;

	public static JRibbonComponent zoomSliderComponent;
	private JSlider zoomSlider;
	public static JRibbonComponent variantSliderComponent;
	private JSlider variantSlider;

	private static final int ZOOM_MIN = 1;
	private static final int ZOOM_DEF = 7;
	private static final int ZOOM_MAX = 25;

	HomeAdjustBand(WinMain winMain)
	{
		super(RB.getString("gui.ribbon.HomeAdjustBand.title"),
			new EmptyResizableIcon(32));

		this.winMain = winMain;

		// Determine the initial zoom level for the reads canvas
		int zoom = Prefs.visReadsCanvasZoom;
		if (zoom < ZOOM_MIN || zoom > ZOOM_MAX)
			zoom = ZOOM_DEF;

		zoomSlider = new JSlider(ZOOM_MIN, ZOOM_MAX, zoom);
		zoomSlider.addChangeListener(this);

		zoomSliderComponent = new JRibbonComponent(
			RibbonController.getIcon("ZOOM16", 16),
			RB.getString("gui.ribbon.HomeAdjustBand.zoom"),
			zoomSlider);
		zoomSliderComponent.setRichTooltip(new RichTooltip(
			RB.format("gui.ribbon.HomeAdjustBand.zoom.tooltip",
			Tablet.winKey, Tablet.winKey),
			RB.getString("gui.ribbon.HomeAdjustBand.zoom.richtip")));
		zoomSliderComponent.setKeyTip("Z");


		// Determine the initial zoom level for the variant highlighting
		int vLevel = Prefs.visVariantAlpha;
		if (vLevel < 0 || vLevel > 200)
			vLevel = 0;

		variantSlider = new JSlider(0, 200, vLevel);
		variantSlider.addChangeListener(this);

		variantSliderComponent = new JRibbonComponent(
			RibbonController.getIcon("VARIANT16", 16),
			RB.getString("gui.ribbon.HomeAdjustBand.variants"),
			variantSlider);
		variantSliderComponent.setRichTooltip(new RichTooltip(
			RB.getString("gui.ribbon.HomeAdjustBand.variants.tooltip"),
			RB.getString("gui.ribbon.HomeAdjustBand.variants.richtip")));
		variantSliderComponent.setKeyTip("V");

		addRibbonComponent(zoomSliderComponent);
		addRibbonComponent(variantSliderComponent);

		createKeyboardShortcuts();
	}

	private void createKeyboardShortcuts()
	{
		Action zoomIn = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (zoomSlider.getValue() < ZOOM_MAX)
					zoomSlider.setValue(zoomSlider.getValue()+1);
			}
		};

		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, Tablet.menuShortcut);
		zoomSlider.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "zoomIn");
		zoomSlider.getActionMap().put("zoomIn", zoomIn);

		Action zoomOut = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (zoomSlider.getValue() > ZOOM_MIN)
					zoomSlider.setValue(zoomSlider.getValue()-1);
			}
		};

		ks = KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Tablet.menuShortcut);
		zoomSlider.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "zoomOut");
		zoomSlider.getActionMap().put("zoomOut", zoomOut);

		Action zoomReset = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				zoomSlider.setValue(ZOOM_DEF);
			}
		};

		ks = KeyStroke.getKeyStroke(KeyEvent.VK_0, Tablet.menuShortcut);
		zoomSlider.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, "zoomReset");
		zoomSlider.getActionMap().put("zoomReset", zoomReset);
	}

	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource() == zoomSlider)
		{
			Prefs.visReadsCanvasZoom = zoomSlider.getValue();
			winMain.getAssemblyPanel().computePanelSizes();
		}

		else if (e.getSource() == variantSlider)
		{
			Prefs.visVariantAlpha = variantSlider.getValue();

			winMain.getAssemblyPanel().computePanelSizes();
			winMain.getAssemblyPanel().repaint();
		}
	}
}
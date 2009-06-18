package tablet.gui.ribbon;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import tablet.gui.*;
import tablet.gui.viewer.*;

import org.jvnet.flamingo.common.model.*;
import org.jvnet.flamingo.common.*;
import org.jvnet.flamingo.common.icon.*;
import org.jvnet.flamingo.ribbon.*;

public class HomeVisualizationBand extends JRibbonBand
	implements ActionListener, ChangeListener
{
	private WinMain winMain;

	public static JRibbonComponent zoomSliderComponent;
	private JSlider zoomSlider;
	public static JRibbonComponent variantSliderComponent;
	private JSlider variantSlider;

	private JCommandToggleButton bInfoPane;

	HomeVisualizationBand(WinMain winMain)
	{
		super("Reads", new EmptyResizableIcon(32));

		this.winMain = winMain;

		// Determine the initial zoom level for the reads canvas
		int zoom = Prefs.visReadsCanvasZoom;
		if (zoom < 1 || zoom > 25) zoom = 7;

		zoomSlider = new JSlider(1, 25, zoom);
		zoomSlider.addChangeListener(this);

		zoomSliderComponent = new JRibbonComponent(RibbonController.getIcon("ZOOM16", 16), "Zoom:", zoomSlider);
		addRibbonComponent(zoomSliderComponent);


		// Determine the initial zoom level for the variant highlighting
		int vLevel = Prefs.visVariantAlpha;
		if (vLevel < 0 || vLevel > 200)
			vLevel = 0;

		variantSlider = new JSlider(0, 200, vLevel);
		variantSlider.addChangeListener(this);

		variantSliderComponent = new JRibbonComponent(RibbonController.getIcon("VARIANT16", 16), "Variant highlighting:", variantSlider);
		addRibbonComponent(variantSliderComponent);

		startGroup();

		bInfoPane = new JCommandToggleButton("Info", RibbonController.getIcon("INFOPANE16", 16));
//		Actions.homeAssembliesOpen32 = new ActionRepeatableButtonModel(bOpen32);
//		Actions.homeAssembliesOpen32.addActionListener(this);
//		bOpen32.setActionModel(Actions.homeAssembliesOpen32);
//		bOpen32.setActionKeyTip("O");

//		addCommandButton(bInfoPane, RibbonElementPriority.MEDIUM);
	}

	public void actionPerformed(ActionEvent e)
	{
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
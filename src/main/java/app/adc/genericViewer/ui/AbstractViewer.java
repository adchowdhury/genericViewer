package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.net.URI;

import javax.swing.JPanel;

public  abstract class  AbstractViewer extends JPanel {

	public AbstractViewer(URI source) {
		
		setLayout(new BorderLayout());
	}
}
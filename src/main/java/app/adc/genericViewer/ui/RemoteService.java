package app.adc.genericViewer.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JPanel;

public interface RemoteService {
	
	List<RemoteService> services = new ArrayList<RemoteService>();

	JPanel getConfigUI(Properties a_savedProperies);
	
	Properties getProperties();
	
	String getDisplayText();
	
	String toString();
	
	AbstractViewer getViewer();
}
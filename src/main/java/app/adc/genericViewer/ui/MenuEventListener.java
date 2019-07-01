package app.adc.genericViewer.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

public class MenuEventListener implements ActionListener{
	
	private static MainContainer parentComponent = null;
	
	public MenuEventListener(MainContainer a_parentComponent) {
		parentComponent = a_parentComponent;
	}

	public void actionPerformed(ActionEvent a_actionEvent) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.showOpenDialog((Component)parentComponent);
		
		File selectedFile = fileChooser.getSelectedFile();
		//selectedFile.
		openResource(selectedFile.toURI());
		
	}
	
	public static void openResource(URI source) {
		AbstractViewer viewer = ViewerFactory.getViewer(source);
		if(viewer == null) {
			JOptionPane.showMessageDialog(null, "File type not supported yet");
			return;
		}
		
		JInternalFrame frm = new JInternalFrame(new File(source).getName());
		frm.add(viewer);
		frm.setLocation(0, 0);
		frm.setSize(800, 600);
		frm.setClosable(true);
		frm.setResizable(true);
		frm.setIconifiable(true);
		frm.setMaximizable(true);
		
		parentComponent.getDesktopPane().add(frm);
		frm.setVisible(true);
		
		try {
	        frm.setSelected(true);
	    } catch (java.beans.PropertyVetoException e) {}
	}

}

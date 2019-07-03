package app.adc.genericViewer.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public final class MenuEventListener {
	
	
	
	public static void openClicked(ActionEvent a_actionEvent) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.showOpenDialog((Component)MainContainer.getMainContainer());
		
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
		frm.setName(frm.getTitle());
		frm.setToolTipText(frm.getTitle());
		
		frm.add(viewer);
		frm.setLocation(0, 0);
		frm.setSize(800, 600);
		frm.setClosable(true);
		frm.setResizable(true);
		frm.setIconifiable(true);
		frm.setMaximizable(true);
		
		MainContainer.getMainContainer().getDesktopPane().add(frm);
		frm.setVisible(true);
		MainContainer.getMainContainer().addInternalFrame(frm);
		
		frm.addInternalFrameListener(new InternalFrameListener() {
			
			public void internalFrameOpened(InternalFrameEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void internalFrameIconified(InternalFrameEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void internalFrameDeiconified(InternalFrameEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void internalFrameDeactivated(InternalFrameEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void internalFrameClosing(InternalFrameEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			public void internalFrameClosed(InternalFrameEvent a_ifEvent) {
				MainContainer.getMainContainer().removeInternalFrame(a_ifEvent.getInternalFrame());				
			}
			
			public void internalFrameActivated(InternalFrameEvent a_ifEvent) {
				MainContainer.getMainContainer().internalFrameActivated(a_ifEvent.getInternalFrame());				
			}
		});
		
		try {
	        frm.setSelected(true);
	    } catch (java.beans.PropertyVetoException a_excp) {
	    	a_excp.printStackTrace();
	    }
	}

}

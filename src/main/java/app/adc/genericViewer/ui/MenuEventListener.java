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
		int chooserReturn = fileChooser.showOpenDialog((Component)MainContainer.getMainContainer());
		
		if(chooserReturn == JFileChooser.CANCEL_OPTION) {
			return;
		}
		
		File selectedFile = fileChooser.getSelectedFile();
		//selectedFile.
		if(selectedFile == null) {
			return;
		}
		
		AbstractViewer viewer = ViewerFactory.getViewer(selectedFile.toURI());
		if(viewer == null) {
			JOptionPane.showMessageDialog(null, "File type not supported yet");
			return;
		}
		openResource(viewer, selectedFile.getName());
	}
	
	public static void openResource(AbstractViewer viewer, String displayName) {
		if(viewer == null || displayName == null) {
			return;
		}
		
		int WIDTH = (MainContainer.getMainContainer().getDesktopPane().getWidth() / 100) * 85, 
				HEIGHT = (MainContainer.getMainContainer().getDesktopPane().getHeight() / 100) * 85;
		
		JInternalFrame frm = new JInternalFrame(displayName);
		frm.setName(frm.getTitle());
		frm.setToolTipText(frm.getTitle());
		
		frm.add(viewer);
		frm.setLocation((MainContainer.getMainContainer().getDesktopPane().getWidth() / 2) - (WIDTH / 2), 
				(MainContainer.getMainContainer().getDesktopPane().getHeight() / 2) - (HEIGHT / 2));
		frm.setSize(WIDTH, HEIGHT);
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

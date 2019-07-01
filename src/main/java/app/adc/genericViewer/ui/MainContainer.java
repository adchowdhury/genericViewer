package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class MainContainer extends JPanel {

	private JDesktopPane	desktopPane	= null;
	private JMenuBar		menuBar		= null;
	private JMenu			fileMenu	= null;
	private JMenuItem		openMenItem	= null;

	public MainContainer() {
		init();
	}
	
	public JDesktopPane getDesktopPane() {
		return desktopPane;
	}

	private void init() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		menuBar.add(fileMenu);
		openMenItem = new JMenuItem("Open");
		openMenItem.addActionListener(new MenuEventListener(this));
		fileMenu.add(openMenItem);
		
		setLayout(new BorderLayout());
		desktopPane = new JDesktopPane();
		add(desktopPane, BorderLayout.CENTER);
		add(menuBar, BorderLayout.NORTH);
		desktopPane.setDropTarget(new DropTarget() {

			@Override
			public synchronized void drop(DropTargetDropEvent a_dtde) {
				// TODO Auto-generated method stub
				System.out.println(a_dtde.isLocalTransfer());
				a_dtde.acceptDrop(DnDConstants.ACTION_COPY);
				//a_dtde.dropComplete(true);
				Transferable t = a_dtde.getTransferable();
		        DataFlavor[] dataFlavors = t.getTransferDataFlavors();
				for(DataFlavor df : dataFlavors) {
					try {
						File[] filesArray = (File[]) ((List<File>) t.getTransferData(df)).toArray();
						for(File f : filesArray) {
							System.out.println(f.getAbsolutePath());
							MenuEventListener.openResource(f.toURI());
						}
					} catch (Throwable a_th) {
						// TODO: handle exception
					}
				}
				//System.out.println(a_dtde.getTransferable());
				// super.drop(a_dtde);
				a_dtde.dropComplete(true);
			}
		});
	}
}
package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainContainer extends JPanel {

	private JDesktopPane	desktopPane			= null;
	private JMenuBar		menuBar				= null;
	private JMenu			fileMenu			= null;
	private JMenuItem		openMenItem			= null;
	private JMenu			lafMenu				= null;

	private ButtonGroup		lafMenuGroup		= new ButtonGroup();

	private static String	currentLookAndFeel	= null;

	public MainContainer() {
		init();
	}

	public JDesktopPane getDesktopPane() {
		return desktopPane;
	}

	private void init() {
		menuBar		= new JMenuBar();
		fileMenu	= new JMenu("File");
		fileMenu.setMnemonic('F');

		openMenItem = new JMenuItem("Open");
		openMenItem.addActionListener(new MenuEventListener(this));
		fileMenu.add(openMenItem);
		lafMenu = new JMenu("Look & Feel");

		menuBar.add(fileMenu);
		menuBar.add(lafMenu);

		JMenuItem					mi;

		UIManager.LookAndFeelInfo[]	lafInfo	= UIManager.getInstalledLookAndFeels();
		currentLookAndFeel = UIManager.getLookAndFeel().getClass().getName();

		for (int counter = 0; counter < lafInfo.length; counter++) {
			String			className	= lafInfo[counter].getClassName();
			final boolean	selected	= className.equals(currentLookAndFeel);
			mi = createLafMenuItem(lafMenu, lafInfo[counter]);
			mi.setSelected(selected);
		}
		

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
				// a_dtde.dropComplete(true);
				Transferable	t			= a_dtde.getTransferable();
				DataFlavor[]	dataFlavors	= t.getTransferDataFlavors();
				for (DataFlavor df : dataFlavors) {
					try {
						File[] filesArray = (File[]) ((List<File>) t.getTransferData(df)).toArray();
						for (File f : filesArray) {
							System.out.println(f.getAbsolutePath());
							MenuEventListener.openResource(f.toURI());
						}
					} catch (Throwable a_th) {
						// TODO: handle exception
					}
				}
				// System.out.println(a_dtde.getTransferable());
				// super.drop(a_dtde);
				a_dtde.dropComplete(true);
			}
		});
	}

	public JMenuItem createLafMenuItem(JMenu menu, UIManager.LookAndFeelInfo lafInfo) {
		JMenuItem mi = (JRadioButtonMenuItem) menu.add(new JRadioButtonMenuItem(lafInfo.getName()));
		lafMenuGroup.add(mi);
		mi.addActionListener(new ChangeLookAndFeelAction(this, lafInfo.getClassName()));

		mi.setEnabled(isAvailableLookAndFeel(lafInfo.getClassName()));

		return mi;
	}

	class ChangeLookAndFeelAction extends AbstractAction {
		MainContainer	swingset;
		String			laf;

		protected ChangeLookAndFeelAction(MainContainer swingset, String laf) {
			super("ChangeTheme");
			this.swingset	= swingset;
			this.laf		= laf;
		}

		public void actionPerformed(ActionEvent e) {
			swingset.setLookAndFeel(laf);
		}
	}

	public void setLookAndFeel(String laf) {
		if (!currentLookAndFeel.equals(laf)) {
			currentLookAndFeel = laf;
			/*
			 * The recommended way of synchronizing state between multiple controls that
			 * represent the same command is to use Actions. The code below is a workaround
			 * and will be replaced in future version of SwingSet2 demo.
			 */
			UIManager.LookAndFeelInfo[]	lafInfo	= UIManager.getInstalledLookAndFeels();

			String						lafName	= null;
			for (int counter = 0; counter < lafInfo.length; counter++) {
				String className = lafInfo[counter].getClassName();
				if (className.equals(laf)) {
					lafName = lafInfo[counter].getName();
					break;
				}
			}

			// themesMenu.setEnabled(laf.equals("javax.swing.plaf.metal.MetalLookAndFeel"));
			updateLookAndFeel();
			for (int i = 0; i < lafMenu.getItemCount(); i++) {
				JMenuItem item = lafMenu.getItem(i);
				item.setSelected(item.getText().equals(lafName));
			}
		}
	}

	public void updateLookAndFeel() {
		try {
			UIManager.setLookAndFeel(currentLookAndFeel);
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception ex) {
			System.out.println("Failed loading L&F: " + currentLookAndFeel);
			System.out.println(ex);
		}

	}

	protected boolean isAvailableLookAndFeel(String laf) {
		try {
			Class		lnfClass	= Class.forName(laf);
			LookAndFeel	newLAF		= (LookAndFeel) (lnfClass.newInstance());
			return newLAF.isSupportedLookAndFeel();
		} catch (Exception e) { // If ANYTHING weird happens, return false
			return false;
		}
	}
}
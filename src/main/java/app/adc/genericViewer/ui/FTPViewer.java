package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.net.URI;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

public class FTPViewer extends AbstractViewer {

	private Properties					props			= null;
	private JTextField					txtLocalPath	= null;
	private JTextField					txtRemotePath	= null;
	private JList<String>				lstLocalFolder	= null;
	private JList<FTPFile>				lstRemoteFolder	= null;
	private JTextArea					txtLog			= null;
	private JToolBar					toolBar			= null;
	private DefaultListModel<String>	listModelLocal	= null;
	private DefaultListModel<FTPFile>	listModelRemote	= null;

	public FTPViewer(URI a_source, Properties a_props) {
		super(a_source);
		props = a_props;
		init();
		loadLocalFolder(System.getProperty("user.home"), true);
		loadRemoteFolder();
	}

	private void init() {
		
		listModelLocal = new DefaultListModel<String>();
		listModelRemote = new DefaultListModel<FTPFile>();
		
		JPanel localPanel = new JPanel(new BorderLayout());
		localPanel.add(txtLocalPath = new JTextField(), BorderLayout.NORTH);
		txtLocalPath.setEditable(false);

		localPanel.add(new JScrollPane(lstLocalFolder = new JList<String>(listModelLocal)), BorderLayout.CENTER);
		lstLocalFolder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstLocalFolder.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent a_event) {
				if(a_event.getValueIsAdjusting()) {
					return;
				}
				
				if(lstLocalFolder.getSelectedIndex() == 0 && lstLocalFolder.getSelectedValue().equalsIgnoreCase("..")) {
					File f = new File(txtLocalPath.getText());
					loadLocalFolder(f.getParent(), false);
				}else {
					File f = new File(txtLocalPath.getText() + File.separatorChar + lstLocalFolder.getSelectedValue());
					if(f.isDirectory()) {
						loadLocalFolder(f.getAbsolutePath(), false);
					}
				}
			}
		});

		JPanel remotePanel = new JPanel(new BorderLayout());
		remotePanel.add(txtRemotePath = new JTextField(), BorderLayout.NORTH);
		txtRemotePath.setEditable(false);

		remotePanel.add(new JScrollPane(lstRemoteFolder = new JList<FTPFile>(listModelRemote)), BorderLayout.CENTER);
		JSplitPane localRemote = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, localPanel, remotePanel);
		lstRemoteFolder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		lstLocalFolder.setCellRenderer(new FileRenderer());

		add(localRemote, BorderLayout.CENTER);

	}

	private void loadLocalFolder(String a_path, boolean isRoot) {
		if(a_path == null) {
			return;
		}
		
		File parentFolder = new File(a_path);
		if(parentFolder.exists() == false || parentFolder.isDirectory() == false) {
			return;
		}
		txtLocalPath.setText(a_path);
		listModelLocal.clear();
		if(isRoot == false) {
			listModelLocal.addElement("..");
		}
		File[] files = parentFolder.listFiles();
		for(File f : files) {
			if(f.isHidden()) {
				continue;
			}
			listModelLocal.addElement(f.getName());
		}
	}
	
	private void loadRemoteFolder() {
		FTPSClient ftp  = new FTPSClient();
		try {
			ftp.connect(props.getProperty(IConstant.FTPConstants.FTPHost.getText()), Integer.parseInt(props.getProperty(IConstant.FTPConstants.FTPPort.getText())));
			
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new Exception("Exception in connecting to FTP Server");
			}
			ftp.login(props.getProperty(IConstant.FTPConstants.Username.getText()), 
					props.getProperty(IConstant.FTPConstants.Password.getText()));
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
			FTPFile files[] =  ftp.listDirectories();
			listModelRemote.clear();
			for(FTPFile f : files) {
				listModelRemote.addElement(f);
			}
		} catch (Throwable a_th) {
			a_th.printStackTrace();
		}
	}
	
	class FileRenderer extends JLabel implements ListCellRenderer<String> {

		public Component getListCellRendererComponent(JList<? extends String> a_list, String a_value, int a_index,
				boolean a_isSelected, boolean a_cellHasFocus) {
			setOpaque(true); 
			setText(a_value);
			
			if(a_index == 0 && a_value.equalsIgnoreCase("..")) {
				setIcon(IConstant.FolderIcon);
			}else {
				File f = new File(txtLocalPath.getText() + File.separatorChar + a_value);
				if(f.isDirectory()) {
					setIcon(IConstant.FolderIcon);
				}else {
					setIcon(IConstant.FileIcon);
				}
			}
			
			if (a_isSelected || a_cellHasFocus) {
				setBackground(a_list.getSelectionBackground());
	            setForeground(a_list.getSelectionForeground()); 
	        } else {
	            setBackground(a_list.getBackground());
	            setForeground(a_list.getForeground());
	        } 
			return this;
		}

	}
}
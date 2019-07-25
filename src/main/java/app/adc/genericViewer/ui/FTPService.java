package app.adc.genericViewer.ui;

import java.awt.GridLayout;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class FTPService extends JPanel implements RemoteService {

	private JTextField		txtHostname				= null;
	private JTextField		txtPortNumber			= null;
	private JTextField		txtUserName				= null;
	private JPasswordField	txtPassword				= null;
	private JTextField		txtRemoteInitialFolder	= null;
	
	public FTPService() {
		init();
	}
	
	private void init() {
		setLayout(new GridLayout(5, 2));
		
		txtHostname = new JTextField("192.168.102.58");
		txtHostname.setToolTipText("Provide host name here");
		txtHostname.setColumns(25);
		
		txtPortNumber = new JTextField("22");
		txtPortNumber.setToolTipText("Provide port number here");
		txtPortNumber.setColumns(25);
		
		txtUserName = new JTextField("root");
		txtUserName.setToolTipText("Provide username here");
		txtUserName.setColumns(25);
		
		txtPassword = new JPasswordField("M@st3r@4321");
		txtPassword.setToolTipText("Provide password here");
		txtPassword.setColumns(25);
		
		txtRemoteInitialFolder = new JTextField();
		txtRemoteInitialFolder.setToolTipText("Provide initial folder here");
		txtRemoteInitialFolder.setColumns(25);

		add(new JLabel("Host name"));
		add(txtHostname);
		
		add(new JLabel("Port number"));
		add(txtPortNumber);
		
		add(new JLabel("Username"));
		add(txtUserName);
		
		add(new JLabel("Password"));
		add(txtPassword);
		
		add(new JLabel("Inital fodler"));
		add(txtRemoteInitialFolder);
	}

	public JPanel getConfigUI(Properties a_savedProperies) {
		return this;
	}

	public Properties getProperties() {
		Properties props = new Properties();
		
		props.put(IConstant.FTPConstants.FTPHost.getText(), txtHostname.getText());
		props.put(IConstant.FTPConstants.FTPPort.getText(), txtPortNumber.getText());
		props.put(IConstant.FTPConstants.Username.getText(), txtUserName.getText());
		props.put(IConstant.FTPConstants.Password.getText(), txtPassword.getText());

		return props;
	}

	public String getDisplayText() {
		return "FTP";
	}

	public AbstractViewer getViewer() {
		return new FTPViewer(null, getProperties());
	}

	public String getTitleText() {
		return "FTP : " + txtHostname.getText();
	}
	
	@Override
	public String toString() {
		return getDisplayText();
	}

}

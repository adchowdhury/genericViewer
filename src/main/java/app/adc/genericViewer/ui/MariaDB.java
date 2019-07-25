package app.adc.genericViewer.ui;

import static app.adc.genericViewer.ui.IConstant.MariaDBConstants;

import java.awt.GridLayout;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import app.adc.genericViewer.ui.IConstant.MariaDBConstants;

public class MariaDB extends JPanel implements RemoteService {

	private JTextField		txtHostname		= null;
	private JTextField		txtPortNumber	= null;
	private JTextField		txtUserName		= null;
	private JPasswordField	txtPassword		= null;

	public MariaDB() {
		init();
	}

	private void init() {
		setLayout(new GridLayout(4, 2));
		
		txtHostname = new JTextField("localhost");
		txtHostname.setToolTipText("Provide host name here");
		txtHostname.setColumns(25);
		
		txtPortNumber = new JTextField("3306");
		txtPortNumber.setToolTipText("Provide port number here");
		txtPortNumber.setColumns(25);
		
		txtUserName = new JTextField("root");
		txtUserName.setToolTipText("Provide username here");
		txtUserName.setColumns(25);
		
		txtPassword = new JPasswordField();
		txtPassword.setToolTipText("Provide password here");
		txtPassword.setColumns(25);
		
		add(new JLabel("Host name"));
		add(txtHostname);
		
		add(new JLabel("Port number"));
		add(txtPortNumber);
		
		add(new JLabel("Username"));
		add(txtUserName);
		
		add(new JLabel("Password"));
		add(txtPassword);
	}

	public JPanel getConfigUI(Properties a_savedProperies) {
		return this;
	}

	public Properties getProperties() {
		Properties props = new Properties();
		
		if(txtHostname.getText().trim().length() < 1) {
			JOptionPane.showMessageDialog(null, "Please enter hostname");
			txtHostname.transferFocus();
			throw new RuntimeException("Hostname not provided");
		}
		
		if(txtPortNumber.getText().trim().length() < 1) {
			JOptionPane.showMessageDialog(null, "Please enter port");
			txtPortNumber.transferFocus();
			throw new RuntimeException("Port not provided");
		}
		
		if(txtUserName.getText().trim().length() < 1) {
			JOptionPane.showMessageDialog(null, "Please enter username");
			txtUserName.transferFocus();
			throw new RuntimeException("Username not provided");
		}
		
		props.put(MariaDBConstants.DBHost.getText(), txtHostname.getText());
		props.put(MariaDBConstants.DBPort.getText(), txtPortNumber.getText());
		props.put(MariaDBConstants.Username.getText(), txtUserName.getText());
		if(txtPassword.getText() != null && txtPassword.getText().trim().length() >= 1) {
			props.put(MariaDBConstants.Password.getText(), txtPassword.getText());
		}else {
			props.remove(MariaDBConstants.Password.getText());
		}
		
		return props;
	}

	public String getDisplayText() {
		return "Maria DB";
	}

	@Override
	public String toString() {
		return getDisplayText();
	}

	public AbstractViewer getViewer() {
		return new SQLViewer(null, getProperties());
	}

	public String getTitleText() {
		return txtHostname.getText();
	}

}
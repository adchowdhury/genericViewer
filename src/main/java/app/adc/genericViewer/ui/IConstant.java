package app.adc.genericViewer.ui;

import javax.swing.ImageIcon;

public interface IConstant {
	ImageIcon FolderIcon = new ImageIcon(IConstant.class.getResource("/app/adc/genericViewer/icons/folder.png"));
	ImageIcon FileIcon = new ImageIcon(IConstant.class.getResource("/app/adc/genericViewer/icons/file.png"));
	
	enum MariaDBConstants{
		DBHost("db_host"),
		DBPort("db_port"),
		Username("db_username"),
		Password("db_password");
		
		private String keyText = null;
		MariaDBConstants(String a_keyText){
			keyText = a_keyText;
		}
		
		
		String getText() {
			return keyText;
		}
	}
	
	enum FTPConstants{
		FTPHost("ftp_host"),
		FTPPort("ftp_port"),
		Username("ftp_username"),
		Password("ftp_password");
		
		private String keyText = null;
		FTPConstants(String a_keyText){
			keyText = a_keyText;
		}
		
		
		String getText() {
			return keyText;
		}
	}
	
	enum SVNConstants{
		SVNHost("svn_host"),
		Username("svn_username"),
		Password("svn_password");
		
		private String keyText = null;
		SVNConstants(String a_keyText){
			keyText = a_keyText;
		}
		
		
		String getText() {
			return keyText;
		}
	}
}
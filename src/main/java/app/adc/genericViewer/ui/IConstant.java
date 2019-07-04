package app.adc.genericViewer.ui;

public interface IConstant {
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
}
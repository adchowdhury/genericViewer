package app.adc.genericViewer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

//https://stackoverflow.com/questions/5158961/unzip-into-treemap-in-java

public class ZipViewer extends AbstractViewer {

	private URI					sourceFile	= null;
	private JTable				tblZip		= null;
	private List<ZipNode>		filesList	= null;
	private ZipEntryTableModel	tblModel	= null;
	private SimpleDateFormat	dateFormat	= null;

	public ZipViewer(URI source) {
		super(source);
		sourceFile = source;

		init();
		try {
			loadFile();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void init() {
		filesList	= new ArrayList<ZipNode>();
		tblZip		= new JTable(tblModel = new ZipEntryTableModel());
		tblZip.getColumnModel().getColumn(0).setCellRenderer(new ImageRenderer());
		tblZip.getColumnModel().getColumn(2).setCellRenderer(new NumberTableCellRenderer());
		tblZip.getColumnModel().getColumn(3).setCellRenderer(new NumberTableCellRenderer());

		tblZip.getColumnModel().getColumn(0).setPreferredWidth(24);
		tblZip.getColumnModel().getColumn(0).setMinWidth(24);
		tblZip.getColumnModel().getColumn(0).setMaxWidth(24);
		tblZip.getColumnModel().getColumn(0).setResizable(false);

		tblZip.getColumnModel().getColumn(1).setPreferredWidth(175);
		tblZip.setIntercellSpacing(new Dimension(5, 5));
		
		((DefaultTableCellRenderer)tblZip.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
		tblZip.setAutoCreateRowSorter(true);
		tblZip.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tblZip.setColumnSelectionAllowed(false);
		tblZip.setRowHeight(24);
		
		add(new JScrollPane(tblZip), BorderLayout.CENTER);
		
		dateFormat	= new SimpleDateFormat("dd-MMM-yyyy hh:mm");
		
		tblZip.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent mouseEvent) {
		    	JTable table =(JTable) mouseEvent.getSource();
		    	
		    	if(mouseEvent.getClickCount() < 2 || table.getSelectedRow() < 0) {
		    		return;
		    	}
		    	
		        
		        Point point = mouseEvent.getPoint();
		        int row = table.rowAtPoint(point);
		        
		        if(filesList.get(row).isDirectory()) {
		        	if(level == 0) {
		        		level++;
		        	}else {
		        		if(row == 0) {
		        			level--;
		        		}else {
		        			level++;
		        		}
		        	}
		        	loadZipNode(filesList.get(row));
		        }else {
		        	File f = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + UUID.randomUUID().toString() + "_" + getName(filesList.get(row).getEntry()));
		        	
		        	try {
		        		ZipFile	zipFile	= new ZipFile(new File(sourceFile));
			        	InputStream inputStream = zipFile.getInputStream(filesList.get(row).getEntry());
			        	
			        	FileOutputStream outputStream = new FileOutputStream(f);
			        	int data = inputStream.read();
	                    while(data != -1){
	                        outputStream.write(data);
	                        data = inputStream.read();
	                    }
	                    outputStream.flush();
	                    outputStream.close();
	                    zipFile.close();
					} catch (Throwable a_th) {
						a_th.printStackTrace();
					}
		        	
                    
		        	MenuEventListener.openResource(ViewerFactory.getViewer(f.toURI()), f.getName());
		        }
		    }
		});
	}
	int level = 0;
	private void loadZipNode(ZipNode zNode) {
		
		if(zNode == null) {
			return;
		}
		
		filesList.clear();
		
		if(level > 0) {
			filesList.add(zNode.getParent());
		}
		
		
		for(Entry<String, ZipNode> entry : zNode.getChildren().entrySet()) {
			filesList.add(entry.getValue());
		}
		
		tblModel.fireTableDataChanged();
	}

	private void loadFile() throws Throwable {
		ZipFile	zipFile	= new ZipFile(new File(sourceFile));

		ZipNode zNode = ZipNode.fromZipFile(zipFile);
		loadZipNode(zNode);
		System.out.println("ZipViewer.loadFile()");
		tblModel.fireTableDataChanged();
	}

	public class NumberTableCellRenderer extends DefaultTableCellRenderer {

		public NumberTableCellRenderer() {
			setHorizontalAlignment(JLabel.RIGHT);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			if (value instanceof Number) {
				value = NumberFormat.getNumberInstance().format(value);
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}

	}

	class ImageRenderer extends DefaultTableCellRenderer {
		JLabel lbl = new JLabel();

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			lbl.setIcon((ImageIcon) value);
			return lbl;
		}
	}
	
	public static String getName(ZipEntry zipEntry) {
		String name = zipEntry.getName();
		//return name;
		
		if(zipEntry.isDirectory()) {
			return name.substring(name.substring(0, name.lastIndexOf('/')).lastIndexOf('/') + 1, name.length() - 1);
		}else {
			return name.substring(name.lastIndexOf('/') + 1);
		}
	}

	class ZipEntryTableModel extends DefaultTableModel {
		private String[] columns = new String[] { "", "Name", "Size", "Compressed Size", "Modified", "Comment" };

		@Override
		public int getColumnCount() {
			return columns.length;
		}

		@Override
		public String getColumnName(int column) {
			return columns[column];
		}

		@Override
		public int getRowCount() {
			return filesList.size();
		}
		
		@Override
	    public boolean isCellEditable(int row, int column) {
	       //all cells false
	       return false;
	    }

		@Override
		public Object getValueAt(int row, int column) {

			switch (column) {
				case 0:
					if (filesList.get(row).isDirectory()) {
						return IConstant.FolderIcon;
					} else {
						return IConstant.FileIcon;
					}
					// return filesList.get(row).isDirectory();
				case 1:
					if(row == 0 && level > 0) {
						return "..";
					}
					return getName(filesList.get(row).getEntry());
				case 2:
					if (filesList.get(row).isDirectory()) {
						return "";
					} else {
						return filesList.get(row).getEntry().getSize();
					}
				case 3:
					if (filesList.get(row).isDirectory()) {
						return "";
					} else {
						return filesList.get(row).getEntry().getCompressedSize();
					}
				case 4:
					if(row == 0 && level > 0) {
						return "";
					}
					return dateFormat.format(new Date(filesList.get(row).getEntry().getLastModifiedTime().toMillis()));

			}

			return "";
		}
	}
}

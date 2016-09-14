package unice.miage.m1.ui;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import unice.miage.m1.ui.Renderer;

/**
 * 
 * @author Thibaut
 *
 */

public class JTablePerso extends JTable{
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private ArrayList<JLabel> alNomFichier = new ArrayList<JLabel>();
	private ArrayList<String> alModifie = new ArrayList<String>();
	private ArrayList<Object> alTaille = new ArrayList<Object>();
	private String[] title;
	private File[] tFile;
	private JLabelPerso labelPerso;
	private Font fontLabelPerso = new Font("Microsoft YaHei UI Light", Font.PLAIN,12);
	public JTablePerso(File[] tFiles, String[] title) {
		this.title = title;
		this.tFile = tFiles;
		this.createRow();
		Object[][] data = this.createHeader();
		this.setModel(new MyModel(data, title));
		this.setBorder(null);
		this.getTableHeader().setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN,12));
		this.setShowGrid(false);
		this.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 12));
		this.setRowHeight(20);
		this.getTableHeader().setBackground(Color.white);	
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		this.setDefaultRenderer(JLabel.class, new Renderer() );
	
	}	
	public void buildTable(File[] tFiles) {
		this.tFile = tFiles;
		this.createRow();
		Object[][] data = this.createHeader();
		int nbRowTableau = this.getRowCount();
		int nbRow = tFiles.length;
		for (int i = nbRowTableau-1; i > -1; i--) {
			((DefaultTableModel)this.getModel()).removeRow(i);
		}
		((DefaultTableModel)this.getModel()).setRowCount(nbRow);
		this.setModel(new MyModel(data,title));
	
	}

	public void createRow(){
		alNomFichier.clear();
		alModifie.clear();
		alTaille.clear();
		for (File file : this.tFile) {
			Icon icon = fileSystemView.getSystemIcon(file);
			labelPerso = new JLabelPerso(file,icon, JLabel.LEFT);
			labelPerso.setFont(fontLabelPerso);
			this.alNomFichier.add(labelPerso);
			Object taille;
			if(file.isDirectory()) {
				taille = "";
				this.alTaille.add(taille);
			}
			else {
				taille = file.length();
				long tailleKo = ((long)taille/1024);
				if(tailleKo==0){this.alTaille.add(1+" Ko");}
				else{this.alTaille.add(tailleKo+" Ko");}
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			this.alModifie.add(sdf.format(file.lastModified()));
		}
	}

	public void setFontLabelPerso(Font fontLabelPerso) {
		this.fontLabelPerso = fontLabelPerso;
	}
	public Font getFontLabelPerso() {
		return this.fontLabelPerso;
	}
	public File[] getTFile() {
		return tFile;
	}
	public Object[][] createHeader(){
		Object[][] data = new Object[this.alNomFichier.size()][this.title.length];
		for(int i=0; i<data.length; i++){
				 data[i][0] = this.alNomFichier.get(i);
				 data[i][1] = this.alModifie.get(i);
				 data[i][2] = this.alTaille.get(i);
		}
		return data;
	}
}

package unice.miage.m1.ui;

import java.awt.Font;
import java.io.File;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
/**
 * Cette classe permet d'externaliser le code de la classe ExplorateurFichiers
 * Il cree un JLabel avec un icone. Ce JLabel sera contenu dans la liste des fichiers/dossier du JTable
 * @author Fabrice
 *
 */

public class JLabelPerso extends JLabel{
	private File file;
	private Icon icon;
	private int horizontalAlignement;
	
	public JLabelPerso(File file,Icon icon,int horizontalAlignement){
		super(file.getName(),icon,horizontalAlignement);
		this.file = file;
		this.icon = icon; 
		this.horizontalAlignement = horizontalAlignement;
		this.setFocusable(true);
		this.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 12));
		this.setBorder(new EmptyBorder(0, 6, 0, 0));
	}
	public File getFile(){
		return this.file;
	}
}

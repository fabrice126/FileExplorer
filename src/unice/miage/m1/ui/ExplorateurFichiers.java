package unice.miage.m1.ui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import unice.miage.m1.plugins.MyClassLoader;


public class ExplorateurFichiers extends JFrame implements Serializable {
	/*Declaration des panneaux*/
	private JPanel panelMainContent; // contiendra tout les panel de l'application
	private JPanel panelListDirectory;//Contient la liste des fichiers/dossiers (JScrollTable)
	private JPanel panelBarreAdresse;//Contient la barre d'adresse, le bouton preceent suivant et valider
	private JPanel panelArborescence;//Contient l'arborescence (JScrollTree)
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView(); //récupére les icone du systeme
	/*Déclaration du JTable contenant la liste des dossiers/fichiers*/
	private JTablePerso tableau;// liste des fichiers/dossiers
	private JScrollPane jScrollTable;//Contient la liste des fichiers (JTablePerso)
	
	/*JSplitPane entre le JTree et JTable. Permet de redimentionner le JTree et JTable*/
	private JSplitPane splitPaneBtwTreeAndTable;
	/*Déclaration du JTree contenant l'arborescence du systeme*/
	private DefaultMutableTreeNode racine;//Racine de l'arborescence
	private JTreePerso tree;//Arborescence
	private DefaultTreeModel treeModel;
	private JScrollPane jScrollTree;
	
	/*Déclaration des popup s'affichant au clique doit sur la barre d'adresse et sur le panelBarreAdresse*/
	private JPopupMenu popupMenuInTextFieldPath;
	private JPopupMenu popupMenuInContentPanel;
	private JPopupMenu popupMenuInPanelBarreAdresse;
	/*Declaration de la barre de menu des onglets et des items contenus dans les onglets*/
	private JMenuBar menuBar = new JMenuBar();
	private JMenu ongletFichier = new JMenu("Fichier");
	private JMenu ongletPlugin = new JMenu("Plugins");
	private JMenuItem ongletCloseAppBtnInOngletFichier = new JMenuItem("Fermer");
	private JMenuItem ongletSaveAppBtnInOngletFichier = new JMenuItem("Sauvegarder");
	private JMenuItem ongletLoadAppBtnInOngletFichier = new JMenuItem("Charger");
	private JMenuItem ongletPluginBtnInOngletPlugin = new JMenuItem("Ajouter un plugin");
	private JPopupMenu popupMenu = new JPopupMenu();
	/*Déclaration des listes permettant de gerer les plugins active/non actif
	 * De gerer les boutons retour/suivant*/
	private int indexTextFieldPath = 0;
	private List<String> historytextFieldPath = new ArrayList<String>();
	private ArrayList<File> listPlugins = new ArrayList<File>();
	private ArrayList<Boolean> listPluginsActive = new ArrayList<Boolean>();
	private ArrayList<Object> alPreLoadPluginInst = new ArrayList<Object>();
	private ArrayList<Class> alPreLoadPluginClass = new ArrayList<Class>();

	/*Declaration des composants situes dans panelBarreAdresse*/
	private JTextField textFieldPath; // barre d'adresse
	private JButton btnSuiv;
	private JButton btnPrev;
	private boolean precedent = false;
	private boolean suivant;
	/*Gerer liste des fichiers contenue dans le roots*/
	private File[] roots;
	private File fileCourant;
	/*Permet de savoir si le plugin implement Launchage*/
	public boolean isLaunchable = false;
	public JMenuItem launchable;
	
	public ExplorateurFichiers() {
		this.lookAndFeel();
		//______________Creation du Panel Principal contenant le JTree, JTable,Barre de menu______________
		panelMainContent = new JPanel();
		panelMainContent.getPreferredSize();
		panelMainContent.setLayout(new BorderLayout());
		this.getContentPane().add(panelMainContent);
		//__Creation de l'objet du menu contexuel au clique droit au dessus de la barre d'adresse__
		popupMenuInPanelBarreAdresse = new JPopupMenu();
		popupMenuInPanelBarreAdresse.setToolTipText("");
		popupMenuInPanelBarreAdresse.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 12));
		popupMenuInPanelBarreAdresse.setLabel("");
		this.addPopup(panelMainContent, popupMenuInPanelBarreAdresse);
		//______________Creation du JMenuItem Permettant d'importer un plugin lors du clique droit sur  le PanelBarreAdresse______________
		JMenuItem mntmAjouterUnPlugin = new JMenuItem("Ajouter un plugin...");
		mntmAjouterUnPlugin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {ImportWithFileChooser();}
		});
		mntmAjouterUnPlugin.setIcon(new ImageIcon("icone/addPlugin.png"));
		mntmAjouterUnPlugin.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 12));
		popupMenuInPanelBarreAdresse.add(mntmAjouterUnPlugin);
		//________________Creation de l'objet contenant la liste des fichiers/dossiers________________
		panelListDirectory = new JPanel();
		panelListDirectory.setLayout(new BoxLayout(panelListDirectory, BoxLayout.PAGE_AXIS));
		panelListDirectory.setBorder(null);
		//________________Creation de l'objet du menu contexuel sur la barre d'adresse________________
		popupMenuInTextFieldPath = new JPopupMenu();
		popupMenuInTextFieldPath.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 12));
		//__Creation du Panel contenant la barre d'adresse et les boutons suivant precedent et valider_
		panelBarreAdresse = new JPanel();
		panelBarreAdresse.setBackground(SystemColor.window);
		panelBarreAdresse.setBorder(new EmptyBorder(50, 0, 0, 0));
		panelBarreAdresse.setLayout(new BoxLayout(panelBarreAdresse, BoxLayout.X_AXIS));
		//____________________________Creation du Panel contenant la le JTree____________________________
		panelArborescence = new JPanel();
		//____________________________Creation du JSplitPane Permettant d'agrandir le JTree______________
	    splitPaneBtwTreeAndTable = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panelArborescence, panelListDirectory);
	    splitPaneBtwTreeAndTable.setBorder(null);
	    splitPaneBtwTreeAndTable.setUI(new BasicSplitPaneUI() {
	        public BasicSplitPaneDivider createDefaultDivider() {
	        	return new BasicSplitPaneDivider(this) {
		            public void setBorder(Border b) {}
		            @Override
	                public void paint(Graphics g) {
	                g.setColor(Color.WHITE);
	                g.fillRect(0, 0, getSize().width, getSize().height);
	                    super.paint(g);
	                }
		        };
	        }
	    });
	    splitPaneBtwTreeAndTable.setBorder(null);
	    panelMainContent.add(splitPaneBtwTreeAndTable,BorderLayout.CENTER);
	    ongletSaveAppBtnInOngletFichier.setIcon(new ImageIcon("icone/save.png"));
	    
	    /*Item contenu dans l'onglet Fichier de la barre de menu. Permet defermer l'application*/
	    ongletSaveAppBtnInOngletFichier.setIcon(new ImageIcon("icone/save.png"));
	    ongletSaveAppBtnInOngletFichier.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 12));
	    ongletSaveAppBtnInOngletFichier.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent arg0) {
	    	  serialize();
	      }        
	    });
	    ongletLoadAppBtnInOngletFichier.setIcon(new ImageIcon("icone/load.png"));
	    ongletLoadAppBtnInOngletFichier.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 12));
	    ongletLoadAppBtnInOngletFichier.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent arg0) {
	    	  deserialize();
	      }        
	    });
	    /*Item contenu dans l'onglet Fichier de la barre de menu. Permet defermer l'application*/
	    ongletCloseAppBtnInOngletFichier.setIcon(new ImageIcon("icone/close.png"));
	    ongletCloseAppBtnInOngletFichier.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 12));
	    ongletCloseAppBtnInOngletFichier.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent arg0) {
	        System.exit(0);
	      }        
	    });
	    
	    /*Permet d'importer des plugins; Contenu dans l'onglet plugin*/
	    ongletPluginBtnInOngletPlugin.setIcon(new ImageIcon("icone/addPlugin.png"));
	    ongletPluginBtnInOngletPlugin.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 12));
	    ongletPluginBtnInOngletPlugin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ImportWithFileChooser();
			}
		});
		ongletFichier.add(ongletLoadAppBtnInOngletFichier);
		ongletFichier.add(ongletSaveAppBtnInOngletFichier);
		ongletFichier.add(ongletCloseAppBtnInOngletFichier);
	    ImportFileDirectory();
	    chargerPlugins();
	    menuBar.add(ongletFichier);
	    menuBar.add(ongletPlugin);
	    this.setJMenuBar(menuBar);
		/**
		 * tFieldPath => barre de saisie d'adresse
		 */
		//Recuperer les dossiers roots
	    roots = File.listRoots();
		textFieldPath = new JTextField();
		initTable(new File(roots[0].getAbsolutePath()));
		this.changeIconTextJframe(roots[0].getAbsolutePath(), new File(roots[0].getAbsolutePath()));
		textFieldPath.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN,12));	
		textFieldPath.setBorder(new MatteBorder(1, 1, 1, 1, (Color) SystemColor.control));
		textFieldPath.setColumns(30);
		textFieldPath.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e){
				if(e.getKeyCode() == KeyEvent.VK_ENTER ){
					ExplorateurFichiers.this.refreshTFieldPath(new File(textFieldPath.getText()));
				}
			}
		});	
		/*_________________________________________________________________________________________________
		 *__________________________________DEBUT Bouton Precedent/Suivant_________________________________
		 *_________________________________________________________________________________________________*/
		//Bouton precedent
		btnPrev = new JButton(new ImageIcon("icone/prev.png"));
		btnPrev.setText("Prev");
		btnPrev.setUI((ButtonUI)MetalButtonUI.createUI(btnPrev));
		btnPrev.setFocusable(false);
		btnPrev.addMouseListener(new MouseAdapterHover(btnPrev,Color.white,UIManager.getColor("control")));
		btnPrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				
				if(indexTextFieldPath>1){
					indexTextFieldPath-=2;
					precedent = true;
					refreshTFieldPath(new File(historytextFieldPath.get(indexTextFieldPath)));	
				}
			}
		});
		//Bouton suivant
		btnSuiv = new JButton(new ImageIcon("icone/next.png"));
		btnSuiv.setText("Next");
		btnSuiv.setUI((ButtonUI)MetalButtonUI.createUI(btnSuiv));
		btnSuiv.setFocusable(false);
		btnSuiv.addMouseListener(new MouseAdapterHover(btnPrev,Color.white,UIManager.getColor("control")));
		btnSuiv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(indexTextFieldPath<(historytextFieldPath.size())){
					suivant = true;
					refreshTFieldPath(new File(historytextFieldPath.get(indexTextFieldPath)));
				}
			}
		});

		/**
		 * Permet de gerer le code du bouton permettant de valider ce qui est
		 * saisi dans la barre de recherche de chemin (tFieldPath)
		 */
		final JButton btnValider = new JButton("");
		btnValider.setIcon(new ImageIcon("icone/btnValider.png"));
		btnValider.setUI((ButtonUI)MetalButtonUI.createUI(btnValider));
		btnValider.setFocusable(false);
		btnValider.addMouseListener(new MouseAdapterHover(btnValider,Color.white,UIManager.getColor("control")));
		btnValider.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String file = textFieldPath.getText().trim();
				refreshTFieldPath(new File(file));
			}
		});
		/*_________________________________________________________________________________________________
		 *___________________________________FIN Bouton Precedent/Suivant__________________________________
		 *_________________________________________________________________________________________________*/
		panelBarreAdresse.add(btnPrev);		
		panelBarreAdresse.add(btnSuiv);
		panelBarreAdresse.add(textFieldPath);
		panelBarreAdresse.add(btnValider);
		panelMainContent.add(panelBarreAdresse,BorderLayout.NORTH);
		addPopup(textFieldPath, popupMenuInTextFieldPath);
		/*_________________________________________________________________________________________________
		 *_______________________________DEBUT Clique droit / menu contextuel______________________________
		 *_________________________________________________________________________________________________*/
		JMenuItem mntmCopierLadresse = new JMenuItem("Copier l'adresse");
		mntmCopierLadresse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection entry = new StringSelection(textFieldPath.getText());
				clipboard.setContents(entry, entry);
			}
		});
		mntmCopierLadresse.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 12));
		mntmCopierLadresse.setIcon(new ImageIcon("icone/copy.png"));
		popupMenuInTextFieldPath.add(mntmCopierLadresse);
		JMenuItem mntmCollerLadresse = new JMenuItem("Coller l'adresse");
		mntmCollerLadresse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable contents = clipboard.getContents(null);
				textFieldPath.setText("");
				String result;
				try {
					result = (String)contents.getTransferData(DataFlavor.stringFlavor);
					textFieldPath.setText(result);	
				} 
				catch (UnsupportedFlavorException e) {e.printStackTrace();} 
				catch (IOException e) {e.printStackTrace();}
			}
		});
		mntmCollerLadresse.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 12));
		mntmCollerLadresse.setIcon(new ImageIcon("icone/paste.png"));
		popupMenuInTextFieldPath.add(mntmCollerLadresse);
		/*_________________________________________________________________________________________________
		 *________________________________FIN Clique droit / menu contextuel_______________________________
		 *_________________________________________________________________________________________________*/
		/*_________________________________________________________________________________________________
		 *______________________________________DEBUT JTree Dynamique______________________________________
		 *_________________________________________________________________________________________________*/
		racine = new DefaultMutableTreeNode("racine");
        treeModel = new DefaultTreeModel(racine,true);
	    /**
	    * Permet d'afficher les noeuds enfants des racines
	    */
        for (File fileRoot : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileRoot);
            racine.add(node);
            File[] files = fileSystemView.getFiles(fileRoot,true);
            for (File file : files) {
                if (file.isDirectory()) {
                    node.add(new DefaultMutableTreeNode(file.getName()));
                }
            }
        }
        tree = new JTreePerso(this,treeModel);
	    jScrollTree = new JScrollPane(tree);
	    jScrollTree.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
	    jScrollTree.getVerticalScrollBar().setUnitIncrement(20);//Augmente la rapiditer du scroll
	    jScrollTree.getHorizontalScrollBar().setUnitIncrement(20);//Augmente la rapiditer du scroll
	    jScrollTree.setPreferredSize(new Dimension(200, 460));
	    panelArborescence.setLayout(new BoxLayout(panelArborescence, BoxLayout.X_AXIS));
	    panelArborescence.add(jScrollTree);
		/*_________________________________________________________________________________________________
		 * _____________________________________FIN DU JTree Dynamique_____________________________________
		 *_________________________________________________________________________________________________*/
		/*_________________________________________________________________________________________________
		 *_____________________________________FIN EXPLORATEUR FICHIER_____________________________________
		 *_________________________________________________________________________________________________*/
		this.setVisible(true);
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    this.setBounds(100, 100, (int)(screenSize.getWidth()*0.6), (int)(screenSize.getHeight()*0.7));
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Permet de recharger la liste des elements contenus dans un dossier grace au champs tFieldPath (champ de saisie de l'url)
	 * Cette fonction est appelee 
	 * 			-lors d'un double clique sur un dossier de la liste des dossiers/fichiers ou si on tape sur la touche entrer
	 * 			-Lors d'un double ou simple clique sur un dossier de l'arborescence
	 * 			-Quand l'utilisateur saisi un chemin dans la barre d'adresse et clique sur entre ou sur le bouton valider
	 * @param fileReload
	 */
    public void refreshTFieldPath(File fileReload){
		this.fileCourant = fileReload;
    	if(fileReload.getPath().isEmpty()){
            textFieldPath.setText("");
    	}else{
            textFieldPath.setText(fileReload.getAbsolutePath());
    	}
		if (textFieldPath.getText().length()>0) {
	    	File testExist = new File(fileReload.getPath());
			if (!testExist.exists()) {
	            textFieldPath.setText("");
				JOptionPane.showMessageDialog(null,"Vous devez specifier un chemin existant","Mauvais chemin",JOptionPane.ERROR_MESSAGE,new ImageIcon("icone/error.png"));
			} else {
				if(precedent == false && suivant == false) {
					historytextFieldPath.add(fileReload.getAbsolutePath());
				}
				else {
					precedent = false;
					suivant = false;
				} 
				indexTextFieldPath++;
				FileSystemView fileSystemView = FileSystemView.getFileSystemView();
		    	File[] alFiles = fileSystemView.getFiles(new File(fileReload.getPath()), true);
				this.changeIconTextJframe(fileReload.getAbsolutePath(),fileReload);
				modifTable(alFiles);
			}
		}else {
			JOptionPane.showMessageDialog(null,"Vous devez specifier un chemin","Mauvais chemin",JOptionPane.ERROR_MESSAGE,new ImageIcon("icone/error.png"));
		}
    }
    
    /**
	 * Permet la creation du tableau contenant le detail des fichiers
	 * @param alFiles
	 */
    public void initTable(File fileReload){
    	File[] alFiles = fileSystemView.getFiles(new File(fileReload.getPath()), true);
		String[] titleHeader = {"Nom", "Modifie le", "Taille"};
		tableau = new JTablePerso(alFiles,titleHeader);
		tableau.addMouseListener(new MouseAdapter() {
			/*Si l'utilisateur fait un double clique gauche sur un dossier element du JTable cette fonction
			 * appelle refreshTextFieldOnListener(file) qui ouvrira un fichier ou mettra a jour la JTable si c'est un dossier*/
			   public void mouseClicked(MouseEvent e) {
				   //si double clique gauche
				   if (e.getClickCount() == 2 && e.getButton()==1) {
					   JTable target = (JTable)e.getSource();
					   int row = target.getSelectedRow();
					   JLabelPerso jlabel = (JLabelPerso) target.getValueAt(row, 0);
					   File file = jlabel.getFile();
					   ExplorateurFichiers.this.refreshTextFieldOnListener(file);
				   }
			   }
			});
		/*Si une ligne dela JTable et selectionne et que l'utilisateur clique sur entrer
		 * le fichier s'ouvre et le dossier remet a jour la JTable en affichant son contenu*/
			tableau.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent e){
					if(e.getKeyCode() ==KeyEvent.VK_ENTER ){
						JTable target = (JTable)e.getSource();
					    int row = target.getSelectedRow();
					    JLabelPerso jlabel = (JLabelPerso) target.getValueAt(row, 0);
					    File file = jlabel.getFile();
						ExplorateurFichiers.this.refreshTextFieldOnListener(file);
					}
				}
			});
		jScrollTable = new JScrollPane(tableau);
		jScrollTable.getViewport().setBackground(tableau.getBackground());		
		jScrollTable.setBorder(BorderFactory.createEmptyBorder( 0, 0, 0, 0 ));
		panelListDirectory.add(jScrollTable);
		refreshTFieldPath(fileReload);
    	
	}
    
	/**
	 * Permet la modification du tableau contenant le detail des fichiers
	 * @param alFiles
	 */
    public void modifTable(File[] alFiles){
    	
    	tableau.buildTable(alFiles);
		panelListDirectory.updateUI();
		panelListDirectory.revalidate();
	}
    
    /**
     * Cette fonction permet de reload les elements du JTabel:
     * 	- lors d'un double clique sur un dossier 
     * 	- lorsque on appuie sur la touche "entrer" sur le fichier selectionne
     * Cette fonction permet d'ouvrir un fichier :
     * 	- lors d'un double clique sur un fichier 
     * 	- lorsque on appuie sur la touche "entrer" sur le fichier selectionne
     * @param target
     */
    public void refreshTextFieldOnListener(File file) {
		   this.fileCourant = file;
		   if(file.isDirectory()){
			   //on repaint
			   ArrayList<String> temp = new ArrayList<String>();
			   for(int j=0; j < indexTextFieldPath; j++){
				   temp.add(historytextFieldPath.get(j));	   
			   }
			   historytextFieldPath.clear();
			   historytextFieldPath.addAll(temp);
			   ExplorateurFichiers.this.refreshTFieldPath(file);
		   }
		   else{
			   //C'est un fichier et on l'ouvre.
			   Desktop desktop = Desktop.getDesktop();
			   try {
				   desktop.open(file);
			   } catch (IOException ioe) {
				   ioe.printStackTrace();
			   }
		   }		
	}
	
    /**
     * Permet de transformer une Icon en Image
     * @param icon
     * @return image
     */
	public Image iconToImage(Icon icon) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon)icon).getImage();
        } else {
            int w = icon.getIconWidth();
            int h = icon.getIconHeight();
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(w, h);
            Graphics2D g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return image;
        }
    }
    /**
     * Permet d'afficher les icones 32x32 du systeme d'exploitation
     * @param name
     * @param filePathIcone
     */
    @SuppressWarnings("restriction")
	public void changeIconTextJframe(String name,File filePathIcone){
    	this.setTitle(name);		
    	/*Permet de gerer le chargement de la racine pour linux*/
    	if(roots[0].getAbsolutePath().equals("/")){
			 this.setIconImage(iconToImage(fileSystemView.getSystemIcon(filePathIcone)));
    	}
    	else{
			 this.setIconImage(iconToImage(fileSystemView.getSystemIcon(filePathIcone)));
	    }
    }
    /**
     * Permet d'adopter le style de l'OS
     */
	public void lookAndFeel(){
		try {
			  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			  SwingUtilities.updateComponentTreeUI(this);
		}
		catch (InstantiationException e) {}
		catch (ClassNotFoundException e) {}
		catch (UnsupportedLookAndFeelException e) {}
		catch (IllegalAccessException e) {}
	}
	/**
	 * FileChooser permettant d'importer des .jar, .zip
	 */
	public void ImportWithFileChooser() {
		JFileChooser fileChooser = new JFileChooser(".");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers zip ,jar", "zip","jar");
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setAcceptAllFileFilterUsed(false);
		String fichier;
		if(fileChooser.showOpenDialog(null)==0){
            fichier = fileChooser.getSelectedFile().getAbsolutePath();
            File file = new File(fichier);
			String path = System.getProperty("user.dir")+File.separator+"plugins";
			File newFile = new File(path+File.separator+file.getName());
			try {
				copyFile(file,newFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			listPlugins.add(newFile);
			listPluginsActive.add(false);
			ongletPlugin.removeAll();
			chargerPlugins();
        }
	}

	/**
	 * Affichage des plugins dans le menu Plugin et chargement de chaque plugin.
	 */

	public void chargerPlugins() {
		alPreLoadPluginInst.clear();
		alPreLoadPluginClass.clear();
		ongletPlugin.removeAll();
		for (int i = 0; i < listPlugins.size(); i++) {
			JMenu plugin = new JMenu(listPlugins.get(i).getName());
			final ExplorateurFichiers explorer = this;
			final int tmpI = i;
			File file = listPlugins.get(i);
			if(file.exists()) {
				MyClassLoader mcl = new MyClassLoader(listPlugins.get(tmpI).getAbsolutePath(),explorer);
				Object instance;
				try {
					instance = mcl.loadClassData();
					alPreLoadPluginInst.add(instance);

					//si la l'instance implemente launchable
					for(int j = 0 ; j < alPreLoadPluginInst.get(tmpI).getClass().getInterfaces().length; j ++ ){
						if(alPreLoadPluginInst.get(tmpI).getClass().getInterfaces()[j].getName().toString().endsWith("Launchable") ) {
							isLaunchable = true;
						}
					}
					
					if(isLaunchable){
						launchable = new JMenuItem("Lancer le plugin");
						if(listPluginsActive.get(i) == true) {
							launchable.setEnabled(true);
						}
						else {
							launchable.setEnabled(false);
						}
						launchable.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								listPluginsActive.set(tmpI, true);
								lancer(alPreLoadPluginInst.get(tmpI), alPreLoadPluginClass.get(tmpI));
							}
						});
						plugin.add(launchable);
						isLaunchable = false;
					}
					
					JCheckBoxMenuItem activer;
					activer = new JCheckBoxMenuItem("Activer");
					activer.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							if(listPluginsActive.get(tmpI).booleanValue() == false) {
								listPluginsActive.set(tmpI, true);
								active(alPreLoadPluginInst.get(tmpI), alPreLoadPluginClass.get(tmpI));
								if(isLaunchable){
									launchable.setEnabled(true);
								}
							}
							else {
								listPluginsActive.set(tmpI, false);
								desactive(alPreLoadPluginInst.get(tmpI), alPreLoadPluginClass.get(tmpI));
								if(isLaunchable){
									launchable.setEnabled(false);
								}
							}
						}
					});
					
					JMenuItem supprimer = new JMenuItem("Supprimer");
	
					supprimer.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							suppressionPlugin(listPlugins.get(tmpI), tmpI);
						}
					});
					if(listPluginsActive.get(i) == true) {
						activer.setSelected(true);
						active(alPreLoadPluginInst.get(i), alPreLoadPluginClass.get(i));
					}
					plugin.add(activer);
					plugin.add(supprimer);
					ongletPlugin.add(plugin);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}	
			}
		}
	    ongletPlugin.addSeparator();
	    ongletPlugin.add(ongletPluginBtnInOngletPlugin);
	}
	/**
	 * Liste des .jar present dans le dossier courant
	 */
	public ArrayList<File> ImportFileDirectory() {
		String path = System.getProperty("user.dir")+File.separator+"plugins";
		File file = new File(path);
		if(file.isDirectory()){
			File[] myFiles = file.listFiles(new FileFilter(){
				public boolean accept(File directory) {
					if(directory.getName().endsWith(".jar")){
						return true;
					}
					return false;
				}
			});
			for(File fichier:myFiles){
				listPlugins.add(fichier);
				listPluginsActive.add(false);
			}
		}
		return listPlugins;
	}
	
	private void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());			
			}
		});
	}

	private void suppressionPlugin (File file, int i) {
		JOptionPane popupSuppression;
		popupSuppression = new JOptionPane();
		int option = popupSuppression.showConfirmDialog(null, "Voulez-vous supprimer ce plugin ?", "Suppression du plugin", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if(option != JOptionPane.NO_OPTION && 
		   option != JOptionPane.CLOSED_OPTION){
			desactive(alPreLoadPluginInst.get(i), alPreLoadPluginClass.get(i));
			listPlugins.remove(i);
			listPluginsActive.remove(i);
			if(file.exists()) {
				file.delete();
			}
			chargerPlugins();
		}
	}
	
	public void copyFile (File src, File dest) throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(src));
		OutputStream out = new BufferedOutputStream(new FileOutputStream(dest));
		byte[] buf = new byte[4096];
		int n;
		while ((n=in.read(buf, 0, buf.length)) > 0)
		out.write(buf, 0, n);
		
		in.close();
		out.close();
	}
	
	public void lancer(Object instance, Class tmpClass){
		try {
			Method method = tmpClass.getMethod("lancer");
			method.invoke(instance);
		} catch ( IllegalArgumentException| NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.fillInStackTrace();
		}	}
	public void active(Object instance, Class tmpClass) {
		try {
			Method method = tmpClass.getMethod("plug", this.getClass());
			method.invoke(instance, new Object[]{this});
		} catch ( IllegalArgumentException| NoSuchMethodException | SecurityException | IllegalAccessException | InvocationTargetException e) {
			e.fillInStackTrace();
		}
	}
	public void desactive(Object instance, Class tmpClass) {

		try {
			Method method = tmpClass.getMethod("unplug", this.getClass());
			method.invoke(instance, new Object[]{this});
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void serialize() {
		ExplorateurFichiers explorer = this; //implemente Serializable
		FileOutputStream fos;
		
		File fichier = new File("options.tmp");
		try {
			fichier.createNewFile();
			fos = new FileOutputStream(fichier);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(listPlugins);
			oos.writeObject(listPluginsActive);
			oos.writeObject(fileCourant);
			oos.writeObject(historytextFieldPath);
			oos.writeObject(indexTextFieldPath);
			oos.writeObject(textFieldPath);
			oos.writeObject(panelBarreAdresse);
			
			/* Modification de l'arborescence tree */
			DefaultTreeCellRenderer rendererTree = (DefaultTreeCellRenderer)getTree().getCellRenderer();
			oos.writeObject(rendererTree.getBackgroundNonSelectionColor());
			oos.writeObject(rendererTree.getBackgroundSelectionColor());
			oos.writeObject(rendererTree.getTextNonSelectionColor());
			oos.writeObject(rendererTree.getTextSelectionColor());
			oos.writeObject(tree.getBackground());
			oos.writeObject(tree.getFont());
			

			/* Modification de la jtable */
			oos.writeObject(getjTablePerso().getSelectionBackground());//ligne
			oos.writeObject(getjTablePerso().getSelectionForeground());//ligne
			oos.writeObject(getjTablePerso().getBackground());
			oos.writeObject(getjTablePerso().getForeground());//Couleur texte
			oos.writeObject(getjTablePerso().getTableHeader().getForeground());//Couleur texte
			oos.writeObject(getjTablePerso().getTableHeader().getBackground());
			oos.writeObject(getjTablePerso().getTableHeader().isOpaque());
			/*Font du text*/
			oos.writeObject(getjTablePerso().getTableHeader().getFont());
			oos.writeObject(getjTablePerso().getFont());
			oos.writeObject(getjTablePerso().getTFile());

			oos.writeObject(getjScrollTable().getViewport().getBackground());
			
			oos.flush();
			oos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deserialize() {
		ExplorateurFichiers explorer = this; //implemente Serializable

		FileInputStream fis;
		try {
			fis = new FileInputStream("options.tmp");
			ObjectInputStream ois = new ObjectInputStream(fis);
			ArrayList<File> listPluginsSave = (ArrayList<File>)ois.readObject();
			ArrayList<Boolean> listPluginsActiveSave = (ArrayList<Boolean>)ois.readObject();
			File fileCourantSave = (File)ois.readObject();
			List<String> historytextFieldPathSave = (List<String>)ois.readObject();
			int indexTextFieldPathSave = (int)ois.readObject();
			JTextField textFieldPathSave = (JTextField)ois.readObject();
			JPanel panelBarreAdresseSave = (JPanel)ois.readObject();
			

			/* Modification de l'arborescence tree */
			Color treeBackgroundNonSelectionColor = (Color)ois.readObject();
			Color treeBackgroundSelectionColor = (Color)ois.readObject();
			Color treeTextNonSelectionColor = (Color)ois.readObject();
			Color treeTextSelectionColor = (Color)ois.readObject();
			Color treeBackground = (Color)ois.readObject();
			Font treeFont = (Font)ois.readObject();

			DefaultTreeCellRenderer rendererTree = (DefaultTreeCellRenderer)getTree().getCellRenderer();
			rendererTree.setBackgroundNonSelectionColor(treeBackgroundNonSelectionColor);
			rendererTree.setBackgroundSelectionColor(treeBackgroundSelectionColor);
			rendererTree.setTextNonSelectionColor(treeTextNonSelectionColor);
			rendererTree.setTextSelectionColor(treeTextSelectionColor);
			getTree().setBackground(treeBackground);
			getTree().setFont(treeFont);
			

			/* Modification de la jtable */
			Color tableSelectionBackground = (Color)ois.readObject();
			Color tableSelectionForeground = (Color)ois.readObject();
			Color tableBackground = (Color)ois.readObject();
			Color tableForeground = (Color)ois.readObject();
			Color tableHeaderForeground = (Color)ois.readObject();
			Color tableHeaderBackground = (Color)ois.readObject();
			boolean tableHeaderOpaque = (boolean)ois.readObject();
			Font tableHeaderFont = (Font)ois.readObject();
			Font tableFont = (Font)ois.readObject();
			File[] tableFile = (File[])ois.readObject();
			Color scrollBackground = (Color)ois.readObject();
			
			
			/*JTable par defaut*/
			getjTablePerso().setSelectionBackground(tableSelectionBackground);//ligne
			getjTablePerso().setSelectionForeground(tableSelectionForeground);//ligne
			getjTablePerso().setBackground(tableBackground);
			getjTablePerso().setForeground(tableForeground);//Couleur texte
			getjTablePerso().getTableHeader().setForeground(tableHeaderForeground);//Couleur texte
			getjTablePerso().getTableHeader().setBackground(tableHeaderBackground);
			getjTablePerso().getTableHeader().setOpaque(tableHeaderOpaque);
			/*Font du text*/
			getjTablePerso().getTableHeader().setFont(tableHeaderFont);
			getjTablePerso().setFont(tableFont);
    		getjTablePerso().buildTable(tableFile);
			getjScrollTable().getViewport().setBackground(scrollBackground);
			
			/* Modification de l'arborescence tree */
			listPlugins.clear();
			listPluginsActive.clear();
			getListPlugins().addAll(listPluginsSave);
			getListPluginsActive().addAll(listPluginsActiveSave);
			chargerPlugins();
			fileCourant = fileCourantSave;
			historytextFieldPath = historytextFieldPathSave;
			indexTextFieldPath = indexTextFieldPathSave;

			/*Panel de la barre par defaut*/
			getTextFieldPath().setFont(textFieldPathSave.getFont());
			getTextFieldPath().setForeground(textFieldPathSave.getForeground());
			getTextFieldPath().setBackground(textFieldPathSave.getBackground());
			getPanelBarreAdresse().setBackground(panelBarreAdresseSave.getBackground());
			
            textFieldPath.setText(fileCourant.getAbsolutePath());
    	
			fis.close();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public JPanel getPanelMainContent() {
		return panelMainContent;
	}

	public JTextField getTextFieldPath() {
		return textFieldPath;
	}

	public FileSystemView getFileSystemView() {
		return fileSystemView;
	}

	public JPanel getPanelListDirectory() {
		return panelListDirectory;
	}

	public JPanel getPanelBarreAdresse() {
		return panelBarreAdresse;
	}

	public JPanel getPanelArborescence() {
		return panelArborescence;
	}

	public JTreePerso getTree() {
		return tree;
	}

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	public DefaultMutableTreeNode getRacine() {
		return racine;
	}

	public JScrollPane getjScrollTable() {
		return jScrollTable;
	}

	public JScrollPane getjScrollTree() {
		return jScrollTree;
	}

	public JTablePerso getjTablePerso() {
		return tableau;
	}
	
	public JPopupMenu getPopupMenuInTextFieldPath() {
		return popupMenuInTextFieldPath;
	}

	public JPopupMenu getPopupMenuInContentPanel() {
		return popupMenuInPanelBarreAdresse;
	}

	public JMenu getOngletFichier() {
		return ongletFichier;
	}

	public JMenu getOngletPlugin() {
		return ongletPlugin;
	}

	public JMenuItem getOngletCloseAppBtnInOngletFichier() {
		return ongletCloseAppBtnInOngletFichier;
	}

	public JMenuItem getOngletPluginBtnInOngletPlugin() {
		return ongletPluginBtnInOngletPlugin;
	}

	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}
	
	public JSplitPane getSplitPaneBtwTreeAndTable() {
		return splitPaneBtwTreeAndTable;
	}

	public List<String> getHistorytextFieldPath() {
		return historytextFieldPath;
	}

	public int getIndexTextFieldPath() {
		return indexTextFieldPath;
	}

	public JButton getBtnSuiv() {
		return btnSuiv;
	}

	public JButton getBtnPrev() {
		return btnPrev;
	}

	public boolean isPrecedent() {
		return precedent;
	}

	public boolean isSuivant() {
		return suivant;
	}

	public File[] getRoots() {
		return roots;
	}
	
	public File getFileCourant() {
		return fileCourant;
	}

	public ArrayList<Object> getAlPreLoadPluginInst() {
		return alPreLoadPluginInst;
	}

	public ArrayList<Class> getAlPreLoadPluginClass() {
		return alPreLoadPluginClass;
	}
	
	public ArrayList<File> getListPlugins() {
		return listPlugins;
	}

	public ArrayList<Boolean> getListPluginsActive() {
		return listPluginsActive;
	}
}

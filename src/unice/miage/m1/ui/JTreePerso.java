package unice.miage.m1.ui;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.Serializable;

import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Classe permettant d'externaliser le code du JTree de la classe ExplorateurFichiers
 * @author Fabrice
 *
 */

public class JTreePerso extends JTree{
	private TreeExpansionListener treeExpansionListener;
	private TreeSelectionListener treeSelectionListener;
	private ExplorateurFichiers explorer;
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	JTreePerso(ExplorateurFichiers explorer,DefaultTreeModel treeModel){
		super(treeModel);
		this.explorer = explorer;
	    this.setBorder(new EmptyBorder(10, 0, 0, 0));
	    this.expandRow(0);
	    this.setCursor(Cursor.getPredefinedCursor(12));
	    this.setFont(new Font("Microsoft YaHei UI Light", Font.PLAIN, 13));
	    this.setRootVisible(false);
	    this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    this.addTreeListenerSelectionExpension();
	    this.addTreeSelectionListener(treeSelectionListener);
	    this.addTreeExpansionListener(treeExpansionListener);
		/*Quand le curseur va sur une node le curseur pointeur se transforme en curseur main*/
		this.addMouseMotionListener(new MouseMotionAdapter() {
		    @Override
		    public void mouseMoved(MouseEvent e) {
		        int x = (int) e.getPoint().getX();
		        int y = (int) e.getPoint().getY();
		        TreePath path = getPathForLocation(x, y);
		        if (path == null) {
		            setCursor(Cursor.getDefaultCursor());
		        } else {
		            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		        }
		    }
		});
	}
    /**
     * Quand on clique sur un libelle du JTree
     */
	public void addTreeListenerSelectionExpension(){
		/*Quand on clique sur l'icone permettant d'extend et collapse un noeud du JTree*/
        treeExpansionListener = new TreeExpansionListener(){
			@Override
			public void treeCollapsed(TreeExpansionEvent event) {}
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				DefaultMutableTreeNode node = loadNode(event.getPath());
	    		((DefaultTreeModel) getModel()).reload(node);
			}
        };
        /*Quand on etend le noeud via un double clique*/
        treeSelectionListener = new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event) {
				loadNode(event.getPath());
			}
      };
    }
	
	/**
	 * Fonction permettant de recuperer les fichiers contenue dans un noeud lorsqu'on double clique dessus ou qu'on l'extend
	 * @param TreePath pathEvent
	 * @return
	 */
	private DefaultMutableTreeNode loadNode(TreePath pathEvent) {
		int i = 1;
		String pathNode = "";
		
		if(pathEvent.getPathCount()>2){
			while(i!=pathEvent.getPathCount()){
				TreePath tp = pathEvent;
				if(i==1){
					pathNode+=tp.getPathComponent(i);
				}else{
					pathNode+=tp.getPathComponent(i)+File.separator;
				}
				i++;
			}
		}else{
			TreePath tp = pathEvent;
			pathNode+=tp.getPathComponent(1);
		}
		File file = new File(pathNode);
		DefaultMutableTreeNode node =(DefaultMutableTreeNode)pathEvent.getLastPathComponent();
		if (file.isDirectory()) {
			
            File[] files = fileSystemView.getFiles(file, true);//Afficher ou non les fichiers caches
            explorer.refreshTFieldPath(file);//on refresh la liste des elements contenus dans le dossier
            if (node.isLeaf()) {
                for (File child : files) {
                    if (child.isDirectory()) {
                    	node.add(new DefaultMutableTreeNode(child.getName()));
                    }
                }
            }
        }	
		return node;
	}
}

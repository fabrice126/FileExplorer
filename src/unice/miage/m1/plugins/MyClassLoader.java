package unice.miage.m1.plugins;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarFile;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import unice.miage.m1.ui.ExplorateurFichiers;


public class MyClassLoader{
	private String pathParent = null;
	private transient ExplorateurFichiers explorer;
	private Class tmpClass;
	/**
	 * Specifier le chemin a partir du quel va commencer la recherche du fichier
	 * Plus le chemin est precis plus la recherche sera rapide...
	 * exemple : C:/Users/UserName/workspace/
	 */
	public MyClassLoader(String pathParent,ExplorateurFichiers explorer){
		this.pathParent = pathParent;
		this.explorer= explorer; 
	}

	/*Cette fonction lis les .jar et .zip*/
	public Object loadClassData() throws ClassNotFoundException {	
		Object instance = null;
		URL url;
		if(pathParent.endsWith(".jar")||pathParent.endsWith(".zip")){
			try {
				url = new URL("file:///"+this.pathParent);
				URLClassLoader loader = new URLClassLoader(new URL[] {url}); 
				System.out.println("this.pathParent ="+this.pathParent);
				JarFile jar = new JarFile(this.pathParent);
				Enumeration enumeration = jar.entries();
				while(enumeration.hasMoreElements()){
					String tmp = enumeration.nextElement().toString();
					//On verifie que le fichier courant est un .class
					if(tmp.length() > 6 && tmp.substring(tmp.length()-6).compareTo(".class") == 0) {
						tmp = tmp.substring(0,tmp.length()-6);
						tmp = tmp.replaceAll("/",".");

						tmpClass = Class.forName(tmp ,true,loader);
						
						for(int i = 0 ; i < tmpClass.getInterfaces().length; i ++ ){
							if(tmpClass.getInterfaces()[i].getName().toString().endsWith("IPlugin") ) {
								instance = tmpClass.newInstance();
								explorer.getAlPreLoadPluginClass().add(tmpClass);
							}
						}
					}
				}
				jar.close();
				loader.close();
			} catch (IOException e) {e.printStackTrace();} 
			catch (IllegalAccessException e) {e.printStackTrace();} 
			catch (IllegalArgumentException e) {e.printStackTrace();} 
			catch (InstantiationException e) {e.printStackTrace();} 	
		}
		else{
			JOptionPane.showMessageDialog(null,"Vous devez specifier le chemin d'un .jar","Ce n'est pas un .jar !",JOptionPane.ERROR_MESSAGE,new ImageIcon("icone/error.png"));
		}

		return instance;
	 } 	
}	


package unice.miage.m1.IPlugins;

import unice.miage.m1.ui.ExplorateurFichiers;

public interface IPlugin {
	public void plug(ExplorateurFichiers explorer);
	public void unplug(ExplorateurFichiers explorer);
}

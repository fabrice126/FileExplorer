package unice.miage.m1.launcher;

import java.awt.EventQueue;
import java.io.Serializable;

import unice.miage.m1.ui.ExplorateurFichiers;

public class Launcher implements Serializable{
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ExplorateurFichiers frame = new ExplorateurFichiers();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

package unice.miage.m1.ui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.UIManager;
/**
 * Classe permettant de reduire le nombre de MouseAdapter Contenu dans ExplorateurFichier
 * @author Fabrice
 *
 */
public class MouseAdapterHover extends MouseAdapter implements Serializable{

	private Color mouseEnteredColor;
	private Color mouseExitedColor;
	public MouseAdapterHover(JButton myButton, Color mouseEnteredColor, Color mouseExitedColor){
		this.mouseEnteredColor = mouseEnteredColor;
		this.mouseExitedColor = mouseExitedColor;
	}
	public void mouseEntered(MouseEvent evt) {
		JButton button = (JButton)evt.getComponent();
		button.setBackground(mouseEnteredColor);
	}
	public void mouseExited(MouseEvent evt) {
		JButton button = (JButton)evt.getComponent();
		button.setBackground(mouseExitedColor);
	}
}

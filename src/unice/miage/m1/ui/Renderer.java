package unice.miage.m1.ui;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * 
 * @author Thibaut
 *
 */
public class Renderer extends DefaultTableCellRenderer{
    public void fillColor(JTable t,JLabel l,boolean isSelected ){
    	
        if(isSelected){
            l.setBackground(t.getSelectionBackground());
            l.setForeground(t.getSelectionForeground());
        }
 
        else{
            l.setBackground(t.getBackground());
            l.setForeground(t.getForeground());
        }
 
    }
    
    public void fillTable(JTable table,JLabel label,boolean isSelected) {
        fillColor(table,label,isSelected);
    }
 
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
         boolean hasFocus, int row, int column)
     {
        if(value instanceof JLabel){
            JLabel label = (JLabel)value;
            label.setOpaque(true);
            fillTable(table,label,isSelected);
            return label;
        }
        else
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
     }
 
}
package unice.miage.m1.ui;


/**
 * 
 * @author Thibaut
 *
 */
public class MyModel extends javax.swing.table.DefaultTableModel{
 

	public MyModel (Object [][] row, Object [] col){
        for(Object c: col)
            this.addColumn(c);
 
        for(Object[] r: row)
            addRow(r);
 
    }
    
    @Override
    public Class<?> getColumnClass(int colNum) {
        switch (colNum) {
            case 0:
                return JLabelPerso.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            default:
                return String.class;
        }
    } 
    
    @Override
	public boolean isCellEditable(int rowIndex,int columnIndex){
		return false;
	}

}

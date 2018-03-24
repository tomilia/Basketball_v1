
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author tommylee
 */
public class RowPopup extends JPopupMenu{
    public RowPopup(JTable table)
    {
        JMenuItem remove=new JMenuItem("移除");
        remove.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(remove,"Delete");//To change body of generated methods, choose Tools | Templates.
            }
           
        });
        add(remove);
    }
}

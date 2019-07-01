package app.adc.genericViewer;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import app.adc.genericViewer.ui.MainContainer;

/**
 * Hello world!
 *
 */
public class StartViewer 
{
    public static void main( String[] args )
    {
        System.out.println( "Generic viewer..." );
        JFrame frmMain = new JFrame();
        frmMain.setTitle("Generic Viewer");
        frmMain.setLayout(new BorderLayout());
        frmMain.add(new MainContainer(), BorderLayout.CENTER);
        frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmMain.setSize(1200, 800);
        frmMain.setLocationRelativeTo(null);
        frmMain.setVisible(true);
    }
}

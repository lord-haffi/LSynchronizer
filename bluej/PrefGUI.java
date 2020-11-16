import java.awt.*;
import java.awt.event.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import library.streaming.Streamer;

/**
 *
 * Beschreibung
 *
 * @version 1.0 vom 15.08.2015
 * @author 
 */

public class PrefGUI extends Frame {
    // Anfang Attribute
    private Label labelLan = new Label();
    private Choice choice1 = new Choice();
    private Button buttonOverride = new Button();
    private Button buttonCancel = new Button();
    private Checkbox checkboxBackground = new Checkbox();
    private Button browseBackground = new Button();
    private Label labelBackground = new Label();
    
    private GUI g;
    private File backgIm = null;
    // Ende Attribute

    public PrefGUI(GUI g) { 
        // Frame-Initialisierung
        super(Main.nameOfProgram+" - "+Main.language[35]);
        this.g = g;
        g.setEnabled(false);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) { close();}
            });
        int frameWidth = 540; 
        int frameHeight = 152;
        setSize(frameWidth, frameHeight);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);
        setResizable(false);
        Panel cp = new Panel(null);
        add(cp);
        // Anfang Komponenten

        labelLan.setBounds(24, 16, 150, 23);
        labelLan.setText(Main.language[37]+":");
        labelLan.setFont(new Font("Arial", Font.PLAIN, 12));
        cp.add(labelLan);
        choice1.setBounds(184, 16, 246, 23);
        choice1.add("GE");
        listLanguages();
        choice1.select(Main.setInfos[1]);
        cp.add(choice1);
        buttonOverride.setBounds(352, 72, 163, 33);
        buttonOverride.setLabel(Main.language[38]);
        buttonOverride.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    apply(evt);
                }
            });
        buttonOverride.setFont(new Font("Arial", Font.PLAIN, 18));
        cp.add(buttonOverride);
        buttonCancel.setBounds(168, 72, 155, 33);
        buttonCancel.setLabel(Main.language[12]);
        buttonCancel.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    close();
                }
            });
        buttonCancel.setFont(new Font("Arial", Font.PLAIN, 18));
        cp.add(buttonCancel);
        checkboxBackground.setBounds(9, 40, 165, 23);
        checkboxBackground.setLabel(Main.language[45]+":");
        checkboxBackground.setFont(new Font("Arial", Font.PLAIN, 12));
        if(!Main.setInfos[2].equals("")) checkboxBackground.setState(true);
        cp.add(checkboxBackground);
        browseBackground.setBounds(448, 40, 35, 25);
        browseBackground.setLabel("...");
        browseBackground.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    browseBackground_ActionPerformed(evt);
                }
            });
        browseBackground.setFont(new Font("Arial", Font.PLAIN, 18));
        cp.add(browseBackground);
        labelBackground.setBounds(184, 40, 246, 23);
        labelBackground.setText(Main.setInfos[2]);
        labelBackground.setFont(new Font("Arial", Font.PLAIN, 12));
        cp.add(labelBackground);
        // Ende Komponenten

        setVisible(true);
    } // end of public PrefGUI

    // Anfang Methoden

    private void listLanguages(){
        File userDir = new File(System.getProperty("user.dir"));
        File[] list = userDir.listFiles();
        //System.out.println("Dir: "+userDir.getAbsolutePath());
        for(int i=0; i<list.length; i++){
            String n = list[i].getName();
            //System.out.println(i+": Name des Files: "+n);
            if(!list[i].isDirectory() && n.startsWith("language_")){
                choice1.add(n.substring(n.lastIndexOf('_')+1,n.length()));
                //System.out.println("'"+n+"' gefunden.");
            }
        }
    }
    private void deleteOldBackground(){
        File[] list = new File(System.getProperty("user.dir")).listFiles();
        for(int i=0; i<list.length; i++){
            if(!list[i].isDirectory() && list[i].getName().startsWith("background")){
                list[i].delete();
            }
        }
    }

    private void apply(ActionEvent evt){
        File settings = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"settings");
        Main.setInfos[1] = choice1.getSelectedItem();
        if(backgIm != null) deleteOldBackground();
        if(!checkboxBackground.getState()){
            deleteOldBackground();
            Main.setInfos[2] = "";
        }else if(labelBackground.getText().equals(Main.setInfos[2])){
            
        }else if(Streamer.copyFile(backgIm, new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"background."+Streamer.getEnd(backgIm)), true)){
            Main.setInfos[2] = labelBackground.getText();
        }else{
            g.writeLine(Main.language[48]+": "+Streamer.getCopyFehler());
            Main.setInfos[2] = "";
        }
        
        g.writeLine(Main.language[39]+".");

        Streamer.datenSchreiben(Streamer.stringParsen("recL:"+Main.setInfos[0]+"\r\nlan:"+Main.setInfos[1]+"\r\nbackgr:"+Main.setInfos[2]), settings, true, false);
        close();
    } // end of buttonOverride_ActionPerformed
    public void close(){
        dispose();
        g.setEnabled(true);
        g.toFront();
    } // end of buttonCancel_ActionPerformed

    private void browseBackground_ActionPerformed(ActionEvent evt){
        JFileChooser chooser = new JFileChooser();
        FileFilter fil = new FileNameExtensionFilter(Main.language[46]+" (*.jpeg, *.jpg, *.png, *.gif)", "gif", "jpg", "jpeg", "png");
        /*FileFilter fil = new FileFilter(){
                @Override public boolean accept(File file){
                    return file.isDirectory() || file.getName().equalsIgnoreCase("Synchronisation_update.jar");
                }

                @Override public String getDescription(){
                    return "\"Synchronisation_Update.jar\"-"+Main.language[41];
                }
            };*/
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(fil);
        if(chooser.showDialog(null, Main.language[47]) == JFileChooser.APPROVE_OPTION){
            //File src = chooser.getSelectedFile();
            backgIm = chooser.getSelectedFile();
            labelBackground.setText(backgIm.getName());
            checkboxBackground.setState(true);
            /*if(library.streaming.Streamer.copyFile(src, new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"background"+library.streaming.Streamer.getEnd(src)), true)){
                
            }else{
                
            }*/
        }
    } // end of browseBackground_ActionPerformed

    // Ende Methoden
} // end of class PrefGUI

import java.awt.*;
import java.awt.event.*;
import javax.swing.JFileChooser;
import javax.swing.SpringLayout;
import javax.swing.Spring;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import library.lists.Liste;
import library.streaming.Streamer;

/**
 *
 * Dies ist das GUI.
 *
 * @version 1.0 vom 21. 06. 15
 * @author Leon Haffmans
 */

public class GUI extends Frame {
    // Anfang Attribute
    private MenuBar menuBar;
    private PopupMenu popup;
    private Menu menuDatei;
    private MenuItem menuDateiMenuItemSourceverzeichnisOeffnen;
    private MenuItem menuDateiMenuItemZielverzeichnisOeffnen;
    private MenuItem menuDateiMenuItemAkt;
    private Menu menuRecent;
    private Menu menuHelp;
    private MenuItem menuHelpMenuItemHilfe;
    private MenuItem menuHelpMenuItemBeschreibung;
    private Menu menuOption;
    private MenuItem menuOptionMenuItemOptions;
    private Choice choiceSrc;
    private Label label1;
    private Label label2;
    private Choice choiceZiel;
    private Button buttonBrowseSrc;
    private Button buttonBrowseZiel;
    private Button buttonSync;
    private TextArea ausgabe;
    private Button buttonAbbruch;
    private Button buttonLog;
    private Label labelProcess;
    private Label label3;
    private Label label4;
    private Label labelTime;
    private Button buttonScannen;
    private List listSource;
    private List listZiel;
    private TextArea textAreaSource;
    private TextArea textAreaZiel;
    private final Image background;
    private Panel cp;

    private WindowAdapter wa = null;
    private boolean abb = false;
    private String logText = "";
    private Liste<String[]> rec = null;
    private File[] lasts = null;
    private int recSize = 0;
    private final int recentLength;
    private boolean logActivated = true;
    private float fort = 0;
    private long folderSizes = -1;
    private long timeStart = -1;
    boolean a = false; //siehe addToList(...)
    // Ende Attribute

    /**
     * Default-GUI (ohne Parameterangaben)
     */
    public GUI() {
        this(null);
    } // end of public GUI

    /**
     * Der Parameter enthält einen String-array mit maximal zwei Strings. Dies sind die Ordnerpfade, die dem Programm (main-Methode) als Parameter übergeben werden müssen.
     * Im Konstruktor wird u.a. die Recent-datei ausgelesen (sofern sie vorhanden ist) und wird zusammen mit den Parametern dem Menü hinzugefügt.
     */
    public GUI(String[] toSync){
        // Frame-Initialisierung
        super(Main.nameOfProgram);
        wa = new WindowAdapter() {
            public void windowClosing(WindowEvent evt) { System.exit(0); }
        };
        addWindowListener(wa);
        int frameWidth = 1134; 
        int frameHeight = 623;
        setSize(frameWidth, frameHeight);
        //System.out.println("Original Size (w, h): "+frameWidth+", "+frameHeight);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (d.width - getSize().width) / 2;
        int y = (d.height - getSize().height) / 2;
        setLocation(x, y);
        setResizable(false);
        
        cp = new Panel(null){public void paint(Graphics g){if(background != null) g.drawImage(background, 0, 0, cp.getWidth(), cp.getHeight(), cp);}};
        add(cp);
        String im = System.getProperty("user.dir")+System.getProperty("file.separator")+"background";
        if(new File(im+".jpg").exists()) im+=".jpg";
        else if(new File(im+".jpeg").exists()) im+=".jpeg";
        else if(new File(im+".png").exists()) im+=".png";
        else if(new File(im+".gif").exists()) im+=".gif";
        else im = null;
        
        if(im != null){
            background = getToolkit().getImage(im);
            MediaTracker mt = new MediaTracker(cp);
            mt.addImage(background, 0);
            try{
                mt.waitForAll();
            }catch(InterruptedException e){}
        }else
            background = null;
        
        int r = 15;
        try{
            r = Integer.parseInt(Main.setInfos[0]);
        }catch(NumberFormatException e){}
        recentLength = r;
        // Anfang Komponenten
        
        ausgabe = new TextArea("", 1, 1, TextArea.SCROLLBARS_BOTH);
        menuBar = new MenuBar();
        popup = new PopupMenu();
        menuDatei = new Menu(Main.language[0]);
        menuDateiMenuItemSourceverzeichnisOeffnen = new MenuItem(Main.language[1]);
        menuDateiMenuItemZielverzeichnisOeffnen = new MenuItem(Main.language[2]);
        menuDateiMenuItemAkt = new MenuItem(Main.language[3]);
        menuRecent = new Menu(Main.language[4]);
        menuHelp = new Menu(Main.language[5]);
        menuHelpMenuItemHilfe = new MenuItem(Main.language[5]);
        menuHelpMenuItemBeschreibung = new MenuItem(Main.language[6]);
        menuOption = new Menu(Main.language[35]);
        menuOptionMenuItemOptions = new MenuItem(Main.language[35]);
        choiceSrc = new Choice();
        label1 = new Label();
        label2 = new Label();
        choiceZiel = new Choice();
        buttonBrowseSrc = new Button();
        buttonBrowseZiel = new Button();
        buttonSync = new Button();
        buttonAbbruch = new Button();
        buttonLog = new Button();
        labelProcess = new Label();
        label3 = new Label();
        label4 = new Label();
        labelTime = new Label();
        buttonScannen = new Button();
        listSource = new List();
        listZiel = new List();
        textAreaSource = new TextArea("", 1, 1, TextArea.SCROLLBARS_BOTH);
        textAreaZiel = new TextArea("", 1, 1, TextArea.SCROLLBARS_BOTH);

        add(popup);
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        cp.addMouseListener(new MouseAdapter(){
                public void mouseReleased(MouseEvent evt){
                    if(evt.isPopupTrigger())
                        popup.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            });
        setMenuBar(menuBar);
        //popup.add(menuDatei);
        menuBar.add(menuDatei);
        menuDateiMenuItemSourceverzeichnisOeffnen.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent evt){
                    sourceOeffnenBrowse(evt);
                }
            });

        menuDateiMenuItemZielverzeichnisOeffnen.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent evt){
                    zielOeffnenBrowse(evt);
                }
            });

        menuDateiMenuItemAkt.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent evt){
                    aktualisieren(evt);
                }
            });

        menuDatei.add(menuDateiMenuItemAkt);
        menuDatei.addSeparator();
        menuDatei.add(menuDateiMenuItemSourceverzeichnisOeffnen);
        menuDatei.add(menuDateiMenuItemZielverzeichnisOeffnen);
        menuDatei.addSeparator();
        menuDatei.add(menuRecent);
        
        menuBar.add(menuOption);
        //popup.add(menuOption);
        menuOption.add(menuOptionMenuItemOptions);
        menuOptionMenuItemOptions.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent evt){
                    options(evt);
                }
            });
        
        menuBar.add(menuHelp);
        //popup.add(menuHelp);
        menuHelpMenuItemHilfe.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent evt){
                    hilfe(evt);
                }
            });

        menuHelpMenuItemBeschreibung.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent evt){
                    beschreibung(evt);
                }
            });

        menuHelp.add(menuHelpMenuItemHilfe);
        menuHelp.add(menuHelpMenuItemBeschreibung);

        label1.setBounds(32, 16, 231, 20);
        label1.setText(Main.language[7]+":");
        //label1.setOpaque(false);
        cp.add(label1);
        choiceSrc.setBounds(32, 40, 398, 23);
        choiceSrc.setFont(new Font("Arial Narrow", Font.PLAIN, 14));
        cp.add(choiceSrc);
        buttonBrowseSrc.setBounds(440, 40, 27, 23);
        buttonBrowseSrc.setLabel("...");
        buttonBrowseSrc.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    sourceOeffnenBrowse(evt);
                }
            });
        buttonBrowseSrc.setFont(new Font("Arial Narrow", Font.PLAIN, 20));
        cp.add(buttonBrowseSrc);
        label2.setBounds(480, 16, 214, 20);
        label2.setText(Main.language[8]+":");
        cp.add(label2);
        labelProcess.setBounds(480, 80, 134, 28);
        labelProcess.setText("");
        labelProcess.setFont(new Font("Arial", Font.PLAIN, 14));
        cp.add(labelProcess);
        label3.setBounds(360, 80, 78, 28);
        label3.setText(Main.language[9]+":");
        label3.setFont(new Font("Arial", Font.PLAIN, 14));
        cp.add(label3);
        label4.setBounds(360, 104, 118, 28);
        label4.setText(Main.language[10]+":");
        label4.setFont(new Font("Arial", Font.PLAIN, 14));
        cp.add(label4);
        labelTime.setBounds(480, 104, 134, 28);
        labelTime.setText("");
        setFortschritt(100);
        labelTime.setFont(new Font("Arial", Font.PLAIN, 14));
        cp.add(labelTime);
        choiceZiel.setBounds(480, 40, 398, 23);
        choiceZiel.setFont(new Font("Arial Narrow", Font.PLAIN, 14));
        cp.add(choiceZiel);
        buttonBrowseZiel.setBounds(888, 40, 27, 23);
        buttonBrowseZiel.setLabel("...");
        buttonBrowseZiel.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    zielOeffnenBrowse(evt);
                }
            });
        buttonBrowseZiel.setFont(new Font("Arial Narrow", Font.PLAIN, 20));
        cp.add(buttonBrowseZiel);
        buttonSync.setBounds(144, 88, 203, 33);
        buttonSync.setLabel(Main.language[11]);
        final GUI g = this;
        buttonSync.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    Main.start(g,evt,false);
                }
            });
        buttonSync.setFont(new Font("Arial", Font.PLAIN, 20));
        cp.add(buttonSync);
        ausgabe.setBounds(32, 136, 576, 396);
        ausgabe.setEditable(false);
        cp.add(ausgabe);
        buttonAbbruch.setBounds(440, 536, 163, 33);
        buttonAbbruch.setLabel(Main.language[12]);
        buttonAbbruch.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    abbrechen(evt);
                }
            });
        buttonAbbruch.setFont(new Font("Arial", Font.PLAIN, 16));
        buttonAbbruch.setEnabled(false);
        cp.add(buttonAbbruch);
        buttonLog.setBounds(32, 536, 163, 33);
        buttonLog.setLabel(Main.language[13]);
        buttonLog.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    buttonLog_ActionPerformed(evt);
                }
            });
        buttonLog.setFont(new Font("Arial", Font.PLAIN, 16));
        buttonLog.setEnabled(false);
        cp.add(buttonLog);
        buttonScannen.setBounds(32, 88, 107, 33);
        buttonScannen.setLabel(Main.language[14]);
        buttonScannen.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent evt) { 
                    Main.start(g,evt,true);
                }
            });
        buttonScannen.setFont(new Font("Arial", Font.PLAIN, 18));
        cp.add(buttonScannen);
        listSource.setBounds(616, 80, 230, 332);
        listSource.setFont(new Font("Arial", Font.PLAIN, 12));
        listSource.setMultipleMode(false);
        cp.add(listSource);
        listZiel.setBounds(856, 80, 230, 332);
        listZiel.setFont(new Font("Arial", Font.PLAIN, 12));
        listZiel.setMultipleMode(false);
        cp.add(listZiel);
        textAreaSource.setBounds(616, 424, 230, 148);
        textAreaSource.setEditable(false);
        cp.add(textAreaSource);
        textAreaZiel.setBounds(856, 424, 230, 148);
        textAreaZiel.setEditable(false);
        cp.add(textAreaZiel);

        File recent = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"recent");
        if(recent.exists() && !recent.isDirectory()){
            byte[] seq = {(byte)'\r', (byte)'\n'};
            Liste<byte[]> dat = Synchronizer.split(seq, Streamer.datenAuslesen(recent));
            rec = new Liste<>();//rec[0] -> Src-Filenames, rec[1] -> Ziel-Filenames
            dat.toFirst();
            recSize = (recentLength-1)*2;//\r\n...
            int last = 0;
            if(dat.getCurObject().length > 0){
                for(int i=0; i<dat.length(); i++){
                    String s = Streamer.bytearrayParsen(dat.getCurObject());
                    //if(i!=recentLength-1) recSize+=2;
                    if(s != null && !s.equals("")){
                        recSize += s.length();
                        String[] ss = s.split("/t");
                        String s0 = replace(ss[0],false), s1 = replace(ss[1],false);
                        //rec[0][i] = ss[0];
                        //rec[1][i] = ss[1];
                        rec.hintersLetzteEinfuegen(ss);
                        //ss[0] = replace(ss[0], false);
                        //ss[1] = replace(ss[1], false);
                        dat.next();

                        MenuItem it = new MenuItem("\""+s0+"\" "+Main.language[40]+" \""+s1+"\"");
                        final int varI = i;
                        it.addActionListener(new ActionListener() { 
                                public void actionPerformed(ActionEvent evt) { 
                                    recent(evt, varI);
                                }
                            });
                        menuRecent.add(it);
                        if(!existsIn(choiceSrc,s0)) choiceSrc.add(s0);
                        if(!existsIn(choiceZiel,s1)) choiceZiel.add(s1);
                        last = i;
                    }
                }
                rec.goTo(last);
                String[] arr = rec.getCurObject();
                choiceSrc.select(replace(arr[0], false));
                choiceZiel.select(replace(arr[1], false));
            }else{
                recSize = (recentLength-1)*2;
            }
        }else{
            try{if(recent.createNewFile()) rec = new Liste<>();recSize = (recentLength-1)*2;}catch(Exception e){}
        }
        lasts = new File[2];
        if(toSync != null && toSync.length != 0){
            if(toSync.length == 1){
                boolean inChoice = false, s = true;
                for(int i=0; i<choiceSrc.getItemCount() || i<choiceZiel.getItemCount(); i++){
                    if(choiceSrc.getItem(i).equals(toSync[0])){
                        inChoice = true;
                        i = choiceSrc.getItemCount();
                    }else if(choiceZiel.getItem(i).equals(toSync[0])){
                        inChoice = true;
                        s = false;
                        i = choiceSrc.getItemCount();
                    }
                }
                if(!inChoice) choiceSrc.add(toSync[0]);
                if(s) choiceSrc.select(toSync[0]);
                else choiceZiel.select(toSync[0]);
            }else{
                byte whatCase1 = 0, whatCase2 = 0;
                /*
                 * case1 0: toSync[0] nicht vorhanden
                 * case1 1: toSync[0] in Src
                 * case1 2: toSync[0] in Ziel
                 * <=> zu case2
                 */
                for(int i=0; i<choiceSrc.getItemCount(); i++){
                    if(choiceSrc.getItem(i).equals(toSync[0]))
                        whatCase1 = 1;
                    if(choiceZiel.getItem(i).equals(toSync[0]))
                        whatCase1 = 2;
                    if(choiceSrc.getItem(i).equals(toSync[1]))
                        whatCase2 = 1;
                    if(choiceZiel.getItem(i).equals(toSync[1]))
                        whatCase2 = 2;
                }
                if(whatCase1 == 0){
                    if(whatCase2 != 1){
                        choiceSrc.add(toSync[0]);
                        whatCase1 = 1;
                    }else{
                        choiceZiel.add(toSync[0]);
                        whatCase1 = 2;
                    }
                }
                if(whatCase2 == 0){
                    if(whatCase1 != 2){
                        choiceZiel.add(toSync[1]);
                        whatCase2 = 2;
                    }else{
                        choiceSrc.add(toSync[1]);
                        whatCase2 = 1;
                    }
                }
                if(whatCase1 == 1)
                    choiceSrc.select(toSync[0]);
                else
                    choiceZiel.select(toSync[0]);
                if(whatCase2 == 1)
                    choiceSrc.select(toSync[1]);
                else
                    choiceZiel.select(toSync[1]);
            }
        }
        //createSpringLayout();
        // Ende Komponenten
        
        //cp.repaint();

        setVisible(true);
    }
    /*public void paintAll(Graphics g){
        g.drawImage(background, 0, 0, cp);
    }*/

    // Anfang Methoden
    private void createSpringLayout2(){
        SpringLayout l = new SpringLayout();
        cp.setLayout(l);
        
        //Button eastSide = new Button();
        //System.out.println("Size return (w, h): "+getSize().width+", "+getSize().height);
        //eastSide.setLocation(cp.getSize().width, cp.getSize().height/2);
        //cp.add(eastSide);
        //l.putConstraint(SpringLayout.EAST, cp, 0, SpringLayout.EAST, eastSide);
        //l.putConstraint(SpringLayout.EAST, eastSide, 0, SpringLayout.WEST, eastSide);
        
        Spring
        windToCompX = Spring.constant(32),
        windToCompY = Spring.constant(16),
        constDistX = Spring.constant(13),
        constDistY = Spring.constant(4),
        browseWidth = Spring.constant(27),
        firstHeight = Spring.constant(23),
        distY2 = Spring.constant(14);
        
        //x-Springs, 1. Zeile
        l.putConstraint(SpringLayout.WEST, cp, windToCompX, SpringLayout.WEST, label1);
        l.putConstraint(SpringLayout.EAST, label1, constDistX, SpringLayout.WEST, label2);
        l.putConstraint(SpringLayout.EAST, label2, windToCompX, SpringLayout.EAST, cp);
        //ENDE
        //y-Springs, 1. Zeile
        /*l.putConstraint(SpringLayout.NORTH, label1, windToCompY, SpringLayout.NORTH, cp);
        l.putConstraint(SpringLayout.NORTH, label2, windToCompY, SpringLayout.NORTH, cp);
        l.putConstraint(SpringLayout.SOUTH, label1, firstHeight, SpringLayout.NORTH, label1);
        l.putConstraint(SpringLayout.SOUTH, label2, firstHeight, SpringLayout.NORTH, label2);
        //ENDE
        //x-Springs, 2. Zeile
        l.putConstraint(SpringLayout.WEST, choiceSrc, windToCompX, SpringLayout.WEST, cp);
        l.putConstraint(SpringLayout.WEST, buttonBrowseSrc, constDistX, SpringLayout.EAST, choiceSrc);
        l.putConstraint(SpringLayout.EAST, buttonBrowseSrc, browseWidth, SpringLayout.WEST, buttonBrowseSrc);
        l.putConstraint(SpringLayout.WEST, choiceZiel, constDistX, SpringLayout.EAST, buttonBrowseSrc);
        l.putConstraint(SpringLayout.WEST, buttonBrowseZiel, constDistX, SpringLayout.EAST, choiceZiel);
        l.putConstraint(SpringLayout.EAST, buttonBrowseZiel, browseWidth, SpringLayout.WEST, buttonBrowseZiel);
        l.putConstraint(SpringLayout.WEST, eastSide, windToCompX, SpringLayout.EAST, buttonBrowseZiel);
        //l.putConstraint(SpringLayout.EAST, label2, 1, SpringLayout.EAST, buttonBrowseZiel);
        //ENDE
        //y-Springs, 2. Zeile
        l.putConstraint(SpringLayout.NORTH, choiceSrc, constDistY, SpringLayout.SOUTH, label1);
        l.putConstraint(SpringLayout.NORTH, buttonBrowseSrc, constDistY, SpringLayout.SOUTH, label1);
        l.putConstraint(SpringLayout.NORTH, choiceZiel, constDistY, SpringLayout.SOUTH, label2);
        l.putConstraint(SpringLayout.NORTH, buttonBrowseZiel, constDistY, SpringLayout.SOUTH, label2);
        l.putConstraint(SpringLayout.SOUTH, choiceSrc, firstHeight, SpringLayout.NORTH, choiceSrc);
        l.putConstraint(SpringLayout.SOUTH, buttonBrowseSrc, firstHeight, SpringLayout.NORTH, buttonBrowseSrc);
        l.putConstraint(SpringLayout.SOUTH, choiceZiel, firstHeight, SpringLayout.NORTH, choiceZiel);
        l.putConstraint(SpringLayout.SOUTH, buttonBrowseZiel, firstHeight, SpringLayout.NORTH, buttonBrowseZiel);
        l.putConstraint(SpringLayout.NORTH, buttonScannen, distY2, SpringLayout.SOUTH, choiceSrc);
        l.putConstraint(SpringLayout.NORTH, buttonSync, distY2, SpringLayout.SOUTH, choiceSrc);
        l.putConstraint(SpringLayout.NORTH, label3, distY2, SpringLayout.SOUTH, choiceSrc);
        l.putConstraint(SpringLayout.NORTH, labelProcess, distY2, SpringLayout.SOUTH, choiceSrc);
        l.putConstraint(SpringLayout.NORTH, listSource, distY2, SpringLayout.SOUTH, choiceSrc);
        l.putConstraint(SpringLayout.NORTH, listZiel, distY2, SpringLayout.SOUTH, choiceSrc);
        //ENDE
        //x-Springs, 3. Zeile
        l.putConstraint(SpringLayout.WEST, buttonScannen, windToCompX, SpringLayout.WEST, cp);
        l.putConstraint(SpringLayout.WEST, buttonSync, 5, SpringLayout.EAST, buttonScannen);
        l.putConstraint(SpringLayout.WEST, label3, 13, SpringLayout.EAST, buttonSync);
        l.putConstraint(SpringLayout.WEST, labelProcess, 42, SpringLayout.EAST, label3);
        l.putConstraint(SpringLayout.WEST, label4, 13, SpringLayout.EAST, buttonSync);
        l.putConstraint(SpringLayout.WEST, labelTime, 2, SpringLayout.EAST, label4);
        l.putConstraint(SpringLayout.WEST, listSource, 2, SpringLayout.EAST, labelProcess);
        l.putConstraint(SpringLayout.WEST, listSource, 2, SpringLayout.EAST, labelTime);
        l.putConstraint(SpringLayout.WEST, listZiel, 10, SpringLayout.EAST, listSource);
        l.putConstraint(SpringLayout.WEST, eastSide, 48, SpringLayout.EAST, listZiel);*/
        //l.putConstraint(SpringLayout.EAST, listZiel, 0, SpringLayout.EAST, buttonBrowseZiel);
        //ENDE
    }
    private void createSpringLayout(){
        SpringLayout l = new SpringLayout();
        cp.setLayout(l);
        
        Button eastSide = new Button();
        //System.out.println("Size return (w, h): "+getSize().width+", "+getSize().height);
        eastSide.setLocation(cp.getSize().width, cp.getSize().height/2);
        //cp.add(eastSide);
        l.putConstraint(SpringLayout.EAST, cp, 0, SpringLayout.EAST, eastSide);
        l.putConstraint(SpringLayout.EAST, eastSide, 0, SpringLayout.WEST, eastSide);
        
        Spring windToCompX = Spring.constant(32),
        windToCompY = Spring.constant(16),
        constDistX = Spring.constant(13),
        constDistY = Spring.constant(4),
        browseWidth = Spring.constant(27),
        //labelsWidth = Spring.constant(231, 500, 100000),
        firstHeight = Spring.constant(23),
        distY2 = Spring.constant(14);
        
        //x-Springs, 1. Zeile
        l.putConstraint(SpringLayout.WEST, label1, windToCompX, SpringLayout.WEST, cp);
        //l.putConstraint(SpringLayout.EAST, label1, labelsWidth, SpringLayout.WEST, label1);
        l.putConstraint(SpringLayout.WEST, label2, constDistX, SpringLayout.EAST, label1);
        //l.putConstraint(SpringLayout.EAST, label2, labelsWidth, SpringLayout.WEST, label2);
        l.putConstraint(SpringLayout.WEST, eastSide, windToCompX, SpringLayout.EAST, label2);
        //l.putConstraint(SpringLayout.EAST, cp, Spring.constant(0), SpringLayout.EAST, label2);
        //l.putConstraint(SpringLayout.EAST, label2, windToCompX, SpringLayout.EAST, cp);
        //ENDE
        //y-Springs, 1. Zeile
        l.putConstraint(SpringLayout.NORTH, label1, windToCompY, SpringLayout.NORTH, cp);
        l.putConstraint(SpringLayout.NORTH, label2, windToCompY, SpringLayout.NORTH, cp);
        l.putConstraint(SpringLayout.SOUTH, label1, firstHeight, SpringLayout.NORTH, label1);
        l.putConstraint(SpringLayout.SOUTH, label2, firstHeight, SpringLayout.NORTH, label2);
        //ENDE
        //x-Springs, 2. Zeile
        l.putConstraint(SpringLayout.WEST, choiceSrc, windToCompX, SpringLayout.WEST, cp);
        l.putConstraint(SpringLayout.WEST, buttonBrowseSrc, constDistX, SpringLayout.EAST, choiceSrc);
        l.putConstraint(SpringLayout.EAST, buttonBrowseSrc, browseWidth, SpringLayout.WEST, buttonBrowseSrc);
        l.putConstraint(SpringLayout.WEST, choiceZiel, constDistX, SpringLayout.EAST, buttonBrowseSrc);
        l.putConstraint(SpringLayout.WEST, buttonBrowseZiel, constDistX, SpringLayout.EAST, choiceZiel);
        l.putConstraint(SpringLayout.EAST, buttonBrowseZiel, browseWidth, SpringLayout.WEST, buttonBrowseZiel);
        //l.putConstraint(SpringLayout.EAST, label2, windToCompX, SpringLayout.EAST, buttonBrowseZiel);
        //l.putConstraint(SpringLayout.EAST, label2, 1, SpringLayout.EAST, buttonBrowseZiel);
        //ENDE
        //y-Springs, 2. Zeile
        l.putConstraint(SpringLayout.NORTH, choiceSrc, constDistY, SpringLayout.SOUTH, label1);
        l.putConstraint(SpringLayout.NORTH, buttonBrowseSrc, constDistY, SpringLayout.SOUTH, label1);
        l.putConstraint(SpringLayout.NORTH, choiceZiel, constDistY, SpringLayout.SOUTH, label2);
        l.putConstraint(SpringLayout.NORTH, buttonBrowseZiel, constDistY, SpringLayout.SOUTH, label2);
        l.putConstraint(SpringLayout.SOUTH, choiceSrc, firstHeight, SpringLayout.NORTH, choiceSrc);
        l.putConstraint(SpringLayout.SOUTH, buttonBrowseSrc, firstHeight, SpringLayout.NORTH, buttonBrowseSrc);
        l.putConstraint(SpringLayout.SOUTH, choiceZiel, firstHeight, SpringLayout.NORTH, choiceZiel);
        l.putConstraint(SpringLayout.SOUTH, buttonBrowseZiel, firstHeight, SpringLayout.NORTH, buttonBrowseZiel);
        l.putConstraint(SpringLayout.NORTH, buttonScannen, distY2, SpringLayout.SOUTH, choiceSrc);
        l.putConstraint(SpringLayout.NORTH, buttonSync, distY2, SpringLayout.SOUTH, choiceSrc);
        l.putConstraint(SpringLayout.NORTH, label3, distY2, SpringLayout.SOUTH, choiceSrc);
        l.putConstraint(SpringLayout.NORTH, labelProcess, distY2, SpringLayout.SOUTH, choiceSrc);
        l.putConstraint(SpringLayout.NORTH, listSource, distY2, SpringLayout.SOUTH, choiceSrc);
        l.putConstraint(SpringLayout.NORTH, listZiel, distY2, SpringLayout.SOUTH, choiceSrc);
        //ENDE
        //x-Springs, 3. Zeile
        l.putConstraint(SpringLayout.WEST, buttonScannen, windToCompX, SpringLayout.WEST, cp);
        l.putConstraint(SpringLayout.WEST, buttonSync, 5, SpringLayout.EAST, buttonScannen);
        l.putConstraint(SpringLayout.WEST, label3, 13, SpringLayout.EAST, buttonSync);
        l.putConstraint(SpringLayout.WEST, labelProcess, 42, SpringLayout.EAST, label3);
        l.putConstraint(SpringLayout.WEST, label4, 13, SpringLayout.EAST, buttonSync);
        l.putConstraint(SpringLayout.WEST, labelTime, 2, SpringLayout.EAST, label4);
        l.putConstraint(SpringLayout.WEST, listSource, 2, SpringLayout.EAST, labelProcess);
        l.putConstraint(SpringLayout.WEST, listSource, 2, SpringLayout.EAST, labelTime);
        l.putConstraint(SpringLayout.WEST, listZiel, 10, SpringLayout.EAST, listSource);
        //l.putConstraint(SpringLayout.WEST, eastSide, 48, SpringLayout.EAST, listZiel);
        //l.putConstraint(SpringLayout.EAST, listZiel, 0, SpringLayout.EAST, buttonBrowseZiel);
        //ENDE
    }
    
    /**
     * Dies dient dazu, keine Probleme mit Umlauten zu bekommen.
     */
    private static String replace(String string, boolean toDatFormat){
        if(toDatFormat){
            String s = string;
            s = s.replaceAll("ä", "/ae");
            s = s.replaceAll("ö", "/oe");
            s = s.replaceAll("ü", "/ue");
            return s;
        }else{
            String s = string;
            s = s.replaceAll("/ae", "ä");
            s = s.replaceAll("/oe", "ö");
            s = s.replaceAll("/ue", "ü");
            return s;
        }
    }

    /**
     * Gibt den Fortschritt als Prozentzahl zurück.
     */
    public float getFortschritt(){
        return fort;
    }

    /**
     * Setzt den Fortschritt auf den übergebenen und rundet diesen auf zwei Nachkommastellen.
     */
    public void setFortschritt(float percent){
        //System.out.println(""+percent);
        if(percent>=0 && percent<=100){
            fort = percent;
            labelProcess.setText(Math.round(fort*100)/100+"%");
        }
    }

    /**
     * Gibt die Größe der beiden Verzeichnisse zurück, die momentan synchronisiert werden.
     */
    public long getFolderSizes(){
        return folderSizes;
    }

    /**
     * Gibt den Anfangszeitpunkt des Synchronisierens zurück (Dient der Zeitberechnung der Restdauer).
     */
    public long getTimeStart(){
        return timeStart;
    }

    /**
     * Diese Methode berechnet die vermutliche Restdauer des Prozesses.
     */
    public void berechneZeit(double geschwByteProMilli, long newTimeStart){
        long milli = (long)((long)folderSizes/geschwByteProMilli);
        if(milli < 60000)
            labelTime.setText("ca. "+(int)(milli/1000)+"s");
        else
            labelTime.setText("ca. "+(int)(milli/60000)+"min");
        timeStart = newTimeStart;
    }

    /**
     * Schreibt eine Zeile ins Textarea. Wird außerdem dem log-Text hinzugefügt.
     */
    public synchronized void writeLine(String line){
        ausgabe.append(line+"\n");
        logText += line+"\r\n";
    }

    /**
     * Funktioniert wie die Methode "printStackTrace()", nur schreibt sie die Informationen in's Textarea.
     */
    public synchronized void writeException(Exception e){
        String fehler = e.getMessage()+"\r\n"+e.toString();
        StackTraceElement[] st = e.getStackTrace();
        for(int i=0; i<st.length; i++){
            fehler+="\r\n\tat "+st[i].toString();
        }
        writeLine("Error: \r\n"+fehler);
    }
    
    public synchronized void addToList(String s){
        if(a){
            listSource.add(s);
        }else{
            listZiel.add(s);
        }
    }
    public synchronized void addToArea(String line){
        if(a){
            textAreaSource.append(line+"\n");
        }else{
            textAreaZiel.append(line+"\n");
        }
    }

    /**
     * Gibt zurück, ob der Vorgang abgebrochen wurde.
     */
    public boolean abbruch(){
        return abb;
    }

    /**
     * Bricht den Vorgang ab (Abbrechen wurde gedrückt).
     */
    private void abbrechen(ActionEvent evt) {
        abb = true;
    } // end of buttonAbbruch_ActionPerformed

    private boolean existsIn(Choice ch, String s){
        for(int i=0; i<ch.getItemCount(); i++){
            if(ch.getItem(i).equals(s)){
                return true;
            }
        }
        return false;
    }

    private boolean existsIn(Menu m, String s){
        for(int i=0; i<m.getItemCount(); i++){
            if(m.getItem(i).getLabel().equals(s)){
                return true;
            }
        }
        return false;
    }

    private void recent(ActionEvent evt, int index){
        rec.goTo(index);
        String[] s = rec.getCurObject();
        choiceSrc.select(replace(s[0],false));
        choiceZiel.select(replace(s[1],false));
        listSource.removeAll();
        listZiel.removeAll();
        textAreaSource.setText("");
        textAreaZiel.setText("");
    }

    /**
     * Wenn eine Datei mit dem Namen "Update_installer.jar" ausgewählt wird, wird das Programm aktualisiert.
     */
    private void aktualisieren(ActionEvent evt){
        JFileChooser chooser = new JFileChooser();
        //FileFilter fil = new FileNameExtensionFilter("Aktualisierungsdatei", "jar");
        FileFilter fil = new FileFilter(){
                @Override public boolean accept(File file){
                    return file.isDirectory() || file.getName().equalsIgnoreCase("Synchronisation_update.jar");
                }

                @Override public String getDescription(){
                    return "\"Synchronisation_Update.jar\"-"+Main.language[41];
                }
            };
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(fil);
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            File f = chooser.getSelectedFile();
            //if(f.getName().equalsIgnoreCase("Update_installer.jar")){
            String s = f.getAbsolutePath();
            String cmd = "java -jar \""+s+"\" \""+System.getProperty("user.dir")+"\" \"update the program\"";
            writeLine(Main.language[15]+"...");
            try{
                Runtime.getRuntime().exec(cmd);
                System.exit(0);
            }catch(Exception e){
                writeLine(Main.language[16]+":");
                writeException(e);
            }
            //}
            //Hier Aktualisierungsvorgang
        }
    }

    private void sourceOeffnenBrowse(ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            File src = chooser.getSelectedFile();
            if(!existsIn(choiceSrc, src.getAbsolutePath()))
                choiceSrc.add(src.getAbsolutePath());
            choiceSrc.select(src.getAbsolutePath());
            listSource.removeAll();
            listZiel.removeAll();
            textAreaSource.setText("");
            textAreaZiel.setText("");
        }
    } // end of menuDateiMenuItemSourceverzeichnis�ffnen_ActionPerformed
    private void zielOeffnenBrowse(ActionEvent evt) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
            File ziel = chooser.getSelectedFile();
            if(!existsIn(choiceZiel, ziel.getAbsolutePath()))
                choiceZiel.add(ziel.getAbsolutePath());
            choiceZiel.select(ziel.getAbsolutePath());
            listSource.removeAll();
            listZiel.removeAll();
            textAreaSource.setText("");
            textAreaZiel.setText("");
        }
    } // end of menuDateiMenuItemZielverzeichnis�ffnen_ActionPerformed
    private void hilfe(ActionEvent evt) {
        try{
            Desktop.getDesktop().open(new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"help.txt"));
        }catch(IOException e){
            writeLine(Main.language[17]+":\r\n\t"+e.getMessage());
        }catch(IllegalArgumentException e2){
            writeLine(Main.language[17]+":\r\n\t"+e2.getMessage());
        }
    } // end of menuHelpMenuItemHilfe_ActionPerformed
    private void options(ActionEvent evt){
        new PrefGUI(this);
    }
    /**
     * Öffnet die readme-Datei.
     */
    private void beschreibung(ActionEvent evt) {
        try{
            Desktop.getDesktop().open(new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"readme.txt"));
        }catch(IOException e){
            writeLine(Main.language[17]+":\r\n\t"+e.getMessage());
        }catch(IllegalArgumentException e2){
            writeLine(Main.language[17]+":\r\n\t"+e2.getMessage());
        }
    } // end of beschreibung
    public void scannen(ActionEvent evt) {
        unready();
        ausgabe.setText(Main.language[18]+".\n");
        listSource.removeAll();
        listZiel.removeAll();
        String srcS = choiceSrc.getSelectedItem(), zielS = choiceZiel.getSelectedItem();
        if(srcS != null && !srcS.equals("") && zielS != null && !zielS.equals("")){
            File srcF = null, zielF = null;
            try{
                srcF = new File(srcS);
                zielF = new File(zielS);
                //lasts[0] = srcF;
                //lasts[1] = zielF;
            }catch(Exception e){
                writeLine(Main.language[19]+".");
                logActivated = false;
                try{
                    (new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"recent")).delete();
                }catch(Exception e2){
                    writeLine(Main.language[20]+": "+e2.getMessage());
                }
            }
            if(srcF != null && zielF != null){
                a = true;
                Liste<String> inf1 = Synchronizer.folderInformation(srcF, this);
                a = false;
                Liste<String> inf2 = Synchronizer.folderInformation(zielF, this);
                if(inf1 != null && inf2 != null){
                    inf1.goTo(1);
                    inf2.goTo(1);
                    long folderSize1 = Long.parseLong(inf1.getCurObject()), folderSize2 = Long.parseLong(inf2.getCurObject());
                    int folderCount1 = Integer.parseInt(inf1.getFirstObject()), folderCount2 = Integer.parseInt(inf2.getFirstObject());
                    textAreaSource.setText(Main.language[21]+": "+folderCount1+" ("+folderSize1+" Bytes)");
                    textAreaZiel.setText(Main.language[21]+": "+folderCount2+" ("+folderSize2+" Bytes)");
                    inf1.next();
                    inf2.next();
                    for(int i=2; i<inf1.length() || i<inf2.length(); i++){
                        if(i<inf1.length()){
                            String cur = inf1.getCurObject(), ext = "";
                            int first = -1, last = cur.lastIndexOf('\\');
                            for(int j=0; j<cur.length(); j++){
                                if(cur.charAt(j) != '\\'){
                                    ext+=cur.charAt(j);
                                }else{
                                    first = j;
                                    j=cur.length();
                                }
                            }
                            int anz = Integer.parseInt(cur.substring(first+1, last));
                            long size = Long.parseLong(cur.substring(last+1, cur.length()));
                            if(!ext.equals("")) textAreaSource.append("\n*."+ext+"-"+Main.language[22]+": "+anz+" "+Main.language[22]+" (~"+(anz*100/folderCount1)+"%), "+size+" Bytes (~"+(size*100/folderSize1)+"%)");
                            else textAreaSource.append("\n"+Main.language[23]+": "+anz+" "+Main.language[22]+" (~"+(anz*100/folderCount1)+"%), "+size+" Bytes (~"+(size*100/folderSize1)+"%)");
                            inf1.next();
                        }
                        if(i<inf2.length()){
                            String cur = inf2.getCurObject(), ext = "";
                            int first = -1, last = cur.lastIndexOf('\\');
                            for(int j=0; j<cur.length(); j++){
                                if(cur.charAt(j) != '\\'){
                                    ext+=cur.charAt(j);
                                }else{
                                    first = j;
                                    j=cur.length();
                                }
                            }
                            int anz = Integer.parseInt(cur.substring(first+1, last));
                            long size = Long.parseLong(cur.substring(last+1, cur.length()));
                            if(!ext.equals("")) textAreaZiel.append("\n*."+ext+"-"+Main.language[22]+": "+anz+" "+Main.language[22]+" (~"+(anz*100/folderCount2)+"%), "+size+" Bytes (~"+(size*100/folderSize2)+"%)");
                            else textAreaZiel.append("\n"+Main.language[23]+": "+anz+" "+Main.language[22]+" (~"+(anz*100/folderCount2)+"%), "+size+" Bytes (~"+(size*100/folderSize2)+"%)");
                            inf2.next();
                        }
                    }
                }else if(abb) writeLine(Main.language[24]);
            }
        }else{
            writeLine(Main.language[25]);
        }
        writeLine(Main.language[26]);
        ready();
    } // end of buttonScannen_ActionPerformed
    /**
     * Startet das eigentliche Programm. Hier wird die recent-Datei aktualisiert und die Informationen der Klasse "Synchronizer" übergeben, die die Verzeichnisse synchronisiert.
     */
    public void start(ActionEvent evt) {
        unready();
        ausgabe.setText(Main.language[27]+":\n");
        setFortschritt(0);
        
        String srcL = listSource.getSelectedItem(), zielL = listZiel.getSelectedItem();
        String oc = choiceSrc.getSelectedItem(), oz = choiceZiel.getSelectedItem();
        if(oc != null && !oc.equals("") && oz != null && !oz.equals("")){
            int mode = 0;
            if(srcL == null) srcL = "";
            else{
                int ind = srcL.indexOf(System.getProperty("file.separator"));
                if(ind != -1){
                    srcL = srcL.substring(ind,srcL.lastIndexOf(" ///"));
                    mode = 1;
                }else
                    srcL = System.getProperty("file.separator");
            }
            if(zielL == null) zielL = "";
            else{
                int ind = zielL.indexOf(System.getProperty("file.separator"));
                if(ind != -1){
                    zielL = zielL.substring(ind,zielL.lastIndexOf(" ///"));
                    mode = 1;
                }else
                    zielL = System.getProperty("file.separator");
            }
            boolean run = true;
            if(!srcL.equals(zielL)){
                //run = false;
                if(JOptionPane.showConfirmDialog(null, Main.language[42], Main.nameOfProgram+" - "+Main.language[43], JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION){
                    run = false;
                    writeLine(Main.language[31]);
                }
                mode = 2;
            }
            //if(mode == 0 && !srcL.equals("")) mode = 1;
            
            if(run){
                File fsrc = null, fziel = null, spart = null, zpart = null;
                try{
                    fsrc = new File(oc);
                    fziel = new File(oz);
                    spart = new File(oc+srcL);
                    zpart = new File(oz+zielL);
                    if(mode == 2){lasts[0]=spart; lasts[1]=zpart;}
                    else{lasts[0]=fsrc; lasts[1]=fziel;}
                    //System.out.println("Files erstellt.");
                }catch(Exception e){
                    writeLine(Main.language[19]+".");
                    logActivated = false;
                    try{
                        (new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"recent")).delete();
                    }catch(Exception e2){
                        writeLine(Main.language[20]+": "+e2.getMessage());
                    }
                }
                if(fsrc != null && fziel != null && spart != null && zpart != null){
                    String newRec1 = "\""+oc+"\" "+Main.language[40]+" \""+oz+"\"", newRec2 = "\""+oz+"\" "+Main.language[40]+" \""+oc+"\"";
                    if(mode == 2){
                        newRec1 = "\""+spart.getAbsolutePath()+"\" "+Main.language[40]+" \""+zpart.getAbsolutePath()+"\"";
                        newRec2 = "\""+zpart.getAbsolutePath()+"\" "+Main.language[40]+" \""+spart.getAbsolutePath()+"\"";
                    }
                    boolean inMenu = false;
                    for(int i=0; i<menuRecent.getItemCount(); i++){
                        String s = menuRecent.getItem(i).getLabel();
                        if(s.equals(newRec1)){
                            inMenu = true;
                            i = menuRecent.getItemCount();
                        }else if(s.equals(newRec2)){
                            inMenu = true;
                            i = menuRecent.getItemCount();
                        }
                    }
                    if(!inMenu){
                        if(rec != null){
                            MenuItem i = new MenuItem(newRec1);
                            final int ind = rec.length();
                            i.addActionListener(new ActionListener() { 
                                    public void actionPerformed(ActionEvent evt) { 
                                        recent(evt, ind);
                                    }
                                });
                            menuRecent.add(i);
                            //Noch in die Recent-datei speichern!
                            String srcS2 = null, zielS2 = null;
                            if(mode == 2){
                                srcS2 = replace(spart.getAbsolutePath(), true);
                                zielS2 = replace(zpart.getAbsolutePath(), true);
                            }else{
                                srcS2 = replace(oc, true);
                                zielS2 = replace(oz, true);
                            }
    
                            if(rec.length() >= recentLength)
                                rec.removeFirstObject();
                            String[] newS = {srcS2, zielS2};
                            rec.hintersLetzteEinfuegen(newS);
                            recSize += srcS2.length()+zielS2.length()+2;//+/t
                            File recent = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"recent");
                            if(!Streamer.datenSchreiben(recIntoFile(), recent, true, false))
                                writeLine("Error: "+Main.language[44]+": "+Streamer.getSchreibeFehler());
                        }
                    }
    
                    labelTime.setText(Main.language[28]+"...");
                    writeLine(Main.language[29]+"...");
                    folderSizes = Synchronizer.folderSize(spart, this)+Synchronizer.folderSize(zpart, this);
                    timeStart = System.currentTimeMillis();
                    if(Synchronizer.synchronizeDirectories(fsrc, fziel, this, spart, zpart, mode))
                        writeLine(Main.language[30]);
                    else if(abbruch())
                        writeLine(Main.language[31]);
                    else
                        writeLine(Main.language[32]+":\r\n"+Synchronizer.getFehler());
                }
            }
        }else{
            writeLine(Main.language[25]);
        }

        /*String srcO = choiceSrc.getSelectedItem(), zielO = choiceZiel.getSelectedItem();
        String srcS = listSource.getSelectedItem(), zielS = listZiel.getSelectedItem();
        if(srcS == null || srcS.length() == 0)
            srcS = choiceSrc.getSelectedItem();
        else
            srcS = "parent"+srcS;
        if(zielS == null || zielS.length() == 0)
            zielS = choiceZiel.getSelectedItem();
        //String srcS = choiceSrc.getSelectedItem(), zielS = choiceZiel.getSelectedItem();
        if(srcS != null && !srcS.equals("") && zielS != null && !zielS.equals("")){
            File srcF = null, zielF = null;
            try{
                srcF = new File(srcS);
                zielF = new File(zielS);
                lasts[0] = srcF;
                lasts[1] = zielF;
            }catch(Exception e){
                writeLine(Main.language[19]+".");
                logActivated = false;
                try{
                    (new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"recent")).delete();
                }catch(Exception e2){
                    writeLine(Main.language[20]+": "+e2.getMessage());
                }
            }
            if(srcF != null && zielF != null){
                String newRec1 = "\""+srcS+"\" und \""+zielS+"\"", newRec2 = "\""+zielS+"\" und \""+srcS+"\"";
                boolean inMenu = false;
                for(int i=0; i<menuRecent.getItemCount(); i++){
                    String s = menuRecent.getItem(i).getLabel();
                    if(s.equals(newRec1)){
                        inMenu = true;
                        i = menuRecent.getItemCount();
                    }else if(s.equals(newRec2)){
                        inMenu = true;
                        i = menuRecent.getItemCount();
                    }
                }
                if(!inMenu){
                    if(rec != null){
                        MenuItem i = new MenuItem(newRec1);
                        final int ind = rec.length();
                        i.addActionListener(new ActionListener() { 
                                public void actionPerformed(ActionEvent evt) { 
                                    recent(evt, ind);
                                }
                            });
                        menuRecent.add(i);
                        //Noch in die Recent-datei speichern!
                        String srcS2 = replace(srcS, true), zielS2 = replace(zielS, true);

                        if(rec.length() >= recentLength)
                            rec.removeFirstObject();
                        String[] newS = {srcS2, zielS2};
                        rec.hintersLetzteEinfuegen(newS);
                        recSize += srcS2.length()+zielS2.length()+2;//+/t
                        File recent = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"recent");
                        if(!Streamer.datenSchreiben(recIntoFile(), recent, true, false))
                            writeLine("Error: Recent-datei konnte nicht aktualisiert werden: "+Streamer.getSchreibeFehler());
                    }
                }

                labelTime.setText(Main.language[28]+"...");
                writeLine(Main.language[29]+"...");
                //int g1 = Synchronizer.folderSize(srcF), g2 = Synchronizer.folderSize(zielF);
                folderSizes = Synchronizer.folderSize(srcF, this)+Synchronizer.folderSize(zielF, this);
                if(!abbruch()){
                    timeStart = System.currentTimeMillis();
                    if(Synchronizer.synchronizeDirectories(srcF, zielF, this))
                        writeLine(Main.language[30]);
                    else if(abbruch())
                        writeLine(Main.language[31]);
                    else
                        writeLine(Main.language[32]+":\r\n"+Synchronizer.getFehler());
                }else
                    writeLine(Main.language[31]);
                setFortschritt(100);
                labelTime.setText("");
            }
        }else
            writeLine(Main.language[25]);*/

        ready();
    } // end of buttonSync_ActionPerformed
    /**
     * Erstellt ein log-File des letzten Prozesses.
     */
    private void buttonLog_ActionPerformed(ActionEvent evt) {
        String dir = System.getProperty("user.home")+System.getProperty("file.separator")+"Desktop";
        File f = new File(dir+System.getProperty("file.separator")+"logs_sync"), log = new File(f.getAbsolutePath()+System.getProperty("file.separator")+"log_"+lasts[0].getName()+"-"+lasts[1].getName()+".txt");
        f.mkdir();
        if(Streamer.datenSchreiben(library.streaming.Streamer.stringParsen(logText), log, true))
            ausgabe.append("\n"+Main.language[33]+"'"+log.getAbsolutePath()+"' "+Main.language[34]+".");
        else
            ausgabe.append("\n"+Streamer.getSchreibeFehler());
    } // end of buttonLog_ActionPerformed

    private byte[] recIntoFile(){
        if(rec != null){
            byte[] ret = new byte[recSize];
            int zeiger = 0;
            rec.toFirst();
            for(int i=0; i<recentLength; i++){
                if(i < rec.length()){
                    String[] s = rec.getCurObject();
                    String into = s[0]+"/t"+s[1];
                    for(int j=0; j<into.length(); j++){
                        ret[zeiger] = (byte)into.charAt(j);
                        zeiger++;
                    }
                    rec.next();
                }
                if(i != recentLength-1){
                    ret[zeiger] = (byte)'\r';
                    ret[zeiger+1] = (byte)'\n';
                    zeiger+=2;
                }
            }
            return ret;
        }else
            return null;
    }

    private void unready(){
        logText = "";
        logActivated = true;
        removeWindowListener(wa);
        menuDatei.setEnabled(false);
        menuHelp.setEnabled(false);
        buttonAbbruch.setEnabled(true);
        buttonSync.setEnabled(false);
        buttonBrowseSrc.setEnabled(false);
        buttonBrowseZiel.setEnabled(false);
        choiceSrc.setEnabled(false);
        choiceZiel.setEnabled(false);
        buttonScannen.setEnabled(false);
        abb = false;
    }
    /**
     * Alles wird auf den Normalzustand zurückgesetzt.
     */
    public void ready(){
        addWindowListener(wa);
        menuDatei.setEnabled(true);
        menuHelp.setEnabled(true);
        buttonAbbruch.setEnabled(false);
        buttonSync.setEnabled(true);
        buttonBrowseSrc.setEnabled(true);
        buttonBrowseZiel.setEnabled(true);
        choiceSrc.setEnabled(true);
        choiceZiel.setEnabled(true);
        buttonScannen.setEnabled(true);
        setFortschritt(100);
        labelTime.setText("");
        if(logActivated) buttonLog.setEnabled(true);
    }

    /**
     * Diese Methode wird zur Benennung eines Fehlerberichtes benötigt.
     */
    public String getNamesForError(){
        return lasts[0].getName()+"-"+lasts[1].getName();
    }

    /**
     * Gibt den log-Text zurück (auch für den Fehlerbericht).
     */
    public String getLogText(){
        return logText;
    }

    /**
     * Löscht zur Sicherheit die log-Datei des letzten Prozesses (in dem ein interner Fehler aufgetreten ist).
     */
    public void deleteLog(){
        File logFile = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"log_"+lasts[0].getName()+"-_-_-"+lasts[1].getName()),
        logFileB = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"log_"+lasts[1].getName()+"-_-_-"+lasts[0].getName());
        if(logFile.exists()){
            logFile.delete();
        }else if(logFileB.exists()){
            logFileB.delete();
        }
    }
    
    /*private class BackgroundPanel extends Panel {
        Image img;
        public BackgroundPanel(Image img) {
            this.img = img;
        }
        protected void paintAll(Graphics g) {
            super.paintAll(g);
            if (this.icon != null) {
                g.drawImage(img, 0, 0, this);
            }
        }
        public Dimension getPreferredSize() {
            if (icon != null) {
                return new Dimension(icon.getWidth(), icon.getHeight());
            } else {
                return super.getPreferredSize();
            }
        }
    }*/

    // Ende Methoden
} // end of class GUI

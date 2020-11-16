import java.io.File;
import library.streaming.Streamer;
import library.lists.Liste;
/**
 * Dies ist die Klasse, die das eigentliche Programm ausführt.
 * Sie synchronisiert zwei Verzeichnisse miteinander und speichert möglich auftretende Fehler.
 * 
 * @author Leon Haffmans
 * @version 22. 06. 2015
 */
public class Synchronizer
{
    private static String fehler = "";
    private static int verarb = 0, verarbLast = 0;
    public final static long timeAkt = 5000; //Aktualisierungsrate der Zeitberechnung. Wird hier in Millisekunden angegeben.
    /**
     * Gibt die möglich aufgetretenen Fehler zurück.
     */
    public static String getFehler(){
        String f = Synchronizer.fehler;
        Synchronizer.fehler = "";
        return f;
    }
    /**
     * Synchronisiert zwei übergebene Ordner und zeigt den Prozess auf dem ebenfalls übergebenen GUI an.
     * Liest hierzu eine vom Programm angelegte Log-Datei aus (oder erstellt sie, falls noch nicht vorhanden) und aktualisiert sie hinterher.
     * Hinterher wird außerdem die settings-Datei aktualisiert.
     */
    public static boolean synchronizeDirectories(File dirSrc, File dirBackup, GUI pr, File partSPath, File partZPath, final int mode){
        if(dirSrc != null && dirSrc.exists() && dirSrc.isDirectory() && dirBackup != null && dirBackup.exists() && dirBackup.isDirectory() && pr != null){
            fehler = "";
            verarb = 0;
            verarbLast = 0;
            File logFile = null, logFileB = null;
            ListWithName lastSync = new ListWithName(Streamer.stringParsen(dirSrc.getName())), toSync = null;
            String searchedPath = null;
            boolean bool = true;
            for(int f=0; bool; f++){
                //logFile = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"log_"+dirSrc.getName()+"-_-_-"+dirBackup.getName()+"_"+f);
                //logFileB = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"log_"+dirBackup.getName()+"-_-_-"+dirSrc.getName()+"_"+f);
                if(mode == 0 || mode == 1){
                    logFile = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"log_"+dirSrc.getName()+"-_-_-"+dirBackup.getName()+"_"+f);
                    logFileB = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"log_"+dirBackup.getName()+"-_-_-"+dirSrc.getName()+"_"+f);
                }else{
                    logFile = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"log_"+partSPath.getName()+"-_-_-"+partZPath.getName()+"_"+f);
                    logFileB = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"log_"+partZPath.getName()+"-_-_-"+partSPath.getName()+"_"+f);
                }
                if(logFile.exists() || logFileB.exists()){
                    byte[] logDat = null;
                    pr.writeLine(Main.language[49]);
                    if(logFile.exists())
                        logDat = Streamer.datenAuslesen(logFile);
                    else
                        logDat = Streamer.datenAuslesen(logFileB);
                    int curEb = 1;
                    byte[] spl = {13, 10}; //{\r, \n}
                    Liste<byte[]> zeilen = Synchronizer.split(spl, logDat);
                    zeilen.toFirst();
                    byte[] firstLine = zeilen.getCurObject();
                    //firstLine = firstLine.replaceAll("/ae", "ä");
                    //firstLine = firstLine.replaceAll("/oe", "ö");
                    //firstLine = firstLine.replaceAll("/ue", "ü");
                    if(Synchronizer.equals(firstLine, Streamer.stringParsen(dirSrc.getAbsolutePath()+"/t"+dirBackup.getAbsolutePath())) || Synchronizer.equals(firstLine, Streamer.stringParsen(dirBackup.getAbsolutePath()+"/t"+dirSrc.getAbsolutePath())) ||
                        Synchronizer.equals(firstLine, Streamer.stringParsen(partSPath.getAbsolutePath()+"/t"+partZPath.getAbsolutePath())) || Synchronizer.equals(firstLine, Streamer.stringParsen(partZPath.getAbsolutePath()+"/t"+partSPath.getAbsolutePath()))){
                        ListWithName cur = lastSync;
                        zeilen.goTo(2);
                        byte[] sp = null;
                        byte[] cp = null;
                        int z = 0;
                        if(mode == 1){
                            searchedPath = partSPath.getAbsolutePath().substring(dirSrc.getAbsolutePath().length(),partSPath.getAbsolutePath().length());
                            sp = Streamer.stringParsen(searchedPath);
                            cp = new byte[sp.length];
                        }
                        //String curPath = "";
                        for(int i=2; i<zeilen.length() && !pr.abbruch(); i++){
                            byte[] zeile = zeilen.getCurObject();
                            int ts = Synchronizer.zaehleT(zeile);
                            if(ts < curEb){
                                cur = lastSync;
                                if(mode == 1){
                                    //curPath = "";
                                    cp = new byte[sp.length];
                                    z=0;
                                }
                                curEb = ts;
                                for(int j=0; j<ts-1; j++){
                                    cur = (ListWithName)cur.getLastObject();
                                    if(mode == 1){
                                        //curPath+=System.getProperty("file.separator")+Streamer.bytearrayParsen(cur.getName());
                                        byte[] sep = Streamer.stringParsen(System.getProperty("file.separator"));
                                        
                                        add(cp,sep,z);
                                        z+=sep.length;
                                        add(cp,cur.getName(),z);
                                        z+=cur.getName().length;
                                    }
                                }
                                //if(mode == 1) curPath = curPath.substring(0,curPath.lastIndexOf(System.getProperty("file.separator")));
                            }
                            if(zeile[zeile.length-1] == (byte)'V'){
                                ListWithName n = new ListWithName(Synchronizer.subbyte(zeile, curEb, zeile.length-1), cur);
                                if(mode == 1){
                                    //curPath += System.getProperty("file.separator")+Streamer.bytearrayParsen(n.getName());
                                    byte[] sep = Streamer.stringParsen(System.getProperty("file.separator"));
                                    /*    System.arraycopy(sep,0,cp,z,sep.length);
                                        System.arraycopy(n.getName(),0,cp,z+sep.length,n.getName().length);
                                        z+=sep.length+n.getName().length;*/
                                    add(cp,sep,z);
                                    z+=sep.length;
                                    add(cp,n.getName(),z);
                                    z+=n.getName().length;
                                    if(/*curPath.equals(searchedPath) || */equals(cp,sp)){
                                        toSync = n;
                                    }
                                }
                                curEb++;
                                cur.add(n);
                                cur = n;
                            }else{
                                cur.add(Synchronizer.subbyte(zeile, curEb, zeile.length-1));//Bis "F"
                            }
                            zeilen.next();
                        }
                        if(pr.abbruch()){
                            Synchronizer.fehler += "Der Vorgang wurde abgebrochen.\r\n";
                            return false;
                        }
                        bool = false;
                    }else{
                        //try{logFile.createNewFile();}catch(Exception e){}
                    }
                }else{
                    try{logFile.createNewFile();}catch(Exception e){}
                    bool = false;
                }
                if(f>=1000){ //Sicherung, um keine Endlosschleife zu erzeugen.
                    Synchronizer.fehler += "Zu viele verschiedene Log-Files mit dem selben Namen.";
                    return false;
                }
            }
                                    //*************************************************************************************************************************
            String spath = dirSrc.getAbsolutePath(), zpath = dirBackup.getAbsolutePath();
            if(mode == 1){
                if(toSync != null){
                    spath+=searchedPath;
                    zpath+=searchedPath;
                }else{
                    pr.writeLine(Main.language[50]+"\r\n"+Main.language[51]);
                    try{
                        if(logFile.exists()) logFile.delete();
                        else logFileB.delete();
                    }catch(Exception e){}
                    return false;
                }
            }else if(mode == 2){
                spath = partSPath.getAbsolutePath();
                zpath = partZPath.getAbsolutePath();
                toSync = lastSync;
            }else
                toSync = lastSync;
            boolean ret = Synchronizer.synchronizeDirectories(new File(spath), new File(zpath), toSync, pr);
            
            pr.writeLine(Main.language[52]);
            
            String fl = dirSrc.getAbsolutePath()+"/t"+dirBackup.getAbsolutePath();
            if(mode == 2) fl = spath+"/t"+zpath;
            byte[] newDat = Synchronizer.listIntoLogFile(lastSync,0,fl);
            if(logFile.exists())
                Streamer.datenSchreiben(newDat, logFile, true);
            else
                Streamer.datenSchreiben(newDat, logFileB, true);
            
            return ret;
        }else{
            if(dirSrc == null)
                Synchronizer.fehler += "Kein Source-File angegeben.";
            else if(!dirSrc.exists())
                Synchronizer.fehler += "Source-File existiert nicht.";
            else if(!dirSrc.isDirectory())
                Synchronizer.fehler += "Source-File ist kein Ordner.";
            else if(dirBackup == null)
                Synchronizer.fehler += "Kein Backup-File angegeben.";
            else if(!dirBackup.exists())
                Synchronizer.fehler += "Backup-File existiert nicht.";
            else if(!dirBackup.isDirectory())
                Synchronizer.fehler += "Backup-File ist kein Ordner.";
            else if(pr == null)
                Synchronizer.fehler += "Keine GUI übergeben.";
            return false;
        }
    }
    /**
     * Aktualisiert die log-Datei des Prozesses (oder erstellt sie).
     */
    private static byte[] listIntoLogFile(ListWithName newLog, int ebene, String firstLine){
        if(newLog != null){
            int wert = 0;
            if(ebene == 0){
                wert = firstLine.length()+2;
            }
            byte[] ret = new byte[newLog.getByteSizeForLogDat()+ebene*(newLog.getL()+1)+wert];
            int zeiger = 0;
            if(ebene == 0){
                for(int i=0; i<firstLine.length(); i++){
                    ret[i] = (byte)firstLine.charAt(i);
                }
                ret[firstLine.length()] = (byte)'\r';
                ret[firstLine.length()+1] = (byte)'\n';
                zeiger = firstLine.length()+2;
            }
            byte[] name = newLog.getName();
            for(int i=0; i<ebene; i++){
                ret[zeiger] = (byte)'\t';
                zeiger++;
            }
            System.arraycopy(name,0,ret,zeiger,name.length);
            zeiger+=name.length;
            ret[zeiger] = (byte)'V';
            zeiger+=1;
            if(newLog.length() > 0){
                ret[zeiger] = (byte)'\r';
                ret[zeiger+1] = (byte)'\n';
                zeiger+=2;
            }
            newLog.toFirst();
            for(int i=0; i<newLog.length(); i++){
                if(newLog.getCurObject() != null){
                    if(newLog.getCurObject().getClass() == ListWithName.class && i != newLog.length()-1){
                        byte[] b = Synchronizer.listIntoLogFile((ListWithName)newLog.getCurObject(), ebene+1, firstLine);
                        System.arraycopy(b,0,ret,zeiger,b.length);
                        zeiger+=b.length;
                        ret[zeiger] = (byte)'\r';
                        ret[zeiger+1] = (byte)'\n';
                        zeiger+=2;
                    }else if(newLog.getCurObject().getClass() == ListWithName.class){
                        byte[] b = Synchronizer.listIntoLogFile((ListWithName)newLog.getCurObject(), ebene+1, firstLine);
                        System.arraycopy(b,0,ret,zeiger,b.length);
                        zeiger+=b.length;
                    }else if(i != newLog.length()-1){
                        for(int j=0; j<ebene+1; j++){
                            ret[zeiger] = (byte)'\t';
                            zeiger++;
                        }
                        byte[] b = (byte[])newLog.getCurObject();
                        System.arraycopy(b,0,ret,zeiger,b.length);
                        zeiger+=b.length;
                        ret[zeiger] = (byte)'F';
                        ret[zeiger+1] = (byte)'\r';
                        ret[zeiger+2] = (byte)'\n';
                        zeiger+=3;
                    }else{
                        for(int j=0; j<ebene+1; j++){
                            ret[zeiger] = (byte)'\t';
                            zeiger++;
                        }
                        byte[] b = (byte[])newLog.getCurObject();
                        System.arraycopy(b,0,ret,zeiger,b.length);
                        zeiger+=b.length;
                        ret[zeiger] = (byte)'F';
                        zeiger++;
                    }
                }else{
                    
                }
                newLog.next();
            }
            return ret;
        }else
            return null;
    }
    /**
     * Synchronisiert die beiden Verzeichnisse.
     */
    private static boolean synchronizeDirectories(File dirSrc, File dirBackup, ListWithName list, GUI pr){
        if(dirSrc != null && dirSrc.exists() && dirSrc.isDirectory() && dirBackup != null && dirBackup.exists() && dirBackup.isDirectory()){
            File[] listSrc = dirSrc.listFiles();
            File[] listBackup = dirBackup.listFiles();
            boolean ret = true;
            pr.writeLine("Vergleiche '"+dirSrc.getAbsolutePath()+"' mit '"+dirBackup.getAbsolutePath());
            for(int i=0; (i<listSrc.length || i<listBackup.length) && !pr.abbruch(); i++){
                long m = System.currentTimeMillis();
                if(m-pr.getTimeStart() > timeAkt){
                    pr.berechneZeit((double)verarbLast/(double)(m-pr.getTimeStart()), m);
                    verarbLast = 0;
                }
                boolean toB = true;
                if(i<listSrc.length && i<listBackup.length){
                    if(listSrc[i].isDirectory()){
                        if(listSrc[i].getName().equalsIgnoreCase(listBackup[i].getName())){
                            ListWithName li = Synchronizer.searchForDirectoryInList(list, listSrc[i].getName());
                            if(li == null){
                                li = new ListWithName(Streamer.stringParsen(listSrc[i].getName()), list);
                                list.add(li);
                            }
                            if(!Synchronizer.synchronizeDirectories(listSrc[i], listBackup[i], li, pr))
                                ret = false;
                            toB = false;
                        }else{
                            int ind = Synchronizer.searchForFile(listSrc[i], listBackup);
                            if(ind != -1){
                                ListWithName li = Synchronizer.searchForDirectoryInList(list, listSrc[i].getName());
                                if(li == null){
                                    li = new ListWithName(Streamer.stringParsen(listSrc[i].getName()), list);
                                    list.add(li);
                                }
                                if(!Synchronizer.synchronizeDirectories(listSrc[i], listBackup[ind], li, pr))
                                    ret = false;
                            }else{
                                //neuer Ordner vorhanden!
                                ListWithName l = Synchronizer.searchForDirectoryInList(list, listSrc[i].getName());
                                if(l == null){
                                    if(copyDir(listSrc[i], new File(dirBackup.getAbsolutePath()+System.getProperty("file.separator")+listSrc[i].getName()))){
                                        pr.writeLine("Verzeichnis '"+listSrc[i].getAbsolutePath()+"' wurde kopiert.");
                                    }else{
                                        Synchronizer.fehler += "Verzeichnis '"+listSrc[i].getAbsolutePath()+"' konnte nicht kopiert werden.\r\n";
                                        ret = false;
                                    }
                                    list.addDir(listSrc[i]);
                                }else{
                                    //Wurde bei Backup gelöscht
                                    if(deleteDir(listSrc[i])){
                                        pr.writeLine("Verzeichnis '"+listSrc[i].getAbsolutePath()+"' wurde gelöscht.");
                                    }else{
                                        Synchronizer.fehler += "Verzeichnis '"+listSrc[i].getAbsolutePath()+"' konnte nicht gelöscht werden.\r\n";
                                        ret = false;
                                    }
                                    list.removeCur();
                                }
                            }
                        }
                    }else{
                        if(listSrc[i].getName().equalsIgnoreCase(listBackup[i].getName())){
                            if(listSrc[i].length() != listBackup[i].length()){
                                if(listSrc[i].lastModified() > listBackup[i].lastModified()){
                                    if(Streamer.copyFile(listSrc[i], listBackup[i], true)){
                                        //Datei wurde erfolgreich von Src nach Backup aktualisiert
                                        pr.writeLine("Datei '"+listSrc[i].getAbsolutePath()+"' wurde nach '"+listBackup[i].getAbsolutePath()+"' aktualisiert.");
                                    }else{
                                        Synchronizer.fehler += "Dateien '"+listSrc[i].getAbsolutePath()+"' und '"+listBackup[i].getAbsolutePath()+"' konnten nicht synchronisiert werden.\r\n";
                                        ret = false;
                                    }
                                }else if(listSrc[i].lastModified() < listBackup[i].lastModified()){
                                    if(Streamer.copyFile(listBackup[i], listSrc[i], true)){
                                        //Datei wurde erfolgreich von Backup nach Src aktualisiert
                                        pr.writeLine("Datei '"+listBackup[i].getAbsolutePath()+"' wurde nach '"+listSrc[i].getAbsolutePath()+"' aktualisiert.");
                                    }else{
                                        Synchronizer.fehler += "Dateien '"+listSrc[i].getAbsolutePath()+"' und '"+listBackup[i].getAbsolutePath()+"' konnten nicht synchronisiert werden.\r\n";
                                        ret = false;
                                    }
                                }
                            }
                            verarb+=listSrc[i].length()+listBackup[i].length();
                            verarbLast+=listSrc[i].length()+listBackup[i].length();
                            pr.setFortschritt((float)verarb/pr.getFolderSizes()*100);
                            if(Synchronizer.searchForFileInList(list, listSrc[i].getName()) == null){
                                list.add(Streamer.stringParsen(listSrc[i].getName()));
                            }
                            toB = false;
                        }else{
                            int ind = Synchronizer.searchForFile(listSrc[i], listBackup);
                            if(ind != -1){
                                if(listSrc[i].length() != listBackup[ind].length()){
                                    if(listSrc[i].lastModified() > listBackup[ind].lastModified()){
                                        if(Streamer.copyFile(listSrc[i], listBackup[ind], true)){
                                            //Datei wurde erfolgreich von Src nach Backup aktualisiert
                                            pr.writeLine("Datei '"+listSrc[i].getAbsolutePath()+"' wurde nach '"+listBackup[ind].getAbsolutePath()+"' aktualisiert.");
                                        }else{
                                            Synchronizer.fehler += "Dateien '"+listSrc[i].getAbsolutePath()+"' und '"+listBackup[ind].getAbsolutePath()+"' konnten nicht synchronisiert werden.\r\n";
                                            ret = false;
                                        }
                                    }else if(listSrc[i].lastModified() < listBackup[ind].lastModified()){
                                        if(Streamer.copyFile(listBackup[ind], listSrc[i], true)){
                                            //Datei wurde erfolgreich von Backup nach Src aktualisiert
                                            pr.writeLine("Datei '"+listBackup[ind].getAbsolutePath()+"' wurde nach '"+listSrc[i].getAbsolutePath()+"' aktualisiert.");
                                        }else{
                                            Synchronizer.fehler += "Dateien '"+listSrc[i].getAbsolutePath()+"' und '"+listBackup[ind].getAbsolutePath()+"' konnten nicht synchronisiert werden.\r\n";
                                            ret = false;
                                        }
                                    }
                                }
                                verarb+=listSrc[i].length()+listBackup[ind].length();
                                verarbLast+=listSrc[i].length()+listBackup[ind].length();
                                pr.setFortschritt((float)verarb/pr.getFolderSizes()*100);
                                if(Synchronizer.searchForFileInList(list, listSrc[i].getName()) == null){
                                    list.add(Streamer.stringParsen(listSrc[i].getName()));
                                }
                            }else{
                                //neue Datei vorhanden!
                                byte[] s = Synchronizer.searchForFileInList(list, listSrc[i].getName());
                                if(s == null){
                                    if(Streamer.copyFile(listSrc[i], new File(dirBackup.getAbsolutePath()+System.getProperty("file.separator")+listSrc[i].getName()), false)){
                                        //Datei erfolgreich kopiert
                                        pr.writeLine("Datei '"+listSrc[i].getAbsolutePath()+"' wurde kopiert.");
                                    }else{
                                        Synchronizer.fehler += "Datei '"+listSrc[i].getAbsolutePath()+"' konnte nicht kopiert werden.\r\n";
                                        ret = false;
                                    }
                                    list.add(Streamer.stringParsen(listSrc[i].getName()));
                                }else{
                                    //Wurde bei Backup gelöscht
                                    //Current-Zeiger ist auf Element s
                                    if(listSrc[i].delete()){
                                        //Datei erfolgreich gelöscht
                                        pr.writeLine("Datei '"+listSrc[i].getAbsolutePath()+"' wurde gelöscht.");
                                    }else{
                                        Synchronizer.fehler += "Datei '"+listSrc[i].getAbsolutePath()+"' konnte nicht gelöscht werden.\r\n";
                                        ret = false;
                                    }
                                    list.removeCur();
                                }
                                verarb+=listSrc[i].length();
                                verarbLast+=listSrc[i].length();
                                pr.setFortschritt((float)verarb/pr.getFolderSizes()*100);
                            }
                        }
                    }
                    if(toB && Synchronizer.searchForFile(listBackup[i], listSrc) == -1){
                        //File nicht in Src vorhanden
                        if(listBackup[i].isDirectory()){
                            ListWithName l = Synchronizer.searchForDirectoryInList(list, listBackup[i].getName());
                            if(l == null){
                                if(copyDir(listBackup[i], new File(dirSrc.getAbsolutePath()+System.getProperty("file.separator")+listBackup[i].getName()))){
                                    //Ordner erfolgreich kopiert
                                    pr.writeLine("Verzeichnis '"+listBackup[i].getAbsolutePath()+"' wurde kopiert.");
                                }else{
                                    Synchronizer.fehler += "Verzeichnis '"+listBackup[i].getAbsolutePath()+"' konnte nicht kopiert werden.\r\n";
                                    ret = false;
                                }
                                list.addDir(listBackup[i]);
                            }else{
                                if(deleteDir(listBackup[i])){
                                    //Ordner erfolgreich gelöscht
                                    pr.writeLine("Verzeichnis '"+listBackup[i].getAbsolutePath()+"' wurde gelöscht.");
                                }else{
                                    Synchronizer.fehler += "Verzeichnis '"+listBackup[i].getAbsolutePath()+"' konnte nicht gelöscht werden.\r\n";
                                    ret = false;
                                }
                                list.removeCur();
                            }
                        }else{
                            if(Synchronizer.searchForFileInList(list, listBackup[i].getName()) == null){//********************************************************************************************************
                                if(Streamer.copyFile(listBackup[i], new File(dirSrc.getAbsolutePath()+System.getProperty("file.separator")+listBackup[i].getName()), false)){
                                    //Datei erfolgreich kopiert
                                    pr.writeLine("Datei '"+listBackup[i].getAbsolutePath()+"' wurde kopiert.");
                                }else{
                                    Synchronizer.fehler += "Datei '"+listBackup[i].getAbsolutePath()+"' konnte nicht kopiert werden.\r\n";
                                    ret = false;
                                }
                                list.add(Streamer.stringParsen(listBackup[i].getName()));
                            }else{
                                if(listBackup[i].delete()){
                                    //Datei erfolgreich gelöscht
                                    pr.writeLine("Datei '"+listBackup[i].getAbsolutePath()+"' wurde gelöscht.");
                                }else{
                                    Synchronizer.fehler += "Datei '"+listBackup[i].getAbsolutePath()+"' konnte nicht gelöscht werden.\r\n";
                                    ret = false;
                                }
                                list.removeCur();
                            }
                            verarb+=listBackup[i].length();
                            verarbLast+=listBackup[i].length();
                            pr.setFortschritt((float)verarb/pr.getFolderSizes()*100);
                        }
                    }
                }else if(i<listSrc.length){
                    if(listSrc[i].isDirectory()){
                        int ind = Synchronizer.searchForFile(listSrc[i], listBackup);
                        if(ind != -1){
                            ListWithName li = Synchronizer.searchForDirectoryInList(list, listSrc[i].getName());
                            if(li == null){
                                li = new ListWithName(Streamer.stringParsen(listSrc[i].getName()), list);
                                list.add(li);
                            }
                            if(!Synchronizer.synchronizeDirectories(listSrc[i], listBackup[ind], li, pr))
                                ret = false;
                        }else{
                            //neuer Ordner vorhanden! 
                            ListWithName l = Synchronizer.searchForDirectoryInList(list, listSrc[i].getName());
                            if(l == null){
                                if(copyDir(listSrc[i], new File(dirBackup.getAbsolutePath()+System.getProperty("file.separator")+listSrc[i].getName()))){
                                    //Ordner erfolgreich kopiert
                                    pr.writeLine("Verzeichnis '"+listSrc[i].getAbsolutePath()+"' wurde kopiert.");
                                }else{
                                    Synchronizer.fehler += "Verzeichnis '"+listSrc[i].getAbsolutePath()+"' konnte nicht kopiert werden.\r\n";
                                    ret = false;
                                }
                                list.addDir(listSrc[i]);
                            }else{
                                //Wurde bei Backup gelöscht
                                if(deleteDir(listSrc[i])){
                                    //Ordner erfolgreich gelöscht
                                    pr.writeLine("Verzeichnis '"+listSrc[i].getAbsolutePath()+"' wurde gelöscht.");
                                }else{
                                    Synchronizer.fehler += "Verzeichnis '"+listSrc[i].getAbsolutePath()+"' konnte nicht gelöscht werden.\r\n";
                                    ret = false;
                                }
                                list.removeCur();
                            }
                        }
                    }else{
                        int ind = Synchronizer.searchForFile(listSrc[i], listBackup);
                        if(ind != -1){
                            if(listSrc[i].length() != listBackup[ind].length()){
                                if(listSrc[i].lastModified() > listBackup[ind].lastModified()){
                                    if(Streamer.copyFile(listSrc[i], listBackup[ind], true)){
                                        //Datei wurde erfolgreich von Src nach Backup aktualisiert
                                        pr.writeLine("Datei '"+listSrc[i].getAbsolutePath()+"' wurde nach '"+listBackup[ind].getAbsolutePath()+"' aktualisiert.");
                                    }else{
                                        Synchronizer.fehler += "Dateien '"+listSrc[i].getAbsolutePath()+"' und '"+listBackup[ind].getAbsolutePath()+"' konnten nicht synchronisiert werden.\r\n";
                                        ret = false;
                                    }
                                }else if(listSrc[i].lastModified() < listBackup[ind].lastModified()){
                                    if(Streamer.copyFile(listBackup[ind], listSrc[i], true)){
                                        //Datei wurde erfolgreich von Backup nach Src aktualisiert
                                        pr.writeLine("Datei '"+listBackup[ind].getAbsolutePath()+"' wurde nach '"+listSrc[i].getAbsolutePath()+"' aktualisiert.");
                                    }else{
                                        Synchronizer.fehler += "Dateien '"+listSrc[i].getAbsolutePath()+"' und '"+listBackup[ind].getAbsolutePath()+"' konnten nicht synchronisiert werden.\r\n";
                                        ret = false;
                                    }
                                }
                            }
                            verarb+=listSrc[i].length()+listBackup[ind].length();
                            verarbLast+=listSrc[i].length()+listBackup[ind].length();
                            pr.setFortschritt((float)verarb/pr.getFolderSizes()*100);
                            if(Synchronizer.searchForFileInList(list, listSrc[i].getName()) == null){
                                list.add(Streamer.stringParsen(listSrc[i].getName()));
                            }
                        }else{
                            //neue Datei vorhanden!
                            byte[] s = Synchronizer.searchForFileInList(list, listSrc[i].getName());
                            if(s == null){
                                if(Streamer.copyFile(listSrc[i], new File(dirBackup.getAbsolutePath()+System.getProperty("file.separator")+listSrc[i].getName()), false)){
                                    //Datei erfolgreich kopiert
                                    pr.writeLine("Datei '"+listSrc[i].getAbsolutePath()+"' wurde kopiert.");
                                }else{
                                    Synchronizer.fehler += "Datei '"+listSrc[i].getAbsolutePath()+"' konnte nicht kopiert werden.\r\n";
                                    ret = false;
                                }
                                list.add(Streamer.stringParsen(listSrc[i].getName()));
                            }else{
                                //Wurde bei Backup gelöscht
                                //Current-Zeiger ist auf Element s
                                if(listSrc[i].delete()){
                                    //Datei erfolgreich gelöscht
                                    pr.writeLine("Datei '"+listSrc[i].getAbsolutePath()+"' wurde gelöscht.");
                                }else{
                                    Synchronizer.fehler += "Datei '"+listSrc[i].getAbsolutePath()+"' konnte nicht gelöscht werden.\r\n";
                                    ret = false;
                                }
                                list.removeCur();
                            }
                            verarb+=listSrc[i].length();
                            verarbLast+=listSrc[i].length();
                            pr.setFortschritt((float)verarb/pr.getFolderSizes()*100);
                        }
                    }
                }else{// i<listBackup.length
                    if(listBackup[i].isDirectory()){
                        int ind = Synchronizer.searchForFile(listBackup[i], listSrc);
                        if(ind != -1){
                            ListWithName li = Synchronizer.searchForDirectoryInList(list, listBackup[i].getName());
                            if(li == null){
                                li = new ListWithName(Streamer.stringParsen(listBackup[i].getName()), list);
                                list.add(li);
                            }
                            if(!Synchronizer.synchronizeDirectories(listBackup[i], listSrc[ind], li, pr))
                                ret = false;
                        }else{
                            //neuer Ordner vorhanden!
                            ListWithName l = Synchronizer.searchForDirectoryInList(list, listBackup[i].getName());
                            if(l == null){
                                if(copyDir(listBackup[i], new File(dirSrc.getAbsolutePath()+System.getProperty("file.separator")+listBackup[i].getName()))){
                                    //Ordner erfolgreich kopiert
                                    pr.writeLine("Verzeichnis '"+listBackup[i].getAbsolutePath()+"' wurde kopiert.");//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                }else{
                                    Synchronizer.fehler += "Verzeichnis '"+listBackup[i].getAbsolutePath()+"' konnte nicht kopiert werden.\r\n";
                                    ret = false;
                                }
                                list.addDir(listBackup[i]);
                            }else{
                                //Wurde bei Backup gelöscht
                                if(deleteDir(listBackup[i])){
                                    //Ordner erfolgreich gelöscht
                                    pr.writeLine("Verzeichnis '"+listBackup[i].getAbsolutePath()+"' wurde gelöscht.");
                                }else{
                                    Synchronizer.fehler += "Verzeichnis '"+listBackup[i].getAbsolutePath()+"' konnte nicht gelöscht werden.\r\n";
                                    ret = false;
                                }
                                list.removeCur();
                            }
                        }
                    }else{
                        int ind = Synchronizer.searchForFile(listBackup[i], listSrc);
                        if(ind != -1){
                            if(listBackup[i].length() != listSrc[ind].length()){
                                if(listBackup[i].lastModified() > listSrc[ind].lastModified()){
                                    if(Streamer.copyFile(listBackup[i], listSrc[ind], true)){
                                        //Datei wurde erfolgreich von Backup nach Src aktualisiert
                                        pr.writeLine("Datei '"+listBackup[i].getAbsolutePath()+"' wurde nach '"+listSrc[ind].getAbsolutePath()+"' aktualisiert.");
                                    }else{
                                        Synchronizer.fehler += "Dateien '"+listSrc[ind].getAbsolutePath()+"' und '"+listBackup[i].getAbsolutePath()+"' konnten nicht synchronisiert werden.\r\n";
                                        ret = false;
                                    }
                                }else if(listBackup[i].lastModified() < listSrc[ind].lastModified()){
                                    if(Streamer.copyFile(listSrc[ind], listBackup[i], true)){
                                        //Datei wurde erfolgreich von Src nach Backup aktualisiert
                                        pr.writeLine("Datei '"+listSrc[ind].getAbsolutePath()+"' wurde nach '"+listBackup[i].getAbsolutePath()+"' aktualisiert.");
                                    }else{
                                        Synchronizer.fehler += "Dateien '"+listSrc[ind].getAbsolutePath()+"' und '"+listBackup[i].getAbsolutePath()+"' konnten nicht synchronisiert werden.\r\n";
                                        ret = false;
                                    }
                                }
                            }
                            verarb+=listBackup[i].length()+listSrc[ind].length();
                            verarbLast+=listBackup[i].length()+listSrc[ind].length();
                            pr.setFortschritt((float)verarb/pr.getFolderSizes()*100);
                            if(Synchronizer.searchForFileInList(list, listBackup[i].getName()) == null){
                                list.add(Streamer.stringParsen(listBackup[i].getName()));
                            }
                        }else{
                            //neue Datei vorhanden!
                            byte[] s = Synchronizer.searchForFileInList(list, listBackup[i].getName());
                            if(s == null){
                                if(Streamer.copyFile(listBackup[i], new File(dirSrc.getAbsolutePath()+System.getProperty("file.separator")+listBackup[i].getName()), false)){
                                    //Datei erfolgreich kopiert
                                    pr.writeLine("Datei '"+listBackup[i].getAbsolutePath()+"' wurde kopiert.");
                                }else{
                                    Synchronizer.fehler += "Datei '"+listBackup[i].getAbsolutePath()+"' konnte nicht kopiert werden.\r\n";
                                    ret = false;
                                }
                                list.add(Streamer.stringParsen(listBackup[i].getName()));
                            }else{
                                //Wurde bei Backup gelöscht
                                //Current-Zeiger ist auf Element s
                                if(listBackup[i].delete()){
                                    //Datei erfolgreich gelöscht
                                    pr.writeLine("Datei '"+listBackup[i].getAbsolutePath()+"' wurde gelöscht.");
                                }else{
                                    Synchronizer.fehler += "Datei '"+listBackup[i].getAbsolutePath()+"' konnte nicht gelöscht werden.\r\n";
                                    ret = false;
                                }
                                list.removeCur();
                            }
                            verarb+=listBackup[i].length();
                            verarbLast+=listBackup[i].length();
                            pr.setFortschritt((float)verarb/pr.getFolderSizes()*100);
                        }
                    }
                }
            }
            if(pr.abbruch()){
                Synchronizer.fehler += "Der Vorgang wurde abgebrochen.\r\n";
                return false;
            }
            return ret;
        }else{
            return false;
        }
    }
    private static void add(byte[] array, byte[] insert, int ind){
        if(insert.length+ind <= array.length)
            System.arraycopy(insert,0,array,ind,insert.length);
    }
    private static int searchForFile(File file, File[] filelist){
        if(file != null && filelist != null){
            for(int i=0; i<filelist.length; i++){
                if(filelist[i].getName().equalsIgnoreCase(file.getName())){
                    return i;
                }
            }
            return -1;
        }else
            return -1;
    }
    /**
     * Hilfsmethode zum Auslesen der log-Datei.
     * Zählt die Anzahl der "Tabs", die in diesem Programm für die Struktur der Verzeichnisebenen erforderlich ist.
     */
    private static int zaehleT(byte[] zeile){
        if(zeile != null){
            int ret = 0;
            for(int i=0; i<zeile.length; i++){
                if(zeile[i] == (byte)'\t'){
                    ret++;
                }else{
                    i = zeile.length;
                }
            }
            return ret;
        }else
            return -1;
    }
    private static ListWithName searchForDirectoryInList(ListWithName list, String directoryName){
        if(directoryName != null){
            byte[] dirName = Streamer.stringParsen(directoryName);
            list.toFirst();
            for(int i=0; i<list.length(); i++){
                if(list.getCurObject().getClass() == ListWithName.class && Synchronizer.equals(((ListWithName)list.getCurObject()).getName(), dirName)){
                    return (ListWithName)list.getCurObject();
                }else{
                    list.next();
                }
            }
            return null;
        }else
            return null;
    }
    private static byte[] searchForFileInList(ListWithName list, String directoryName){
        if(directoryName != null){
            byte[] dirName = Streamer.stringParsen(directoryName);
            list.toFirst();
            for(int i=0; i<list.length(); i++){
                if(list.getCurObject().getClass() == byte[].class && Synchronizer.equals((byte[])list.getCurObject(), dirName)){
                    return (byte[])list.getCurObject();
                }else{
                    list.next();
                }
            }
            return null;
        }else
            return null;
    }
    private static boolean copyDir(File input, File output){
        output.mkdirs();
        if (input != null && input.exists() && input.isDirectory()){
            File[] files = input.listFiles();
            File newFile = null;
            boolean ret = true;
            for(int i=0; i < files.length; i++){
                newFile = new File(output.getAbsolutePath()+System.getProperty("file.separator")+files[i].getName());
                if(files[i].isDirectory()){
                    if(!copyDir(files[i], newFile))
                        ret = false;
                }else{
                    if(!Streamer.copyFile(files[i], newFile, false))
                        ret = false;
                    verarb+=files[i].length();
                    verarbLast+=files[i].length();
                }
            }
            return ret;
        }else{
            return false;
        }
    }
    /**
     * Diese Methode löscht das angegebene Verzeichnis.
     */
    private static boolean deleteDir(File dir){
        if(dir != null && dir.exists() && dir.isDirectory()){
            File[] list = dir.listFiles();
            boolean ret = true;
            for(int i=0; i<list.length; i++){
                if(list[i].isDirectory()){
                    if(!deleteDir(list[i]))
                        ret = false;
                }else{
                    if(!list[i].delete())
                        ret = false;
                    verarb+=list[i].length();
                    verarbLast+=list[i].length();
                }
            }
            if (!dir.delete())
                ret = false;
            return ret;
        }else{
            return false;
        }
    }
    /**
     * Berechnet die Größe des übergebenen Verzeichnisses.
     */
    public static int folderSize(File dir, GUI g){
        if(dir != null && dir.exists() && dir.isDirectory()){
            File[] list = dir.listFiles();
            int ret = 0;
            for(int i=0; i<list.length && !g.abbruch(); i++){
                if(list[i].isDirectory())
                    ret+=folderSize(list[i], g);
                else
                    ret+=list[i].length();
            }
            if(g.abbruch()) return -1;
            return ret;
        }else
            return -1;
    }
    public static Liste<String> folderInformation(File dir, GUI g){
        if(dir != null && dir.exists() && dir.isDirectory()){
            File[] list = dir.listFiles();
            if(list != null){
                g.addToList(dir.getName()+" /// "+list.length+" Dat. & Verz.");
                ExtensionList ret = new ExtensionList();
                for(int i=0; i<list.length && !g.abbruch(); i++){
                    if(list[i].isDirectory()){
                        folderInformation(list[i], g, ret, dir.getName()+System.getProperty("file.separator")+list[i].getName());
                    }else{
                        ret.updateExtension(getEnd(list[i]), list[i].length());
                    }
                }
                if(g.abbruch()) return null;
                return ret;
            }else{
                g.writeLine("Verzeichnis \""+dir.getAbsolutePath()+"\" konnte nicht gelesen werden.");
                return null;
            }
        }else
            return null;
    }
    private static void folderInformation(File dir, GUI g, ExtensionList l, String path){
        if(dir != null && dir.exists() && dir.isDirectory()){
            File[] list = dir.listFiles();
            if(list != null){
                g.addToList(path+" /// "+list.length+" Dat. & Verz.");
                for(int i=0; i<list.length && !g.abbruch(); i++){
                    if(list[i].isDirectory()){
                        folderInformation(list[i], g, l, path+System.getProperty("file.separator")+list[i].getName());
                    }else{
                        l.updateExtension(getEnd(list[i]), list[i].length());
                    }
                }
            }else
                g.writeLine("Verzeichnis \""+path+"\" konnte nicht gelesen werden.");
        }
    }
    private static String getEnd(File f){
        if(f != null){
            int last = f.getName().lastIndexOf('.');
            if(last != -1)
                return f.getName().substring(last+1, f.getName().length());
            else
                return "";
        }else
            return null;
    }
    private static class ExtensionList extends Liste<String>{
        public ExtensionList(){
            super();
            hintersLetzteEinfuegen("0");
            hintersLetzteEinfuegen("0");
        }
        public void updateExtension(String ext, long size){
            goTo(1);
            replaceFirstObject(Integer.toString(Integer.parseInt(getFirstObject())+1));
            replaceCurObject(Long.toString(Long.parseLong(getCurObject())+size));
            next();
            boolean exists = false;
            for(int i=2; i<length(); i++){
                String cur = getCurObject();
                int first = cur.indexOf('\\');
                if(cur.substring(0, first).equalsIgnoreCase(ext)){
                    int last = cur.lastIndexOf('\\');
                    replaceCurObject(ext+'\\'+(Integer.parseInt(cur.substring(first+1, last))+1)+'\\'+(Long.parseLong(cur.substring(last+1, cur.length()))+size));
                    exists = true;
                    i=length();
                }
                next();
            }
            if(!exists){
                hintersLetzteEinfuegen(ext+'\\'+1+'\\'+size);
            }
        }
    }
    /**
     * Splittet ein byte-array.
     */
    public static Liste<byte[]> split(byte[] sequence, byte[] input){
        if(sequence != null && input != null && sequence.length > 0){
            Liste<byte[]> l = new Liste<>();
            int pos = 0;
            for(int i=0; i<input.length; i++){
                boolean b = true;
                if((i+sequence.length-1)<input.length){
                    for(int j=0; j<sequence.length; j++){
                        if(sequence[j] != input[i+j]){
                            b = false;
                            j = sequence.length;
                        }
                    }
                }else{
                    b = false;
                    i = input.length;
                }
                
                if(b){
                    byte[] el = new byte[i-pos];
                    System.arraycopy(input, pos, el, 0, i-pos);
                    pos = i+sequence.length;
                    i += sequence.length-1;
                    l.hintersLetzteEinfuegen(el);
                }
            }
            byte[] el = new byte[input.length-pos];
            System.arraycopy(input, pos, el, 0, el.length);
            l.hintersLetzteEinfuegen(el);
            
            return l;
        }else
            return null;
    }
    private static boolean equals(byte[] a, byte[] b){
        if(a != null && b != null){
            if(a.length == b.length){
                for(int i=0; i<a.length; i++){
                    if(a[i] != b[i])
                        return false;
                }
                return true;
            }else
                return false;
        }else if(a == null && b == null)
            return true;
        else
            return false;
    }
    /**
     * Analog zur Methode substring(int,int).
     */
    public static byte[] subbyte(byte[] b, int first, int last){
        if(first >= 0 && last >= 0 && first < b.length && last < b.length && first < last){
            byte[] ret = new byte[last-first];
            System.arraycopy(b, first, ret, 0, last-first);
            return ret;
        }else
            return null;
    }
    /**
     * Wertet die recent-Datei aus und gibt einen String[2][15]-array mit den letzten Verzeichnispfaden zurück.
     * (String[0][n] -> 1. Verzeichnis, String[1][n] -> 2. Verzeichnis)
     * Es können also maximal die letzten 15 Prozesse gespeichert werden.
     */
    /*public static String[][] getRecents(){
        File recent = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"recent");
        if(recent.exists() && !recent.isDirectory()){
            byte[] seq = {(byte)'\r', (byte)'\n'};
            Liste dat = Synchronizer.split(seq, Streamer.datenAuslesen(recent));
            //byte[][] ret = new byte[2][0];
            String[][] ret = new String[2][15];
            dat.toFirst();
            for(int i=0; i<ret[0].length; i++){
                String s = Streamer.bytearrayParsen((byte[])dat.getCurObject());
                s = s.replaceAll("/ae", "ä");
                s = s.replaceAll("/oe", "ö");
                s = s.replaceAll("/ue", "ü");
                String[] ss = s.split(":");
                ret[0][i] = ss[0];
                ret[1][i] = ss[1];
                dat.next();
            }
            
            return ret;
        }else
            return null;
    }*/
    
    /**
     * Eine innere Klasse, die von der Klasse "library.lists.Liste" erbt.
     * Sie wurde eigens für dieses Programm "zugeschnitten".
     * Sie stellt die Verzeichnisse und Dateien dar, indem ein Objekt der Klasse "ListWithName"
     * ein Verzeichnis darstellt und ein Objekt der Klasse "byte[]" eine Datei.
     * In dieser Klasse wird außerdem die spätere Größe des log-Files organisiert (die ist nötig,
     * da es sich um byte[]-array handelt).
     */
    private static class ListWithName extends Liste<Object>{
        private int byteSize, l;
        private ListWithName over;
        private byte[] name;
        /**
         * Erzeugt ein "Verzeichnis".
         */
        public ListWithName(byte[] name){
            super();
            this.byteSize = 0;
            this.l = 0;
            this.name = name;
            this.over = null;
            if(name != null){
                this.byteSize = name.length+1;//+V
            }
        }
        /**
         * Erzeugt ein "Unterverzeichnis", dem das jeweilige "Oberverzeichnis" übergeben werden muss.
         */
        public ListWithName(byte[] name, ListWithName over){
            super();
            this.byteSize = 0;
            this.name = name;
            this.over = over;
            if(name != null){
                this.byteSize = name.length+1;//+V
            }
        }
        private void addToOver(int zahl){
            this.byteSize+=zahl;
            this.l++;
            if(this.over != null){
                this.over.addToOver(zahl+1);//+ \t
            }
        }
        private void removeToOver(int betrag){
            this.byteSize-=betrag;
            this.l--;
            if(this.over != null){
                this.over.removeToOver(betrag+1);//+ \t
            }
        }
        public void removeCur(){
            if(getCurIndex() != -1){
                Object object = getCurObject();
                int toOver = 0;
                if(object.getClass() == byte[].class){
                    if(this.byteSize > 0){
                        toOver+=2;
                    }
                    byte[] b = (byte[])object;
                    toOver+=b.length+2;
                    removeToOver(toOver);
                }else{
                    if(this.byteSize > 0){
                        toOver+=2;
                    }
                    ListWithName l = (ListWithName)object;
                    toOver+=l.byteSize+l.l+1;
                    removeToOver(toOver);
                    this.l-=l.l;
                }
                removeCurObject();
            }
        }
        public void add(Object object){
            if(object != null){
                hintersLetzteEinfuegen(object);
                if(object.getClass() == byte[].class){
                    int toOver = 0;
                    if(this.byteSize > 0){
                        toOver+=2;
                    }
                    byte[] b = (byte[])object;
                    toOver+=b.length+2;
                    addToOver(toOver);
                }else{
                    int toOver = 0;
                    if(this.byteSize > 0){
                        toOver+=2;
                    }
                    ListWithName l = (ListWithName)object;
                    toOver+=l.byteSize+l.l+1;
                    addToOver(toOver);
                    this.l+=l.l;
                }
            }
        }
        public void addDir(File dir){
            if(dir != null && dir.exists() && dir.isDirectory()){
                File[] fList = dir.listFiles();
                ListWithName cur = new ListWithName(Streamer.stringParsen(dir.getName()), this);
                add(cur);
                for(int i=0; i<fList.length; i++){
                    if(fList[i].isDirectory()){
                        cur.addDir(fList[i]);
                    }else{
                        byte[] name = Streamer.stringParsen(fList[i].getName());
                        cur.add(name);
                    }
                }
            }
        }
        public byte[] getName(){
            return this.name;
        }
        public int getByteSizeForLogDat(){
            return this.byteSize;
        }
        public int getL(){
            return this.l;
        }
    }
}
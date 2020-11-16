import java.io.File;
import library.streaming.Streamer;
import library.lists.Liste;
import java.io.InputStream;
/**
 * Main-Klasse. Enthält nur die main-Methode, eine Methode, die von der GUI verwendet wird und den Namen des Programms.
 * 
 * @author Leon Haffmans
 * @version 21. 06. 2015
 */
public class Main
{
    public static String[] setInfos = getSettInfos();
    public static String[] language = {"Datei","Sourceverzeichnis öffnen","Zielverzeichnis öffnen","Programm aktualisieren","Kürzlich synchronisiert","Hilfe","Beschreibung","Geben Sie das Source-Verzeichnis an","Geben Sie das Zielverzeichnis an",
                                "Fortschritt","Verbleibende Zeit","Synchronisieren","Abbrechen","Log-Datei erstellen","Scannen","Das Programm wird aktualisiert","Es ist ein Fehler beim Aktualisieren aufgetreten","Error: Die readme-datei konnte nicht geöffnet werden",
                                "Verzeichnisse werden gescannt.","Die Recent-datei scheint beschädigt zu sein","Recent-datei konnte nicht gelöscht werden","Gesamtanzahl der Dateien","Dateien","Dateien ohne Endung","Scan wurde abgebrochen","Keine zwei Verzeichnisse ausgewählt",
                                "Scan abgeschlossen","Synchronisierung gestartet","Zeitberechnung","Berechnung der Größe der beiden Verzeichnisse","Der Vorgang wurde erfolgreich abgeschlossen","Der Vorgang wurde abgebrochen","Es sind folgende Fehler aufgetreten",
                                "Log-Datei","wurde erstellt","Optionen","Synchronisationsprogramm","Sprache","Übernehmen","Starten Sie das Programm neu, um die Änderungen zu übernehmen","und","Datei","Achtung: Sie wollen zwei Ordner mit ungleichem Namen synchronisieren. Soll fortgefahren werden?",
                                "Warnung","Recent-datei konnte nicht aktualisiert werden","Hintergrundbild","Bilddatei","Bilddatei öffnen","Error: Bilddatei konnte nicht geladen werden","Log-File wird ausgelesen.","Error: Die beiden ausgewählten Verzeichnisse sind neu.",
                                "Bitte synchronisieren Sie ein Oberverzeichnis was beim letzten Synchronisationsvorgang schon existiert hat.","Log-File wird aktualisiert."};
    public static String nameOfProgram = "Synchronisationsprogramm";
    /**
     * Dieses Programm unterstützt maximal zwei Parameter (die Ordnerpfade, deren Ordner synchronisiert werden sollen).
     * Das Programm wird über eine Art Security-Manager laufen gelassen, die womögliche interne Fehler abfängt und einen Fehlerbericht erstellt, der zur Problemfindung dient.
     * Es wird außerdem die GUI erstellt, die den Prozess anzeigt (siehe Klasse "GUI").
     */
    public static void main(String[] args){
        if(setInfos == null){
            System.out.println("Error: 'settings'-Datei konnte nicht gefunden werden.");
            System.exit(0);
        }
        int len = language.length;
        if(!setInfos[1].equalsIgnoreCase("GE")){
            String[] lg = loadLan(setInfos[1]);
            if(lg != null){
                if(lg.length == language.length)
                    language = lg;
                else{
                    System.out.println("Error: Die 'language_"+setInfos[1]+"'-Datei ist möglicherweise unvollständig.");
                    System.exit(0);
                }
            }else{
                System.out.println("Error: Die 'language_"+setInfos[1]+"'-Datei konnte nicht geladen werden.");
                System.exit(0);
            }
        }
        nameOfProgram = language[36];
        
        GUI g = null;
        try{
            if(args.length == 0){
                g = new GUI();
            }else if(args.length == 1){
                String[] par = {args[0]};
                g = new GUI(par);
            }else if(args.length == 2){
                String[] par = {args[0], args[1]};
                g = new GUI(par);
            }else{
                g = new GUI();
                g.writeLine("Default: Dieses Programm unterstützt nur höchstens 2 Parameter. (die Ordnerpfade, deren Ordner synchronisiert werden sollen)");
            }
        }catch(Exception e1){
            String fehlerS = e1.getMessage()+"\r\n"+e1.toString();
            StackTraceElement[] e = e1.getStackTrace();
            for(int i=0; i<e.length; i++){
                fehlerS+="\r\n\tat "+e[i].toString();
            }
            if(g != null) g.writeLine("Error: \r\n"+fehlerS);
            if(g != null){
                File berichtF = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"fehlerbericht_"+g.getNamesForError()+".txt");
                if(Streamer.datenSchreiben(Streamer.stringParsen(g.getLogText()), berichtF, false)){
                    g.writeLine("\r\n'"+berichtF.getAbsolutePath()+"' wurde erstellt.");
                }else{
                    g.writeLine("\r\n'"+berichtF.getAbsolutePath()+"' konnte nicht erstellt werden: "+Streamer.getSchreibeFehler());
                }
            }
            
            if(g != null){
                g.writeLine("Zur Sicherheit wird die log-Datei dieses Prozesses gelöscht.");
                try{g.deleteLog();}catch(Exception e2){g.writeLine("Die Log-Datei konnte nicht gelöscht werden: "+e2.getMessage()+"\r\nDie Log-Datei sollte manuell gelöscht werden.");}
            }
            if(g != null){
                g.ready();
            }else{
                System.exit(0);
            }
        }
    }
    /**
     * Diese Methode wird ausgeführt, wenn auf den "Synchronisieren"-Button des GUI gedrückt wird.
     * Sie dient auch lediglich zum Abfangen interner Fehler und erstellt zur Not einen Fehlerbericht.
     */
    public static void start(GUI g, java.awt.event.ActionEvent evt, boolean scanMode){
        try{
            if(scanMode) g.scannen(evt);
            else g.start(evt);
        }catch(Exception e1){
            String fehlerS = e1.getMessage()+"\r\n"+e1.toString();
            StackTraceElement[] e = e1.getStackTrace();
            for(int i=0; i<e.length; i++){
                fehlerS+="\r\n\tat "+e[i].toString();
            }
            if(g != null) g.writeLine("Error: \r\n"+fehlerS);
            if(g != null){
                File berichtF = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"fehlerbericht_"+g.getNamesForError()+".txt");
                if(Streamer.datenSchreiben(Streamer.stringParsen(g.getLogText()), berichtF, false)){
                    g.writeLine("\r\n'"+berichtF.getAbsolutePath()+"' wurde erstellt.");
                }else{
                    g.writeLine("\r\n'"+berichtF.getAbsolutePath()+"' konnte nicht erstellt werden: "+Streamer.getSchreibeFehler());
                }
            }
            
            if(g != null){
                g.writeLine("Zur Sicherheit wird die log-Datei dieses Prozesses gelöscht.");
                try{g.deleteLog();}catch(Exception e2){g.writeLine("Die Log-Datei konnte nicht gelöscht werden: "+e2.getMessage()+"\r\nDie Log-Datei sollte manuell gelöscht werden.");}
            }
            if(g != null){
                g.ready();
            }else{
                System.exit(0);
            }
        }
    }
    
    private static String[] getSettInfos(){
        File set = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"settings");
        byte[] dat = Streamer.datenAuslesen(set);
        if(dat != null){
            byte[] seq = {(byte)'\r',(byte)'\n'};
            Liste<byte[]> list = Synchronizer.split(seq, dat);
            list.toFirst();
            String[] ret = new String[list.length()];
            for(int i=0; i<list.length(); i++){
                ret[i] = "";
                //byte[] dat = list.getCurObject();
                for(int j=list.getCurObject().length-1; j>=0; j--){
                    if(list.getCurObject()[j] != (byte)':'){
                        ret[i]=(char)(list.getCurObject()[j]) + ret[i];
                    }else j=-1;
                }
                list.next();
            }
            return ret;
        }else{
            return null;
        }
    }
    private static String[] loadLan(String lan){
        File fl = new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"language_"+lan);
        if(fl.exists()){
            byte[] dat = Streamer.datenAuslesen(fl), seq = {(byte)'\r',(byte)'\n'};
            Liste<byte[]> l = Synchronizer.split(seq, dat);
            String[] ret = new String[l.length()];
            l.toFirst();
            for(int i=0; i<l.length(); i++){
                ret[i] = Streamer.bytearrayParsen(l.getCurObject());
                l.next();
            }
            return ret;
        }else{
            //writeLine("Fehler beim Laden des Language-Paketes ("+lan+").");
            return null;
        }
    }
    public static int lanLength(){
        return language.length;
    }
    /*private static String[] loadLan2(String lan){
        Liste<String> ret = new Liste<>();
        InputStream is = Main.class.getClassLoader().getResourceAsStream("language_"+lan);
        if (is == null) return null;
        try{
            int count = 0;
            boolean r = false;
            String cur = "";
            while(is.available() > 0){
                char z = (char)is.read();
                if(r && z == '\n'){
                    ret.hintersLetzteEinfuegen(cur);
                    cur = "";
                    r = false;
                }else if(r){
                    r = false;
                    cur+='\r';
                }else if(z == '\r'){
                    r = true;
                }else{
                    cur+=z;
                }
            }
            String[] lang = new String[ret.length()];
            return ret.toArray(lang);
        }catch(Exception e){return null;}
    }*/
    /*public static synchronized byte[] getJarDat(String filename){
        InputStream is = Main.class.getClassLoader().getResourceAsStream(filename);
        
        if (is == null) return null;
        try
        {
            //System.out.println("a: "+is.available());
            while (is.available() > 0)
                System.out.print((char)is.read());
            //System.out.println();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/
}

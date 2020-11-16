package library.streaming;

import java.io.*;
/**
 * Diese Klasse ist für die Interaktion mit den Dateien zuständig.
 * Sie liest Daten aus, und schreibt sie.
 * Sie enthält drei Attribute, in denen die möglichen Fehler als String gespeichert werden.
 * Sie können in anderen Programmcodes dann einfach ausgelesen werden.
 * Sie enthält außerdem verschiedene Methoden, die benötigt werden, oder nützlich sein könnten.
 * 
 * @author Leon Haffmans
 * @version 09. 01. 2015
 */
public class Streamer
{
    private static String ausleseFehler = "";
    private static String schreibeFehler = "";
    private static String copyFehler = "";

    /**
     * Gibt den Fehler zurück, der bei der überladenen Methode "datenAuslesen" passiert ist.
     */
    public static String getAusleseFehler(){
        String ret = Streamer.ausleseFehler;
        Streamer.ausleseFehler = "";
        return ret;
    }

    /**
     * Gibt den Fehler zurück, der bei der überladenen Methode "datenSchreiben" passiert ist.
     */
    public static String getSchreibeFehler(){
        String ret = Streamer.schreibeFehler;
        Streamer.schreibeFehler = "";
        return ret;
    }

    /**
     * Gibt den Fehler zurück, der bei der Methode "copyFile" passiert ist.
     */
    public static String getCopyFehler(){
        String ret = Streamer.copyFehler;
        Streamer.copyFehler = "";
        return ret;
    }

    /**
     * Liest die Daten aus einem übergebenen File aus.
     * Wenn die Datei aus irgendeinem Grund nicht gelesen werden kann, gibt diese Methode "null" zurück.
     * Den Fehler kann man dann mit der Methode "getAusleseFehler()" erfahren.
     */
    public static byte[] datenAuslesen(File file){
        if(file != null && !file.isDirectory() && file.exists()){
            byte[] inhalt = null;
            FileInputStream input = null;
            try{
                input = new FileInputStream(file);
                inhalt = new byte[(int)file.length()];
                input.read(inhalt);

                Streamer.ausleseFehler = "";
            }catch(IOException e){
                Streamer.ausleseFehler = e.getMessage();
                inhalt = null;
            }finally{
                if(input != null) try{input.close();}catch(Exception e1){}
                return inhalt;
            }
        }else{
            if(file == null)
                Streamer.ausleseFehler = "Kein File uebergeben.";
            else if(file.isDirectory())
                Streamer.ausleseFehler = "File ist ein Verzeichnis.";
            else if(!file.exists())
                Streamer.ausleseFehler = "File existiert nicht.";
            return null;
        }
    }

    /**
     * Liest die Daten aus einem übergebenen File aus, speichert diese im übergebenen bytearray und gibt die Anzahl an bytes zurück, die im Array gespeichert wurden.
     * Wenn der Puffer nicht groß genug ist, wird ein Fehler erzeugt und es wird -1 zurückgegeben.
     * Wenn die Datei aus irgendeinem Grund nicht gelesen werden kann, gibt diese Methode -1 zurück.
     * Den Fehler kann man dann mit der Methode "getAusleseFehler()" erfahren.
     */
    public static int datenAuslesen(File file, byte[] puffer){
        if(file != null && !file.isDirectory() && file.exists() && puffer != null){
            int erg = -1;
            FileInputStream input = null;
            try{
                input = new FileInputStream(file);
                if(puffer.length >= (int)file.length()){
                    erg = input.read(puffer);
                    Streamer.ausleseFehler = "";
                }else{
                    Streamer.ausleseFehler = "Der übergebene Puffer ist nicht groß genug.";
                }
            }catch(IOException e){
                Streamer.ausleseFehler = e.getMessage();
                erg = -1;
            }finally{
                if(input != null)
                    try{input.close();}catch(Exception e1){}
                return erg;
            }
        }else{
            if(file == null)
                Streamer.ausleseFehler = "Kein File uebergeben.";
            else if(file.isDirectory())
                Streamer.ausleseFehler = "File ist ein Verzeichnis.";
            else if(!file.exists())
                Streamer.ausleseFehler = "File existiert nicht.";
            else if(puffer == null)
                Streamer.ausleseFehler = "Keinen Puffer übergeben.";
            return -1;
        }
    }

    /**
     * Diese Methode schreibt die übergebenen daten in das Outputfile.
     * Diese Methode geht davon aus, dass das array "daten" vom Anfang bis zum Ende "gefüllt" ist.
     * Der Parameter "change" gibt an, ob das Outputfile, falls es schon existiert, überschrieben werden soll.
     */
    public static boolean datenSchreiben(byte[] daten, File fileoutput, boolean change){
        if(daten != null)
            return Streamer.datenSchreiben(daten, daten.length, fileoutput, change, false);
        else{
            Streamer.schreibeFehler = "Keine Daten uebergeben.";
            return false;
        }
    }

    /**
     * Diese Methode schreibt die übergebenen daten in das Outputfile.
     * Diese Methode geht davon aus, dass das "daten" vom Anfang bis zum Ende nur Daten der Datei enthält.
     * Der Parameter "change" gibt an, ob das Outputfile, falls es schon existiert, geändert werden darf.
     * Der Parameter append gibt an, ob die Daten an die womöglich schon bestehenden Datei angefügt werden sollen.
     */
    public static boolean datenSchreiben(byte[] daten, File fileoutput, boolean change, boolean append){
        if(daten != null)
            return Streamer.datenSchreiben(daten, daten.length, fileoutput, change, append);
        else{
            Streamer.schreibeFehler = "Keine Daten uebergeben.";
            return false;
        }
    }

    /**
     * Diese Methode speichert die Daten des Parameters "daten" im File des Parameters "output".
     * Der Parameter "datenLen" gibt an, bis zu welcher Stelle im array "daten" auch wirklich Daten drinstehen.
     * Der Parameter "change" gibt an, ob die Datei des "output"-Files, die es möglicherweise schon gibt, geändert werden darf.
     * Der Parameter "append" gibt außerdem an, ob die Daten an die Datei angehängt werden sollen.
     * 
     * Die Methode gibt "false" zurück, falls der Vorgang nicht beendet werden konnte.
     * Der Fehler kann dann wieder mit "getSchreibeFehler()" herausgefunden werden.
     */
    public static boolean datenSchreiben(byte[] daten, int datenLen, File fileoutput, boolean change, boolean append){
        if(fileoutput != null && !fileoutput.isDirectory() && daten != null){
            if(change || !change && !fileoutput.exists()){
                boolean ret = false;
                FileOutputStream output = null;
                try{
                    output = new FileOutputStream(fileoutput, append);
                    output.write(daten, 0, datenLen);
                    output.flush();

                    Streamer.schreibeFehler = "";
                    ret = true;
                }catch(IOException e){
                    Streamer.schreibeFehler = e.getMessage();
                    ret = false;
                }finally{
                    if(output != null)
                        try{output.close();}catch(Exception e1){}
                    return ret;
                }
            }else{
                Streamer.schreibeFehler = "File existiert bereits.";
                return false;
            }
        }else{
            if(fileoutput == null)
                Streamer.schreibeFehler = "Kein File uebergeben.";
            else if(fileoutput.isDirectory())
                Streamer.schreibeFehler = "File ist ein Verzeichnis.";
            else if(daten == null)
                Streamer.schreibeFehler = "Keine Daten uebergeben.";
            return false;
        }
    }

    /**
     * Diese Methode kopiert eine Datei "File input" nach "File output".
     * Der zusätzliche Parameter "outputOwerwrite" gibt an, ob die Datei, falls sie bereits existiert, überschrieben werden darf.
     * 
     * Falls der Vorgang fehlschlägt, gibt die Methode "false" zurück.
     * Den Fehler findet man mit "getCopyFehler()" heraus.
     */
    public static boolean copyFile(File input, File output, boolean outputOverwrite){
        if(outputOverwrite){
            if(input != null && input.exists() && !input.isDirectory() &&
            output != null && !output.isDirectory()){
                if(output.exists()){
                    boolean ret = false;
                    String sp = "";
                    FileInputStream stin = null;
                    FileOutputStream stout = null;
                    try{
                        stin = new FileInputStream(input);
                        stout = new FileOutputStream(output);
                        byte[] daten = new byte[(int)input.length()];
                        int anzahl = stin.read(daten);
                        stout.write(daten, 0, anzahl);
                        ret = true;
                    }catch(IOException e){
                        ret = false;
                        sp = e.getMessage();
                    }finally{
                        try{stin.close();}catch(Exception e2){}
                        try{stout.close();}catch(Exception e3){}
                        Streamer.copyFehler = sp;
                        return ret;
                    }
                }else{
                    try{
                        if(output.createNewFile()){
                            boolean ret = false;
                            String sp = "";
                            FileInputStream stin = null;
                            FileOutputStream stout = null;
                            try{
                                stin = new FileInputStream(input);
                                stout = new FileOutputStream(output);
                                byte[] daten = new byte[(int)input.length()];
                                int anzahl = stin.read(daten);
                                stout.write(daten, 0, anzahl);
                                ret = true;
                            }catch(IOException e){
                                ret = false;
                                sp = e.getMessage();
                            }finally{
                                try{stin.close();}catch(IOException e2){}
                                try{stout.close();}catch(IOException e3){}
                                Streamer.copyFehler = sp;
                                return ret;
                            }
                        }else{
                            Streamer.copyFehler = "Output-Datei konnte nicht erstellt werden.";
                            return false;
                        }
                    }catch(IOException e1){
                        Streamer.copyFehler = e1.getMessage();
                        return false;
                    }
                }
            }else{
                Streamer.copyFehler = "Input- und/oder Output-Datei fehlerhaft.";
                return false;
            }
        }else{
            if(input != null && input.exists() && !input.isDirectory() &&
            output != null && !output.isDirectory() && !output.exists()){
                boolean ret = false;
                String sp = "";
                FileInputStream stin = null;
                FileOutputStream stout = null;
                try{
                    stin = new FileInputStream(input);
                    stout = new FileOutputStream(output);
                    byte[] daten = new byte[(int)input.length()];
                    int anzahl = stin.read(daten);
                    stout.write(daten, 0, anzahl);
                    ret = true;
                }catch(IOException e){
                    ret = false;
                    sp = e.getMessage();
                }finally{
                    try{stin.close();}catch(Exception e2){}
                    try{stout.close();}catch(Exception e3){}
                    Streamer.copyFehler = sp;
                    return ret;
                }
            }else if(output.exists()){
                Streamer.copyFehler = "Output-Datei existiert bereits.";
                return false;
            }else{
                Streamer.copyFehler = "Input- und/oder Output-Datei fehlerhaft.";
                return false;
            }
        }
    }

    /**
     * Diese Methode kopiert ein Verzeichnis von "input" nach "output".
     */
    public static boolean copyDir(File input, File output){
        output.mkdirs();
        if (input != null && input.exists() && input.isDirectory()){
            File[] files = input.listFiles();
            File newFile = null;
            boolean ret = true;
            for(int i=0; i < files.length; i++){
                newFile = new File(output.getAbsolutePath()+System.getProperty("file.separator")+files[i].getName());
                if(files[i].isDirectory()){
                    if(!Streamer.copyDir(files[i], newFile))
                        ret = false;
                }else{
                    if(!Streamer.copyFile(files[i], newFile, false))
                        ret = false;
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
    public static boolean deleteDir(File dir){
        if(dir != null && dir.exists() && dir.isDirectory()){
            File[] list = dir.listFiles();
            boolean ret = true;
            for(int i=0; i<list.length; i++){
                if(list[i].isDirectory()){
                    if(!Streamer.deleteDir(list[i]))
                        ret = false;
                }else{
                    if(!list[i].delete())
                        ret = false;
                }
            }
            if (!dir.delete())
                ret = false;
            return ret;
        }else{
            return false;
        }
    }

  
    //***********************************weitere Methoden*******************************************
    
    /**
     * Wandelt einen String in ein byte-Array um.
     */
    public static byte[] stringParsen(String string){
        if(string != null){
            byte[] erg = new byte[string.length()];
            for(int i=0; i<string.length(); i++){
                erg[i] = (byte)string.charAt(i);
            }
            return erg;
        }else
            return null;
    }
    
    /**
     * Wandelt ein byte-Array in einen String um.
     */
    public static String bytearrayParsen(byte[] array){
        if(array != null){
            String erg = "";
            for(int i=0; i<array.length; i++){
                erg+=(char)array[i];
            }
            return erg;
        }else
            return null;
    }
    
    /**
     * Fügt am Dateinamen des Files "file" vor der Dateiendung den übergebenen String "text" an.
     */
    public static File anDateinamenAnfuegen(String text, File file){
        if(file != null){
            String newPfad = "";
            String[] strings = file.getAbsolutePath().split("\\.");

            for(int i=0; i<strings.length; i++){
                if(i == strings.length-2){
                    newPfad+=strings[i]+text+"."+strings[i+1];
                    i++;
                }else
                    newPfad+=strings[i]+".";
            }
            return new File(newPfad);
        }else
            return null;
    }

    /**
     * Ändert die Dateiendung des Files "file" zum übergebenen Parameter "end" um.
     * Bei "end" == "" wird die Dateiendung entfernt.
     */
    public static File setEnding(String end, File file){
        if(file != null && end != null){
            if(!end.equals("")){
                String newPfad = "";
                String[] strings = file.getAbsolutePath().split("\\.");

                for(int i=0; i<strings.length; i++){
                    if(i == strings.length-1){
                        newPfad+=end;
                        i++;
                    }else
                        newPfad+=strings[i]+".";
                }
                return new File(newPfad);
            }else{
                String newPfad = "";
                String[] strings = file.getAbsolutePath().split("\\.");

                for(int i=0; i<strings.length; i++){
                    if(i == strings.length-2){
                        newPfad+=strings[i];
                        i++;
                    }else
                        newPfad+=strings[i]+".";
                }
                return new File(newPfad);
            }
        }
        return null;
    }

    /**
     * Ändert den Namen des Files "file", verändert dabei aber nicht die Dateiendung.
     */
    public static File setName(String newName, File file){
        if(file != null && newName != null && !newName.equals("")){
            String newPfad = "";
            String[] strings = file.getAbsolutePath().split("\\\\");

            for(int i=0; i<strings.length; i++){
                if(i == strings.length-1){
                    newPfad+=newName;
                    i++;
                }else
                    newPfad+=strings[i]+"\\";
            }
            return new File(newPfad);
        }else
            return null;
    }

    /**
     * Gibt die Dateiendung eines Files zurück.
     */
    public static String getEnd(File file)
    {
        if(file != null && file.getAbsolutePath().lastIndexOf(".") > 0) return file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")+1);

        return null;
    }

    /**
     * Gibt das "Oberverzeichnis" zurück.
     */
    public static File getParentOf(File file){
        if(file != null){
            String path = file.getAbsolutePath(), newPath = "";
            int li = path.lastIndexOf("\\");
            for(int i=0; i<li; i++){
                newPath+=path.charAt(i);
            }
            return new File(newPath);
        }else
            return null;
    }
}

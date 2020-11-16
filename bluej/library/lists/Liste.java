package library.lists;

 


/**
 * Dies ist ein Beispiel für eine lineare Liste.
 * Sie hat stets einen separaten Zeiger auf das erste und das letzte Element.
 * Darüber hinaus kann mit einem "current"-Zeiger über die verschiedenen Methoden wie toFirst(), toLast(), next() oder back() die Liste Stück für Stück durchgegangen werden.
 * Die Liste kann auch mit Indizes arbeiten. Dies ist bei längeren Listen allerdings sehr Performancelastig, weil unter Umständen die ganze Liste durchgegangen werden muss.
 * Man kann auch zwei Elemente in der Liste vertauschen.
 * Die Liste kann aus einem <T>-Array erschaffen werden und sich selbst auch wieder in eines umwandeln.
 * Bei Erschaffung eines Objektes der Klasse "Liste" wird der Datentyp erwartet, mit dem die Liste arbeiten soll.
 * 
 * @author Leon Haffmans
 * @version 24. 05. 2015
 */
public class Liste<T>
{
    private Knoten<T> curEl, firstEl, lastEl;
    private int length, curIndex;
    
    /**
     * Erschafft eine leere Liste.
     */
    public Liste(){
        this.curEl = null;
        this.firstEl = null;
        this.lastEl = null;
        this.length = 0;
        this.curIndex = -1;
    }
    /**
     * Erschafft eine Liste aus dem übergebenen Array.
     */
    public Liste(T[] array){
        this();
        if(array != null){
            for(int i=0; i<array.length; i++){
                hintersLetzteEinfuegen(array[i]);
            }
        }
    }
    
    /**
     * Gibt die Länge der Liste zurück.
     */
    public int length(){
        return this.length;
    }
    
    /**
     * Fügt am Ende der Liste ein Element hinzu.
     */
    public void hintersLetzteEinfuegen(T object){
        if(length == 0){
            Knoten<T> k = new Knoten<>(object, null, null);
            this.firstEl = k;
            this.lastEl = k;
        }else{
            this.lastEl = new Knoten<>(object, this.lastEl, null);
        }
        this.length++;
    }
    /**
     * Fügt vor das letzte Element ein neues Element ein.
     */
    public void vorsLetzteEinfuegen(T object){
        if(length == 0){
            Knoten<T> k = new Knoten<>(object, null, null);
            this.firstEl = k;
            this.lastEl = k;
        }else{
            if(this.curEl != null)
                if(this.curEl.equals(this.lastEl))
                    this.curIndex++;
            new Knoten<T>(object, this.lastEl.getLast(), this.lastEl);
        }
        this.length++;
    }
    /**
     * Fügt hinter das erste Element ein neues Element ein.
     */
    public void hintersErsteEinfuegen(T object){
        if(length == 0){
            Knoten<T> k = new Knoten<>(object, null, null);
            this.firstEl = k;
            this.lastEl = k;
        }else{
            if(this.curEl != null)
                if(!this.curEl.equals(this.firstEl))
                    this.curIndex++;
            new Knoten<T>(object, this.firstEl, this.firstEl.getNext());
        }
        this.length++;
    }
    /**
     * Fügt an erster Stelle ein neues Element ein.
     */
    public void vorsErsteEinfuegen(T object){
        if(length == 0){
            Knoten<T> k = new Knoten<>(object, null, null);
            this.firstEl = k;
            this.lastEl = k;
        }else{
            this.firstEl = new Knoten<>(object, null, this.firstEl);
            if(this.curEl != null)
                this.curIndex++;
        }
        this.length++;
    }
    /**
     * Fügt hinter dem aktuellen Element ein neues ein.
     * Wenn der current-Zeiger auf kein Element verweist, passiert nichts.
     */
    public void hintersAktuelleEinfuegen(T object){
        if(this.curEl != null){
            if(this.curEl.equals(this.firstEl)){
                hintersErsteEinfuegen(object);
            }else if(this.curEl.equals(this.lastEl)){
                hintersLetzteEinfuegen(object);
            }else{
                new Knoten<T>(object, this.curEl, this.curEl.getNext());
                this.length++;
            }
        }
    }
    /**
     * Fügt vor dem aktuellen Element ein neues ein.
     * Wenn der current-Zeiger auf kein Element verweist, passiert nichts.
     */
    public void vorsAktuelleEinfuegen(T object){
        if(this.curEl != null){
            if(this.curEl.equals(this.firstEl)){
                vorsErsteEinfuegen(object);
            }else if(this.curEl.equals(this.lastEl)){
                vorsLetzteEinfuegen(object);
            }else{
                new Knoten<T>(object, this.curEl.getLast(), this.curEl);
                this.curIndex++;
                this.length++;
            }
        }
    }
    
    /**
     * Ersetzt das aktuelle Element durch ein anderes.
     * Wenn der current-Zeiger auf kein Element verweist, passiert nichts.
     */
    public void replaceCurObject(T object){
        if(this.curEl != null){
            this.curEl.setObject(object);
        }
    }
    /**
     * Ersetzt das erste Element durch ein anderes.
     * Wenn die Liste leer ist, passiert nichts.
     */
    public void replaceFirstObject(T object){
        if(this.firstEl != null){
            this.firstEl.setObject(object);
        }
    }
    /**
     * Ersetzt das letzte Element durch ein anderes.
     * Wenn die Liste leer ist, passiert nichts.
     */
    public void replaceLastObject(T object){
        if(this.lastEl != null){
            this.lastEl.setObject(object);
        }
    }
    
    /**
     * Entfernt das aktuelle Element.
     * Der current-Zeiger wird auf das darauffolgende Element gesetzt.
     * Wenn es bereits das Ende der Liste ist, verweist der current-Zeiger auf kein Element mehr (null).
     * Wenn der current-Zeiger auf kein Element verweist, passiert nichts.
     */
    public void removeCurObject(){
        if(this.curEl != null){
            if(this.curEl.equals(this.firstEl))
                removeFirstObject();
            else if(this.curEl.equals(this.lastEl))
                removeLastObject();
            else{
                Knoten<T> c = this.curEl;
                this.curEl = this.curEl.getNext();
                c.remove();
                this.length--;
                if(this.curEl == null)
                    this.curIndex = -1;
            }
        }
    }
    /**
     * Entfernt das erste Element.
     * Wenn die Liste leer ist, passiert nichts.
     */
    public void removeFirstObject(){
        if(this.length == 1){
            Knoten<T> f = this.firstEl;
            this.firstEl = null;
            this.lastEl = null;
            this.curEl = null;
            f.remove();
            this.length--;
            this.curIndex = -1;
        }else if(this.length > 0){
            if(this.curEl != null)
                if(this.curEl.equals(this.firstEl)){
                    this.curEl = this.firstEl.getNext();
                }else{
                    this.curIndex--;
                }
            Knoten<T> f = this.firstEl;
            this.firstEl = f.getNext();
            f.remove();
            this.length--;
        }
    }
    /**
     * Entfernt das letzte Element.
     * Wenn die Liste leer ist, passiert nichts.
     */
    public void removeLastObject(){
        if(this.length == 1){
            Knoten<T> f = this.lastEl;
            this.firstEl = null;
            this.lastEl = null;
            this.curEl = null;
            f.remove();
            this.length--;
            this.curIndex = -1;
        }else if(this.length > 0){
            if(this.curEl != null)
                if(this.curEl.equals(this.lastEl)){
                    this.curEl = null;
                    this.curIndex = -1;
                }
            Knoten<T> f = this.lastEl;
            this.lastEl = f.getLast();
            f.remove();
            this.length--;
        }
    }
    
    /**
     * Gibt das aktuelle Element zurück.
     * Wenn der current-Zeiger auf kein Element verweist, wird null zurückgegeben.
     */
    public T getCurObject(){
        if(this.curEl != null)
            return this.curEl.getObject();
        else
            return null;
    }
    /**
     * Gibt das erste Element zurück.
     * Wenn die Liste leer ist, wird null zurückgegeben.
     */
    public T getFirstObject(){
        if(this.firstEl != null)
            return this.firstEl.getObject();
        else
            return null;
    }
    /**
     * Gibt das letzte Element zurück.
     * Wenn die Liste leer ist, wird null zurückgegeben.
     */
    public T getLastObject(){
        if(this.lastEl != null)
            return this.lastEl.getObject();
        else
            return null;
    }
    
    /**
     * Setzt den current-Zeiger auf das darauffolgende Element.
     * Wenn es bereits das Ende der Liste ist, verweist der current-Zeiger auf kein Element mehr (null).
     * Wenn der current-Zeiger auf kein Element verweist, passiert nichts.
     */
    public void next(){
        if(this.curEl != null){
            this.curEl = this.curEl.getNext();
            if(this.curEl != null)
                this.curIndex++;
            else
                this.curIndex = -1;
        }
    }
    /**
     * Setzt den current-Zeiger auf das vorherige Element.
     * Wenn es bereits der Anfang der Liste ist, verweist der current-Zeiger auf kein Element mehr (null).
     * Wenn der current-Zeiger auf kein Element verweist, passiert nichts.
     */
    public void back(){
        if(this.curEl != null){
            this.curEl = this.curEl.getLast();
            if(this.curEl != null)
                this.curIndex--;
            else
                this.curIndex = -1;
        }
    }
    /**
     * Setzt den current-Zeiger auf das erste Element.
     * Wenn die Liste leer ist, passiert nichts.
     */
    public void toFirst(){
        if(this.firstEl != null){
            this.curEl = this.firstEl;
            this.curIndex = 0;
        }
    }
    /**
     * Setzt den current-Zeiger auf das letzte Element.
     * Wenn die Liste leer ist, passiert nichts.
     */
    public void toLast(){
        if(this.lastEl != null){
            this.curEl = this.lastEl;
            this.curIndex = this.length-1;
        }
    }
    /**
     * Setzt den current-Zeiger auf das Element mit dem übergebenen Index.
     * Wenn die Liste leer ist oder der Index nicht in der Liste vorhanden ist, passiert nichts.
     */
    public void goTo(int index){
        if(index >= 0 && index < this.length){
            if(index < this.length/2){
                if(this.curIndex != -1){
                    if(this.curIndex-index < index && this.curIndex > index){
                        for(int i=this.curIndex; i>index; i--){
                            this.curEl = this.curEl.getLast();
                        }
                    }else if(this.curIndex < index){
                        for(int i=this.curIndex; i<index; i++){
                            this.curEl = this.curEl.getNext();
                        }
                    }else{
                        this.curEl = this.firstEl;
                        for(int i=0; i<index; i++){
                            this.curEl = this.curEl.getNext();
                        }
                    }
                }else{
                    this.curEl = this.firstEl;
                    for(int i=0; i<index; i++){
                        this.curEl = this.curEl.getNext();
                    }
                }
            }else{
                if(this.curIndex != -1){
                    if(index-this.curIndex < this.length-1-index && this.curIndex < index){
                        for(int i=this.curIndex; i<index; i++){
                            this.curEl = this.curEl.getNext();
                        }
                    }else if(this.curIndex > index){
                        for(int i=this.curIndex; i>index; i--){
                            this.curEl = this.curEl.getLast();
                        }
                    }else{
                        this.curEl = this.lastEl;
                        for(int i=this.length-1; i>index; i--){
                            this.curEl = this.curEl.getLast();
                        }
                    }
                }else{
                    this.curEl = this.lastEl;
                    for(int i=this.length-1; i>index; i--){
                        this.curEl = this.curEl.getLast();
                    }
                }
            }
            this.curIndex = index;
        }
    }
    /**
     * Gibt den Index des Aktuellen Elements an.
     * Wenn der current-Zeiger auf kein Element verweist, wird -1 zurückgegeben.
     */
    public int getCurIndex(){
        return this.curIndex;
    }
    /**
     * Vertauscht das aktuelle Element mit dem des übergebenen Index.
     * Wenn der current-Zeiger auf kein Element verweist oder der Index nicht in der Liste vorhanden ist, passiert nichts.
     */
    public void tauscheMit(int index){
        if(this.curEl != null && index >= 0 && index < this.length && this.curIndex != index){
            int cI = this.curIndex;
            Knoten<T> c = this.curEl;
            goTo(index);
            c.tauscheMit(this.curEl);
            if(c.equals(this.firstEl))
                this.firstEl = this.curEl;
            else if(c.equals(this.lastEl))
                this.lastEl = this.curEl;
            if(this.curEl.equals(this.firstEl))
                this.firstEl = c;
            else if(this.curEl.equals(this.lastEl))
                this.lastEl = c;
            this.curIndex = cI;
        }
    }
    /**
     * Vertauscht zwei Elemente in der Liste.
     * Wenn einer der beiden Indizes nicht in der Liste vorhanden ist, passiert nichts.
     */
    public void tausche(int index1, int index2){
        if(index1 >= 0 && index1 < this.length && index2 >= 0 && index2 < this.length && index1 != index2){
            if(this.curIndex == index1){
                tauscheMit(index2);
            }else if(this.curIndex == index2){
                tauscheMit(index1);
            }else{
                Knoten<T> c = this.curEl;
                int cI = this.curIndex;
                goTo(index1);
                tauscheMit(index2);
                this.curEl = c;
                this.curIndex = cI;
            }
        }
    }
    
    /**
     * Wandelt die Liste in ein Array um und gibt diesen zurück.
     * Ist die Liste leer, wird ein leeres Array zurückgegeben.
     * Das zurückgegebene Array ist so lang wie das übergebene Array, außer dieses Array ist zu kurz. In dem Fall ist das zurückgegebene Array so lang wie die Liste. (Übrige Elemente bleiben so wie sie sind.)
     * Die Elemente der Liste werden ab dem Index 0 im Array gespeichert.
     */
    @SuppressWarnings("unchecked") public T[] toArray(T[] a) {
        return toArray(a,0);
    }
    
    /**
     * Wandelt die Liste in ein Array um und gibt diesen zurück.
     * Ist die Liste leer, wird ein leeres Array zurückgegeben.
     * Das zurückgegebene Array ist so lang wie das übergebene Array, außer dieses Array ist zu kurz. In dem Fall ist das zurückgegebene Array so lang wie die Liste. (Übrige Elemente bleiben so wie sie sind.)
     * Die Elemente der Liste werden ab dem Index "offset" im Array gespeichert.
     */
    @SuppressWarnings("unchecked") public T[] toArray(T[] a, int offset) {
        if(a != null){
            if (a.length < this.length)
                a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), this.length);
            int i = 0;
            for (Knoten<T> k = this.firstEl; k != null; k = k.getNext())
                a[i++] = k.getObject();
            
            if (a.length > this.length)
                a[this.length] = null;
            
            return a;
        }else
            return null;
    }
    
    /**
     * Gibt die Liste auf der Konsole aus.
     */
    @Deprecated public void listeAusgeben(){
        Knoten<T> k = this.firstEl;
        for(int i=0; i<this.length; i++){
            System.out.println(i+": "+k.getObject());
            k = k.getNext();
        }
    }
    
    
    
    /**
     * Diese innere Klasse stellt einen Knoten in der Liste dar.
     * Sie beinhaltet das Element und die Zeiger zum nächsten bzw. zum vorherigen Knoten.
     * Sie ist protected, damit alle Klassen, die von dieser Liste erben, die Klasse verwenden können.
     */
    protected class Knoten<T>{
        private T object;
        private Knoten<T> last, next;
        
        /**
         * Erzeugt einen Knoten mit den Zeigern auf den vorherigen und den nächsten Knoten.
         */
        public Knoten(T object, Knoten<T> last, Knoten<T> next){
            this.object = object;
            this.last = last;
            this.next = next;
            
            if(last != null)
                last.setNext(this);
            if(next != null)
                next.setLast(this);
        }
        
        private void setNext(Knoten<T> next){
            this.next = next;
        }
        private void setLast(Knoten<T> last){
            this.last = last;
        }
        /**
         * Hier kann das beinhaltete Element überschrieben werden.
         */
        public void setObject(T object){
            this.object = object;
        }
        
        /**
         * Gibt den vorherigen Knoten zurück.
         */
        public Knoten<T> getLast(){
            return this.last;
        }
        /**
         * Gibt den nächsten Knoten zurück.
         */
        public Knoten<T> getNext(){
            return this.next;
        }
        /**
         * Gibt das Element zurück.
         */
        public T getObject(){
            return this.object;
        }
        
        /**
         * Entfernt den Knoten, und korrigiert zu diesem Zwecke die Zeiger, der anderen.
         */
        public void remove(){
            if(this.last != null){
                this.last.setNext(this.next);
            }
            if(this.next != null){
                this.next.setLast(this.last);
                this.next = null;
            }
            if(this.last != null){
                this.last = null;
            }
        }
        /**
         * tauscht den Knoten mit dem übergebenen, und korrigiert zu diesem Zwecke die Zeiger.
         */
        public void tauscheMit(Knoten<T> k){
            Knoten<T> kn = k.getNext(), kl = k.getLast();
            
            if(!k.equals(this.last))
                k.setLast(this.last);
            else
                k.setLast(this);
            if(!k.equals(this.next))
                k.setNext(this.next);
            else
                k.setNext(this);
            
            if(this.next != null)
                if(!k.equals(this.next))
                    this.next.setLast(k);
            if(this.last != null)
                if(!k.equals(this.last))
                    this.last.setNext(k);
            
            if(kn != null)
                if(!kn.equals(this))
                    kn.setLast(this);
            if(kl != null)
                if(!kl.equals(this))
                    kl.setNext(this);
            
            if(kn != null)
                if(!kn.equals(this))
                    this.next = kn;
                else
                    this.next = k;
            else
                this.next = null;
            if(kl != null)
                if(!kl.equals(this))
                    this.last = kl;
                else
                    this.last = k;
            else
                this.last = null;
        }
    }
}

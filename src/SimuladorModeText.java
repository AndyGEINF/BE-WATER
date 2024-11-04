/** @file SimuladorModeText.java
    @brief Classe SimuladorModeText
*/

/** @class SimuladorModeText
    @brief Simula les operacions de construcció, modificació i consulta d'una xarxa de distribució d'aigua a partir d'un fitxer de text i mostrar els resultats en un fitxer de text de sortida.
    @author Miquel Coll Barneto
*/

import java.io.*;
import java.util.*;

public class SimuladorModeText {

    private Xarxa xarxa = new Xarxa();///< Xarxa de distribució d'aigua

    /**
     * @brief Simula operacions sobre una xarxa de distribució d'aigua a partir d'un fitxer d'entrada.
     * @param fitxer_entrada El nom del fitxer de text que conté les operacions a realitzar sobre la xarxa.
     * @param fitxer_sortida El nom del fitxer de sortida on es guardarà el resultat de les operacions.
     * @pre fitxer_entrada és el nom d'un fitxer de text que conté una seqüència d'operacions a realitzar sobre una xarxa de distribució d'aigua.
     * @post S'han realitzat les operacions descrites al fitxer d'entrada sobre la xarxa de distribució d'aigua, i el resultat s'ha guardat al fitxer de sortida.
     * Si alguna operació no es pot realitzar, es mostrarà un missatge d'error per la sortida estàndard.
     * @throws Exception Si hi ha algun error.
     */
    public void simular(String fitxer_entrada, String fitxer_sortida){
        Boolean seguir=true;
        BufferedReader br=null;
        try{
            br = new BufferedReader(new FileReader(fitxer_entrada));
        }
        catch (Exception e){
            System.out.println("Error amb el fitxer d'entrada");
            seguir=false;
        }
        Writer out=null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fitxer_sortida), "UTF-8"));
        }
        catch (Exception e){
            System.out.println("Error amb el fitxer de sortida");
            seguir=false;
        }
        if(seguir){
            try{
                String linea;
                // Llegeix fins acabar el fitxer d'entrada
                while ((linea = llegir(br)) != null) {//fitxer no acabat
                    gestionarOpcions(br, out, linea);
                }
                out.close();
            }
            catch (Exception e){
                escriure(out,"Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * @brief Gestiona les diferents opcions segons la línia llegida del fitxer d'entrada.
     *
     * @param br BufferedReader utilitzat per llegir el fitxer d'entrada.
     * @param out Writer utilitzat per escriure al fitxer de sortida.
     * @param linea Línia llegida del fitxer d'entrada
     * 
     * @pre El BufferedReader (br) i el Writer (out) no han de ser nuls
     * @post S'ha gestionat l'opció corresponent a la línia llegida del fitxer d'entrada.
     * 
     * @throws IllegalArgumentException Si la línia llegida no correspon a cap opció vàlida.
     */
    private void gestionarOpcions(BufferedReader br, Writer out, String linea){
        if(linea.equals("terminal") || linea.equals("origen") || linea.equals("connexio")){
            altaPunt(br, linea);
        }
        else if(linea.equals("connectar")){
            connectar(br);
        }
        else if (linea.equals("abonar")) {
            abonar(br);
        } else if (linea.equals("tancar")) {
            tancar(br);
        } else if (linea.equals("obrir")) {
            obrir(br);
        } else if (linea.equals("backtrack")) {
            recular(br);
        } else if (linea.equals("cabal")) {
            establirCabal(br);
        } else if (linea.equals("demanda")) {
            establirDemanda(br);
        } else if (linea.equals("cicles")) {
            cicles(br, out);
        } else if (linea.equals("arbre")) {
            arbre(br, out);
        } else if (linea.equals("cabalminim")) {
            cabalMinim(br, out);
        } else if (linea.equals("excescabal")) {
            String aux=excesCabal(br, out);
            if(aux!=null){
                gestionarOpcions(br, out, aux);
            }
        } else if (linea.equals("situacio")) {
            String aux=situacio(br,out);
            if(aux!=null){
                gestionarOpcions(br, out, aux);
            }
        } else if (linea.equals("cabalabonat")) {
            cabalAbonat(br, out);
        } else if (linea.equals("proximitat")) {
            String aux=proximitat(br,out);
            if(aux!=null){
                gestionarOpcions(br, out, aux);
            }
        } else if (linea.equals("dibuix")) {
            dibuixar(br);
        } else if (linea.equals("max-flow")) {
            maxFlow(br);
        } else {
            throw new IllegalArgumentException("Opcio no valida");
        }
    }

    /**
     * @brief Comprova si la línia especificada correspon a una opció vàlida.
     *
     * @param linea Línia llegida del fitxer d'entrada
     * 
     * @pre La línia llegida (línea) no ha de ser nul·la.
     * @post Retorna true si la línia especificada correspon a una opció vàlida, i false en cas contrari.
     */
    private Boolean esOpcio(String linea){
        if (linea.equals("terminal") || linea.equals("origen") || linea.equals("connexio") ||
            linea.equals("connectar") || linea.equals("abonar") || linea.equals("tancar") ||
            linea.equals("obrir") || linea.equals("backtrack") || linea.equals("cabal") ||
            linea.equals("demanda") || linea.equals("cicles") || linea.equals("arbre") ||
            linea.equals("cabalminim") || linea.equals("excescabal") || linea.equals("situacio") ||
            linea.equals("cabalabonat") || linea.equals("proximitat") || linea.equals("dibuix") ||
            linea.equals("max-flow")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @brief Llegeix una línia del BufferedReader.
     *
     * @pre El BufferedReader (br) no ha de ser nul.
     * @post Retorna la línia llegida sense espais, o null si s'arriba al final del fitxer.
     */
    private String llegir(BufferedReader br){
        String linea="";
        try{
            linea=br.readLine();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        if(linea==null){
            return null;
        }
        return linea.replaceAll("\\s", "");//Eliminem tots els espais per evitar errors d'entrada
    }

    /**
     * @brief Escriu un missatge al Writer especificat.
     * 
     * @param out El Writer en què s'escriurà el missatge.
     * @param missatge El missatge que es vol escriure.
     * 
     * @pre \p out && \p missatge no ha de ser nul.
     * @post El missatge s'ha escrit correctament al fitxer de sortida.
     * 
     * @throws IOException Si es produeix un error d'entrada/sortida en escriure al Writer.
     */
    private void escriure(Writer out, String missatge){
        try{
            out.write(missatge);
            out.write(System.lineSeparator());
            out.flush(); // assegurar que el missatge s'envii correctament
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Retorna les coordenades corresponents
     * 
     * @param textC La cadena de text que conté les coordenades.
     * 
     * @pre El paràmetre 'textC' no ha de ser nul i ha de tenir un format vàlid per a ser convertit a coordenades.
     * @post Retorna un objecte Coordenades amb les coordenades convertides.
     * 
     * @throws IllegalArgumentException Si el format de la coordenada és incorrecte.
     */
    private Coordenades convCoordenades(String textC){ 
        try{
            String[] partes = textC.split(",");

            // latitud
            String[] latitud = partes[0].split(":");
            int grausLatitud = Integer.parseInt(latitud[0]);
            int minutsLatitud = Integer.parseInt(latitud[1]);
            double segonsLatitud = Double.parseDouble(latitud[2].substring(0, latitud[2].length() - 1)); // Eliminar la letra de la dirección
            char direccioLatitud = latitud[2].charAt(latitud[2].length() - 1); // Obtener la dirección

            // longitud
            String[] longitud = partes[1].split(":");
            int grausLongitud = Integer.parseInt(longitud[0]);
            int minutsLongitud = Integer.parseInt(longitud[1]);
            double segonsLongitud = Double.parseDouble(longitud[2].substring(0, longitud[2].length() - 1)); // Eliminar la letra de la dirección
            char direccioLongitud = longitud[2].charAt(longitud[2].length() - 1); // Obtener la dirección
            Coordenades c= new Coordenades(grausLatitud, minutsLatitud, segonsLatitud,direccioLatitud,grausLongitud,minutsLongitud,segonsLongitud,direccioLongitud);
            
            return c;
        }
        catch (Exception e){
            throw new IllegalArgumentException("Coordenada errònia - " + textC);
        }
    }

    /**
     * @brief Retorna un Terminal basat en el seu identificador.
     * 
     * @param id L'identificador del node a cercar.
     * 
     * @pre cert
     * @post Es retorna el Terminal corresponent a l'identificador si és vàlid.
     * 
     * @throws IllegalArgumentException Si l'identificador no correspon a cap Terminal.
     */
    private Terminal retornarTerminal(String id){
        Nodo node=xarxa.node(id);
        if(node instanceof Terminal){
            return (Terminal) node;
        }
        throw new IllegalArgumentException(id + " no és un terminal");
    }

    /**
     * @brief Retorna un Origen basat en el seu identificador.
     * @param id L'identificador del node a cercar.
     * @pre cert
     * @post Es retorna l'Origen corresponent a l'identificador si és vàlid.
     * @throws IllegalArgumentException Si l'identificador no correspon a cap Origen.
     */
    private Origen retornarOrigen(String id){
        Nodo node=xarxa.node(id);
        if(node instanceof Origen){
            return (Origen) node;
        }
        throw new IllegalArgumentException(id + " no és un origen");
    }

    /**
     * @brief Crear un nou node (Terminal, Origen o Connexió) a la xarxa.
     * @param opcio L'opció que indica el tipus de punt a afegir ('terminal', 'origen' o 'connexio').
     * @pre 'opcio' ha de ser una de les opcions vàlides
     * @post Es crea i s'afegeix el punt corresponent a la xarxa segons l'opció especificada.
     */
    private void altaPunt(BufferedReader br, String opcio){
        String nom=llegir(br);
        Coordenades c=convCoordenades(llegir(br));
        if(opcio.equals("terminal")){
            float cabal=Float.parseFloat(llegir(br));
            Terminal t= new Terminal(nom,c,cabal);
            xarxa.afegir(t);
        }
        else if(opcio.equals("origen")){
            Origen o= new Origen(nom,c);
            xarxa.afegir(o);
        }
        else{
            Connexio con= new Connexio(nom,c);
            xarxa.afegir(con);
        }
    }

    /**
     * @brief Connecta dos nodes de la xarxa amb una canonada.
     * @pre cert
     * @post Es connecten els dos nodes de la xarxa amb una canonada amb el cabal especificat.
     */
    private void connectar(BufferedReader br){
        Nodo n1=xarxa.node(llegir(br));
        Nodo n2=xarxa.node(llegir(br));
        float cabal=Float.parseFloat(llegir(br));
        xarxa.connectarAmbCanonada(n1,n2,cabal);
    }

    /**
     * @brief Abona un usuari a un terminal de la xarxa.
     * @pre El terminal especificat ha de ser vàlid
     * @post L'usuari s'abona correctament al terminal especificat a la xarxa.
     */
    private void abonar(BufferedReader br){
        String dni=llegir(br);
        Terminal t=retornarTerminal(llegir(br));
        xarxa.abonar(dni, t);
    }

    /**
     * @brief Obre l'aixeta
     * @pre L'identificador llegit ha de correspondre a un node existent a la xarxa.
     * @post L'aixeta del node corresponent s'obre.
     */
    private void obrir(BufferedReader br){
        Nodo node=xarxa.node(llegir(br));
        xarxa.obrirAixeta(node);
    }

    /**
     * @brief Tancar l'aixeta
     * @pre L'identificador llegit ha de correspondre a un node existent a la xarxa.
     * @post L'aixeta del node corresponent tancada.
     */
    private void tancar(BufferedReader br){
        Nodo node=xarxa.node(llegir(br));
        xarxa.tancarAixeta(node);
    }

    /**
     * @brief Recula els canvis d'estat de les aixetes
     * @pre cert
     * @post La xarxa recula el nombre de passos especificat.
     */
    private void recular(BufferedReader br){
        int n = Integer.parseInt(llegir(br));
        xarxa.recular(n);
    }

    /**
     * @brief Estableix el cabal d'un origen de la xarxa.
     * @pre cert
     * @post L'origen de la xarxa té el cabal especificat.
     */
    private void establirCabal(BufferedReader br){
        Origen o=retornarOrigen(llegir(br));
        float cabal=Float.parseFloat(llegir(br));
        xarxa.establirCabal(o, cabal);
    }

    /**
     * @brief Estableix la demanda d'un terminal de la xarxa.
     * @pre cert
     * @post El terminal de la xarxa té la demanda especificada.
     */
    private void establirDemanda(BufferedReader br){
        Terminal t=retornarTerminal(llegir(br));
        float demanda=Float.parseFloat(llegir(br));
        xarxa.establirDemanda(t, demanda);
    }

    /**
     * @brief Verifica si una xarxa té cicles
     * @pre cert
     * @post Escriu si la xarxa té cicles o no.
     */
    private void cicles(BufferedReader br, Writer out){
        Origen o=retornarOrigen(llegir(br));
        if(GestorXarxes.teCicles(xarxa, o)){
            escriure(out, o.id() + " te cicles");
        }
        else{
            escriure(out, o.id() + " no te cicles");
        }
    }

    /**
     * @brief Verifica si una xarxa és un arbre
     * @pre cert
     * @post Escriu si la xarxa és un arbre o no.
     */
    private void arbre(BufferedReader br, Writer out){
        Origen o=retornarOrigen(llegir(br));
        if(GestorXarxes.esArbre(xarxa, o)){
            escriure(out, o.id() + " es un arbre");
        }
        else{
            escriure(out, o.id() + " no es un arbre");
        }
    }

    /**
     * @brief Cabal mínim necessari
     * @pre La xarxa no té cicles
     * @post Calcular el cabal mínim que hi hauria d'haver als punts d'origen, per tal que cap terminal,
     * d'entre aquells on arribi aigua, no rebi menys del percentatge entrat.
     * @throws IllegalArgumentException Si la xarxa té cicles.
     */
    private void cabalMinim(BufferedReader br, Writer out){
        Origen o=retornarOrigen(llegir(br));
        if(GestorXarxes.teCicles(xarxa, o)){
            throw new IllegalArgumentException("La xarxa no hauria de tenir cicles");
        }
        else{
            String linea=llegir(br);
            float percentatge=Float.parseFloat(linea.substring(0, linea.length() - 1));
            float minim=GestorXarxes.cabalMinim(xarxa, o, percentatge);
            escriure(out,"cabal minim");
            escriure(out, Float.toString(minim));
        
        }
    }

    /**
     * @brief Detecta les canonades amb excés de cabal.
     * @pre Xarxa sense cicles
     * @post Les canonades amb excés de cabal són escrites en el fitxer de sortida.
     */
    private String excesCabal(BufferedReader br, Writer out){
        Set<Canonada> ctjCanonadas = new HashSet<>();
        String linea=llegir(br);
        while (linea!=null && !esOpcio(linea)) {//mentres no s'hagi acabat el fitxer i no sigui una opcio
            Canonada canonada = xarxa.canonada(linea);
            ctjCanonadas.add(canonada);
            linea=llegir(br);
        }
        Set<Canonada> exces=GestorXarxes.excesCabal(xarxa, ctjCanonadas);
        escriure(out, "exces cabal");
        for (Canonada canonada : exces) {
            escriure(out, canonada.id());
        }
        return linea;
    }

    /**
     * @brief Determina les aixetes a tancar.
     * @pre Els punts terminals entrats pertanyen a una xarxa en forma d'arbre
     * @post Les aixetes a tancar són escrites en el fitxer de sortida.
     * @throws IllegalArgumentException Si l'entrada del fitxer és incorrecte.
     */
    private String situacio(BufferedReader br, Writer out){
        Map<Terminal, Boolean> aiguaArriba = new HashMap<>();
        String linea=llegir(br);
        while (linea!=null && !esOpcio(linea)) {//mentres no s'hagi acabat el fitxer i no sigui una opcio
            String estat=linea.substring(linea.length() - 2);
            Terminal t=retornarTerminal(linea.substring(0, linea.length() - 2));
            if(estat.equals("NO")){
                aiguaArriba.put(t, false);
            }
            else if(estat.equals("SI")){
                aiguaArriba.put(t, true);
            }
            else{
                throw new IllegalArgumentException("Entrada incorrecte");
            }
            linea=llegir(br);
        }
        Set<Nodo> tancar=GestorXarxes.aixetesTancar(xarxa, aiguaArriba);
        escriure(out, "tancar");
        for(Nodo n : tancar){
            escriure(out, n.id());
        }
        return linea;
    }

    /**
     * @brief Cabal que hauria d'arribar a l'abonat
     * @pre Xarxa no té cicles
     * @post El cabal que hauria d'arribar a l'abonat és escrit al fitxer de sortida.
     */
    private void cabalAbonat(BufferedReader br, Writer out){
        float cabal=xarxa.cabalAbonat(llegir(br));
        escriure(out, "cabal abonat");
        escriure(out, Float.toString(cabal));
    }

    /**
     * @brief Llistar les aixetes ordenades segons la distància
     * @pre cert
     * @post Mostrar pel fitxer de sortida les aixetes ordenades segons la seva proximitat a la posició
     * geogràfica donada i, en cas d'empat, alfabèticament.
     */
    private String proximitat(BufferedReader br, Writer out){
        Set<Nodo> ctjNodo = new HashSet<>();
        Coordenades coord=convCoordenades(llegir(br));
        String linea=llegir(br);
        while (linea!=null && !esOpcio(linea)) {//mentres no s'hagi acabat el fitxer i no sigui una opcio
            Nodo n = xarxa.node(linea);
            ctjNodo.add(n);
            linea=llegir(br);
        }
        List<Nodo> Nodesordenats=GestorXarxes.nodesOrdenats(coord, ctjNodo);
        escriure(out, "proximitat");
        for (Nodo n : Nodesordenats) {
            escriure(out, n.id());
        }
        
        return linea;
    }
    
    /**
     * @brief Dibuixar xarxa
     * @pre cert
     * @post Mostrar un dibuix de la xarxa per pantalla.
     * @throws IllegalArgumentException Si l'entrada del fitxer és incorrecte.
     * 
     */
    private void dibuixar(BufferedReader br){
        Origen o=retornarOrigen(llegir(br));
        if(GestorXarxes.teCicles(xarxa, o)){
            throw new IllegalArgumentException("opció dibuix - La xarxa no hauria de tenir cicles");
        }
        else{
            xarxa.dibuixar(o);
        }
        
        
    }

    /**
     * @brief Calcular el flux màxim d'una xarxa
     * @pre cert
     * @post Es visualitzarà per pantalla el dibuix resultant de calcular el flux màxim de la xarxa.
     */
    private void maxFlow(BufferedReader br){
        Origen o=retornarOrigen(llegir(br));
        GestorXarxes.fluxMaxim(xarxa, o);
    }

}

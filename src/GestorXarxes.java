/** @file GestorXarxes.java
    @brief Classe GestorXarxes
*/

/** @class GestorXarxes
    @brief Mòdul funcional amb funcions per a la gestió de xarxes de distribució d'aigua
    @author Andy Moreno Ramon
*/
import java.util.*;
import java.util.stream.Stream;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public abstract class GestorXarxes { 
    private static final String SUPERORIGEN = "superOrigen";///< Atribut que té el node Super Origen
    private static final String SUPERTERMINAL = "superTerminal"; ///< Atribut que té el node Super Terminal
    private static final String atributFlux = "flux";///< Atribut que tenen els edges del graph, on guardarem el seu flux
    private static final String atributCapacitat = "capacitat"; ///< Atribut que tenen els edges del graph, on guardarem la seva capacitat
    
    /**
     * @brief Diu si la component connexa de la xarxa x que conté nodeOrigen té cicles.
     *
     * @param x Xarxa on volem trobar cicles.
     * @param nodeOrigen node Origen que ens indica quina component connexa volem avaluar.
     *
     *
     * @pre nodeOrigen pertany a la xarxa x
     * @post Retorna cert si la component connexa de la xarxa x que conté nodeOrigen té cicles,
     * fals altrament.
     *
     */
    public static boolean teCicles(Xarxa x, Origen nodeOrigen){

        boolean trobat=false;
        Map<String,ArrayList<String>> entrades= new HashMap<>();
        omplirEntrades(entrades,x, nodeOrigen);
        Iterator<Map.Entry<String, ArrayList<String>>> itM;
        itM=entrades.entrySet().iterator();
        while(itM.hasNext() && !trobat){
            Map.Entry<String, ArrayList<String>> seg = itM.next();
            String id=seg.getKey();
            List<String> visitats= new ArrayList<>();
            trobat=iteCicles(entrades, visitats, id);
            visitats.clear();
            
        }

        return trobat;

    }

    /**
     * @brief Emparella cada node amb una llista de quins nodes entren en aquest.
     * 
     * @param entrades per cada id d'un node hi haurà una llista de ids dels nodes que li entren.
     * 
     * @pre cert
     * @post Omple entrades de la component connexa de la Xarxa x on pertany nodeOrigen.
     * 
     */
    private static void omplirEntrades(Map<String,ArrayList<String>> entrades,Xarxa x, Origen nodeOrigen) {
        Graph subGfraf =x.componentConexa(nodeOrigen);
        for (Node node : subGfraf){
			Nodo n = new Nodo(x.node(node.getId()));
            Iterator<Canonada> itE= x.entrades(n);
            String nom= node.getId();
            entrades.put(nom,new ArrayList<>());
            while(itE.hasNext()) {
                String id=itE.next().node1().id();
                entrades.get(nom).add(id);
            }
        }
    }

    /**
     * @brief Part recursiva de la funció teCicles,
     * 
     * @param entrades per cada id d'un node hi haurà una llista de ids dels nodes que li entren.
     * 
     * @pre Els ids dels nodes de entrades pertanyen a una mateixa component connexa.
     * @post Si el node identificat per seg forma part d'un cicle en el graf representat per entrades, la funció retorna true.
     * Si el node identificat per seg no forma part d'un cicle en el graf, la funció retorna false.
     * La llista visitats pot haver estat modificada durant l'execució de la funció, però
     * es restaura al seu estat original abans de retornar.
     * 
     */
    private static boolean iteCicles(Map<String,ArrayList<String>> entrades, List<String> visitats, String seg){
        boolean trobat =false;
        if(visitats.contains(seg)){
            trobat=true;         
                
        }
        else{
            visitats.add(seg);
            if(entrades.containsKey(seg)){
                List<String> conexs= new ArrayList<>(entrades.get(seg));
                if(entrades.get(seg)!=null){
                    int i=0;
                    while(i< conexs.size() && !trobat){
                        trobat=iteCicles(entrades, visitats, conexs.get(i));
                        visitats.remove(conexs.get(i));
                        i++;
                    }
                }
            }
        }
        

        return trobat;
    }
    
    /**
     * @brief Indica si la component connexa a la qual pertany nodeOrigen és un arbre.
     *
     * @param entrades per cada id d'un node hi haurà una llista de ids dels nodes que li entren
     *
     * @pre nodeOrigen pertany a la xarxa x.
     * @post Retorna cert si la component connexa de la xarxa x que conté nodeOrigen no té cicles,
     * si el nombre de nodes-1 és igual a les arestes, i només té una arrel, fals altrament
     *
     */
    public static boolean esArbre(Xarxa x, Origen nodeOrigen){
        Graph subGfraf =x.componentConexa(nodeOrigen);
        int contArrels=0;
        for(Node nodeGraph: subGfraf){
            Nodo nodo=x.node(nodeGraph.getId());
            if(nodo instanceof Origen){
                contArrels++;
            }
        }
    
        return !teCicles(x, nodeOrigen) && subGfraf.getNodeCount()-1==subGfraf.getEdgeCount() && contArrels==1;
         
    }

    /**
     * @brief Cabal mínim donat un percentatge
     * 
     * @param percentatgeDemandaSatisfet percentatge de demanda que ha de satisfer cada terminal de manera proporcional.
     * 
     * @pre nodeOrigen pertany a la xarxa x, la component connexa de la xarxa x que conté nodeOrigen no té cicles,
     * i percentatgeDemandaSatisfet > 0.
     * @post Retorna el cabal mínim que hi hauria d’haver entre tots els nodes d’origen de la component connexa
     * de la xarxa x que conté nodeOrigen, per tal que cap node terminal de la mateixa component, d'entre aquells on 
     * arribi aigua, no rebi menys d'un percentatgeDemandaSatisfet% de la seva demanda.
     * 
     */
    public static float cabalMinim(Xarxa x, Origen nodeOrigen, float percentatgeDemandaSatisfet){
        float cabal=0;
        Graph subGraph=x.componentConexa(nodeOrigen);
        Set<Terminal> terminals= new HashSet<>();
        Set<String> explorats = new HashSet<>();
        trobarTerminals(x,nodeOrigen,terminals, explorats);//trobem tots els terminals de la component connexa
        Set<Terminal> auxTerminals = new HashSet<>(terminals);
        for(Terminal t: auxTerminals){
            if(!arribaNodeOrigen(t, subGraph,x)){//mirem si arriba cabal a t
                terminals.remove(t);
            }
        }
        //calculem cabal mínim
        for(Terminal t:terminals){
            float aux=t.demandaActual();
            if(aux==0){
                aux=t.demandaPunta();//si no té cap demanda actual interpretem la seva demanda punta
            }
            cabal+=aux*(percentatgeDemandaSatisfet/100);
        }
        return cabal;
        
    }

     
    
    /**
     * @brief Troba els terminals d'una component connexa de la xarxa.
     *
     * @param x Xarxa sobre la qual buscar les terminals.
     * @param n Node des del qual comença la cerca.
     * @param t Conjunt de terminals trobades fins al moment.
     * @param explorats Conjunt de nodes explorats per evitar repeticions.
     * 
     * @pre n pertany a la xarxa x.
     * @post Omple t de tots els terminals que es poden arribar des de n mirant les seves entrades i sortides
     * sempre que no estiguin visitades
     */
    private static void trobarTerminals(Xarxa x, Nodo n, Set<Terminal> t,Set<String> explorats){
        explorats.add(n.id());
        if(n instanceof Terminal){
            Terminal ter = (Terminal) n;
            if (ter.aixetaOberta()){      
                t.add(ter);
            }
        }
        Iterator<Canonada> itE = x.entrades(n);
        while(itE.hasNext()){
            Canonada canonadaE = itE.next();
            if(!explorats.contains(canonadaE.node1().id())){
                trobarTerminals(x, canonadaE.node1(), t, explorats);
            }
        }

        Iterator<Canonada> itS = x.sortides(n);
        while(itS.hasNext()){
            Canonada canonadaS = itS.next();
            if(!explorats.contains(canonadaS.node2().id())){
                Nodo aux= canonadaS.node2();
                trobarTerminals(x, aux, t, explorats);
                             
            }
        } 
    }
    
    /**
     * @brief Excés de cabal de les canonades
     *
     * @param ctjcCanonades canonades que volem saber si es satisfés la comanda dels terminals
     * es sobrepassaria la seva capacitat.
     * 
     * @pre Les canonades de cjtCanonades pertanyen a una mateixa component connexa, sense cicles, de la xarxa x
     * @post Retorna el subconjunt de canonades de cjtCanonades tals que, si es satisfés la demanda de tots els nodes
     * terminals de la mateixa component, es sobrepassaria la seva capacitat
     */
    public static Set<Canonada> excesCabal(Xarxa x, Set<Canonada> ctjcCanonadas){
        
        Graph graph = x.grafic();
        Set<Canonada> exces = new HashSet<>();
        for(Canonada c : ctjcCanonadas){
            Edge edgeGraph = graph.getEdge(c.id());
            if(edgeGraph!=null){
                if(cabalEntrant(edgeGraph, graph, x)>c.capacitat()){
                    exces.add(c);
                }
            }
        }
        
        
        
        return exces;
    }


    /**
     * @brief Cabal teòric d'un node
     * @pre \p nodo pertany a la xarxa
     * @post Retorna el cabal teòric al \p nodo segons la configuració actual de la xarxa
     * @throws NoSuchElementException si \p nodo no pertany a la xarxa
     */
    public static float cabal(Nodo nodo, Graph graph, Xarxa x){
        if(graph.getNode(nodo.id()) == null){//No pertany a la xarxa
            throw new NoSuchElementException("No pertany a la xarxa " + nodo.id());
        }
        if (!nodo.aixetaOberta()) { //aixeta tancada
            return 0;
        }
        if(nodo instanceof Origen){ //és un node origen
            Origen o = ((Origen)nodo);
            float demanda = demanda(nodo, graph,x);
            if(demanda>o.cabal()){
                return o.cabal();
            }
            else{
                return demanda;
            }
        }
        Node graphNode=graph.getNode(nodo.id());
        Stream<Edge> entrades=graphNode.enteringEdges();
        Iterable<Edge> iteradorEntrades = entrades::iterator;
        float cabal=0;
        for (Edge edge : iteradorEntrades) {
            cabal+=cabalEntrant(edge, graph, x);
        }
        return cabal;
    }

    /**
     * @brief Cabal teòric d'una canonada
     * @pre \p edge pertany a la xarxa
     * @post Retorna el cabal teòric de \p edge segons la configuració actual de la xarxa
     */
    private static float cabalEntrant(Edge edge, Graph graph, Xarxa x){
        Node nodeGraph=edge.getNode0();
        Nodo nodo=x.node(nodeGraph.getId());
        float cabalNode=cabal(nodo,graph,x); 
        float demandaNode=demanda(nodo,graph,x); 
        float demandaPropagada=demandaPropagada(edge, graph,x);
    
        return (demandaPropagada/demandaNode)*cabalNode;
    }

    /**
     * @brief Demanda teòrica d'un node
     * @pre \p nodo pertany a la xarxa
     * @post Retorna la demanda teòrica al \p nodo segons la configuració actual de la xarxa
     * @throws NoSuchElementException si \p nodo no pertany a la xarxa
     */
    public static float demanda(Nodo nodo, Graph graph,Xarxa x){
        if(graph.getNode(nodo.id()) == null){
            throw new NoSuchElementException("No pertany a la xarxa " + nodo.id());
        }
        if (!nodo.aixetaOberta()) { //aixeta tancada
            return 0;
        }
        if(nodo instanceof Terminal){ //es un node terminal
            return ((Terminal)nodo).demandaActual();
        }
        Node graphNode=graph.getNode(nodo.id());
        Stream<Edge> sortides=graphNode.leavingEdges();
        float demanda=0;
        Iterable<Edge> iteradorSortides = sortides::iterator;
        for (Edge edge : iteradorSortides) {
            demanda+=demandaPropagada(edge, graph,x);
        }
        return demanda;
        
    }
    



    /**
     * @brief Demanda propagada d'una canonada
     * @pre \p edge pertany a la xarxa
     * @post Retorna la demanda propagada del \p edge segons la configuració actual de la xarxa,
     * sense tenir en compte si la demanda és més gran que la capacitat de la canonada
     */
    private static float demandaPropagada(Edge edge, Graph graph, Xarxa x){
        Node nodeGraph=edge.getTargetNode();
        Nodo nodo=x.node(nodeGraph.getId());
        float demanda=demanda(nodo, graph,x);
        float capacitatsCanonades=capacitatsCanonades(nodeGraph.enteringEdges(),graph,x);
        Canonada canonada=x.canonada(edge.getId());
    
        float proporcio=canonada.capacitat()/capacitatsCanonades;
        return demanda*proporcio;
    

    }

    /**
     * @brief Suma de les capacitats d'un conjunt de canonades
     * @pre cert
     * @post Retorna la suma de les capacitats d'un conjunt de canonades
     * que poden rebre cabal
     */
    private static float capacitatsCanonades(Stream<Edge> entrades, Graph graph, Xarxa x){
        //No cal passar la capacitat de la canonada que ens condueix a un node amb aixeta tancada.
        Iterable<Edge> iteradorEntrades = entrades::iterator;
        float capacitats=0;
        for (Edge edge : iteradorEntrades) {
            Node nodeGraph=edge.getNode0();
            Nodo nodo=x.node(nodeGraph.getId());
            if(arribaNodeOrigen(nodo, graph,x)){//mirem si arriben a un node Origen
                Canonada canonada=x.canonada(edge.getId());
                capacitats+=canonada.capacitat();
            }
            
        }
    
        return capacitats;
    }

    /**
     * @brief Verifica si un node pot arribar a un node Origen
     * 
     * @param n Node des del qual inicia la cerca
     * 
     * @pre El node n existeix a la xarxa.
     * @post Retorna cert si és possible arribar des del node donat fins a un node origen a la xarxa,
     * sempre que les aixetes estiguin obertes, fals altrament.
     * 
     */
    private static boolean arribaNodeOrigen(Nodo n, Graph graph, Xarxa x){
        boolean trobat=false;
        if(n.aixetaOberta()){
            if(n instanceof Origen){
                trobat=true;
            }
            else{
                Node graphNode=graph.getNode(n.id());
                Stream<Edge> entrades=graphNode.enteringEdges();
                Iterator<Edge> itE = entrades.iterator();
                while(itE.hasNext() && !trobat){
                    Node nodeGraph=itE.next().getNode0();
                    
                    Nodo nodo=x.node(nodeGraph.getId());;
                    trobat=arribaNodeOrigen(nodo, graph,x);
                }
            }
        }
        return trobat;
    }

    /**
     * @brief Aixetes que s'han de tancar
     * 
     * @param aiguaArriba conjunt de punts terminals que reben o no aigua
     * 
     * @pre Tots els terminals de aiguaArriba pertanyen a la xarxa x, aiguaArriba.get(t) indica si arriba aigua a t,
     * i la xarxa x té forma d'arbre.
     * @post Retorna el conjunt de nodes n de la xarxa x més propers (seguint la topologia) als terminals t de
     * aiguaArriba, tals que per sota de n la situació actual de la xarxa és incoherent amb aiguaArriba
     * 
     */    
    public static Set<Nodo> aixetesTancar(Xarxa x, Map<Terminal,Boolean> aiguaArriba){
    
        Set<String> tancarNodos_string = new HashSet<>();//per fer la cerca
        Set<Nodo> tancarNodos = new HashSet<>();//per retornar
        for (Map.Entry<Terminal, Boolean> t : aiguaArriba.entrySet()) {
            if (!t.getValue()) {//ens interessen els que no reben aigua
                if(t.getKey().aixetaOberta()){
                    Iterator<Canonada> itE= x.entrades(x.node(t.getKey().id()));
                    while (itE.hasNext()) {
                        Nodo n= new Nodo(itE.next().node1());
                        tancarNodos.add(n);
                        tancarNodos_string.add(n.id());
                    }
                }
            }
            
        }
        Set<Nodo> resultat = new HashSet<>(tancarNodos);
        for (Nodo nodo : tancarNodos){
            boolean trobat=estanConectats(nodo,tancarNodos_string,x);
            if(trobat){//si un node està per sota d'un altre l'eliminem
                resultat.remove(nodo);
            }
        }

        return resultat;
    }

    
    /**
     * @brief Dos nodes estan connectats
     * 
     * @param tancarNodosStrings conjunt de ids de nodes que s'han de tancar
     * 
     * @pre n pertany a la xarxa.
     * @post Retorna cert si des de n es pot arribar a un node de tancarNodosStrings, fals altrament.
     * 
     */  
    private static boolean estanConectats(Nodo n, Set<String> tancarNodosStrings, Xarxa x)
    {
        boolean trobat=false;
        Iterator<Canonada> itE= x.entrades(n);
        while (itE.hasNext() && !trobat) {
            Nodo nodo = itE.next().node1();
            if(tancarNodosStrings.contains(nodo.id())){//miro si des dels nodes possibles a tancar es pot arribar a node
                trobat=true;
            }
            else{
                trobat=estanConectats(nodo,tancarNodosStrings,x);
            }
        }
        return trobat;
    }

    /**
     * @brief Ordena els nodes segons la distància a una coordenada
     * 
     * @param cjtNodes llista de nodes que es volen ordenar
     * 
     * @pre cert.
     * @post Retorna una llista amb els nodes de cjtNodes ordenats segons la seva distància a c i, en cas d'empat,
     * en ordre alfabètic dels seus identificadors.
     * 
     */
    public static List<Nodo> nodesOrdenats(Coordenades c, Set<Nodo> cjtNodes){
        int i =0;
        List<Nodo> nodos = new ArrayList<>();
        for(Nodo n:cjtNodes){
            Iterator<Nodo> itN =nodos.iterator();
            boolean trobat=false;
            while(!trobat && itN.hasNext()){
                Nodo aux= itN.next();
                if(n.coordenades().distancia(c)<aux.coordenades().distancia(c)){
                    trobat=true;
                }
                else if(n.coordenades().distancia(c)==aux.coordenades().distancia(c)){
                    int comparacio = n.id().compareTo(aux.id()); //si es més petit que 0 voldrà dir que n.id < aux.id alfabèticament
                    if (comparacio < 0) {
                        trobat=true;
                    }
                }
                i=nodos.indexOf(aux);//posició de la llista que hem d'inserir
                if(trobat){
                    nodos.add(i,n);
                }
                
            }

            if(!itN.hasNext() && !trobat){//afegim al final de la llista
                nodos.addLast(n);
            }
        }
        
        return nodos;
    }

    /**
     * @brief Dibuixa la xarxa amb un sol origen i un terminal
     * 
     * @pre nodeOrigen pertany a la xarxa x
     * @post Dibuixa el flux màxim que pot circular per la xarxa x, tenint en compte la capacitat de les canonades.
     * 
     */
    public static void fluxMaxim(Xarxa x, Origen nodeOrigen){
    
        Graph graphMaxFlow=crearGraphMaxFlow(x, nodeOrigen);

        Node s=graphMaxFlow.getNode(SUPERORIGEN);
        Node t=graphMaxFlow.getNode(SUPERTERMINAL);
        List<String> nodesVisitats=new ArrayList<>();;
        while(hiHaCami(s, t, nodesVisitats)){
            Float flow = minimCapacitat(graphMaxFlow, nodesVisitats);
            posarFlux(graphMaxFlow, nodesVisitats, flow);
            nodesVisitats.clear();
        }
        //dibuixar max flow
        s.setAttribute("ui.label", "S");
        t.setAttribute("ui.label", "T");
        Stream<Edge> arestes=graphMaxFlow.edges();
        Iterator<Edge> it=arestes.iterator();
        while(it.hasNext()){
            Edge edge=it.next();
            if(edge.hasAttribute(atributCapacitat)){
                Node source=edge.getSourceNode();
                Node target=edge.getTargetNode();
                Edge edge2=target.getEdgeToward(source);
                Float flux=(Float)edge2.getAttribute(atributFlux);
                Float capacitat=(Float)edge.getAttribute(atributCapacitat);
                edge.setAttribute("ui.label", Float.toString(flux) + "/" + Float.toString(capacitat+flux));
            }
            else{
                edge.setAttribute("ui.hide");
            }
        }
        graphMaxFlow.setAttribute("ui.stylesheet", "url('recursos\\estil2.css')");
		graphMaxFlow.display();
    }

    /**
     * @brief Camí de n1 a n2
     * 
     * @pre n1!=null i n2!=null
     * @post Retorna cert si ha trobat un camí de n1 a n2, fals altrament.
     * 
     */
    private static Boolean hiHaCami(Node n1, Node n2, List<String> visitats){
        visitats.add(n1.getId());
        return RhiHaCami(n1, n2, visitats);
    }

    /**
     * @brief Retorna si hi ha camí de n1 a n2
     * 
     * @pre n1!=null i n2!=null
     * @post Retorna cert si ha trobat un camí de n1 a n2,
     * i actualitza la llista de ids dels nodes visitats, fals altrament i visitats no s'actualitzarà
     * 
     */
    private static Boolean RhiHaCami(Node n1,Node n2,List<String> visitats){
        if(n1.hasEdgeToward(n2) && fluxQuePotPortar(n1.getEdgeToward(n2))>0){
            visitats.add(n2.getId());
            return true;
        }
        else{
            Boolean trobat=false;
            Stream<Edge> arestesSortida = n1.leavingEdges();
            Iterator<Edge> itS=arestesSortida.iterator();
            while(itS.hasNext() && !trobat){//sortides mentres no s'hagi arribat a terminal
                Edge e = itS.next();
                Float fluxPossible=fluxQuePotPortar(e);
                if(fluxPossible>0 && !visitats.contains(e.getTargetNode().getId())){//si no es pot portar flux no hi anem
                    visitats.add(e.getTargetNode().getId());
                    trobat=RhiHaCami(e.getTargetNode(), n2, visitats);
                    if(!trobat){
                        visitats.remove(e.getTargetNode().getId());
                    }
                }
            }
            return trobat;
        }
    }

    /**
     * @brief Afegeix el flux que passa per les canonades visitades
     * 
     * @param capacitat_minima és el flux mínim que pot passar per totes les canonades
     * dels nodes visitats.
     * 
     * @pre cert.
     * @post Insereix el flux mínim que pot passar per cada parella de nodes visitats que conformaran una canonada, ja que 
     * els nodes estan ordenats per ordre d'inserció.
     * 
     */
    private static void posarFlux(Graph graphMaxFlow, List<String> nodesVisitats, Float capacitat_minima){
        for(int i = 0; i < nodesVisitats.size(); i++){
            String n1=nodesVisitats.get(i);
            int j=i+1;
            if(j<nodesVisitats.size()){
                String n2=nodesVisitats.get(j);
                Node node2=graphMaxFlow.getNode(n2);
                Edge aresta1=node2.getEdgeFrom(n1);
                Edge aresta2=node2.getEdgeToward(n1);

                String a1=atributFlux;
                String a2=atributCapacitat;
                if(aresta1.hasAttribute(atributCapacitat)){
                    a1=atributCapacitat;
                    a2=atributFlux;
                }
                Float valor1=(Float)aresta1.getAttribute(a1);
                Float valor2=(Float)aresta2.getAttribute(a2);

                aresta1.setAttribute(a1, valor1-capacitat_minima);//disminuïm la capacitat que pot portar la canonada
                aresta2.setAttribute(a2, valor2+capacitat_minima);//incrementem el flux que passa per la canonada
            }
        }
    }

    /**
     * @brief Flux mínim
     * 
     * @pre nodesVisitats!=null
     * @post Retorna el mínim flux que pot passar pels nodesvisitats.
     * 
     */
    private static Float minimCapacitat(Graph graphMaxFlow, List<String> nodesVisitats){
        Float capacitat_minima=fluxQuePotPortar(graphMaxFlow.getNode(nodesVisitats.get(0)).getEdgeToward(nodesVisitats.get(1)));
        for(int i=1; i<nodesVisitats.size()-1; i++){
            if(fluxQuePotPortar(graphMaxFlow.getNode(nodesVisitats.get(i)).getEdgeToward(nodesVisitats.get(i+1)))<capacitat_minima){
                capacitat_minima=fluxQuePotPortar(graphMaxFlow.getNode(nodesVisitats.get(i)).getEdgeToward(nodesVisitats.get(i+1)));
            }
        }
        return capacitat_minima;
    }


    /**
     * @brief Flux que pot portar
     * 
     * @pre e forma part del graphMaxFlow
     * @post Retorna el flux que passa per una canonada o la seva capacitat depenent de
     * l'aresta que passem per paràmetre, ja que cada aresta estarà duplicada i una portarà el flux i 
     * l'altre la capacitat restant que pot entrar.
     * 
     */
    private static Float fluxQuePotPortar(Edge e){
        String atribut=atributFlux;
        if(e.hasAttribute(atributCapacitat)){
            atribut=atributCapacitat;
        }
        return (Float)e.getAttribute(atribut);
    }

    /**
     * @brief Crea el graph de max-flow
     * 
     * @pre nodeOrigen pertany a la xarxa x.
     * @post Retorna un graph un totes les sortides dels orígens estan redirigides a 
     * un super Origen i de la mateixa manera amb els terminals i un super Terminal.
     * 
     */
    private static Graph crearGraphMaxFlow(Xarxa x, Origen nodeOrigen){
        Graph graphMaxFlow = new SingleGraph("MaxFlow");

        //Creem super Terminal i super Origen
        graphMaxFlow.addNode(SUPERORIGEN);
        graphMaxFlow.addNode(SUPERTERMINAL);

        List<String> origensVisitats = new ArrayList<>();
        List<String> terminalsVisitats = new ArrayList<>();
        //Posem tots els nodes connexions i connectem amb arestes
        //posarArestes(x, graphMaxFlow, x.entrades(nodeOrigen));
        posarArestes(x, graphMaxFlow, x.sortides(nodeOrigen), origensVisitats, terminalsVisitats);

        return graphMaxFlow;
    }

    /**
     * @brief Posa les arestes al graphMaxFlow
     * 
     * @pre cert.
     * @post Tots els nodes de x que són instàncies de Connexio i no estaven ja en graphMaxFlow s'han afegit a graphMaxFlow.
     * Totes les arestes d'entrada i sortida d'aquests nodes en x s'han afegit a graphMaxFlow.
     * Per a cada canonada en x que no és ni d'origen ni terminal, si no existia ja una aresta corresponent en graphMaxFlow, 
     * s'ha afegit una nova amb la capacitat de la canonada, i s'ha afegit una aresta auxiliar en la direcció contrària amb un flux inicial de 0.
     * Per a cada node d'origen en x que no estava en origensVisitats, s'han afegit totes les seves arestes de sortida a graphMaxFlow.
     * Per a cada node terminal en x que no estava en terminalsVisitats, s'han afegit totes les seves arestes d'entrada a graphMaxFlow.
     * 
     */
    private static void posarArestes(Xarxa x, Graph graphMaxFlow, Iterator<Canonada> it, List<String> origensVisitats, List<String> terminalsVisitats){
        //poso els nodes si fa falta, després poso l'aresta amb la capacitat
        // corresponent, tenint en compte que si el superorigen o superterminal
        // ja estaven connectats a un node connexió la capacitat es suma.
        while(it.hasNext()){
            Canonada c=it.next();
            Nodo n1=c.node1();
            Nodo n2=c.node2();
            //poso els nodes si son Connexions
            if(n1 instanceof Connexio && graphMaxFlow.getNode(n1.id())==null){//Si és connexio i no pertany a la xarxa
                graphMaxFlow.addNode(n1.id());
                posarArestes(x, graphMaxFlow, x.entrades(n1), origensVisitats, terminalsVisitats);
                posarArestes(x, graphMaxFlow, x.sortides(n1), origensVisitats, terminalsVisitats);
            }
            if(n2 instanceof Connexio && graphMaxFlow.getNode(n2.id())==null){//Si és connexio i no pertany a la xarxa
                graphMaxFlow.addNode(n2.id());
                posarArestes(x, graphMaxFlow, x.entrades(n2), origensVisitats, terminalsVisitats);
                posarArestes(x, graphMaxFlow, x.sortides(n2), origensVisitats, terminalsVisitats);
            }
            if(!(n1 instanceof Origen) && !(n2 instanceof Terminal)){//tots són conexio
                if(graphMaxFlow.getEdge(c.id())==null){//aquella aresta no existeix encara
                    Edge e=graphMaxFlow.addEdge(c.id(), n1.id(), n2.id(), true);
                    e.setAttribute(atributCapacitat, c.capacitat());
                    Edge e2=graphMaxFlow.addEdge(c.id()+"AUX", n2.id(), n1.id(), true);
                    e2.setAttribute(atributFlux,0f);//inicialitzem a 0 el flux
                }
            }
            if(n1 instanceof Origen && !origensVisitats.contains(n1.id())){
                //posar totes les sortides
                origensVisitats.add(n1.id());
                posarSortidesMaxFlow(x, graphMaxFlow, n1, origensVisitats, terminalsVisitats);
            }
            if(n2 instanceof Terminal && !terminalsVisitats.contains(n2.id())){
                //posar totes les entrades
                terminalsVisitats.add(n2.id());
                posarEntradesMaxFlow(x, graphMaxFlow, n2, origensVisitats, terminalsVisitats);
            }
        }
    }

    /**
     * @brief Posa les sortides en graphMaxFlow
     * 
     * @pre cert.
     * @post Tots els nodes de sortida de o en x que no estan en terminalsVisitats s'han afegit a graphMaxFlow 
     * (si són instàncies de Connexio i no estaven ja en graphMaxFlow).
     * Totes les arestes d'entrada i sortida d'aquests nodes en x s'han afegit a graphMaxFlow.
     * Per a cada node de sortida de o en x, si ja existia una aresta des de SUPERORIGEN en graphMaxFlow, 
     * la seva capacitat s'ha incrementat en la capacitat de l'aresta corresponent en x.
     * Si no existia una aresta des de SUPERORIGEN en graphMaxFlow, s'ha afegit una nova amb la capacitat de l'aresta 
     * corresponent en x, i s'ha afegit una aresta auxiliar en la direcció contrària amb un flux inicial de 0.
     * 
     */
    private static void posarSortidesMaxFlow(Xarxa x, Graph graphMaxFlow, Nodo o, List<String> origensVisitats, List<String> terminalsVisitats){
        Iterator<Canonada> it=x.sortides(o);
        while(it.hasNext()){
            Canonada c=it.next();
            Nodo n=c.node2();

            //posem node si fa falta
            if(!terminalsVisitats.contains(n.id())){ //mirem que no sigui un terminal ja visitat
                if(n instanceof Connexio && graphMaxFlow.getNode(n.id())==null){//Si és connexio i no pertany a la xarxa
                    graphMaxFlow.addNode(n.id());
                    posarArestes(x, graphMaxFlow, x.entrades(n), origensVisitats, terminalsVisitats);
                    posarArestes(x, graphMaxFlow, x.sortides(n), origensVisitats, terminalsVisitats);
                }
                String identificador=n.id();
                if(n instanceof Terminal){
                    identificador=SUPERTERMINAL;
                }
                Node ngraph=graphMaxFlow.getNode(identificador);
                //posem aresta mirant si el superfont ja estaba conectat al n

                if(ngraph.hasEdgeFrom(SUPERORIGEN)){//aquella aresta existeix
                    //suma capacitat
                    Edge e=ngraph.getEdgeFrom(SUPERORIGEN);
                    float cap=(Float) e.getAttribute(atributCapacitat);
                    cap+=c.capacitat();
                    e.setAttribute(atributCapacitat, cap);
                }
                else{
                    Edge e=graphMaxFlow.addEdge(c.id(), SUPERORIGEN, identificador, true);
                    e.setAttribute(atributCapacitat, c.capacitat());
                    Edge e2=graphMaxFlow.addEdge(c.id()+"AUX", identificador, SUPERORIGEN, true);
                    e2.setAttribute(atributFlux,0f);//inicialitzem a 0 el flux
                }
            }
        }
    }

    /**
     * @brief Posa les entrades en graphMaxFlow
     * 
     * @pre cert.
     * @post Tots els nodes d'entrada a t en x que no estan en origensVisitats s'han afegit a 
     * graphMaxFlow (si són instàncies de Connexio i no estaven ja en graphMaxFlow).
     * Totes les arestes d'entrada i sortida d'aquests nodes en x s'han afegit a graphMaxFlow.
     * Per a cada node d'entrada a t en x, si ja existia una aresta cap a SUPERTERMINAL en graphMaxFlow, 
     * la seva capacitat s'ha incrementat en la capacitat de l'aresta corresponent en x.
     * Si no existia una aresta cap a SUPERTERMINAL en graphMaxFlow, s'ha afegit una nova amb la capacitat de 
     * l'aresta corresponent en x, i s'ha afegit una aresta auxiliar en la direcció contrària amb un flux inicial de 0.
     * 
     */
    private static void posarEntradesMaxFlow(Xarxa x, Graph graphMaxFlow, Nodo t, List<String> origensVisitats, List<String> terminalsVisitats){
        Iterator<Canonada> it=x.entrades(t);
        while(it.hasNext()){
            Canonada c=it.next();
            Nodo n=c.node1();
            if(!origensVisitats.contains(n.id())){//mirem que no sigui un origen ja visitat
                //posem node si cal
                if(n instanceof Connexio && graphMaxFlow.getNode(n.id())==null){//Si és connexio i no pertany a la xarxa
                    graphMaxFlow.addNode(n.id());
                    posarArestes(x, graphMaxFlow, x.entrades(n), origensVisitats, terminalsVisitats);
                    posarArestes(x, graphMaxFlow, x.sortides(n), origensVisitats, terminalsVisitats);
                }
                //posem aresta mirant si el superpou ja estaba conectat al n
                String identificador=n.id();
                if(n instanceof Origen){
                    identificador=SUPERORIGEN;
                }
                Node ngraph=graphMaxFlow.getNode(identificador);
                if(ngraph.hasEdgeToward(SUPERTERMINAL)){//aquella aresta existeix
                    //suma capacitat
                    Edge e=ngraph.getEdgeToward(SUPERTERMINAL);
                    float cap=(Float) e.getAttribute(atributCapacitat);
                    cap+=c.capacitat();
                    e.setAttribute(atributCapacitat, cap);
                }
                else{
                    Edge e=graphMaxFlow.addEdge(c.id(), identificador, SUPERTERMINAL, true);
                    e.setAttribute(atributCapacitat, c.capacitat());
                    Edge e2=graphMaxFlow.addEdge(c.id()+"AUX", SUPERTERMINAL, identificador, true);
                    e2.setAttribute(atributFlux,0f);//inicialitzem a 0 el flux
                }
            }
        }
    }
}

    
    



/** @file Xarxa.java
    @brief Classe Xarxa
*/

/** @class Xarxa
    @brief Xarxa de distribució d'aigua, no necessàriament connexa (graf dirigit de Node)
    @author Miquel Coll Barneto
*/

import java.util.*;
import java.util.stream.Stream;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.graphicGraph.stylesheet.StyleConstants.Units;
import org.graphstream.ui.spriteManager.*;

public class Xarxa {
    private Graph graph; ///< Graph de graphstream en el qual guardarem tota la informació referent a la xarxa de distribució d'aigua.
    private Stack<String> listOperacions;///< Pila on guardem les operacions de tancar i obrir aixetes
    private static final String atributNode = "node"; ///< Atribut que tenen els nodes del graph, on guardarem un Nodo
    private static final String atributCanonada = "canonada"; ///< Atribut que tenen els edges del graph, on guardarem una Canonada
    private static final String atributGraficOrigen = "gorigen"; ///< Atribut que fem servir en el graph per tal de mostrar els orígens amb unes característiques especials per tal de diferenciar-lo dels altres nodes.
    private static final String atributGraficTerminal = "gterminal"; ///< Atribut que fem servir en el graph per tal de mostrar els terminals amb unes característiques especials per tal de diferenciar-lo dels altres nodes.
    private static final String atributGraficConnexio = "gconnexio"; ///< Atribut que fem servir en el graph per tal de mostrar les connexions amb unes característiques especials per tal de diferenciar-lo dels altres nodes.
    private static final String atributGraficAixetaTancada = "tancada"; ///< Atribut que assignarem els nodes tancats del graph, per mostrar-los diferent dels altres nodes.


    /**
     * @brief Crea una xarxa de distribució d'aigua buida
     * @pre cert
     * @post Crea un graph de Graphstream i inicialitza listOperacions a una pila buida.
     */
    public Xarxa(){
        System.setProperty("org.graphstream.ui", "swing");
		System.setProperty("org.graphstream.debug", "true"); // Recomenable per excepcions imprevistes
        graph=new SingleGraph("Xarxa");
        graph.setStrict(true);
        listOperacions=new Stack<>();
    }

    /**
     * @brief Retorna l'atribut graph
     * @pre cert
     * @post Retorna l'atribut graph
     */
    public Graph grafic(){
        return graph;
    }

    /**
     * @brief Retorna el node amb identificador id
     * @pre cert
     * @post Retorna el node de la xarxa amb identificador id si existeix, altrament retorna null
     */
    public Nodo node(String id){
        Node nodeGraph = graph.getNode(id);
        if(nodeGraph!=null){
            Nodo nodo = (Nodo)nodeGraph.getAttribute(atributNode);
            return nodo;
        }
        return null;
    }

    /**
     * @brief Retorna la Canonada amb identificador id
     * @pre cert
     * @post Retorna la canonada de la xarxa amb identificador \p id si existeix, altrament retorna null
     */
    public Canonada canonada(String id){
        Edge edgeGraph = graph.getEdge(id);
        if(edgeGraph!=null){
            Canonada can = (Canonada)edgeGraph.getAttribute(atributCanonada);
            return can;
        }
        return null;
    }

    /**
     * @brief Sortides d'un node
     * @pre cert
     * @post Si el node existeix a la xarxa retorna un iterador que permet recórrer totes les canonades que surten del node, altrament null
     */
    public Iterator<Canonada> sortides(Nodo node){
        Node nodeGraph = graph.getNode(node.id());
        if(nodeGraph!=null){
            Stream<Edge> edgeStream = nodeGraph.leavingEdges();
            Stream<Canonada> canonadaStream = edgeStream.map(edge -> edge.getAttribute(atributCanonada, Canonada.class));
            return canonadaStream.iterator();
        }
        return null;
    }

    /**
     * @brief Entrades d'un node
     * @pre cert
     * @post Si el node existeix a la xarxa retorna un iterador que permet recórrer totes les canonades que entren al node, altrament null
     */
    public Iterator<Canonada> entrades(Nodo node){
        Node nodeGraph = graph.getNode(node.id());
        if(nodeGraph!=null){
            Stream<Edge> edgeStream = nodeGraph.enteringEdges();
            Stream<Canonada> canonadaStream = edgeStream.map(edge -> edge.getAttribute(atributCanonada, Canonada.class));
            return canonadaStream.iterator();
        }
        return null;
    }

    /**
     * @brief Afegir Origen
     * @pre No existeix cap node amb el mateix id que \p nodeOrigen a la xarxa
     * @post S'ha afegit \p nodeOrigen a la xarxa
     * @throws IllegalArgumentException si ja existeix un node amb aquest id
     */
    public void afegir(Origen nodeOrigen){
        if(graph.getNode(nodeOrigen.id()) != null){//Ja existeix un node amb aquest id
            throw new IllegalArgumentException("ja existeix a les xarxes una aixeta amb nom " + nodeOrigen.id() + ".");
        }
        //No existeix un node amb aquest id
        Node n=graph.addNode(nodeOrigen.id());
        n.setAttribute(atributNode, nodeOrigen);
    }

    /**
     * @brief Afegir Terminal
     * @pre No existeix cap node amb el mateix id que \p nodeTerminal a la xarxa
     * @post S'ha afegit \p nodeTerminal a la xarxa
     * @throws IllegalArgumentException si ja existeix un node amb aquest id
     */
    public void afegir(Terminal nodeTerminal){
        if(graph.getNode(nodeTerminal.id()) != null){//Ja existeix un node amb aquest id
            throw new IllegalArgumentException("ja existeix a les xarxes una aixeta amb nom " + nodeTerminal.id() + ".");
        }
        //No existeix un node amb aquest id
        Node n = graph.addNode(nodeTerminal.id());
        n.setAttribute(atributNode, nodeTerminal);
    }

    /**
     * @brief Afegir Connexio
     * @pre No existeix cap node amb el mateix id que \p nodeConnexio a la xarxa
     * @post S'ha afegit \p nodeConnexio a la xarxa
     * @throws IllegalArgumentException si ja existeix un node amb aquest id
     */
    public void afegir(Connexio nodeConnexio){
        if(graph.getNode(nodeConnexio.id()) != null){//Ja existeix un node amb aquest id
            throw new IllegalArgumentException("ja existeix a les xarxes una aixeta amb nom " + nodeConnexio.id() + ".");
        }
        //No existeix un node amb aquest id
        Node n = graph.addNode(nodeConnexio.id());
        n.setAttribute(atributNode, nodeConnexio);
    }

    /**
     * @brief Connectar 2 nodes
     * @pre \p node1 i \p node2 pertanyen a la xarxa, no estan connectats, i \p node1 no és un node terminal
     * @post S'han connectat els nodes amb una canonada de capacitat c, amb sentit de l'aigua de \p node1 a \p node2. Si el \p node2 és un Origen es converteix en una Connexio.
     * @throws NoSuchElementException si \p node1 o \p node2 no pertanyen a la xarxa
     * @throws IllegalArgumentException si els nodes ja estan connectats o \p node1 és un node terminal.
     */
    public void connectarAmbCanonada(Nodo node1, Nodo node2, float c){
        if(node1==null || graph.getNode(node1.id())==null){
            throw new NoSuchElementException("El primer node no pertany a la xarxa");
        }
        if(node2== null  || graph.getNode(node2.id())==null){//algun node no pertany a la xarxa
            throw new NoSuchElementException("El segon node no pertany a la xarxa");
        }
        if(node1 instanceof Terminal){
            throw new IllegalArgumentException("El primer node es un punt terminal");
        }
        Node nodeGraph1 = graph.getNode(node1.id());
        Node nodeGraph2 = graph.getNode(node2.id());
        if(nodeGraph1.getEdgeBetween(nodeGraph2)!=null){
            throw new IllegalArgumentException("Ja estan connectats");
        }
        //canviar primer a connexio
        if(node2 instanceof Origen){ //si el node2 és un Origen es converteix en una Connexió
            Connexio con = new Connexio(node2.id(), node2.coordenades());
            nodeGraph2.setAttribute(atributNode, con);//substituim l'atribut anterior.
        }
        node2=node(node2.id());//Si s'ha convertit amb una connexio ara node2 és una Connexió
        Canonada canonada = new Canonada(node1, node2, c);
        String nom=node1.id()+"-"+node2.id();
        Edge aresta=graph.addEdge(nom, node1.id(), node2.id(), true);
        aresta.setAttribute(atributCanonada, canonada);
    }

    /**
     * @brief Abonar un client a un Terminal
     * @pre \p nodeTerminal pertany a la xarxa
     * @post El client identificat amb \p idClient queda abonat al node terminal. Si ja ho estava retorna true, altrament false.
     * @throws NoSuchElementException si nodeTerminal no pertany a la xarxa
     */
    public boolean abonar(String idClient, Terminal nodeTerminal){
        if(graph.getNode(nodeTerminal.id()) == null){//No pertany a la xarxa
            throw new NoSuchElementException("No pertany a la xarxa " + nodeTerminal.id());
        }
        boolean existeix=true;
        if(!nodeTerminal.teAbonat(idClient)){//nou abonat
            existeix=false;
            nodeTerminal.nouAbonat(idClient);
        }
        return existeix;
    }

    /**
     * @brief Cabal de l'abonat
     * @pre Existeix un client identificat amb \p idClient a la xarxa
     * @post Retorna el cabal actual al punt d'abastament del client identificat amb \p idClient
     * @throws NoSuchElementException si no existeix un client identificat amb \p idClient a la xarxa
     */
    public float cabalAbonat(String idClient){
        Boolean seguir=true;
        Terminal t = null;
        Stream<Node> nodes=graph.nodes();
        Iterator<Node> it=nodes.iterator();
        while (it.hasNext() && seguir) {
            Node nodeGraph = it.next();
            Nodo nodo=node(nodeGraph.getId());
            if(nodo instanceof Terminal){
                t=(Terminal)nodo;
                if(t.teAbonat(idClient)){
                    seguir=false;
                }
            }
        }
        if(seguir){//no s'ha trobat el client identificiat amb idClient
            throw new NoSuchElementException("Aquest client no existeix a la xarxa");
        }
        return cabal(t);
    }

    /**
     * @brief Obrir aixeta
     * @pre node pertany a la xarxa
     * @post L'aixeta del node està oberta, i s'ha afegit un nou element a la listOperacions
     * @throws NoSuchElementException si node no pertany a la xarxa
     */
    public void obrirAixeta(Nodo node){
        if(graph.getNode(node.id()) == null){//No pertany a la xarxa
            throw new NoSuchElementException("No pertany a la xarxa " + node.id());
        }
        //tenir en compte la pila
        if(node.aixetaOberta()){
            listOperacions.push(null);
        }
        else{
            node.obrirAixeta();
            listOperacions.push(node.id());
        }
        
    }

    /**
     * @brief Tancar aixeta
     * @pre node pertany a la xarxa
     * @post L'aixeta del node està tancada, i s'ha afegit un nou element a la listOperacions
     * @throws NoSuchElementException si node no pertany a la xarxa
     */
    public void tancarAixeta(Nodo node){
        if(graph.getNode(node.id()) == null){//No pertany a la xarxa
            throw new NoSuchElementException("No pertany a la xarxa " + node.id());
        }
        //tenir en compte la pila
        if(node.aixetaOberta()){
            node.tancarAixeta();
            listOperacions.push(node.id());
        }
        else{
            listOperacions.push(null);
        }
    }

    /**
     * @brief Recular operacions realitzades en les aixetes
     * @pre \p nPassos >= 1
     * @post S'ha reculat \p nPassos passos en la seqüència d'operacions realitzades d'obrir i tancar aixetes
     * @throws IllegalArgumentException si \p nPassos és negatiu o zero
     */
    public void recular(int nPassos){
        if(nPassos<=0){
            throw new IllegalArgumentException("nPassos més petit o igual a 0");
        }
        while(nPassos>0 && !listOperacions.empty()){
            String elemSuperior=listOperacions.pop();
            if(elemSuperior!=null){
                Nodo n = node(elemSuperior);;
                if(n.aixetaOberta()){
                    n.tancarAixeta();
                }
                else{
                    n.obrirAixeta();
                }
            }
            nPassos--;
        }
    }

    /**
     * @brief Establir cabal a un node Origen
     * @pre \p nodeOrigen pertany a la xarxa i \p cabal >= 0
     * @post El cabal de \p nodeOrigen és \p cabal
     * @throws NoSuchElementException si \p nodeOrigen no pertany a la xarxa
     * @throws IllegalArgumentException si \p cabal és negatiu
     */
    public void establirCabal(Origen nodeOrigen, float cabal){
        if(graph.getNode(nodeOrigen.id()) == null){//No pertany a la xarxa
            throw new NoSuchElementException("No pertany a la xarxa " + nodeOrigen.id());
        }
        if(cabal<0){
            throw new IllegalArgumentException("Cabal negatiu");
        }
        nodeOrigen.establirCabal(cabal);
    }

    /**
     * @brief Establir demanda a un node Terminal
     * @pre \p nodeTerminal pertany a la xarxa i \p demanda >= 0
     * @post La demanda de \p nodeTerminal és \p demanda
     * @throws NoSuchElementException si \p nodeTerminal no pertany a la xarxa
     * @throws IllegalArgumentException si \p demanda és negatiu
     */
    public void establirDemanda(Terminal nodeTerminal, float demanda){
        if(graph.getNode(nodeTerminal.id()) == null){//No pertany a la xarxa
            throw new NoSuchElementException("No pertany a la xarxa " + nodeTerminal.id());
        }
        if(demanda<0){
            throw new IllegalArgumentException("Demanda negativa");
        }
        nodeTerminal.establirDemandaActual(demanda);
    }

    /**
     * @brief Cabal teòric d'un node
     * @pre \p nodo pertany a la xarxa
     * @post Retorna el cabal teòric al \p nodo segons la configuració actual de la xarxa
     * @throws NoSuchElementException si \p nodo no pertany a la xarxa
     */
    public float cabal(Nodo nodo){
        if(graph.getNode(nodo.id()) == null){//No pertany a la xarxa
            throw new NoSuchElementException("No pertany a la xarxa " + nodo.id());
        }
        if (!nodo.aixetaOberta()) { //aixeta tancada
            return 0;
        }
        if(nodo instanceof Origen){ //es un node origen
            // si la demanda es més gran que el cabal retorna el cabal, altrament retorna la demanda
            Origen o = (Origen)nodo;
            float demanda = demanda(nodo);
            if(demanda>o.cabal()){
                return o.cabal();
            }
            return demanda;
        }
        //no es un origen
        Iterator<Canonada> itEntrades=entrades(nodo);
        float cabal=0;
        while (itEntrades.hasNext()) {
            cabal+=cabalEntrant(itEntrades.next());
        }
        return cabal;
    }

    /**
     * @brief Cabal teòric d'una canonada
     * @pre \p edge pertany a la xarxa
     * @post Retorna el cabal teòric de \p edge segons la configuració actual de la xarxa
     */
    private float cabalEntrant(Canonada edge){
        Nodo nodo=edge.node1();
        float cabalNode=cabal(nodo);
        float demandaNode=demanda(nodo);
        float demandaPropagada=demandaPropagada(edge);
        if(cabalNode>=demandaNode){
            return demandaPropagada;
        }
        else{
            return (demandaPropagada/demandaNode)*cabalNode;
        }
    }

    /**
     * @brief Demanda teòrica d'un node
     * @pre \p nodo pertany a la xarxa
     * @post Retorna la demanda teòrica al \p nodo segons la configuració actual de la xarxa
     * @throws NoSuchElementException si \p nodo no pertany a la xarxa
     */
    public float demanda(Nodo nodo){
        if(graph.getNode(nodo.id()) == null){//No pertany a la xarxa
            throw new NoSuchElementException("No pertany a la xarxa " + nodo.id());
        }
        if (!nodo.aixetaOberta()) { //aixeta tancada
            return 0;
        }
        if(nodo instanceof Terminal){ //es un node terminal
            return ((Terminal)nodo).demandaActual();
        }
        // no es un node terminal
        Iterator<Canonada> itSortides=sortides(nodo);
        float demanda=0;
        while(itSortides.hasNext()){
            demanda+=demandaPropagada(itSortides.next());
        }
        return demanda;
    }

    /**
     * @brief Demanda propagada d'una canonada
     * @pre \p edge pertany a la xarxa
     * @post Retorna la demanda propagada del \p edge segons la configuració actual de la xarxa
     */
    private float demandaPropagada(Canonada edge){
        Nodo nodo=edge.node2();
        float demanda=demanda(nodo);
        float capacitatsCanonades=capacitatsCanonades(entrades(nodo));
        if(demanda>=capacitatsCanonades){
            return edge.capacitat();
        }
        else{
            float proporcio=edge.capacitat()/capacitatsCanonades;
            return demanda*proporcio;
        }
    }

    /**
     * @brief Suma de les capacitats d'un conjunt de canonades
     * @pre cert
     * @post Retorna la suma de les capacitats d'un conjunt de canonades
     */
    private float capacitatsCanonades(Iterator<Canonada> itEntrades){
        float capacitats=0;
        while(itEntrades.hasNext()){
            capacitats+=itEntrades.next().capacitat();
        }
        return capacitats;
    }

    /**
     * @brief Dibuixar xarxa
     * @pre cert
     * @post Dibuixa la xarxa de distribució d'aigua de la qual \p nodeOrigen pertany
     */
    public void dibuixar(Origen nodeOrigen){
        Graph subGraph = componentConexa(nodeOrigen); //creo un subGraph amb només la xarxa corresponent
        SpriteManager sman = new SpriteManager(subGraph);//utilitzat per poder mostrar més informació en el dibuix
        for (Node nodeGraph : subGraph){
            nodeGraph.setAttribute("ui.label", nodeGraph.getId()); //mostrem els id's de tots els nodes del graph
            Nodo nodo = node(nodeGraph.getId());
            String propietatsClass="";

            //a cada nodo afegeixo la informació de les coordeandes 
            dibuixarCoordenades(sman, nodo);

            if(nodo instanceof Terminal){
                Terminal t=(Terminal)nodo;
                propietatsClass+=atributGraficTerminal;
                //afegeixo la informació a cada terminal, sobre la demanda actual i la demanda punta
                dibuixarTerminal(sman, t);
            }
            else if(nodo instanceof Origen){
                propietatsClass+=atributGraficOrigen;
            }
            else{
                propietatsClass+=atributGraficConnexio;
            }
            if(!nodo.aixetaOberta()){//aixeta tancada
                propietatsClass+=", " + atributGraficAixetaTancada;
            }

            //posicionar
            nodeGraph.setAttribute("x", nodo.coordenades().getLongitud());
            nodeGraph.setAttribute("y", nodo.coordenades().getLatitud()); 

            nodeGraph.setAttribute("ui.class", propietatsClass);

            for(Edge edge : nodeGraph){
                Canonada c = canonada(edge.getId());
                float cabal=cabalEntrant(c);
                edge.setAttribute("ui.label", Float.toString(cabal)+ " / " + Float.toString(c.capacitat()));
            }
        }
        subGraph.setAttribute("ui.stylesheet", "url('recursos\\estil1.css')");//associar a un stylesheet
        subGraph.display(false);//mostrar segons les coordenades
        //subGraph.display();
    }

    /**
     * @brief Dibuixar coordenades
     * @pre cert
     * @post Dibuixar les coordenades del \p nodo
     */
    private void dibuixarCoordenades(SpriteManager sman, Nodo nodo){
        Sprite s = sman.addSprite(nodo.id());
        s.attachToNode(nodo.id());
        s.setPosition(Units.PX, 0, 0, 0);
        s.setAttribute("ui.label", nodo.coordenades().mostrarCoordenades());
        s.setAttribute("ui.class", "coord");
    }

    /**
     * @brief Dibuixar terminal
     * @pre cert
     * @post Dibuixar la demanda actual i la demanda punta del terminal \p t
     */
    private void dibuixarTerminal(SpriteManager sman, Terminal t){
        Sprite st = sman.addSprite(t.id()+"t");
        st.attachToNode(t.id());
        st.setPosition(Units.PX, 0, 0, 0);
        st.setAttribute("ui.label", Float.toString(t.demandaActual())+ " / " + Float.toString(t.demandaPunta()));
        st.setAttribute("ui.class", "demandes");
    }

    /**
     * @brief Component connexa de la xarxa
     * @pre cert
     * @post Retorna un Graph amb la component connexa de la xarxa, de la qual pertany el node Origen \p nodeOrigen
     */
    public Graph componentConexa(Origen nodeOrigen){
        Graph subGraph = new SingleGraph("SubGraph");
        afegirNode(subGraph, nodeOrigen);
        posarCanonades(subGraph, sortides(nodeOrigen));
        return subGraph;
    }

    /**
     * @brief Afegeix node al sub-graph
     * @pre cert
     * @post Afegeix el node \p node al graph \p subGraph
     */
    private void afegirNode(Graph subGraph, Nodo node){
        Node nodeSub=subGraph.addNode(node.id());
        if(node instanceof Origen){
            nodeSub.setAttribute(atributNode, (Origen)node);
        }
        else if(node instanceof Terminal){
            nodeSub.setAttribute(atributNode, (Terminal)node);
        }
        else{
            nodeSub.setAttribute(atributNode, (Connexio)node);
        }
    }

    /**
     * @brief Afegeix canonada al sub-graph
     * @pre cert
     * @post Afegeix la canonada \p c al graph \p subGraph
     */
    private void afegirAresta(Graph subGraph, Canonada c){
        Edge aresta=subGraph.addEdge(c.id(), c.node1().id(), c.node2().id(), true);
        aresta.setAttribute(atributCanonada, c);
    }

    /**
     * @brief Metode recursiu que va recorrent el graph i crear un sub-graph
     * @pre cert
     * @post Va fent un recorregut per \p it i va afegint les canonades i els nodes corresponents al sub-graph, cada cop que afegeix un node
     *       es crida recursivament amb les entrades i sortides d'aquell node.
     */ 
    private void posarCanonades(Graph subGraph, Iterator<Canonada> it){
        while(it.hasNext()){
            Canonada c=it.next();
            Nodo n1=c.node1();
            Nodo n2=c.node2();
            if(subGraph.getNode(n1.id())==null){//no pertany a la xarxa
                afegirNode(subGraph, n1);
                posarCanonades(subGraph, entrades(n1));
                posarCanonades(subGraph, sortides(n1));
            }
            if(subGraph.getNode(n2.id())==null){//no pertany a la xarxa
                afegirNode(subGraph, n2);
                posarCanonades(subGraph, entrades(n2));
                posarCanonades(subGraph, sortides(n2));
            }
            if(subGraph.getEdge(c.id())==null){//no pertany a la xarxa
                afegirAresta(subGraph, c);
            }
        }
    }
}
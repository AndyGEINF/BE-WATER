/** @file Nodo.java
    @brief Classe Nodo
*/

/** @class Nodo
    @brief Node d'una xarxa de distribució d'aigua
    @author Miquel Coll Barneto
*/

public class Nodo {

    private final String id; ///< Identificador del node
    private final Coordenades c;///< Coordenades del node
    private boolean aixetaOberta;///< Bool indicant si l'aixeta del node és obert (true) o tancat (false), inicialitzat a true


    /**
     * @brief Constructor de còpia
     * @pre cert
     * @post Crear un nou node que és igual a /p n
     */
    public Nodo(Nodo n) {
        this.id = n.id;
        this.c = n.c;
        this.aixetaOberta = n.aixetaOberta;
    }

    /**
     * @brief Crear un node
     * @pre cert
     * @post id = \p id, c = \p c, aixetaOberta=true
     */
    public Nodo(String id, Coordenades c){
        this.id=id;
        this.c=c;
        aixetaOberta=true;
    }

    /**
     * @brief Retorna l'identificador
     * @pre cert
     * @post Retorna l'identificador del node
     */
    public String id(){
        return id;
    }

    /**
     * @brief Retorna les coordenades
     * @pre cert
     * @post Retorna les coordenades del node
     */
    public Coordenades coordenades(){
        return c;
    }

    /**
     * @brief Estat de l'aixeta
     * @pre cert
     * @post Retorna true si l'aixeta és oberta, altrament false
     */
    public boolean aixetaOberta(){
        return aixetaOberta;
    }

    /**
     * @brief Obrir Aixeta
     * @pre cert
     * @post aixetaOberta=true
     */
    public void obrirAixeta(){
        aixetaOberta=true;
    }

    /**
     * @brief Tancar Aixeta
     * @pre cert
     * @post aixetaOberta=false
     */
    public void tancarAixeta(){
        aixetaOberta=false;
    }
}

/** @file Canonada.java
    @brief Classe Canonada
*/

/** @class Canonada
    @brief Canonada de la xarxa de distribució d'aigua
    @author Miquel Coll Barneto
*/

public class Canonada {
    

    private final String id; ///< Identificador de la canonada
    private final Nodo node1; ///< Primer node de la canonada, o sigui el node d'inici.
    private final Nodo node2; ///< Segon node de la canonada, o sigui el node destí.

    /** @invariant  0 < capacitat */
    private final float capacitat; ///< Capacitat de la canonada, expressada en litres per segon.

    /**
     * @brief Crear una canonada
     * @pre \p capacitat > 0
     * @post Crea una canonada que connecta node1 i node2 amb la capacitat indicada. node1 = \p node1, node2 = \p node2, capacitat = \p capacitat,
     * @throws IllegalArgumentException si \p capacitat <= 0
     */
    public Canonada(Nodo node1, Nodo node2, float capacitat){
        if(capacitat <= 0){
            throw new IllegalArgumentException("La capacitat es menor o igual a 0");
        }
        this.node1=node1;
        this.node2=node2;
        this.capacitat=capacitat;
        id=node1.id()+"-"+node2.id();
    }

    /**
     * @brief Retorna el node d'inici
     * @pre cert
     * @post Retorna el node d'inici de la canonada
     */
    public Nodo node1(){
        return node1;
    }

    /**
     * @brief Retorna el node destí
     * @pre cert
     * @post Retorna el node destí de la canonada
     */
    public Nodo node2(){
        return node2;
    }

    /**
     * @brief Retorna la capacitat
     * @pre cert
     * @post Retorna la capacitat de la canonada
     */
    public float capacitat(){
        return capacitat;
    }

    /**
     * @brief Retorna l'identificador
     * @pre cert
     * @post Retorna l'identificador la canonada
     */
    public String id(){
        return id;
    }
}

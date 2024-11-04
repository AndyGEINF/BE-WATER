/** @file Terminal.java
    @brief Classe Terminal
*/

/** @class Terminal
    @brief Node terminal d'una xarxa de distribució d'aigua
    @author Miquel Coll Barneto
*/

import java.util.*;

public class Terminal extends Nodo {
    
    /** @invariant  0 <= demandaPunta */
    private final float demandaPunta; ///< Demanda punta d'aigua, expressada en litres per segon, en funció del nombre i tipus d'abonats que s'hagi previst connectar-hi.

    /** @invariant  0 <= demandaActual <= demandaPunta */
    private float demandaActual; ///< Demanda d'aigua actual, expressada en litres per segon, inicialitzada a 0
    private Set<String> llistaAbonats; ///< Conjunt d'abonats del punt terminal


    /**
     * @brief Crear un punt terminal
     * @pre \p demandaPunta >= 0
     * @post demandaPunta = \p demandaPunta, llistaAbonats és buida, demandaActual=0
     * @throws IllegalArgumentException si \p demandaPunta < 0
     */
    public Terminal(String id, Coordenades c, float demandaPunta){
        super(id, c); //invocar el constructor de la classe Nodo
        if(demandaPunta < 0){
            throw new IllegalArgumentException("La demandaPunta es menor a 0");
        }
        this.demandaPunta=demandaPunta;
        this.demandaActual=0;
        llistaAbonats=new HashSet<>();
    }

    /**
     * @brief Retorna la demandaPunta
     * @pre cert
     * @post Retorna el valor de la demandaPunta
     */
    public float demandaPunta(){
        return demandaPunta;
    }

    /**
     * @brief Retorna la demandaActual
     * @pre cert
     * @post Retorna el valor de la demandaActual
     */
    public float demandaActual(){
        return demandaActual;
    }


    /**
     * @brief estableix demandaActual
     * @pre \p demanda >= 0
     * @post demandaActual = \p demanda
     * @throws IllegalArgumentException si \p demanda < 0
     */
    public void establirDemandaActual(float demanda){
        if(demanda < 0){
            throw new IllegalArgumentException("La demanda es menor a 0");
        }
        demandaActual=demanda;
    }

    /**
     * @brief Comprova si un client està associat aquest punt Terminal
     * @pre cert
     * @post Retorna true si el client està dintre de la llisaAbonats, altrament false.
     */
    public boolean teAbonat(String idClient){
        return llistaAbonats.contains(idClient);
    }

    /**
     * @brief Abonar un nou client
     * @pre cert
     * @post llistaAbonats conté l'identificador del nou abonat.
     */
    public void nouAbonat(String idClient){
        llistaAbonats.add(idClient);
    }
}

/** @file Origen.java
    @brief Classe Origen
*/

/** @class Origen
    @brief Node origen d'una xarxa de distribució d'aigua
    @author Miquel Coll Barneto
*/

public class Origen extends Nodo {
    /** @invariant  0 <= cabal */
    private float cabal; ///< Cabal d'aigua, expressada en litres per segon, inicialitzada a 0
    

    /**
     * @brief Crear un punt origen
     * @pre cert
     * @post S'ha creat un nou origen amb identificador id i coordenades c, i cabal inicialitzat a 0
     */
    public Origen(String id, Coordenades c){
        super(id, c);
        cabal=0;
    }
    
    /**
     * @brief Retorna el cabal
     * @pre cert
     * @post Retorna el cabal d'aigua que surt de l'origen
     */
    public float cabal(){
        return cabal;
    }

    /**
     * @brief estableix el cabal
     * @pre \p cabal >= 0
     * @post El cabal d'aigua que surt de l'origen és \p cabal
     * @throws IllegalArgumentException si \p cabal < 0
     */
    public void establirCabal(float cabal){
        if (cabal < 0){
            throw new IllegalArgumentException("Cabal es menor a 0");
        }
        this.cabal=cabal;
    }
}

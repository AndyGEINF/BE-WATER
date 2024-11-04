/** @file Connexio.java
    @brief Classe Connexio
*/

/** @class Connexio
    @brief Node connexió d'una xarxa de distribució d'aigua
    @author Miquel Coll Barneto
*/

public class Connexio extends Nodo {

    /**
     * @brief Crear un node connexió
     * @pre cert
     * @post S'ha creat un nou node connexió amb identificador \p id i coordenades \p c
     */
    public Connexio(String id, Coordenades c){
        super(id, c);//invocar el constructor de la classe Nodo
    }
}

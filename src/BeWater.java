/** @file BeWater.java
    @brief Classe BeWater
*/

/** @class BeWater
    @brief Main que ens permet començar la simulació de la xarxa
    @author Andy Moreno Ramon
*/

public abstract class BeWater {
    
    /** @brief Envia els paràmetres necessàris per començar la simulació
	@pre fitxer d'entrada, fitxer de sortida
	@post Simula una xarxa */
    public static void main(String[] args) {
        SimuladorModeText simulador = new SimuladorModeText();
        System.out.println("Be water, my friend");
        simulador.simular(args[0], args[1]);
    }

}

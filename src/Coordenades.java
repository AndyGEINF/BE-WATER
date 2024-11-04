/** @file Coordenades.java
    @brief Classe Coordenades
*/

/** @class Coordenades
    @brief Coordenades geogràfiques (latitud, longitud)
    @author Miquel Coll Barneto
*/

public class Coordenades {

    /** @invariant  -90 <= a_latitud <= 90 */
    private double a_latitud;///< Latitud de les coordenades

    /** @invariant  -180 <= a_longitud <= 180 */
    private double a_longitud;///< Longitud de les coordenades

    private static final int radiTerra = 6371;///< Radi de la Terra en kilòmetres

    /**
     * @brief Crea unes coordenades
     * @pre 0 <= grausLatitud <= 90, 0 <= minutsLatitud <= 60, 0 <= segonsLatitud <= 60, direccioLatitud = 'N' o 'S', 0 <= grausLongitud <= 180, 0 <= minutsLongitud <= 60, 0 <= segonsLongitud <= 60, direccioLatitud = 'E' o 'W'
     * @post Crea unes coordenades segons els valors indicats
     * @throws IllegalArgumentException si es viola la precondició
     */
    public Coordenades(int grausLatitud, int minutsLatitud, double segonsLatitud, char direccioLatitud, int grausLongitud, int minutsLongitud, double segonsLongitud, char direccioLongitud){
        //Comprova la valideza dels parametres
        if (grausLatitud < 0 || grausLatitud > 90 || minutsLatitud < 0 || minutsLatitud > 60 ||
                segonsLatitud < 0 || segonsLatitud > 60 || (direccioLatitud != 'N' && direccioLatitud != 'S') ||
                grausLongitud < 0 || grausLongitud > 180 || minutsLongitud < 0 || minutsLongitud > 60 ||
                segonsLongitud < 0 || segonsLongitud > 60 || (direccioLongitud != 'E' && direccioLongitud != 'W')) {
            throw new IllegalArgumentException("Parametres incorrectes");
        }

        double latitudDecimal = grausLatitud + (minutsLatitud / 60.0) + (segonsLatitud / 3600.0);
        double longitudDecimal = grausLongitud + (minutsLongitud / 60.0) + (segonsLongitud / 3600.0);

        // Ajustar segons la direcció
        if (direccioLatitud == 'S') {
            latitudDecimal *= -1;
        }
        if (direccioLongitud == 'W') {
            longitudDecimal *= -1;
        }

        a_latitud = latitudDecimal;
        a_longitud = longitudDecimal;
    }

    /**
     * @brief Crea unes coordenades
     * @pre -90 <= latitud <= 90, -180 <= longitud <= 180
     * @post Crea unes coordenades segons els valors indicats
     * @throws IllegalArgumentException si es viola la precondició
     */
    public Coordenades(double latitud, double longitud){
        if(latitud<-90 || latitud>90){
            throw new IllegalArgumentException("Latitud incorrecte");
        }
        if(longitud<-180 || longitud>180){
            throw new IllegalArgumentException("Longitud incorrecte");
        }
        a_latitud=latitud;
        a_longitud=longitud;
    }

    /**
     * @brief Retorna la distància
     * @pre cert
     * @post Retorna la distància entre aquestes coordenades i c, expressada en km. Segons la fórmula de Haversine.
     */
    public double distancia(Coordenades c){
        // Convertir les coordenades a radians
        double lat1 = Math.toRadians(a_latitud);
        double lon1 = Math.toRadians(a_longitud);
        double lat2 = Math.toRadians(c.a_latitud);
        double lon2 = Math.toRadians(c.a_longitud);

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        // Calcular la distancia
        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.pow(Math.sin(deltaLon / 2), 2);
        double cVal = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distancia = radiTerra * cVal;

        return distancia;
    }

    /**
     * @brief Mostrar les coordenades
     * @pre cert
     * @post Retorna un string mostrant les coordenades. Mostrar la latitud i longitud amb 2 decimals.
     */
    public String mostrarCoordenades(){
        return "φ: " + String.format("%.2f", a_latitud) +" | λ: "+ String.format("%.2f", a_longitud);
    }
    
    /**
     * @brief Retorna la latitud
     * @pre cert
     * @post Retorna el valor de la latitud
     */
    public double getLatitud() {
        return a_latitud;
    }

    /**
     * @brief Retorna la longitud
     * @pre cert
     * @post Retorna el valor de la longitud
     */
    public double getLongitud() {
        return a_longitud;
    }
}

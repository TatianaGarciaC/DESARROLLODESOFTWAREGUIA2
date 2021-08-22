/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Universidad Ean (BogotÃ¡ - Colombia)
 * Programa de IngenierÃ­a de Sistemas
 * Licenciado bajo el esquema Academic Free License version 2.1
 * <p>
 * Bloque de Estudios: Desarrollo de Software
 * Ejercicio: CÃ¡lculo de Impuestos de Carros
 * Adaptado de: Proyecto CUPI2 - UNIANDES
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package universidadean.impuestoscarro.mundo;

import java.io.*;
import java.util.*;

/**
 * Calculador de impuestos.
 */
public class CalculadorImpuestos {
    // -----------------------------------------------------------------
    // Constantes
    // -----------------------------------------------------------------

    /**
     * Porcentaje de descuento por pronto pago.
     */
    public static final double PORC_DESC_PRONTO_PAGO = 10.0;

    /**
     * Valor de descuento por servicio pÃºblico.
     */
    public static final double VALOR_DESC_SERVICIO_PUBLICO = 50000.0;

    /**
     * Porcentaje de descuento por traslado de cuenta.
     */
    public static final double PORC_DESC_TRASLADO_CUENTA = 5.0;
    

    // -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------

    /**
     * VehÃ­culos que maneja el calculador.
     */
    private Vehiculo[] vehiculos;

    /**
     * VehÃ­culo actual mostrado en la aplicaciÃ³n.
     */
    private int posVehiculoActual;

    /**
     * Rangos de los impuestos.
     */
    private RangoImpuesto[] rangosImpuesto;

    // -----------------------------------------------------------------
    // Constructores
    // -----------------------------------------------------------------

    /**
     * Crea un calculador de impuestos, cargando la informaciÃ³n de dos archivos. <br>
     * <b>post: </b> Se inicializaron los arreglos de vehÃ­culos y rangos.<br>
     * Se cargaron los datos correctamente a partir de los archivos.
     *
     * @throws Exception Error al cargar los archivos.
     */
    public CalculadorImpuestos() throws Exception {
        cargarVehiculos("data/vehiculos.txt");
        cargarTablaImpuestos("data/impuestos.properties");
    }

    // ----------------------------------------------------------------
    // MÃ©todos
    // ----------------------------------------------------------------

    /**
     * Carga los datos de los vehÃ­culos que maneja el calculador de impuestos. <br>
     * <b>post: </b> Se cargan todos los vehÃ­culos del archivo con sus datos.
     *
     * @param pArchivo Nombre del archivo donde se encuentran los datos de los vehÃ­culos. pArchivo != null.
     * @throws Exception Si ocurre algÃºn error cargando los datos.
     */
    private void cargarVehiculos(String pArchivo) throws Exception {
        String texto, valores[], sMarca, sLinea, sModelo, sImagen;
        double precio;
        int cantidad = 0;
        Vehiculo vehiculo;
        try {
            File datos = new File(pArchivo);
            FileReader fr = new FileReader(datos);
            BufferedReader lector = new BufferedReader(fr);
            texto = lector.readLine();

            cantidad = Integer.parseInt(texto);
            vehiculos = new Vehiculo[cantidad];
            posVehiculoActual = 0;

            texto = lector.readLine();
            for (int i = 0; i < vehiculos.length; i++) {
                valores = texto.split(",");

                sMarca = valores[0];
                sLinea = valores[1];
                sModelo = valores[2];
                sImagen = valores[4];
                precio = Double.parseDouble(valores[3]);

                vehiculo = new Vehiculo(sMarca, sLinea, sModelo, precio, sImagen);
                vehiculos[i] = vehiculo;
                // Siguiente lÃ­nea
                texto = lector.readLine();
            }
            lector.close();
        }
        catch (IOException e) {
            throw new Exception("Error al cargar los datos almacenados de vehÃ­culos.");
        }
        catch (NumberFormatException e) {
            throw new Exception("El archivo no tiene el formato esperado.");
        }
    }

    /**
     * Carga la tabla de impuestos por los rangos. <br>
     * <b>post: </b> Se cargan todos valores de impuestos segÃºn los rangos de valores.
     *
     * @param pArchivo UbicaciÃ³n del archivo a leer. pArchivo != null.
     * @throws Exception Si ocurre un error al cargar los rangos.
     */
    private void cargarTablaImpuestos(String pArchivo) throws Exception {
        Properties datos = new Properties();
        int rangos = 0;
        String texto, valores[];
        double inicio, fin, porcentaje;
        RangoImpuesto rango;
        try {
            FileInputStream input = new FileInputStream(pArchivo);
            datos.load(input);
            rangos = Integer.parseInt(datos.getProperty("numero.rangos"));
            rangosImpuesto = new RangoImpuesto[rangos];

            for (int i = 0; i < rangos; i++) {
                texto = datos.getProperty("rango" + (i + 1));
                valores = texto.split(",");
                try {
                    inicio = Double.parseDouble(valores[0]);
                    fin = Double.parseDouble(valores[1]);
                    porcentaje = Double.parseDouble(valores[2]);
                }
                catch (Exception e) {
                    throw new Exception("Error en la definiciÃ³n de rango" + i);
                }

                rango = new RangoImpuesto(inicio, fin, porcentaje);
                rangosImpuesto[i] = rango;
            }
        }
        catch (IOException e) {
            throw new Exception("Error al cargar los rangos de impuestos.");
        }
        catch (NumberFormatException e) {
            throw new Exception("Error en el formato del archivo.");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error en el formato del archivo.");
        }
    }

    /**
     * Busca, dado un valor, el rango de impuestos al que corresponde.
     *
     * @param valor Valor a buscar. valor > 0.
     * @return Rango de impuesto que contiene al valor o null si no lo encuentra.
     */
    private RangoImpuesto buscarRangoImpuesto(double valor) {
    	RangoImpuesto rangoImp= null;
    	
    	if(valor > 0) {

    		for(RangoImpuesto rango: rangosImpuesto) {
                if(rango.contieneA(valor)){
                	rangoImp=rango;
                }

    		}
    		
            return rangoImp;
    	} else {
    		return rangoImp;
    	}
    }

    /**
     * Calcula el pago de impuesto que debe hacer el vehÃ­culo actual. <br>
     * <b>pre:</b> Las listas de rangos y vehÃ­culos estÃ¡n inicializadas.
     *
     * @param descProntoPago      Indica si aplica el descuento por pronto pago.
     * @param descServicioPublico Indica si aplica el descuento por servicio pÃºblico.
     * @param descTrasladoCuenta  Indica si aplica el descuento por traslado de cuenta.
     * @return Valor a pagar de acuerdo con las caracterÃ­sticas del vehÃ­culo y los descuentos que se pueden aplicar.
     */
    public double calcularPago(boolean descProntoPago, boolean descServicioPublico, boolean descTrasladoCuenta) {
        double pago = 0.0;
        double precio = darVehiculoActual().darPrecio();

        RangoImpuesto rango = buscarRangoImpuesto(precio);
        
        pago = precio - calcularPorcentaje(rango.darPorcentaje(), precio);
        
        
        if(descProntoPago) {
        	pago = pago - calcularPorcentaje(PORC_DESC_PRONTO_PAGO, pago);
        } 
        
        if(descServicioPublico) {
        	pago = pago - VALOR_DESC_SERVICIO_PUBLICO;

        }
        
        if(descTrasladoCuenta) {
        	pago = pago - calcularPorcentaje(PORC_DESC_TRASLADO_CUENTA, pago);

        }

        return pago;
    }
    
    public Double calcularPorcentaje(double porcent, double cant){
        double resultado = (porcent / 100) * cant;
        return resultado;
    }

    /**
     * Retorna el primer vehÃ­culo. <br>
     * <b>post: </b> Se actualizÃ³ la posiciÃ³n del vehÃ­culo actual.
     *
     * @return El primer vehÃ­culo, que ahora es el vehÃ­culo actual.
     * @throws Exception Si ya se encuentra en el primer vehÃ­culo.
     */
    public Vehiculo darPrimero() throws Exception {
        if (posVehiculoActual == 0) {
            throw new Exception("Ya se encuentra en el primer vehÃ­culo.");
        }
        posVehiculoActual = 0;
        return darVehiculoActual();
    }

    /**
     * Retorna el vehÃ­culo anterior al actual. <br>
     * <b>post: </b> Se actualizÃ³ la posiciÃ³n del vehÃ­culo actual.
     *
     * @return El anterior vehÃ­culo, que ahora es el vehÃ­culo actual.
     * @throws Exception Si ya se encuentra en el primer vehÃ­culo.
     */
    public Vehiculo darAnterior() throws Exception {
        if (posVehiculoActual == 0) {
            throw new Exception("Se encuentra en el primer vehÃ­culo.");
        }
        posVehiculoActual--;
        return darVehiculoActual();
    }

    /**
     * Retorna el vehÃ­culo siguiente al actual. <br>
     * <b>post: </b> Se actualizÃ³ la posiciÃ³n del vehÃ­culo actual.
     *
     * @return El siguiente vehÃ­culo, que ahora es el vehÃ­culo actual.
     * @throws Exception Si ya se encuentra en el Ãºltimo vehÃ­culo
     */
    public Vehiculo darSiguiente() throws Exception {
        if (posVehiculoActual == vehiculos.length - 1) {
            throw new Exception("Se encuentra en el Ãºltimo vehÃ­culo.");
        }
        posVehiculoActual++;
        return darVehiculoActual();
    }

    /**
     * Retorna el Ãºltimo vehÃ­culo. <br>
     * <b>post: </b> Se actualizÃ³ la posiciÃ³n del vehÃ­culo actual.
     *
     * @return El Ãºltimo vehÃ­culo, que ahora es el vehÃ­culo actual.
     * @throws Exception Si ya se encuentra en el Ãºltimo vehÃ­culo
     */
    public Vehiculo darUltimo() throws Exception {
        if (posVehiculoActual == vehiculos.length - 1) {
            throw new Exception("Ya se encuentra en el Ãºltimo vehÃ­culo.");
        }
        posVehiculoActual = vehiculos.length - 1;
        return darVehiculoActual();
    }

    /**
     * Retorna el vehÃ­culo actual.
     *
     * @return El vehÃ­culo actual.
     */
    public Vehiculo darVehiculoActual() {
        return vehiculos[posVehiculoActual];
    }

    /**
     * Busca el vehÃ­culo mÃ¡s caro, lo asigna como actual y lo retorna.
     *
     * @return El vehÃ­culo mÃ¡s caro.
     */
    public Vehiculo buscarVehiculoMasCaro() {
        Vehiculo masCaro = null;

        double precioVehiculo = 0.0;

        for(Vehiculo v : vehiculos){
            if (v.darPrecio() > precioVehiculo) {
                masCaro = v;
                precioVehiculo = v.darPrecio();
            }
        }

        return masCaro;

    }

    /**
     * Busca y retorna el primer vehÃ­culo que encuentra con la marca que llega por parÃ¡metro. <br>
     * <b>post: </b> Se asigna como vehÃ­culo actual al vehÃ­culo encontrado.
     *
     * @param marca Marca buscada.
     * @return El primer vehÃ­culo de la marca. Si no encuentra ninguno retorna null.
     */
    public Vehiculo buscarVehiculoPorMarca(String marca) {
        Vehiculo buscado = null;

        for(Vehiculo v : vehiculos){
            if (v.darMarca().equalsIgnoreCase(marca)) {
                buscado = v;
            }
        }
        return buscado;
    }

    /**
     * Busca y retorna el vehÃ­culo de la lÃ­nea buscada. <br>
     * <b>post: </b> Se asigna como vehÃ­culo actual al vehÃ­culo encontrado.
     *
     * @param linea LÃ­nea buscada. pLinea != null && pLinea != ""
     * @return El vehÃ­culo de la lÃ­nea, null si no encuentra ninguno.
     */
    public Vehiculo buscarVehiculoPorLinea(String linea) {
        Vehiculo buscado = null;

        Vehiculo buscado = null;

        for(Vehiculo v : vehiculos){
            if (v.darLinea().equalsIgnoreCase(linea)) {
                buscado = v;
            }
        }
        return buscado;
    }

    /**
     * Busca el vehÃ­culo mÃ¡s antiguo, lo asigna como actual y lo retorna.
     *
     * @return El vehÃ­culo mÃ¡s antiguo.
     */
    public Vehiculo buscarVehiculoMasAntiguo() {
        Vehiculo buscado = null;
        Integer anioMaximo = 9999;

        for(Vehiculo v : vehiculos){
            Integer anio = Integer.parseInt(v.darAnio());
            DriverManager.println(anio.toString());
            if (anio < anioMaximo){
                anioMaximo = anio;
                buscado = v;
            }
        }
        return buscado;
    }

    /**
     * Calcula el promedio de los precios de todos los automÃ³viles que estÃ¡n en el sistema
     *
     * @return Promedio de precios
     */
    public double promedioPreciosVehiculos() {
        double promedio = 0.0;
        for (Vehiculo v : vehiculos){
            promedio = promedio + v.darPrecio()/vehiculos.length;
        }
        return promedio;
    }


}

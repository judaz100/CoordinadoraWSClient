package co.matisses.carriers.coordinadora;

import com.coordinadora.sandbox.ags._1_4.server.ArrayOfCotizadorDetalleempaques;
import com.coordinadora.sandbox.ags._1_4.server.ArrayOfInt;
import com.coordinadora.sandbox.ags._1_4.server.CotizadorCotizarIn;
import com.coordinadora.sandbox.ags._1_4.server.CotizadorCotizarOut;
import com.coordinadora.sandbox.ags._1_4.server.CotizadorDetalleEmpaques;
import com.coordinadora.sandbox.ags._1_4.server.RpcServerSoapManagerPort;
import com.coordinadora.sandbox.ags._1_4.server.RpcServerSoapManagerService;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dbotero
 */
public class CoordinadoraWSMain {

    /**
     *
     * @param wsdl ruta para el wsdl del servicio
     * @param ciudadDestino el codigo de 8 caracteres de la ciudad destino. Si
     * la ciudad es Medellin, por ejemplo (05001), codigo a enviar debe ser
     * 05001000
     * @param articulos mapa que contiene la ciudad de origen de los productos
     * que vienen en la lista. Esta lista contiene el detalle de cada producto
     * en el siguiente orden: 1.alto, 2. ancho, 3. largo, 4. peso, 5. unidades,
     * 6. valor
     * @return valor total del envio
     * @throws java.lang.Exception
     */
    public int cotizarEnvio(String wsdl, String ciudadDestino, Map<String, List<String[]>> articulos) throws Exception {
        RpcServerSoapManagerService service = new RpcServerSoapManagerService(new URL(wsdl));
        int valoracionMcia = 0;
        int totalEnvio = 0;
        RpcServerSoapManagerPort port = service.getRpcServerSoapManagerPort();
        for (String ciudadOrigen : articulos.keySet()) {
            valoracionMcia = 0;
            CotizadorCotizarIn request = new CotizadorCotizarIn();
            request.setApikey("4b8a29dc-c92d-11e5-9956-625662870761");
            request.setClave("36U1qY{H$656J");
            request.setCuenta("1");
            request.setDiv("01");
            request.setNit("900060329");
            request.setProducto("0");
            request.setDestino(ciudadDestino);
            request.setOrigen(ciudadOrigen);

            ArrayOfInt nivelServ = new ArrayOfInt();
            nivelServ.getItem().add(0);
            request.setNivelServicio(nivelServ);

            ArrayOfCotizadorDetalleempaques detalle = new ArrayOfCotizadorDetalleempaques();
            List<String[]> items = articulos.get(ciudadOrigen);
            for (String[] item : items) {
                CotizadorDetalleEmpaques itm = new CotizadorDetalleEmpaques();
                itm.setAlto(item[0]);
                itm.setAncho(item[1]);
                itm.setLargo(item[2]);
                itm.setPeso(item[3]);
                itm.setUnidades(item[4]);
                itm.setUbl("0");
                detalle.getItem().add(itm);
                valoracionMcia += Integer.parseInt(item[5]) * Integer.parseInt(item[4]);
            }
            request.setValoracion(Integer.toString(valoracionMcia));
            request.setDetalle(detalle);
            CotizadorCotizarOut response = port.cotizadorCotizar(request);
            totalEnvio += response.getFleteTotal();
        }
        return totalEnvio;
    }

    public static void main(String[] args) {
        CoordinadoraWSMain c = new CoordinadoraWSMain();
        Map<String, List<String[]>> m = new HashMap<>();
        List<String[]> s = new ArrayList<>();

        String[] a = new String[6];

        a[0] = "83.000000";
        a[1] = "69.000000";
        a[2] = "84.000000";
        a[3] = "29.500000";
        a[4] = "1.000000";
        a[5] = "4845000";

        s.add(a);
        m.put("11001000", s);

        try {
            System.out.println(c.cotizarEnvio("http://sandbox.coordinadora.com/ags/1.4/server.php?wsdl", "05088000", m));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

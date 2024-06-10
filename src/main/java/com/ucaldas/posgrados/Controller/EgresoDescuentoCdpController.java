package com.ucaldas.posgrados.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.Cdp;
import com.ucaldas.posgrados.Entity.EgresoDescuentoCDP;
import com.ucaldas.posgrados.Entity.EgresosDescuentos;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Entity.TipoDescuento;
import com.ucaldas.posgrados.Repository.CdpRepository;
import com.ucaldas.posgrados.Repository.EgresoDescuentoCdpRepository;
import com.ucaldas.posgrados.Repository.EgresosDescuentosRepository;
import com.ucaldas.posgrados.Repository.TipoDescuentoRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoDescuentoCdp")
public class EgresoDescuentoCdpController {

    @Autowired
    private CdpRepository cdpRepository;

    @Autowired
    private EgresoDescuentoCdpRepository egresoDescuentoCDPRepository;

    @Autowired
    private EgresosDescuentosRepository egresoDescuentoRepository;

    @Autowired
    private TipoDescuentoRepository tipoDescuentoRepository;

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<EgresoDescuentoCDP> listar() {
        return egresoDescuentoCDPRepository.findAll();
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que se tuvo en
     * cuenta en el presupuesto.
     * En el frontend habrá una lista de egresos del presupuesto en la sección de
     * descuentos. Cuando se elija uno, se cargará
     * toda la información de este en los campos, si se guarda así tal como está
     * entonces se pondrá la etiqueta MISMOVALOR, en cambio
     * si se cambia algún valor entonces se pondrá la etiqueta OTROVALOR.
     * 
     * El tipo de descuento no se puede modificar.
     */
    @PostMapping("/crearEgresoCDPDelPresupuesto")
    public @ResponseBody String crearEgresoCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam int numEstudiantes,
            @RequestParam double valor, @RequestParam int numPeriodos,
            @RequestParam int idTipoDescuento, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoDescuento tipoDescuento = tipoDescuentoRepository.findById(idTipoDescuento).orElseThrow();

        EgresosDescuentos egresoDescuento = new EgresosDescuentos();

        egresoDescuento = guardarValoresEgresoCDP(egresoDescuento, numEstudiantes, valor, numPeriodos, tipoDescuento);

        EgresosDescuentos egresoDelPresupuesto = egresoDescuentoRepository.findById(idEgresoDelPresupuesto)
                .orElseThrow();

        // Si el egreso ya fue utilizado en la ejecución presupuestal, entonces no se
        // puede volver a utilizar
        if (egresoDelPresupuesto.getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION) {
            return "ERROR: Ya se creó un CDP con este egreso";
        }

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getNumEstudiantes() == numEstudiantes
                && egresoDelPresupuesto.getValor() == valor
                && egresoDelPresupuesto.getNumPeriodos() == numPeriodos
                && egresoDelPresupuesto.getTipoDescuento().getId() == idTipoDescuento) {
            egresoDescuento.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoDescuento.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoDescuentoRepository.save(egresoDescuento);

        // Marcar cómo utilizado para que no se pueda volver a utilizar
        egresoDelPresupuesto.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION);
        egresoDescuentoRepository.save(egresoDelPresupuesto);

        // Después de creado el egreso general, se puede pasar como atributo al egreso
        // general CDP pues en la base de datos se guardará el id del egreso general

        EgresoDescuentoCDP egresoDescuentoCdp = new EgresoDescuentoCDP();
        egresoDescuentoCdp.setCpc(cpc);
        egresoDescuentoCdp.setDescripcion(descripcionEgresoCDP);
        egresoDescuentoCdp.setCdp(cdp);
        egresoDescuentoCdp.setEgresoDescuento(egresoDescuento);

        egresoDescuentoCDPRepository.save(egresoDescuentoCdp);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosDescuentos guardarValoresEgresoCDP(EgresosDescuentos egresoDescuento, int numEstudiantes,
            double valor, int numPeriodos, TipoDescuento tipoDescuento) {

        egresoDescuento.setNumEstudiantes(numEstudiantes);
        egresoDescuento.setValor(valor);
        egresoDescuento.setNumPeriodos(numPeriodos);
        egresoDescuento.setTipoDescuento(tipoDescuento);
        egresoDescuento.setTotalDescuento(numPeriodos * numEstudiantes * valor);

        egresoDescuento.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoDescuento.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoDescuento;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoCDPFueraDelPresupuesto")
    public @ResponseBody String crearEgresoCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam int numEstudiantes,
            @RequestParam double valor, @RequestParam int numPeriodos,
            @RequestParam int idTipoDescuento, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoDescuento tipoDescuento = tipoDescuentoRepository.findById(idTipoDescuento).orElseThrow();

        EgresosDescuentos egresoDescuento = new EgresosDescuentos();

        egresoDescuento = guardarValoresEgresoCDP(egresoDescuento, numEstudiantes, valor, numPeriodos, tipoDescuento);

        egresoDescuento.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoDescuentoRepository.save(egresoDescuento);

        EgresoDescuentoCDP egresoDescuentoCdp = new EgresoDescuentoCDP();
        egresoDescuentoCdp.setCpc(cpc);
        egresoDescuentoCdp.setDescripcion(descripcionEgresoCDP);
        egresoDescuentoCdp.setCdp(cdp);
        egresoDescuentoCdp.setEgresoDescuento(egresoDescuento);

        egresoDescuentoCDPRepository.save(egresoDescuentoCdp);

        return "OK";
    }
}

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
import com.ucaldas.posgrados.Entity.EgresoGeneralCDP;
import com.ucaldas.posgrados.Entity.EgresosGenerales;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Entity.TipoCosto;
import com.ucaldas.posgrados.Repository.CdpRepository;
import com.ucaldas.posgrados.Repository.EgresoGeneralCdpRepository;
import com.ucaldas.posgrados.Repository.EgresosGeneralesRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoGeneralCdp")
public class EgresoGeneralCdpController {

    @Autowired
    private CdpRepository cdpRepository;

    @Autowired
    private EgresoGeneralCdpRepository egresoGeneralCDPRepository;

    @Autowired
    private EgresosGeneralesRepository egresoGeneralRepository;

    @Autowired
    private TipoCostoRepository tipoCostoRepository;

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<EgresoGeneralCDP> listar() {
        return egresoGeneralCDPRepository.findAll();
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
     * El concepto no se puede modificar.
     */
    @PostMapping("/crearEgresoCDPDelPresupuesto")
    public @ResponseBody String crearEgresoCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoCosto tipoCosto = tipoCostoRepository.findById(idTipoCosto).orElseThrow();

        EgresosGenerales egresoGeneral = new EgresosGenerales();

        egresoGeneral = guardarValoresEgresoCDP(egresoGeneral, concepto, valorUnitario, cantidad, tipoCosto);

        EgresosGenerales egresoDelPresupuesto = egresoGeneralRepository.findById(idEgresoDelPresupuesto).orElseThrow();

        // Si el egreso ya fue utilizado en la ejecución presupuestal, entonces no se
        // puede volver a utilizar
        if (egresoDelPresupuesto.getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION) {
            return "ERROR: Ya se creó un CDP con este egreso";
        }

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getConcepto().equals(concepto)
                && egresoDelPresupuesto.getValorUnitario() == valorUnitario
                && egresoDelPresupuesto.getCantidad() == cantidad
                && egresoDelPresupuesto.getTipoCosto().getId() == idTipoCosto) {
            egresoGeneral.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoGeneral.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoGeneralRepository.save(egresoGeneral);

        // Marcar cómo utilizado para que no se pueda volver a utilizar
        egresoDelPresupuesto.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION);
        egresoGeneralRepository.save(egresoDelPresupuesto);

        // Después de creado el egreso general, se puede pasar como atributo al egreso
        // general CDP pues en la base de datos se guardará el id del egreso general

        EgresoGeneralCDP egresoGeneralCdp = new EgresoGeneralCDP();
        egresoGeneralCdp.setCpc(cpc);
        egresoGeneralCdp.setDescripcion(descripcionEgresoCDP);
        egresoGeneralCdp.setCdp(cdp);
        egresoGeneralCdp.setEgresoGeneral(egresoGeneral);

        egresoGeneralCDPRepository.save(egresoGeneralCdp);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosGenerales guardarValoresEgresoCDP(EgresosGenerales egresoGeneral, String concepto,
            double valorUnitario,
            int cantidad, TipoCosto tipoCosto) {

        egresoGeneral.setConcepto(concepto);
        egresoGeneral.setValorUnitario(valorUnitario);
        egresoGeneral.setCantidad(cantidad);
        egresoGeneral.setValorTotal(cantidad * valorUnitario);
        egresoGeneral.setTipoCosto(tipoCosto);

        egresoGeneral.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoGeneral.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoGeneral;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoCDPFueraDelPresupuesto")
    public @ResponseBody String crearEgresoCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoCosto tipoCosto = tipoCostoRepository.findById(idTipoCosto).orElseThrow();

        EgresosGenerales egresoGeneral = new EgresosGenerales();

        egresoGeneral = guardarValoresEgresoCDP(egresoGeneral, concepto, valorUnitario, cantidad, tipoCosto);

        egresoGeneral.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoGeneralRepository.save(egresoGeneral);

        EgresoGeneralCDP egresoGeneralCdp = new EgresoGeneralCDP();
        egresoGeneralCdp.setCpc(cpc);
        egresoGeneralCdp.setDescripcion(descripcionEgresoCDP);
        egresoGeneralCdp.setCdp(cdp);
        egresoGeneralCdp.setEgresoGeneral(egresoGeneral);

        egresoGeneralCDPRepository.save(egresoGeneralCdp);

        return "OK";
    }
}

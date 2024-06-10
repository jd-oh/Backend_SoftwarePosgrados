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
import com.ucaldas.posgrados.Entity.EgresoRecurrenteAdmCDP;
import com.ucaldas.posgrados.Entity.EgresosRecurrentesAdm;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Repository.CdpRepository;
import com.ucaldas.posgrados.Repository.EgresoRecurrenteAdmCdpRepository;
import com.ucaldas.posgrados.Repository.EgresosRecurrentesAdmRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoRecurrenteAdmCdp")
public class EgresoRecurrenteAdmCdpController {

    @Autowired
    private CdpRepository cdpRepository;

    @Autowired
    private EgresoRecurrenteAdmCdpRepository egresoRecurrenteAdmCDPRepository;

    @Autowired
    private EgresosRecurrentesAdmRepository egresoRecurrenteAdmRepository;

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<EgresoRecurrenteAdmCDP> listar() {
        return egresoRecurrenteAdmCDPRepository.findAll();
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
     * La unidad ni el cargo se pueden modificar.
     */
    @PostMapping("/crearEgresoCDPDelPresupuesto")
    public @ResponseBody String crearEgresoCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String unidad,
            @RequestParam String cargo,
            @RequestParam double valorHora, @RequestParam int numHoras, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        EgresosRecurrentesAdm egresoRecurrenteAdm = new EgresosRecurrentesAdm();

        egresoRecurrenteAdm = guardarValoresEgresoCDP(egresoRecurrenteAdm, unidad, cargo, valorHora, numHoras);

        EgresosRecurrentesAdm egresoDelPresupuesto = egresoRecurrenteAdmRepository.findById(idEgresoDelPresupuesto)
                .orElseThrow();

        // Si el egreso ya fue utilizado en la ejecución presupuestal, entonces no se
        // puede volver a utilizar
        if (egresoDelPresupuesto.getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION) {
            return "ERROR: Ya se creó un CDP con este egreso";
        }

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getUnidad().equals(unidad) && egresoDelPresupuesto.getCargo().equals(cargo)
                && egresoDelPresupuesto.getValorHora() == valorHora && egresoDelPresupuesto.getNumHoras() == numHoras) {
            egresoRecurrenteAdm.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoRecurrenteAdm.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoRecurrenteAdmRepository.save(egresoRecurrenteAdm);

        // Marcar cómo utilizado para que no se pueda volver a utilizar
        egresoDelPresupuesto.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION);
        egresoRecurrenteAdmRepository.save(egresoDelPresupuesto);

        // Después de creado el egreso general, se puede pasar como atributo al egreso
        // general CDP pues en la base de datos se guardará el id del egreso general

        EgresoRecurrenteAdmCDP egresoRecurrenteAdmCdp = new EgresoRecurrenteAdmCDP();
        egresoRecurrenteAdmCdp.setCpc(cpc);
        egresoRecurrenteAdmCdp.setDescripcion(descripcionEgresoCDP);
        egresoRecurrenteAdmCdp.setCdp(cdp);
        egresoRecurrenteAdmCdp.setEgresoRecurrenteAdm(egresoRecurrenteAdm);

        egresoRecurrenteAdmCDPRepository.save(egresoRecurrenteAdmCdp);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosRecurrentesAdm guardarValoresEgresoCDP(EgresosRecurrentesAdm egresoRecurrenteAdm,
            @RequestParam String unidad,
            @RequestParam String cargo,
            @RequestParam double valorHora, @RequestParam int numHoras) {

        egresoRecurrenteAdm.setUnidad(unidad);
        egresoRecurrenteAdm.setCargo(cargo);
        egresoRecurrenteAdm.setValorHora(valorHora);
        egresoRecurrenteAdm.setNumHoras(numHoras);
        egresoRecurrenteAdm.setValorTotal(numHoras * valorHora);

        egresoRecurrenteAdm.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoRecurrenteAdm.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoRecurrenteAdm;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoCDPFueraDelPresupuesto")
    public @ResponseBody String crearEgresoCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String unidad,
            @RequestParam String cargo,
            @RequestParam double valorHora, @RequestParam int numHoras, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        EgresosRecurrentesAdm egresoRecurrenteAdm = new EgresosRecurrentesAdm();

        egresoRecurrenteAdm = guardarValoresEgresoCDP(egresoRecurrenteAdm, unidad, cargo, valorHora, numHoras);
        egresoRecurrenteAdm.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoRecurrenteAdmRepository.save(egresoRecurrenteAdm);

        EgresoRecurrenteAdmCDP egresoRecurrenteAdmCdp = new EgresoRecurrenteAdmCDP();
        egresoRecurrenteAdmCdp.setCpc(cpc);
        egresoRecurrenteAdmCdp.setDescripcion(descripcionEgresoCDP);
        egresoRecurrenteAdmCdp.setCdp(cdp);
        egresoRecurrenteAdmCdp.setEgresoRecurrenteAdm(egresoRecurrenteAdm);

        egresoRecurrenteAdmCDPRepository.save(egresoRecurrenteAdmCdp);

        return "OK";
    }
}

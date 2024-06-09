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
import com.ucaldas.posgrados.Entity.EgresoInversionCDP;
import com.ucaldas.posgrados.Entity.EgresosInversiones;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Entity.TipoInversion;
import com.ucaldas.posgrados.Repository.CdpRepository;
import com.ucaldas.posgrados.Repository.EgresoInversionCdpRepository;
import com.ucaldas.posgrados.Repository.EgresosInversionesRepository;
import com.ucaldas.posgrados.Repository.TipoInversionRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoInversionCdp")
public class EgresoInversionCdpController {

    @Autowired
    private CdpRepository cdpRepository;

    @Autowired
    private EgresoInversionCdpRepository egresoInversionCDPRepository;

    @Autowired
    private EgresosInversionesRepository egresoInversionRepository;

    @Autowired
    private TipoInversionRepository tipoInversionRepository;

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<EgresoInversionCDP> listar() {
        return egresoInversionCDPRepository.findAll();
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
            @RequestParam double valor,
            @RequestParam int idTipoInversion, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoInversion tipoInversion = tipoInversionRepository.findById(idTipoInversion).orElseThrow();

        EgresosInversiones egresoInversion = new EgresosInversiones();

        egresoInversion = guardarValoresEgresoCDP(egresoInversion, concepto, valor, tipoInversion);

        EgresosInversiones egresoDelPresupuesto = egresoInversionRepository.findById(idEgresoDelPresupuesto)
                .orElseThrow();

        // Si el egreso ya fue utilizado en la ejecución presupuestal, entonces no se
        // puede volver a utilizar
        if (egresoDelPresupuesto.getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION) {
            return "ERROR: Ya se creó un CDP con este egreso";
        }

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getValor() == valor && egresoDelPresupuesto.getConcepto().equals(concepto)
                && egresoDelPresupuesto.getTipoInversion().equals(tipoInversion)) {
            egresoInversion.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoInversion.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoInversionRepository.save(egresoInversion);

        // Marcar cómo utilizado para que no se pueda volver a utilizar
        egresoDelPresupuesto.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION);
        egresoInversionRepository.save(egresoDelPresupuesto);

        // Después de creado el egreso general, se puede pasar como atributo al egreso
        // general CDP pues en la base de datos se guardará el id del egreso general

        EgresoInversionCDP egresoInversionCdp = new EgresoInversionCDP();
        egresoInversionCdp.setCpc(cpc);
        egresoInversionCdp.setDescripcion(descripcionEgresoCDP);
        egresoInversionCdp.setCdp(cdp);
        egresoInversionCdp.setEgresoInversion(egresoInversion);

        egresoInversionCDPRepository.save(egresoInversionCdp);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosInversiones guardarValoresEgresoCDP(EgresosInversiones egresoInversion,
            @RequestParam String concepto,
            @RequestParam double valor, TipoInversion tipoInversion) {

        egresoInversion.setConcepto(concepto);
        egresoInversion.setValor(valor);
        egresoInversion.setTipoInversion(tipoInversion);

        egresoInversion.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoInversion.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoInversion;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoCDPFueraDelPresupuesto")
    public @ResponseBody String crearEgresoCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String concepto,
            @RequestParam double valor,
            @RequestParam int idTipoInversion, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoInversion tipoInversion = tipoInversionRepository.findById(idTipoInversion).orElseThrow();

        EgresosInversiones egresoInversion = new EgresosInversiones();

        egresoInversion = guardarValoresEgresoCDP(egresoInversion, concepto, valor, tipoInversion);

        egresoInversion.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoInversionRepository.save(egresoInversion);

        EgresoInversionCDP egresoInversionCdp = new EgresoInversionCDP();
        egresoInversionCdp.setCpc(cpc);
        egresoInversionCdp.setDescripcion(descripcionEgresoCDP);
        egresoInversionCdp.setCdp(cdp);
        egresoInversionCdp.setEgresoInversion(egresoInversion);

        egresoInversionCDPRepository.save(egresoInversionCdp);

        return "OK";
    }
}

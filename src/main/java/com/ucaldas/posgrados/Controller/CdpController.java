package com.ucaldas.posgrados.Controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.Cdp;
import com.ucaldas.posgrados.Entity.EgresoCDP;
import com.ucaldas.posgrados.Entity.EgresoDescuentoCDP;
import com.ucaldas.posgrados.Entity.EgresoGeneralCDP;
import com.ucaldas.posgrados.Entity.EgresoInversionCDP;
import com.ucaldas.posgrados.Entity.EgresoOtroCDP;
import com.ucaldas.posgrados.Entity.EgresoOtroServDocenteCDP;
import com.ucaldas.posgrados.Entity.EgresoRecurrenteAdmCDP;
import com.ucaldas.posgrados.Entity.EgresoServDocenteCDP;
import com.ucaldas.posgrados.Entity.EgresoServNoDocenteCDP;
import com.ucaldas.posgrados.Entity.EgresoTransferenciaCDP;
import com.ucaldas.posgrados.Entity.EgresoViajeCDP;
import com.ucaldas.posgrados.Entity.EgresosDescuentos;
import com.ucaldas.posgrados.Entity.EgresosGenerales;
import com.ucaldas.posgrados.Entity.EgresosInversiones;
import com.ucaldas.posgrados.Entity.EgresosOtros;
import com.ucaldas.posgrados.Entity.EgresosOtrosServDocentes;
import com.ucaldas.posgrados.Entity.EgresosRecurrentesAdm;
import com.ucaldas.posgrados.Entity.EgresosServDocentes;
import com.ucaldas.posgrados.Entity.EgresosServNoDocentes;
import com.ucaldas.posgrados.Entity.EgresosTransferencias;
import com.ucaldas.posgrados.Entity.EgresosViajes;
import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.RegistroFinanciero;
import com.ucaldas.posgrados.Entity.Usuario;
import com.ucaldas.posgrados.Repository.CdpRepository;
import com.ucaldas.posgrados.Repository.EgresosDescuentosRepository;
import com.ucaldas.posgrados.Repository.EgresosGeneralesRepository;
import com.ucaldas.posgrados.Repository.EgresosInversionesRepository;
import com.ucaldas.posgrados.Repository.EgresosOtrosRepository;
import com.ucaldas.posgrados.Repository.EgresosOtrosServDocentesRepository;
import com.ucaldas.posgrados.Repository.EgresosRecurrentesAdmRepository;
import com.ucaldas.posgrados.Repository.EgresosServDocentesRepository;
import com.ucaldas.posgrados.Repository.EgresosServNoDocentesRepository;
import com.ucaldas.posgrados.Repository.EgresosTransferenciasRepository;
import com.ucaldas.posgrados.Repository.EgresosViajesRepository;
import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;
import com.ucaldas.posgrados.Repository.UsuarioRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/cdp")
public class CdpController {

    @Autowired
    private CdpRepository cdpRepository;

    /* REPOSITORIES */

    @Autowired
    private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;

    @Autowired
    private EgresosDescuentosRepository egresosDescuentosRepository;

    @Autowired
    private EgresosGeneralesRepository egresosGeneralesRepository;

    @Autowired
    private EgresosInversionesRepository egresosInversionesRepository;

    @Autowired
    private EgresosOtrosRepository egresosOtrosRepository;

    @Autowired
    private EgresosOtrosServDocentesRepository egresosOtrosServDocentesRepository;

    @Autowired
    private EgresosRecurrentesAdmRepository egresosRecurrentesAdmRepository;

    @Autowired
    private EgresosServDocentesRepository egresosServDocentesRepository;

    @Autowired
    private EgresosServNoDocentesRepository egresosServNoDocentesRepository;

    @Autowired
    private EgresosTransferenciasRepository egresosTransferenciasRepository;

    @Autowired
    private EgresosViajesRepository egresosViajesRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /* CONTROLLERS */

    @Autowired
    private EgresoDescuentoCdpController egresoDescuentoCdpController;

    @Autowired
    private EgresoGeneralCdpController egresoGeneralCdpController;

    @Autowired
    private EgresoInversionCdpController egresoInversionCdpController;

    @Autowired
    private EgresoOtroCdpController egresoOtrosCdpController;

    @Autowired
    private EgresoOtroServDocenteCdpController egresoOtrosServDocentesCdpController;

    @Autowired
    private EgresoRecurrenteAdmCdpController egresoRecurrenteAdmCdpController;

    @Autowired
    private EgresoServDocenteCdpController egresoServDocenteCdpController;

    @Autowired
    private EgresoServNoDocenteCdpController egresoServNoDocenteCdpController;

    @Autowired
    private EgresoTransferenciaCdpController egresoTransferenciaCdpController;

    @Autowired
    private EgresoViajeCdpController egresoViajeCdpController;

    // Una ejecución presupuestal se crea cuando se aprueba un presupuesto
    @GetMapping(path = "/verificarExistenciaEjecucion")
    public @ResponseBody String verificarExistenciaPresupuesto(@RequestParam int idCohorte) {

        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findByPresupuestoCohorteId(idCohorte);

        if (ejecucionPresupuestal.isPresent()) {
            return "OK";
        } else {
            return "Esta cohorte no tiene un presupuesto aprobado";
        }
    }

    // En el front se llama a este método después de verificar la existencia de la
    // ejecución presupuestal
    @GetMapping(path = "/obtenerIdEjecucionPorCohorte")
    public @ResponseBody int obtenerEjecucionPorCohorte(@RequestParam int idCohorte) {

        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findByPresupuestoCohorteId(idCohorte);

        return ejecucionPresupuestal.get().getId();

    }

    @GetMapping("/listarRubros")
    public @ResponseBody ArrayList<String> listarRubros() {
        ArrayList<String> tiposRubros = new ArrayList<String>();

        String rubroEgresosGenerales = EgresosGenerales.getRubro();
        tiposRubros.add(rubroEgresosGenerales);

        String rubroEgresosOtros = EgresosOtros.getRubro();
        tiposRubros.add(rubroEgresosOtros);

        String rubroEgresosOtrosServDocentes = EgresosOtrosServDocentes.getRubro();
        tiposRubros.add(rubroEgresosOtrosServDocentes);

        String rubroEgresosServDocentes = EgresosServDocentes.getRubro();
        tiposRubros.add(rubroEgresosServDocentes);

        String rubroEgresosServNoDocentes = EgresosServNoDocentes.getRubro();
        tiposRubros.add(rubroEgresosServNoDocentes);

        String rubroEgresosTransferencias = EgresosTransferencias.getRubro();
        tiposRubros.add(rubroEgresosTransferencias);

        String rubroEgresosDescuentos = EgresosDescuentos.getRubro();
        tiposRubros.add(rubroEgresosDescuentos);

        String rubroEgresosRecurrentesAdm = EgresosRecurrentesAdm.getRubro();
        tiposRubros.add(rubroEgresosRecurrentesAdm);

        String rubroEgresosInversiones = EgresosInversiones.getRubro();
        tiposRubros.add(rubroEgresosInversiones);

        String rubroEgresosViajes = EgresosViajes.getRubro();
        tiposRubros.add(rubroEgresosViajes);

        return tiposRubros;
    }

    @PostMapping(path = "/crear")
    public @ResponseBody String crear(@RequestParam String rubro, @RequestParam int idEjecucionPresupuestal) {

        // Buscar si el presuesto de la cohorte ya tiene una ejecucion presupuestal
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent()) {

            Cdp cdp = new Cdp();
            cdp.setEjecucionPresupuestal(ejecucionPresupuestal.get());
            cdp.setRubro(rubro);
            cdp.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());

            cdpRepository.save(cdp);
            return cdp.getId() + "";
        } else {
            return "Esta cohorte no tiene un presupuesto aprobado";
        }
    }

    public @ResponseBody Iterable<? extends RegistroFinanciero> listarPorRubro(@RequestParam String rubro,
            @RequestParam int idEjecucionPresupuestal) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .get();

        if (ejecucionPresupuestal == null) {
            return Collections.emptyList();
        }

        int idPresupuesto = ejecucionPresupuestal.getPresupuesto().getId();

        Map<String, CrudRepository<? extends RegistroFinanciero, Integer>> rubroToRepositoryMap = new HashMap<>();
        rubroToRepositoryMap.put(EgresosGenerales.getRubro(), egresosGeneralesRepository);
        rubroToRepositoryMap.put(EgresosOtros.getRubro(), egresosOtrosRepository);
        rubroToRepositoryMap.put(EgresosOtrosServDocentes.getRubro(), egresosOtrosServDocentesRepository);
        rubroToRepositoryMap.put(EgresosServDocentes.getRubro(), egresosServDocentesRepository);
        rubroToRepositoryMap.put(EgresosServNoDocentes.getRubro(), egresosServNoDocentesRepository);
        rubroToRepositoryMap.put(EgresosTransferencias.getRubro(), egresosTransferenciasRepository);
        rubroToRepositoryMap.put(EgresosDescuentos.getRubro(), egresosDescuentosRepository);
        rubroToRepositoryMap.put(EgresosRecurrentesAdm.getRubro(), egresosRecurrentesAdmRepository);
        rubroToRepositoryMap.put(EgresosInversiones.getRubro(), egresosInversionesRepository);
        rubroToRepositoryMap.put(EgresosViajes.getRubro(), egresosViajesRepository);

        CrudRepository<? extends RegistroFinanciero, Integer> repository = rubroToRepositoryMap.get(rubro);

        if (repository != null) {
            List<? extends RegistroFinanciero> allRecords = (List<? extends RegistroFinanciero>) repository.findAll();
            List<? extends RegistroFinanciero> filteredRecords = allRecords.stream()
                    .filter(record -> record.getPresupuesto() != null
                            && record.getPresupuesto().getId() == idPresupuesto)
                    .collect(Collectors.toList());

            return filteredRecords;
        } else {
            return Collections.emptyList();
        }

    }

    @GetMapping(path = "/listarPorRubroDisponibles")
    public @ResponseBody Iterable<? extends RegistroFinanciero> listarPorRubroDisponibles(@RequestParam String rubro,
            @RequestParam int idEjecucionPresupuestal) {
        Iterable<? extends RegistroFinanciero> registros = listarPorRubro(rubro, idEjecucionPresupuestal);
        List<RegistroFinanciero> registrosDisponibles = new ArrayList<>();

        for (RegistroFinanciero registro : registros) {
            if (registro.getEtiquetaEgresoIngreso() == null) {
                registrosDisponibles.add(registro);
            }
        }

        return registrosDisponibles;
    }

    @PostMapping(path = "/adicionarEgresoDescuentoCDPDelPresupuesto")
    public @ResponseBody String adicionarEgresoDescuentoCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam int numEstudiantes,
            @RequestParam double valor, @RequestParam int numPeriodos,
            @RequestParam int idTipoDescuento, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoDescuentoCdpController.crearEgresoCDPDelPresupuesto(idCdp, numEstudiantes, valor,
                numPeriodos, idTipoDescuento, idEgresoDelPresupuesto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valor * numEstudiantes * numPeriodos);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoDescuentoCDPFueraDelPresupuesto")
    public @ResponseBody String adicionarEgresoDescuentoCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam int numEstudiantes,
            @RequestParam double valor, @RequestParam int numPeriodos,
            @RequestParam int idTipoDescuento, @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoDescuentoCdpController.crearEgresoCDPFueraDelPresupuesto(idCdp, numEstudiantes, valor,
                numPeriodos, idTipoDescuento, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valor * numEstudiantes * numPeriodos);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoGeneralCDPDelPresupuesto")
    public @ResponseBody String adicionarEgresoGeneralCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoGeneralCdpController.crearEgresoCDPDelPresupuesto(idCdp, concepto, valorUnitario,
                cantidad, idTipoCosto, idEgresoDelPresupuesto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valorUnitario * cantidad);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoGeneralCDPFueraDelPresupuesto")
    public @ResponseBody String adicionarEgresoGeneralCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        String respuesta = egresoGeneralCdpController.crearEgresoCDPFueraDelPresupuesto(idCdp, concepto, valorUnitario,
                cantidad, idTipoCosto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valorUnitario * cantidad);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoInversionCDPDelPresupuesto")
    public @ResponseBody String adicionarEgresoInversionCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String concepto,
            @RequestParam double valor,
            @RequestParam int idTipoInversion, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoInversionCdpController.crearEgresoCDPDelPresupuesto(idCdp, concepto, valor,
                idTipoInversion, idEgresoDelPresupuesto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valor);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoInversionCDPFueraDelPresupuesto")
    public @ResponseBody String adicionarEgresoInversionCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String concepto,
            @RequestParam double valor,
            @RequestParam int idTipoInversion, @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoInversionCdpController.crearEgresoCDPFueraDelPresupuesto(idCdp, concepto, valor,
                idTipoInversion, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valor);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoOtroCDPDelPresupuesto")
    public @ResponseBody String adicionarEgresoOtroCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad,
            @RequestParam int idTipoCosto, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoOtrosCdpController.crearEgresoCDPDelPresupuesto(idCdp, concepto, valorUnitario,
                cantidad, idTipoCosto, idEgresoDelPresupuesto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valorUnitario * cantidad);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoOtroCDPFueraDelPresupuesto")
    public @ResponseBody String adicionarEgresoOtroCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad,
            @RequestParam int idTipoCosto, @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoOtrosCdpController.crearEgresoCDPFueraDelPresupuesto(idCdp, concepto, valorUnitario,
                cantidad, idTipoCosto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valorUnitario * cantidad);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoOtroServDocenteCDPDelPresupuesto")
    public @ResponseBody String adicionarEgresoOtroServDocenteCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String servicio,
            @RequestParam String descripcion,
            @RequestParam int numHoras, @RequestParam double valorTotal,
            @RequestParam int idTipoCosto, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoOtrosServDocentesCdpController.crearEgresoCDPDelPresupuesto(idCdp, servicio,
                descripcion,
                numHoras, valorTotal, idTipoCosto, idEgresoDelPresupuesto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valorTotal);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoOtroServDocenteCDPFueraDelPresupuesto")
    public @ResponseBody String adicionarEgresoOtroServDocenteCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String servicio,
            @RequestParam String descripcion,
            @RequestParam int numHoras, @RequestParam double valorTotal,
            @RequestParam int idTipoCosto, @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoOtrosServDocentesCdpController.crearEgresoCDPFueraDelPresupuesto(idCdp, servicio,
                descripcion,
                numHoras, valorTotal, idTipoCosto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valorTotal);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoRecurrenteAdmCDPDelPresupuesto")
    public @ResponseBody String adicionarEgresoRecurrenteAdmCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String unidad,
            @RequestParam String cargo,
            @RequestParam double valorHora, @RequestParam int numHoras,
            @RequestParam int idEgresoDelPresupuesto, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        String respuesta = egresoRecurrenteAdmCdpController.crearEgresoCDPDelPresupuesto(idCdp, unidad, cargo,
                valorHora,
                numHoras, idEgresoDelPresupuesto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valorHora);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }
    }

    @PostMapping(path = "/adicionarEgresoRecurrenteAdmCDPFueraDelPresupuesto")
    public @ResponseBody String adicionarEgresoRecurrenteAdmCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String unidad,
            @RequestParam String cargo,
            @RequestParam double valorHora, @RequestParam int numHoras,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoRecurrenteAdmCdpController.crearEgresoCDPFueraDelPresupuesto(idCdp, unidad, cargo,
                valorHora, numHoras, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valorHora * numHoras);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoServDocenteCDPDelPresupuesto")
    public @ResponseBody String adicionarEgresoServDocenteCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String nombreMateria,
            @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat,
            @RequestParam int horasPracticasMat, @RequestParam double valorHoraProfesor,
            @RequestParam int idTipoCompensacion, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoServDocenteCdpController.crearEgresoCDPDelPresupuesto(idCdp, nombreMateria,
                esDocentePlanta, nombreDocente, escalafon, titulo, horasTeoricasMat, horasPracticasMat,
                valorHoraProfesor, idTipoCompensacion, idEgresoDelPresupuesto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + (valorHoraProfesor * (horasTeoricasMat + horasPracticasMat)));
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoServDocenteCDPFueraDelPresupuesto")
    public @ResponseBody String adicionarEgresoServDocenteCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String nombreMateria,
            @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat,
            @RequestParam int horasPracticasMat, @RequestParam double valorHoraProfesor,
            @RequestParam int idTipoCompensacion, @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoServDocenteCdpController.crearEgresoCDPFueraDelPresupuesto(idCdp, nombreMateria,
                esDocentePlanta, nombreDocente, escalafon, titulo, horasTeoricasMat, horasPracticasMat,
                valorHoraProfesor, idTipoCompensacion, descripcionEgresoCDP, cpc);
        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + (valorHoraProfesor * (horasTeoricasMat + horasPracticasMat)));
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoServNoDocenteCDPDelPresupuesto")
    public @ResponseBody String adicionarEgresoServNoDocenteCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String servicio,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad,
            @RequestParam int idTipoCosto, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoServNoDocenteCdpController.crearEgresoCDPDelPresupuesto(idCdp, servicio, valorUnitario,
                cantidad, idTipoCosto, idEgresoDelPresupuesto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valorUnitario * cantidad);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoServNoDocenteCDPFueraDelPresupuesto")
    public @ResponseBody String adicionarEgresoServNoDocenteCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String servicio,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad,
            @RequestParam int idTipoCosto, @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoServNoDocenteCdpController.crearEgresoCDPFueraDelPresupuesto(idCdp, servicio,
                valorUnitario, cantidad, idTipoCosto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valorUnitario * cantidad);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoTransferenciaCDPDelPresupuesto")
    public @ResponseBody String adicionarEgresoTransferenciaCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam int idTipoTransferencia, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoTransferenciaCdpController.crearEgresoCDPDelPresupuesto(idCdp, descripcion, porcentaje,
                idTipoTransferencia, idEgresoDelPresupuesto, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            EjecucionPresupuestal ejecucionPresupuestal = cdp.getEjecucionPresupuestal();
            double ingresosTotales = ejecucionPresupuestal.getPresupuesto().getIngresosTotales();
            cdp.setValorTotal(cdp.getValorTotal() + (ingresosTotales * porcentaje / 100));
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoTransferenciaCDPFueraDelPresupuesto")
    public @ResponseBody String adicionarEgresoTransferenciaCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam int idTipoTransferencia, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        String respuesta = egresoTransferenciaCdpController.crearEgresoCDPFueraDelPresupuesto(idCdp, descripcion,
                porcentaje, idTipoTransferencia, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            EjecucionPresupuestal ejecucionPresupuestal = cdp.getEjecucionPresupuestal();
            double ingresosTotales = ejecucionPresupuestal.getPresupuesto().getIngresosTotales();
            cdp.setValorTotal(cdp.getValorTotal() + (ingresosTotales * porcentaje / 100));
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }
    }

    @PostMapping(path = "/adicionarEgresoViajeCDPDelPresupuesto")
    public @ResponseBody String adicionarEgresoViajeCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String descripcion,
            @RequestParam int numPersonas,
            @RequestParam double apoyoDesplazamiento, @RequestParam int numViajesPorPersona,
            @RequestParam double valorTransporte, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        String respuesta = egresoViajeCdpController.crearEgresoCDPDelPresupuesto(idCdp, descripcion, numPersonas,
                apoyoDesplazamiento, numViajesPorPersona, valorTransporte, idEgresoDelPresupuesto, descripcionEgresoCDP,
                cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valorTransporte * numPersonas * numViajesPorPersona);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @PostMapping(path = "/adicionarEgresoViajeCDPFueraDelPresupuesto")
    public @ResponseBody String adicionarEgresoViajeCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String descripcion,
            @RequestParam int numPersonas,
            @RequestParam double apoyoDesplazamiento, @RequestParam int numViajesPorPersona,
            @RequestParam double valorTransporte, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        String respuesta = egresoViajeCdpController.crearEgresoCDPFueraDelPresupuesto(idCdp, descripcion, numPersonas,
                apoyoDesplazamiento, numViajesPorPersona, valorTransporte, descripcionEgresoCDP, cpc);

        if (respuesta.equals("OK")) {
            Cdp cdp = cdpRepository.findById(idCdp).get();
            cdp.setValorTotal(cdp.getValorTotal() + valorTransporte * numPersonas * numViajesPorPersona);
            cdpRepository.save(cdp);
            return "OK";
        } else {
            return respuesta;
        }

    }

    @GetMapping(path = "/listarEgresosDelCDP")
    public @ResponseBody Iterable<EgresoCDP> listarEgresosDelCDP(@RequestParam int idCdp) {
        Optional<Cdp> cdp = cdpRepository.findById(idCdp);
        if (cdp.isPresent()) {
            return cdp.get().getEgresosCDP();
        } else {
            return Collections.emptyList();
        }
    }

    @GetMapping(path = "/listarTodosLosCDP")
    public @ResponseBody Iterable<Cdp> listar() {
        return cdpRepository.findAll();
    }

    @GetMapping(path = "/encabezadosEgresos")
    private String[] encabezadosEgresos(int idCdp) {
        Cdp cdp = cdpRepository.findById(idCdp).get();
        String rubroEgresosGenerales = EgresosGenerales.getRubro();
        String rubroEgresosOtros = EgresosOtros.getRubro();
        String rubroEgresosOtrosServDocentes = EgresosOtrosServDocentes.getRubro();
        String rubroEgresosServDocentes = EgresosServDocentes.getRubro();
        String rubroEgresosServNoDocentes = EgresosServNoDocentes.getRubro();
        String rubroEgresosTransferencias = EgresosTransferencias.getRubro();
        String rubroEgresosDescuentos = EgresosDescuentos.getRubro();
        String rubroEgresosRecurrentesAdm = EgresosRecurrentesAdm.getRubro();
        String rubroEgresosInversiones = EgresosInversiones.getRubro();
        String rubroEgresosViajes = EgresosViajes.getRubro();

        String[] headers = null;

        if (cdp.getRubro().equals(rubroEgresosGenerales)) {
            headers = new String[] { "concepto", "cantidad",
                    "tipoCosto", "valorUnitario" };
        } else if (cdp.getRubro().equals(rubroEgresosOtros)) {
            headers = new String[] { "concepto", "cantidad",
                    "tipoCosto", "valorUnitario" };
        } else if (cdp.getRubro().equals(rubroEgresosOtrosServDocentes)) {
            headers = new String[] { "servicio", "descripcion", "numHoras",
                    "tipoCosto",
                    "valorTotal" };
        } else if (cdp.getRubro().equals(rubroEgresosServDocentes)) {
            headers = new String[] { "nombreMateria", "esDocentePlanta",
                    "nombreDocente", "escalafon", "titulo", "horasTeoricasMat",
                    "HorasPracticasMat", "valorHoraProfesor", "tipoCompensacion", "valorTotalDocente" };
        } else if (cdp.getRubro().equals(rubroEgresosServNoDocentes)) {
            headers = new String[] { "servicio", "cantidad",
                    "tipoCosto", "valorUnitario" };
        } else if (cdp.getRubro().equals(rubroEgresosTransferencias)) {
            headers = new String[] { "descripcion", "porcentaje", "tipoTransferencia",
                    "valorTotal" };
        } else if (cdp.getRubro().equals(rubroEgresosDescuentos)) {
            headers = new String[] { "cantidadEstudiantes", "valor",
                    "numeroPeriodos", "tipoDescuento", "totalDescuento" };
        } else if (cdp.getRubro().equals(rubroEgresosRecurrentesAdm)) {
            headers = new String[] { "unidad", "cargo", "valorHora",
                    "numHoras", "valorTotal" };
        } else if (cdp.getRubro().equals(rubroEgresosInversiones)) {
            headers = new String[] { "concepto", "tipoInversion", "valor" };
        } else if (cdp.getRubro().equals(rubroEgresosViajes)) {
            headers = new String[] { "descripcion", "numPersonas",
                    "apoyoDesplazamiento", "numViajesPorPersona", "valorTransporte", "valorTotal" };
        } else {
            headers = new String[] {};
        }

        return headers;
    }

    /*
     * /*
     * 
     * 
     * MÉTODOS PARA IMPRIMIR FORMATO CDP
     * 
     * 
     * 
     */

    private String[] encabezadosSegunRubro(int idCdp) {
        Cdp cdp = cdpRepository.findById(idCdp).get();
        String rubroEgresosGenerales = EgresosGenerales.getRubro();
        String rubroEgresosOtros = EgresosOtros.getRubro();
        String rubroEgresosOtrosServDocentes = EgresosOtrosServDocentes.getRubro();
        String rubroEgresosServDocentes = EgresosServDocentes.getRubro();
        String rubroEgresosServNoDocentes = EgresosServNoDocentes.getRubro();
        String rubroEgresosTransferencias = EgresosTransferencias.getRubro();
        String rubroEgresosDescuentos = EgresosDescuentos.getRubro();
        String rubroEgresosRecurrentesAdm = EgresosRecurrentesAdm.getRubro();
        String rubroEgresosInversiones = EgresosInversiones.getRubro();
        String rubroEgresosViajes = EgresosViajes.getRubro();

        String[] headers = null;

        if (cdp.getRubro().equals(rubroEgresosGenerales)) {
            headers = new String[] { "Egreso ID", "Descripción", "CPC", "Concepto", "Cantidad",
                    "Tipo de costo", "Valor unitario" };
        } else if (cdp.getRubro().equals(rubroEgresosOtros)) {
            headers = new String[] { "Egreso ID", "Descripción", "CPC", "Concepto", "Cantidad",
                    "Tipo de costo", "Valor unitario" };
        } else if (cdp.getRubro().equals(rubroEgresosOtrosServDocentes)) {
            headers = new String[] { "Egreso ID", "Descripción", "CPC", "Servicio", "Descripción", "Número de horas",
                    "Tipo de costo",
                    "Valor total" };
        } else if (cdp.getRubro().equals(rubroEgresosServDocentes)) {
            headers = new String[] { "Egreso ID", "Descripción", "CPC", "Nombre de la materia", "Es docente de planta",
                    "Nombre del docente", "Escalafón", "Título", "Horas teóricas de la materia",
                    "Horas prácticas de la materia", "Valor hora profesor", "Tipo de compensación", "Valor total" };
        } else if (cdp.getRubro().equals(rubroEgresosServNoDocentes)) {
            headers = new String[] { "Egreso ID", "Descripción", "CPC", "Servicio", "Cantidad",
                    "Tipo de costo", "Valor unitario" };
        } else if (cdp.getRubro().equals(rubroEgresosTransferencias)) {
            headers = new String[] { "Egreso ID", "Descripción", "CPC", "Porcentaje", "Tipo de transferencia",
                    "Valor total" };
        } else if (cdp.getRubro().equals(rubroEgresosDescuentos)) {
            headers = new String[] { "Egreso ID", "Descripción", "CPC", "Número de estudiantes", "Valor",
                    "Número de periodos", "Tipo de descuento", "Descuento total" };
        } else if (cdp.getRubro().equals(rubroEgresosRecurrentesAdm)) {
            headers = new String[] { "Egreso ID", "Descripción", "CPC", "Unidad", "Cargo", "Valor hora",
                    "Número de horas", "Valor total" };
        } else if (cdp.getRubro().equals(rubroEgresosInversiones)) {
            headers = new String[] { "Egreso ID", "Descripción", "CPC", "Concepto", "Tipo de inversión", "Valor" };
        } else if (cdp.getRubro().equals(rubroEgresosViajes)) {
            headers = new String[] { "Egreso ID", "Descripción", "CPC", "Concepto", "Número de personas",
                    "Apoyo desplazamiento", "Número de viajes por persona", "Valor transporte", "Valor total" };
        } else {
            headers = new String[] { "Egreso ID", "Descripción", "CPC" };
        }

        return headers;
    }

    @GetMapping(path = "/generarReportePDF")
    public ResponseEntity<InputStreamResource> generarReportePDF(@RequestParam int idCdp) throws DocumentException {
        Optional<Cdp> cdp = cdpRepository.findById(idCdp);
        if (cdp.isPresent()) {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(document, out);
            document.open();

            // Font font = FontFactory.getFont(FontFactory.TIMES, 16, BaseColor.BLACK);

            agregarLogo(document);

            agregarUniversidadYFormato(document);
            // Crear un salto de línea más pequeño
            Paragraph smallGap = new Paragraph(" ");
            smallGap.setSpacingBefore(5);

            // Agregar el salto de línea al documento
            document.add(smallGap);

            PdfPTable headerTable = agregarCodigoYVersion();

            document.add(headerTable);

            // Agregar el salto de línea al documento
            document.add(smallGap);

            PdfPTable dateTable = agregarFechaYSCDP(idCdp);

            document.add(dateTable);
            document.add(Chunk.NEWLINE);

            PdfPTable infoTable = crearInformacionSolicitante(cdp);

            // Agregar la tabla de información solicitante al documento
            document.add(infoTable);
            document.add(Chunk.NEWLINE);

            String[] headersTableEgresosCDP = encabezadosSegunRubro(idCdp);
            PdfPTable tablaEgresosCDP = crearTablaEgresosCDP(headersTableEgresosCDP);

            Iterable<EgresoCDP> egresos = cdp.get().getEgresosCDP();

            double totalCDP = agregarEgresosCDPALaTabla(tablaEgresosCDP, egresos);
            document.add(tablaEgresosCDP);

            // Agregar el salto de línea al documento
            document.add(smallGap);

            agregarValorTotal(document, totalCDP);

            document.close();

            ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=reporte.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    private double agregarEgresosCDPALaTabla(PdfPTable table, Iterable<EgresoCDP> egresos) {

        double total = 0;

        for (EgresoCDP egreso : egresos) {

            if (egreso instanceof EgresoDescuentoCDP) {

                total += agregarEgresoDescuento(table, egreso);

            } else if (egreso instanceof EgresoGeneralCDP) {

                total += agregarEgresoGeneral(table, egreso);

            } else if (egreso instanceof EgresoInversionCDP) {

                total += agregarEgresoInversion(table, egreso);

            } else if (egreso instanceof EgresoOtroCDP) {

                total += agregarEgresoOtro(table, egreso);

            } else if (egreso instanceof EgresoOtroServDocenteCDP) {

                total += agregarEgresoOtroServDocente(table, egreso);

            } else if (egreso instanceof EgresoRecurrenteAdmCDP) {

                total += agregarEgresoRecurrenteAdm(table, egreso);

            } else if (egreso instanceof EgresoServDocenteCDP) {

                total += agregarEgresoServDocente(table, egreso);

            } else if (egreso instanceof EgresoServNoDocenteCDP) {

                total += agregarEgresoServNoDocente(table, egreso);

            } else if (egreso instanceof EgresoTransferenciaCDP) {

                total += agregarEgresoTransferencia(table, egreso);

            } else if (egreso instanceof EgresoViajeCDP) {

                total += agregarEgresoViaje(table, egreso);

            }

        }

        return total;
    }

    private double agregarEgresoViaje(PdfPTable table, EgresoCDP egreso) {

        EgresoViajeCDP egresoViajeCDP = (EgresoViajeCDP) egreso;
        EgresosViajes egresoViaje = egresoViajeCDP.getEgresoViaje();

        table.addCell(String.valueOf(egreso.getId()));
        table.addCell(egreso.getDescripcion());
        table.addCell(egreso.getCpc());
        table.addCell(egresoViaje.getDescripcion());
        table.addCell(String.valueOf(egresoViaje.getNumPersonas()));
        table.addCell(String.valueOf(egresoViaje.getApoyoDesplazamiento()));
        table.addCell(String.valueOf(egresoViaje.getNumViajesPorPersona()));
        table.addCell(String.valueOf(egresoViaje.getValorTransporte()));
        table.addCell(String.valueOf(egresoViaje.getValorTotal()));

        return egresoViaje.getValorTotal();
    }

    private double agregarEgresoTransferencia(PdfPTable table, EgresoCDP egreso) {
        EgresoTransferenciaCDP egresoTransferenciaCDP = (EgresoTransferenciaCDP) egreso;
        EgresosTransferencias egresoTransferencia = egresoTransferenciaCDP.getEgresoTransferencia();

        table.addCell(String.valueOf(egreso.getId()));
        table.addCell(egreso.getDescripcion());
        table.addCell(egreso.getCpc());
        table.addCell(String.valueOf(egresoTransferencia.getPorcentaje()));
        table.addCell(egresoTransferencia.getTipoTransferencia().getNombreTipo());
        table.addCell(String.valueOf(egresoTransferencia.getValorTotal()));

        return egresoTransferencia.getValorTotal();
    }

    private double agregarEgresoServNoDocente(PdfPTable table, EgresoCDP egreso) {
        EgresoServNoDocenteCDP egresoServNoDocenteCDP = (EgresoServNoDocenteCDP) egreso;
        EgresosServNoDocentes egresoServNoDocente = egresoServNoDocenteCDP.getEgresoServNoDocente();

        table.addCell(String.valueOf(egreso.getId()));
        table.addCell(egreso.getDescripcion());
        table.addCell(egreso.getCpc());
        table.addCell(egresoServNoDocente.getServicio());
        table.addCell(String.valueOf(egresoServNoDocente.getCantidad()));
        table.addCell(egresoServNoDocente.getTipoCosto().getNombreTipo());
        table.addCell(String.valueOf(egresoServNoDocente.getValorUnitario()));

        return egresoServNoDocente.getValorUnitario();
    }

    private double agregarEgresoServDocente(PdfPTable table, EgresoCDP egreso) {
        EgresoServDocenteCDP egresoServDocenteCDP = (EgresoServDocenteCDP) egreso;
        EgresosServDocentes egresoServDocente = egresoServDocenteCDP.getEgresoServDocente();

        table.addCell(String.valueOf(egreso.getId()));
        table.addCell(egreso.getDescripcion());
        table.addCell(egreso.getCpc());
        table.addCell(egresoServDocente.getNombreMateria());
        table.addCell(String.valueOf(egresoServDocente.isEsDocentePlanta()));
        table.addCell(egresoServDocente.getNombreDocente());
        table.addCell(egresoServDocente.getEscalafon());
        table.addCell(egresoServDocente.getTitulo());
        table.addCell(String.valueOf(egresoServDocente.getHorasTeoricasMat()));
        table.addCell(String.valueOf(egresoServDocente.getHorasPracticasMat()));
        table.addCell(String.valueOf(egresoServDocente.getValorHoraProfesor()));
        table.addCell(egresoServDocente.getTipoCompensacion().getNombreTipo());
        table.addCell(String.valueOf(egresoServDocente.getTotalPagoProfesor()));

        return egresoServDocente.getTotalPagoProfesor();
    }

    private double agregarEgresoRecurrenteAdm(PdfPTable table, EgresoCDP egreso) {
        EgresoRecurrenteAdmCDP egresoRecurrenteAdmCDP = (EgresoRecurrenteAdmCDP) egreso;
        EgresosRecurrentesAdm egresoRecurrenteAdm = egresoRecurrenteAdmCDP.getEgresoRecurrenteAdm();

        table.addCell(String.valueOf(egreso.getId()));
        table.addCell(egreso.getDescripcion());
        table.addCell(egreso.getCpc());
        table.addCell(egresoRecurrenteAdm.getUnidad());
        table.addCell(egresoRecurrenteAdm.getCargo());
        table.addCell(String.valueOf(egresoRecurrenteAdm.getValorHora()));
        table.addCell(String.valueOf(egresoRecurrenteAdm.getNumHoras()));
        table.addCell(String.valueOf(egresoRecurrenteAdm.getValorTotal()));

        return egresoRecurrenteAdm.getValorTotal();
    }

    private double agregarEgresoOtroServDocente(PdfPTable table, EgresoCDP egreso) {
        EgresoOtroServDocenteCDP egresoOtroServDocenteCDP = (EgresoOtroServDocenteCDP) egreso;
        EgresosOtrosServDocentes egresoOtroServDocente = egresoOtroServDocenteCDP
                .getEgresoOtroServDocente();

        table.addCell(String.valueOf(egreso.getId()));
        table.addCell(egreso.getDescripcion());
        table.addCell(egreso.getCpc());
        table.addCell(egresoOtroServDocente.getServicio());
        table.addCell(egresoOtroServDocente.getDescripcion());
        table.addCell(String.valueOf(egresoOtroServDocente.getNumHoras()));
        table.addCell(egresoOtroServDocente.getTipoCosto().getNombreTipo());
        table.addCell(String.valueOf(egresoOtroServDocente.getValorTotal()));

        return egresoOtroServDocente.getValorTotal();
    }

    private double agregarEgresoOtro(PdfPTable table, EgresoCDP egreso) {
        EgresoOtroCDP egresoOtroCDP = (EgresoOtroCDP) egreso;
        EgresosOtros egresoOtro = egresoOtroCDP.getEgresoOtro();

        table.addCell(String.valueOf(egreso.getId()));
        table.addCell(egreso.getDescripcion());
        table.addCell(egreso.getCpc());
        table.addCell(egresoOtro.getConcepto());
        table.addCell(String.valueOf(egresoOtro.getCantidad()));
        table.addCell(egresoOtro.getTipoCosto().getNombreTipo());
        table.addCell(String.valueOf(egresoOtro.getValorUnitario()));

        return egresoOtro.getValorUnitario();
    }

    private double agregarEgresoInversion(PdfPTable table, EgresoCDP egreso) {
        EgresoInversionCDP egresoInversionCDP = (EgresoInversionCDP) egreso;
        EgresosInversiones egresoInversion = egresoInversionCDP.getEgresoInversion();

        table.addCell(String.valueOf(egreso.getId()));
        table.addCell(egreso.getDescripcion());
        table.addCell(egreso.getCpc());
        table.addCell(egresoInversion.getConcepto());
        table.addCell(egresoInversion.getTipoInversion().getNombreTipo());
        table.addCell(String.valueOf(egresoInversion.getValor()));

        return egresoInversion.getValor();
    }

    private double agregarEgresoGeneral(PdfPTable table, EgresoCDP egreso) {
        EgresoGeneralCDP egresoGeneralCDP = (EgresoGeneralCDP) egreso;
        EgresosGenerales egresoGeneral = egresoGeneralCDP.getEgresoGeneral();
        table.addCell(String.valueOf(egreso.getId()));
        table.addCell(egreso.getDescripcion());
        table.addCell(egreso.getCpc());
        table.addCell(egresoGeneral.getConcepto());

        table.addCell(String.valueOf(egresoGeneral.getCantidad()));
        table.addCell(egresoGeneral.getTipoCosto().getNombreTipo());
        table.addCell(String.valueOf(egresoGeneral.getValorUnitario()));

        return egresoGeneral.getValorUnitario();
    }

    private double agregarEgresoDescuento(PdfPTable table, EgresoCDP egreso) {
        EgresoDescuentoCDP egresoDescuentoCDP = (EgresoDescuentoCDP) egreso;
        EgresosDescuentos egresoDescuento = egresoDescuentoCDP.getEgresoDescuento();
        table.addCell(String.valueOf(egreso.getId()));
        table.addCell(egreso.getDescripcion());
        table.addCell(egreso.getCpc());
        table.addCell(String.valueOf(egresoDescuento.getNumEstudiantes()));

        table.addCell(String.valueOf(egresoDescuento.getNumPeriodos()));
        table.addCell(egresoDescuento.getTipoDescuento().getNombreTipo());
        table.addCell(String.valueOf(egresoDescuento.getValor()));
        table.addCell(String.valueOf(egresoDescuento.getTotalDescuento()));

        return egresoDescuento.getTotalDescuento();
    }

    private PdfPTable crearTablaEgresosCDP(String[] headersTableEgresosCDP) {
        PdfPTable table = new PdfPTable(headersTableEgresosCDP.length);
        table.setWidthPercentage(100);

        Font headersTableEgresosCDPFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        // BaseColor headerBackgroundColor = new BaseColor(0, 122, 181); // Color de
        // fondo para las celdas del
        // encabezado

        for (String header : headersTableEgresosCDP) {
            PdfPCell EgresosCell = new PdfPCell(new Phrase(header, headersTableEgresosCDPFont));
            EgresosCell.setBackgroundColor(BaseColor.BLUE);
            table.addCell(EgresosCell);
        }
        return table;
    }

    private PdfPTable agregarFechaYSCDP(int idCdp) throws DocumentException {
        // Crear tabla para fecha y SCDP
        PdfPTable dateTable = new PdfPTable(2); // 2 columnas
        dateTable.setWidthPercentage(60); // Ancho de la tabla como porcentaje del ancho de la página
        dateTable.setWidths(new int[] { 2, 1 }); // Proporción de los anchos de las columnas

        Font fontFecha = FontFactory.getFont(FontFactory.TIMES, 13, BaseColor.BLACK);
        // Agregar celda para fecha
        PdfPCell fechaCell = new PdfPCell(new Phrase(
                "Fecha de la solicitud: " + java.time.LocalDateTime.now().getDayOfMonth()
                        + "/"
                        + java.time.LocalDateTime.now().getMonthValue() + "/"
                        + java.time.LocalDateTime.now().getYear(),
                fontFecha));
        fechaCell.setBorder(Rectangle.NO_BORDER);
        dateTable.addCell(fechaCell);

        // Agregar celda para SCDP
        PdfPCell SCDPCell = new PdfPCell(new Phrase("SCDP No. " + idCdp, fontFecha));
        SCDPCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        SCDPCell.setBorder(Rectangle.NO_BORDER);
        dateTable.addCell(SCDPCell);
        return dateTable;
    }

    private PdfPTable agregarCodigoYVersion() throws DocumentException {
        // Crear tabla para el código y la versión
        PdfPTable headerTable = new PdfPTable(2); // 2 columnas
        headerTable.setWidthPercentage(60); // Ancho de la tabla como porcentaje del ancho de la página
        headerTable.setWidths(new int[] { 2, 1 }); // Proporción de los anchos de las columnas

        Font fontCodigo = FontFactory.getFont(FontFactory.TIMES, 13, BaseColor.BLACK);
        // Agregar celda para el código
        PdfPCell codigoCell = new PdfPCell(new Phrase("CÓDIGO: R - 410 - 1 - GF - 148", fontCodigo));
        codigoCell.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(codigoCell);

        // Agregar celda para la versión
        PdfPCell versionCell = new PdfPCell(new Phrase("VERSIÓN: 2", fontCodigo));
        versionCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        versionCell.setBorder(Rectangle.NO_BORDER);
        headerTable.addCell(versionCell);
        return headerTable;
    }

    private PdfPTable crearInformacionSolicitante(Optional<Cdp> cdp)
            throws DocumentException {
        // Crear tabla para la información solicitante
        PdfPTable infoTable = new PdfPTable(2); // 2 columnas
        infoTable.setWidthPercentage(100); // Ancho de la tabla como porcentaje del ancho de la página
        infoTable.setWidths(new int[] { 3, 5 }); // Proporción de los anchos de las columnas

        // Agregar celda para el título de la sección
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        PdfPCell headerCell = new PdfPCell(new Phrase("Información Solicitante", headerFont));
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerCell.setBackgroundColor(BaseColor.BLUE);
        headerCell.setColspan(2); // Hacer que la celda ocupe ambas columnas
        infoTable.addCell(headerCell);

        String nombreFacultad = cdp.get().getEjecucionPresupuestal().getPresupuesto().getCohorte()
                .getPrograma().getFacultad().getNombre();

        // Agregar los datos de la información solicitante
        infoTable.addCell("Nombre de la dependencia");
        infoTable.addCell("DECANATO FAC. " + nombreFacultad.toUpperCase());
        infoTable.addCell("Código área (SGF)");
        infoTable.addCell("160203");
        infoTable.addCell("Nombre del proyecto");
        infoTable.addCell(cdp.get().getEjecucionPresupuestal().getPresupuesto().getCohorte()
                .getPrograma().getNombre().toUpperCase());
        infoTable.addCell("Código del proyecto");
        infoTable.addCell(String.valueOf(cdp.get().getEjecucionPresupuestal().getPresupuesto().getCohorte()
                .getPrograma().getId()));

        // buscar entre los usuarios el decano de esta facultad
        // se debe comprar el rol.getNombre() con "DECANO"
        Usuario decano = usuarioRepository.findByFacultadNombreAndRolNombre(nombreFacultad, "DECANO");

        if (decano == null) {
            infoTable.addCell("Nombre del ordenador del gasto");
            infoTable.addCell("");
        } else {
            infoTable.addCell("Nombre del ordenador del gasto");
            infoTable.addCell(decano.getNombre() + " " + decano.getApellido());
        }

        return infoTable;
    }

    private void agregarUniversidadYFormato(Document document) throws DocumentException {
        Font fontUniversidad = FontFactory.getFont(FontFactory.TIMES, 16, BaseColor.BLACK);
        Paragraph universidad = new Paragraph("UNIVERSIDAD DE CALDAS", fontUniversidad);
        universidad.setAlignment(Element.ALIGN_CENTER);
        document.add(universidad);
        document.add(Chunk.NEWLINE);

        Font fontFormato = FontFactory.getFont(FontFactory.TIMES, 16, BaseColor.BLACK);
        Paragraph formato = new Paragraph("FORMATO PARA SOLICITUD DE CERTIFICADO DE DISPONIBILIDAD PRESUPUESTAL",
                fontFormato);
        formato.setAlignment(Element.ALIGN_CENTER);
        document.add(formato);
    }

    private void agregarValorTotal(Document document, double total) throws DocumentException {
        Font fontTotal = FontFactory.getFont(FontFactory.TIMES, 13, BaseColor.BLACK);
        Paragraph universidad = new Paragraph("Valor total de la disponibilidad presupuestal solicitada", fontTotal);
        universidad.setAlignment(Element.ALIGN_CENTER);
        document.add(universidad);

        Paragraph smallGap = new Paragraph(" ");
        smallGap.setSpacingBefore(3);
        Font fontValor = FontFactory.getFont(FontFactory.TIMES, 15, BaseColor.BLACK);
        Paragraph formato = new Paragraph("$" + String.valueOf(total),
                fontValor);
        formato.setAlignment(Element.ALIGN_CENTER);
        document.add(formato);
    }

    private void agregarLogo(Document document) throws BadElementException, DocumentException {
        // Agregar la imagen "Sistema integrado de gestión"
        try {
            Image logo = Image.getInstance("/sig.jpg"); // Reemplaza esto con la ruta a tu imagen
            logo.scaleToFit(100, 100); // Ajusta el tamaño de la imagen según sea necesario
            logo.setAlignment(Element.ALIGN_CENTER);
            document.add(logo);
        } catch (IOException e) {
            System.err.println("No se pudo cargar la imagen: " + e.getMessage());
        }
    }
}

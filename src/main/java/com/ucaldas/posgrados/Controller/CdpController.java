package com.ucaldas.posgrados.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.Cdp;
import com.ucaldas.posgrados.Entity.EgresoCDP;
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
            return "OK";
        } else {
            return "Esta cohorte no tiene un presupuesto aprobado";
        }
    }

    public @ResponseBody Iterable<? extends RegistroFinanciero> listarPorRubro(@RequestParam String rubro) {
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
            return repository.findAll();
        } else {
            return Collections.emptyList();
        }

    }

    @GetMapping(path = "/listarPorRubroDisponibles")
    public @ResponseBody Iterable<? extends RegistroFinanciero> listarPorRubroDisponibles(@RequestParam String rubro) {
        Iterable<? extends RegistroFinanciero> registros = listarPorRubro(rubro);
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
}

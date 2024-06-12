package com.ucaldas.posgrados.Controller;

import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.Cohorte;
import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Entity.Usuario;
import com.ucaldas.posgrados.Repository.CohorteRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/presupuesto")
public class PresupuestoController {

    @Autowired
    private CohorteRepository cohorteRepository;

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EjecucionPresupuestalController ejecucionPresupuestalController;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idCohorte) {
        // Buscar la programa por su ID
        Optional<Cohorte> cohorte = cohorteRepository.findById(idCohorte);

        // Verificar si la programa existe
        if (cohorte.isPresent()) {
            Presupuesto presupuesto = new Presupuesto();

            presupuesto.setObservaciones("");
            presupuesto.setIngresosTotales(0);
            presupuesto.setEgresosProgramaTotales(0);
            presupuesto.setEgresosRecurrentesUniversidadTotales(0);
            presupuesto.setBalanceGeneral(0);

            // Siempre se crea como borrador, para que puedan ser ingresados los valores de
            // los gastos e ingresos, después de este paso se puede cambiar el estado
            presupuesto.setEstado("borrador");

            presupuesto.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " " + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute()
                    + ":" + java.time.LocalDateTime.now().getSecond());
            presupuesto.setFechaHoraUltimaModificacion("No ha sido modificado");
            presupuesto.setFechaHoraEnviadoRevision("No ha sido enviado a revisión");
            presupuesto.setFechaHoraAprobado("No ha sido enviado a revisión");

            // Asignar la programa al cohorte
            presupuesto.setCohorte(cohorte.get());

            presupuestoRepository.save(presupuesto);
            return "OK";
        } else {
            return "Error: Cohorte no encontrada";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<Presupuesto> listar() {
        return presupuestoRepository.findAllByOrderByEstadoAsc();
    }

    @GetMapping("/listarPorPrograma")
    public @ResponseBody Iterable<Presupuesto> listarPorPrograma(@RequestParam int idPrograma) {
        return presupuestoRepository.findByCohorteProgramaId(idPrograma);
    }

    @GetMapping("/listarPorFacultadPorRevisar")
    public @ResponseBody Iterable<Presupuesto> listarPorFacultadPorRevisar(
            @AuthenticationPrincipal Usuario usuarioActual) {
        if (usuarioActual.getRol().getNombre().equals("ADMIN")) {
            // Si el usuario es un administrador, devuelve todos los presupuestos en estado
            // revision
            return presupuestoRepository.findByEstado("revision");
        } else {
            // Si el usuario no es un administrador, filtra los presupuestos por la facultad
            // del usuario
            int idFacultad = usuarioActual.getFacultad().getId();
            return presupuestoRepository.findByCohorteProgramaFacultadIdAndEstado(idFacultad, "revision");
        }
    }

    @GetMapping("/listarPorFacultadAprobados")
    public @ResponseBody Iterable<Presupuesto> listarPorFacultadAprobados(
            @AuthenticationPrincipal Usuario usuarioActual) {
        if (usuarioActual.getRol().getNombre().equals("ADMIN")) {
            // Si el usuario es un administrador, devuelve todos los presupuestos
            return presupuestoRepository.findByEstado("aprobado");
        } else {
            // Si el usuario no es un administrador, filtra los presupuestos por la facultad
            // del usuario
            int idFacultad = usuarioActual.getFacultad().getId();
            return presupuestoRepository.findByCohorteProgramaFacultadIdAndEstado(idFacultad, "aprobado");
        }
    }

    @GetMapping("/listarPorFacultadDesaprobados")
    public @ResponseBody Iterable<Presupuesto> listarPorFacultadDesaprobados(
            @AuthenticationPrincipal Usuario usuarioActual) {
        if (usuarioActual.getRol().getNombre().equals("ADMIN")) {
            // Si el usuario es un administrador, devuelve todos los presupuestos
            return presupuestoRepository.findByEstado("desaprobado");
        } else {
            // Si el usuario no es un administrador, filtra los presupuestos por la facultad
            // del usuario
            int idFacultad = usuarioActual.getFacultad().getId();
            return presupuestoRepository.findByCohorteProgramaFacultadIdAndEstado(idFacultad, "desaprobado");
        }
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<Presupuesto> buscar(@RequestParam int id) {
        return presupuestoRepository.findById(id);
    }

    // Buscar el presupuesto por cohorte
    @GetMapping("/buscarPorCohorte")
    public @ResponseBody Optional<Presupuesto> buscarPorCohorte(@RequestParam int idCohorte) {

        Optional<Cohorte> cohorte = cohorteRepository.findById(idCohorte);

        Optional<Presupuesto> presupuesto = presupuestoRepository.findByCohorteId(idCohorte);

        if (cohorte.isPresent() && presupuesto.isPresent()) {
            return presupuesto;
        } else {
            return null;
        }

    }

    @GetMapping("/obtenerEjecucionPresupuestal")
    public @ResponseBody Optional<EjecucionPresupuestal> obtenerEjecucionPresupuestal(@RequestParam int idPresupuesto) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<EjecucionPresupuestal> ejecucion = ejecucionPresupuestalController.buscarPorPresupuesto(idPresupuesto);

        if (presupuesto.isPresent() && ejecucion.isPresent()) {
            return ejecucion;
        } else {
            return null;
        }
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id,
            @RequestParam(required = false) Optional<String> observaciones,
            @RequestParam int idCohorte) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);
        Optional<Cohorte> cohorte = cohorteRepository.findById(idCohorte);

        if (presupuesto.isPresent() && cohorte.isPresent()) {
            // Comprobar que el estado del presupuesto sea "borrador"
            if (!presupuesto.get().getEstado().equals("borrador")) {
                return "Error: No se puede modificar el presupuesto porque no está en estado de borrador";
            }
            observaciones.ifPresent(presupuesto.get()::setObservaciones); // Si observaciones no es null, entonces se
                                                                          // asigna a presupuesto
            presupuesto.get().setCohorte(cohorte.get());

            presupuesto.get()
                    .setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                            + java.time.LocalDateTime.now().getMonthValue() + "/"
                            + java.time.LocalDateTime.now().getYear() + " "
                            + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute()
                            + ":"
                            + java.time.LocalDateTime.now().getSecond());
            presupuestoRepository.save(presupuesto.get());
            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    // Cambia el estado de un presupuesto a "revision".
    // En un futuro se enviará al decano para su aprobación
    @PutMapping(path = "/enviarParaRevision")
    public @ResponseBody String enviarParaRevision(@RequestParam int id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {

            // Comprobar que el presupuesto esté en estado "borrador"
            if (!presupuesto.get().getEstado().equals("borrador")) {
                return "Error: No se puede enviar a revisión porque el presupuesto no está en estado de borrador";
            }

            presupuesto.get().setEstado("revision");
            presupuesto.get().setFechaHoraEnviadoRevision(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " " + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute()
                    + ":" + java.time.LocalDateTime.now().getSecond());
            presupuestoRepository.save(presupuesto.get());
            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @PutMapping(path = "/aprobar")
    public @ResponseBody String aprobar(@RequestParam int id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {

            // Comprobar que el presupuesto esté en estado "revision"
            if (!presupuesto.get().getEstado().equals("revision")) {
                return "Error: No se puede aprobar porque el presupuesto no está en estado de revisión";
            }
            presupuesto.get().setEstado("aprobado");
            presupuesto.get().setFechaHoraAprobado(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " " + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute()
                    + ":" + java.time.LocalDateTime.now().getSecond());

            // Cómo ya está aprobado el presupuesto, se crea una ejecución presupuestal por
            // ahora vacía (sólo tiene el id del presupuesto)
            ejecucionPresupuestalController.crear(id);

            presupuestoRepository.save(presupuesto.get());
            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @PutMapping(path = "/desaprobar")
    public @ResponseBody String desaprobar(@RequestParam int id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {

            // Comprobar que el presupuesto esté en estado "revision"
            if (!presupuesto.get().getEstado().equals("revision")) {
                return "Error: No se puede aprobar porque el presupuesto no está en estado de revisión";
            }
            presupuesto.get().setEstado("desaprobado");

            presupuestoRepository.save(presupuesto.get());
            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {
            Presupuesto presupuestoEliminado = presupuesto.get();
            presupuestoEliminado.setEstado("eliminado");

            presupuestoRepository.save(presupuestoEliminado);

            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }

    }

    // Se utiliza para actualizar el atributo ingresosTotales de la clase
    // Presupuesto
    public String actualizarIngresosTotales(int idPresupuesto,
            double nuevoValor, double antiguoValor, String tipo) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);

        if (presupuesto.isPresent() && tipo.equals("ingreso")) {
            presupuesto.get().setIngresosTotales(presupuesto.get().getIngresosTotales() - antiguoValor + nuevoValor);
            actualizarBalanceGeneral(idPresupuesto);
            return "OK";
        } else if (presupuesto.isPresent() && tipo.equals("descuento")) {
            presupuesto.get().setIngresosTotales(presupuesto.get().getIngresosTotales() - nuevoValor + antiguoValor);
            actualizarBalanceGeneral(idPresupuesto);
            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }

    }

    @GetMapping(path = "/ingresosTotales")
    public @ResponseBody double ingresosTotales(@RequestParam int id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {
            return presupuesto.get().getIngresosTotales();
        } else {
            return 0;
        }
    }

    // Se utiliza para actualizar el atributo egresosProgramaTotales de la clase
    // Presupuesto
    // Cuando se crea: antiguo valor será 0
    // Cuando se modifica: antiguo valor será el valor que se quiere modificar y
    // nuevo valor será el valor nuevo
    // Cuando se elimina: nuevo valor será 0
    public String actualizarEgresosProgramaTotales(int id,
            double nuevoValor, double antiguoValor) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {

            presupuesto.get().setEgresosProgramaTotales(
                    presupuesto.get().getEgresosProgramaTotales() - antiguoValor + nuevoValor);

            actualizarBalanceGeneral(id);

            presupuestoRepository.save(presupuesto.get());
            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }

    }

    // Se utiliza para actualizar el atributo egresosProgramaTotales de la clase
    // Presupuesto
    // Cuando se crea: antiguo valor será 0
    // Cuando se modifica: antiguo valor será el valor que se quiere modificar y
    // nuevo valor será el valor nuevo
    // Cuando se elimina: nuevo valor será 0
    public String actualizarEgresosRecurrentesUniversidadTotales(int id,
            double nuevoValor, double antiguoValor) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {
            presupuesto.get().setEgresosRecurrentesUniversidadTotales(
                    presupuesto.get().getEgresosRecurrentesUniversidadTotales() - antiguoValor + nuevoValor);

            actualizarBalanceGeneral(id);

            presupuestoRepository.save(presupuesto.get());
            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    // Cuando se crea un presupuesto, ingresos y los egresos tienen un valor de 0,
    // por lo que no habrán excepciones al momento de realizar la operación
    public String actualizarBalanceGeneral(int id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {
            presupuesto.get().setBalanceGeneral(presupuesto.get().getIngresosTotales()
                    - presupuesto.get().getEgresosProgramaTotales()
                    - presupuesto.get().getEgresosRecurrentesUniversidadTotales());

            // Hay un error acá
            presupuestoRepository.save(presupuesto.get());
            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @PutMapping(path = "/recalcularIngresosTotales")
    public @ResponseBody String recalcularIngresosTotales(@RequestParam int id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {
            double ingresosTotal = presupuesto.get().getIngresos().stream().mapToDouble(i -> i.getValor()).sum();
            double descuentosTotal = presupuesto.get().getEgresosDescuentos().stream()
                    .mapToDouble(i -> i.getTotalDescuento()).sum();
            presupuesto.get().setIngresosTotales(ingresosTotal - descuentosTotal);

            actualizarBalanceGeneral(id);
            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @PutMapping(path = "/recalcularEgresosProgramaTotales")
    public @ResponseBody String recalcularEgresosProgramaTotales(@RequestParam int id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {

            double egresosTotal = presupuesto.get().getEgresosOtros().stream().mapToDouble(i -> i.getValorTotal())
                    .sum();

            egresosTotal = egresosTotal
                    + presupuesto.get().getEgresosGenerales().stream().mapToDouble(i -> i.getValorTotal()).sum();

            egresosTotal = egresosTotal
                    + presupuesto.get().getEgresosTransferencias().stream().mapToDouble(i -> i.getValorTotal()).sum();

            egresosTotal = egresosTotal
                    + presupuesto.get().getEgresosViaje().stream().mapToDouble(i -> i.getValorTotal()).sum();

            egresosTotal = egresosTotal + presupuesto.get().getEgresosOtrosServDocentes().stream()
                    .mapToDouble(i -> i.getValorTotal()).sum();

            egresosTotal = egresosTotal + presupuesto.get().getEgresosServNoDocentes().stream()
                    .mapToDouble(i -> i.getValorTotal()).sum();

            // Faltan los egresos de Servicios Docentes

            presupuesto.get().setEgresosProgramaTotales(egresosTotal);

            actualizarBalanceGeneral(id);
            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @PutMapping(path = "/recalcularEgresosRecurrentesUniversidadTotales")
    public @ResponseBody String recalcularEgresosRecurrentesUniversidadTotales(@RequestParam int id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {

            double egresosTotal = presupuesto.get().getEgresosRecurrentesAdm().stream()
                    .mapToDouble(i -> i.getValorTotal())
                    .sum();

            egresosTotal = egresosTotal
                    + presupuesto.get().getEgresosInversiones().stream().mapToDouble(i -> i.getValor()).sum();

            // Faltan los egresos de Servicios Personales

            presupuesto.get().setEgresosRecurrentesUniversidadTotales(egresosTotal);
            presupuestoRepository.save(presupuesto.get());
            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @GetMapping("/listarAprobados")
    public @ResponseBody Iterable<Presupuesto> listarAprobados() {
        return presupuestoRepository.findByEstado("aprobado");
    }

    @GetMapping("/listarEnRevision")
    public @ResponseBody Iterable<Presupuesto> listarEnRevision() {
        return presupuestoRepository.findByEstado("revision");
    }

}

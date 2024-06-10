package com.ucaldas.posgrados.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.DTO.TotalEjecucionResponse;
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
import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Repository.CdpRepository;
import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;

import org.springframework.web.bind.annotation.PostMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/ejecucionPresupuestal")
public class EjecucionPresupuestalController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;

    @Autowired
    private CdpRepository cdpRepository;

    // Se crea solo con el id del presupuesto porque al momento de que un
    // presupuesto es aprobado se crea una ejecución presupuestal
    // Pero aún sin gastos/ingresos, pues estos se van creando a medida que se van
    // necesitando
    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuesto) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        if (presupuesto.isPresent()) {
            // Comprobamos que no exista una ejecución presupuestal para el presupuesto
            if (ejecucionPresupuestalRepository.findByPresupuestoId(idPresupuesto).isPresent()) {
                return "Ya existe una ejecución presupuestal para este presupuesto";
            }

            EjecucionPresupuestal ejecucionPresupuestal = new EjecucionPresupuestal();
            ejecucionPresupuestal.setBalanceGeneralEjecucion(0);
            ejecucionPresupuestal.setEgresosProgramaTotalesEjecucion(0);
            ejecucionPresupuestal.setEgresosRecurrentesUniversidadTotalesEjecucion(0);
            ejecucionPresupuestal.setIngresosTotalesEjecucion(0);

            ejecucionPresupuestal.setPresupuesto(presupuesto.get());

            ejecucionPresupuestalRepository.save(ejecucionPresupuestal);
            return "OK";
        } else {
            return "No se encontró el presupuesto";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EjecucionPresupuestal> listar() {
        return ejecucionPresupuestalRepository.findAll();
    }

    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Optional<EjecucionPresupuestal> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return ejecucionPresupuestalRepository.findByPresupuestoId(idPresupuesto);
    }

    @GetMapping("/listarPorFacultad")
    public @ResponseBody Iterable<EjecucionPresupuestal> listarPorFacultad(@RequestParam int idFacultad) {
        return ejecucionPresupuestalRepository.findByPresupuestoCohorteProgramaFacultadId(idFacultad);
    }

    @GetMapping("/listarPorPrograma")
    public @ResponseBody Iterable<EjecucionPresupuestal> listarPorPrograma(@RequestParam int idPrograma) {
        return ejecucionPresupuestalRepository.findByPresupuestoCohorteProgramaId(idPrograma);
    }

    // Se utiliza para actualizar el atributo ingresosTotales de la clase
    // Presupuesto
    public String actualizarIngresosTotales(int idEjecucionPresupuestal,
            double nuevoValor, double antiguoValor, String tipo) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(idEjecucionPresupuestal);

        if (ejecucionPresupuestal.isPresent() && tipo.equals("ingreso")) {
            ejecucionPresupuestal.get().setIngresosTotalesEjecucion(
                    ejecucionPresupuestal.get().getIngresosTotalesEjecucion() - antiguoValor + nuevoValor);
            actualizarBalanceGeneral(idEjecucionPresupuestal);
            return "OK";
        } else if (ejecucionPresupuestal.isPresent() && tipo.equals("descuento")) {
            ejecucionPresupuestal.get().setIngresosTotalesEjecucion(
                    ejecucionPresupuestal.get().getIngresosTotalesEjecucion() - nuevoValor + antiguoValor);
            actualizarBalanceGeneral(idEjecucionPresupuestal);
            return "OK";
        } else {
            return "Error: Ejecucion presupuestal no encontrada";
        }

    }

    @GetMapping(path = "/ingresosTotales")
    public @ResponseBody double ingresosTotales(@RequestParam int id) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(id);

        if (ejecucionPresupuestal.isPresent()) {
            return ejecucionPresupuestal.get().getIngresosTotalesEjecucion();
        } else {
            return 0;
        }
    }

    // Se utiliza para actualizar el atributo egresosProgramaTotales de la clase
    // EjecucionPresupuestal
    // Cuando se crea: antiguo valor será 0
    // Cuando se modifica: antiguo valor será el valor que se quiere modificar y
    // nuevo valor será el valor nuevo
    // Cuando se elimina: nuevo valor será 0
    public String actualizarEgresosProgramaTotales(int id,
            double nuevoValor, double antiguoValor) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(id);

        if (ejecucionPresupuestal.isPresent()) {

            ejecucionPresupuestal.get().setEgresosProgramaTotalesEjecucion(
                    ejecucionPresupuestal.get().getEgresosProgramaTotalesEjecucion() - antiguoValor + nuevoValor);

            actualizarBalanceGeneral(id);

            ejecucionPresupuestalRepository.save(ejecucionPresupuestal.get());
            return "OK";
        } else {
            return "Error: Ejecucion presupuestal no encontrada";
        }

    }

    // Se utiliza para actualizar el atributo egresosProgramaTotales de la clase
    // EjecucionPresupuestal
    // Cuando se crea: antiguo valor será 0
    // Cuando se modifica: antiguo valor será el valor que se quiere modificar y
    // nuevo valor será el valor nuevo
    // Cuando se elimina: nuevo valor será 0
    public String actualizarEgresosRecurrentesUniversidadTotales(int id,
            double nuevoValor, double antiguoValor) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(id);

        if (ejecucionPresupuestal.isPresent()) {
            ejecucionPresupuestal.get().setEgresosRecurrentesUniversidadTotalesEjecucion(
                    ejecucionPresupuestal.get().getEgresosRecurrentesUniversidadTotalesEjecucion() - antiguoValor
                            + nuevoValor);

            actualizarBalanceGeneral(id);

            ejecucionPresupuestalRepository.save(ejecucionPresupuestal.get());
            return "OK";
        } else {
            return "Error: Ejecucion presupuestal no encontrada";
        }
    }

    // Cuando se crea un ejecucionPresupuestal, ingresos y los egresos tienen un
    // valor de 0,
    // por lo que no habrán excepciones al momento de realizar la operación
    public String actualizarBalanceGeneral(int id) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(id);

        if (ejecucionPresupuestal.isPresent()) {
            ejecucionPresupuestal.get()
                    .setBalanceGeneralEjecucion(ejecucionPresupuestal.get().getIngresosTotalesEjecucion()
                            - ejecucionPresupuestal.get().getEgresosProgramaTotalesEjecucion()
                            - ejecucionPresupuestal.get().getEgresosRecurrentesUniversidadTotalesEjecucion());

            // Hay un error acá
            ejecucionPresupuestalRepository.save(ejecucionPresupuestal.get());
            return "OK";
        } else {
            return "Error: Ejecucion presupuestal no encontrada";
        }
    }

    @GetMapping(path = "/totalesEjecucion")
    public @ResponseBody TotalEjecucionResponse totalesEjecucion(@RequestParam int id) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(id);

        double totalDescuentos = 0;
        double totalGenerales = 0;
        double totalInversiones = 0;
        double totalOtros = 0;
        double totalOtrosServDocentes = 0;
        double totalRecurrentesAdm = 0;
        double totalServDocentes = 0;
        double totalServNoDocentes = 0;
        double totalTransferencias = 0;
        double totalViajes = 0;

        if (ejecucionPresupuestal.isPresent()) {
            // buscamos los CDP asociados a la ejecucion presupuestal, dependiendo de su
            // rubro se suman los valores
            Iterable<Cdp> cdps = cdpRepository.findByEjecucionPresupuestalId(id);
            for (Cdp cdp : cdps) {
                if (cdp.getRubro().equals("Descuentos")) {
                    totalDescuentos += cdp.getValorTotal();

                } else if (cdp.getRubro().equals("Generales")) {
                    totalGenerales += cdp.getValorTotal();
                } else if (cdp.getRubro().equals("Inversiones")) {
                    totalInversiones += cdp.getValorTotal();
                } else if (cdp.getRubro().equals("Otros")) {
                    totalOtros += cdp.getValorTotal();
                } else if (cdp.getRubro().equals("Otros Servicios Docentes")) {
                    totalOtrosServDocentes += cdp.getValorTotal();
                } else if (cdp.getRubro().equals("Recurrentes Adm")) {
                    totalRecurrentesAdm += cdp.getValorTotal();
                } else if (cdp.getRubro().equals("Servicios Docentes")) {
                    totalServDocentes += cdp.getValorTotal();
                } else if (cdp.getRubro().equals("Servicios No Docentes")) {
                    totalServNoDocentes += cdp.getValorTotal();
                } else if (cdp.getRubro().equals("Transferencias")) {
                    totalTransferencias += cdp.getValorTotal();
                } else if (cdp.getRubro().equals("Viajes")) {
                    totalViajes += cdp.getValorTotal();
                }
            }

            return TotalEjecucionResponse.builder().totalDescuentos(totalDescuentos).totalGenerales(totalGenerales)
                    .totalInversiones(totalInversiones).totalOtros(totalOtros)
                    .totalOtrosServDocentes(totalOtrosServDocentes)
                    .totalRecurrentesAdm(totalRecurrentesAdm).totalServDocentes(totalServDocentes)
                    .totalServNoDocentes(totalServNoDocentes).totalTransferencias(totalTransferencias)
                    .totalViajes(totalViajes).build();
        } else {
            return null;
        }
    }

    @GetMapping(path = "/TotalesDelPresupuestoMismoValor")
    public @ResponseBody TotalEjecucionResponse TotalesDelPresupuestoMismoValor(@RequestParam int id) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(id);

        double totalDescuentos = 0;
        double totalGenerales = 0;
        double totalInversiones = 0;
        double totalOtros = 0;
        double totalOtrosServDocentes = 0;
        double totalRecurrentesAdm = 0;
        double totalServDocentes = 0;
        double totalServNoDocentes = 0;
        double totalTransferencias = 0;
        double totalViajes = 0;

        if (ejecucionPresupuestal.isPresent()) {

            Iterable<Cdp> cdps = cdpRepository.findByEjecucionPresupuestalId(id);

            for (Cdp cdp : cdps) {

                for (EgresoCDP egresocdp : cdp.getEgresosCDP()) {
                    if (egresocdp instanceof EgresoGeneralCDP) {
                        EgresoGeneralCDP egresoGeneralCDP = (EgresoGeneralCDP) egresocdp;
                        if (egresoGeneralCDP.getEgresoGeneral()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR) {
                            totalGenerales += egresoGeneralCDP.getEgresoGeneral().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoDescuentoCDP) {
                        EgresoDescuentoCDP egresoDescuentoCDP = (EgresoDescuentoCDP) egresocdp;
                        if (egresoDescuentoCDP.getEgresoDescuento()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR) {
                            totalDescuentos += egresoDescuentoCDP.getEgresoDescuento().getTotalDescuento();
                        }
                    } else if (egresocdp instanceof EgresoInversionCDP) {
                        EgresoInversionCDP egresoInversionCDP = (EgresoInversionCDP) egresocdp;
                        if (egresoInversionCDP.getEgresoInversion()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR) {
                            totalInversiones += egresoInversionCDP.getEgresoInversion().getValor();
                        }
                    } else if (egresocdp instanceof EgresoRecurrenteAdmCDP) {
                        EgresoRecurrenteAdmCDP egresoRecurrenteAdmCDP = (EgresoRecurrenteAdmCDP) egresocdp;
                        if (egresoRecurrenteAdmCDP.getEgresoRecurrenteAdm()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR) {
                            totalRecurrentesAdm += egresoRecurrenteAdmCDP.getEgresoRecurrenteAdm().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoServDocenteCDP) {
                        EgresoServDocenteCDP egresoServDocenteCDP = (EgresoServDocenteCDP) egresocdp;
                        if (egresoServDocenteCDP.getEgresoServDocente()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR) {
                            totalServDocentes += egresoServDocenteCDP.getEgresoServDocente().getTotalPagoProfesor();
                        }
                    } else if (egresocdp instanceof EgresoServNoDocenteCDP) {
                        EgresoServNoDocenteCDP egresoServNoDocenteCDP = (EgresoServNoDocenteCDP) egresocdp;
                        if (egresoServNoDocenteCDP.getEgresoServNoDocente()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR) {
                            totalServNoDocentes += egresoServNoDocenteCDP.getEgresoServNoDocente().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoTransferenciaCDP) {
                        EgresoTransferenciaCDP egresoTransferenciaCDP = (EgresoTransferenciaCDP) egresocdp;
                        if (egresoTransferenciaCDP.getEgresoTransferencia()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR) {
                            totalTransferencias += egresoTransferenciaCDP.getEgresoTransferencia().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoViajeCDP) {
                        EgresoViajeCDP egresoViajeCD = (EgresoViajeCDP) egresocdp;
                        if (egresoViajeCD.getEgresoViaje()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR) {
                            totalViajes += egresoViajeCD.getEgresoViaje().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoOtroCDP) {
                        EgresoOtroCDP egresoOtroCDP = (EgresoOtroCDP) egresocdp;
                        if (egresoOtroCDP.getEgresoOtro()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR) {
                            totalOtros += egresoOtroCDP.getEgresoOtro().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoOtroServDocenteCDP) {
                        EgresoOtroServDocenteCDP egresoOtroServDocenteCDP = (EgresoOtroServDocenteCDP) egresocdp;
                        if (egresoOtroServDocenteCDP.getEgresoOtroServDocente()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR) {
                            totalOtrosServDocentes += egresoOtroServDocenteCDP.getEgresoOtroServDocente()
                                    .getValorTotal();
                        }
                    }
                }

            }

            return TotalEjecucionResponse.builder().totalDescuentos(totalDescuentos).totalGenerales(totalGenerales)
                    .totalInversiones(totalInversiones).totalOtros(totalOtros)
                    .totalOtrosServDocentes(totalOtrosServDocentes)
                    .totalRecurrentesAdm(totalRecurrentesAdm).totalServDocentes(totalServDocentes)
                    .totalServNoDocentes(totalServNoDocentes).totalTransferencias(totalTransferencias)
                    .totalViajes(totalViajes).build();
        }
        return null;
    }

    @GetMapping(path = "/TotalesDelPresupuestoOtroValor")
    public @ResponseBody TotalEjecucionResponse TotalesDelPresupuestoOtroValor(@RequestParam int id) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(id);

        double totalDescuentos = 0;
        double totalGenerales = 0;
        double totalInversiones = 0;
        double totalOtros = 0;
        double totalOtrosServDocentes = 0;
        double totalRecurrentesAdm = 0;
        double totalServDocentes = 0;
        double totalServNoDocentes = 0;
        double totalTransferencias = 0;
        double totalViajes = 0;

        if (ejecucionPresupuestal.isPresent()) {

            Iterable<Cdp> cdps = cdpRepository.findByEjecucionPresupuestalId(id);

            for (Cdp cdp : cdps) {

                for (EgresoCDP egresocdp : cdp.getEgresosCDP()) {
                    if (egresocdp instanceof EgresoGeneralCDP) {
                        EgresoGeneralCDP egresoGeneralCDP = (EgresoGeneralCDP) egresocdp;
                        if (egresoGeneralCDP.getEgresoGeneral()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR) {
                            totalGenerales += egresoGeneralCDP.getEgresoGeneral().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoDescuentoCDP) {
                        EgresoDescuentoCDP egresoDescuentoCDP = (EgresoDescuentoCDP) egresocdp;
                        if (egresoDescuentoCDP.getEgresoDescuento()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR) {
                            totalDescuentos += egresoDescuentoCDP.getEgresoDescuento().getTotalDescuento();
                        }
                    } else if (egresocdp instanceof EgresoInversionCDP) {
                        EgresoInversionCDP egresoInversionCDP = (EgresoInversionCDP) egresocdp;
                        if (egresoInversionCDP.getEgresoInversion()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR) {
                            totalInversiones += egresoInversionCDP.getEgresoInversion().getValor();
                        }
                    } else if (egresocdp instanceof EgresoRecurrenteAdmCDP) {
                        EgresoRecurrenteAdmCDP egresoRecurrenteAdmCDP = (EgresoRecurrenteAdmCDP) egresocdp;
                        if (egresoRecurrenteAdmCDP.getEgresoRecurrenteAdm()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR) {
                            totalRecurrentesAdm += egresoRecurrenteAdmCDP.getEgresoRecurrenteAdm().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoServDocenteCDP) {
                        EgresoServDocenteCDP egresoServDocenteCDP = (EgresoServDocenteCDP) egresocdp;
                        if (egresoServDocenteCDP.getEgresoServDocente()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR) {
                            totalServDocentes += egresoServDocenteCDP.getEgresoServDocente().getTotalPagoProfesor();
                        }
                    } else if (egresocdp instanceof EgresoServNoDocenteCDP) {
                        EgresoServNoDocenteCDP egresoServNoDocenteCDP = (EgresoServNoDocenteCDP) egresocdp;
                        if (egresoServNoDocenteCDP.getEgresoServNoDocente()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR) {
                            totalServNoDocentes += egresoServNoDocenteCDP.getEgresoServNoDocente().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoTransferenciaCDP) {
                        EgresoTransferenciaCDP egresoTransferenciaCDP = (EgresoTransferenciaCDP) egresocdp;
                        if (egresoTransferenciaCDP.getEgresoTransferencia()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR) {
                            totalTransferencias += egresoTransferenciaCDP.getEgresoTransferencia().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoViajeCDP) {
                        EgresoViajeCDP egresoViajeCD = (EgresoViajeCDP) egresocdp;
                        if (egresoViajeCD.getEgresoViaje()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR) {
                            totalViajes += egresoViajeCD.getEgresoViaje().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoOtroCDP) {
                        EgresoOtroCDP egresoOtroCDP = (EgresoOtroCDP) egresocdp;
                        if (egresoOtroCDP.getEgresoOtro()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR) {
                            totalOtros += egresoOtroCDP.getEgresoOtro().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoOtroServDocenteCDP) {
                        EgresoOtroServDocenteCDP egresoOtroServDocenteCDP = (EgresoOtroServDocenteCDP) egresocdp;
                        if (egresoOtroServDocenteCDP.getEgresoOtroServDocente()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR) {
                            totalOtrosServDocentes += egresoOtroServDocenteCDP.getEgresoOtroServDocente()
                                    .getValorTotal();
                        }
                    }
                }
            }

            return TotalEjecucionResponse.builder().totalDescuentos(totalDescuentos).totalGenerales(totalGenerales)
                    .totalInversiones(totalInversiones).totalOtros(totalOtros)
                    .totalOtrosServDocentes(totalOtrosServDocentes)
                    .totalRecurrentesAdm(totalRecurrentesAdm).totalServDocentes(totalServDocentes)
                    .totalServNoDocentes(totalServNoDocentes).totalTransferencias(totalTransferencias)
                    .totalViajes(totalViajes).build();

        } else {
            return null;
        }
    }

    @GetMapping(path = "/TotalesFueraDelPresupuesto")
    public @ResponseBody TotalEjecucionResponse TotalesFueraDelPresupuesto(@RequestParam int id) {
        Optional<EjecucionPresupuestal> ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(id);

        double totalDescuentos = 0;
        double totalGenerales = 0;
        double totalInversiones = 0;
        double totalOtros = 0;
        double totalOtrosServDocentes = 0;
        double totalRecurrentesAdm = 0;
        double totalServDocentes = 0;
        double totalServNoDocentes = 0;
        double totalTransferencias = 0;
        double totalViajes = 0;

        if (ejecucionPresupuestal.isPresent()) {

            Iterable<Cdp> cdps = cdpRepository.findByEjecucionPresupuestalId(id);

            for (Cdp cdp : cdps) {

                for (EgresoCDP egresocdp : cdp.getEgresosCDP()) {
                    if (egresocdp instanceof EgresoGeneralCDP) {
                        EgresoGeneralCDP egresoGeneralCDP = (EgresoGeneralCDP) egresocdp;
                        if (egresoGeneralCDP.getEgresoGeneral()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.FUERADELPRESUPUESTO) {
                            totalGenerales += egresoGeneralCDP.getEgresoGeneral().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoDescuentoCDP) {
                        EgresoDescuentoCDP egresoDescuentoCDP = (EgresoDescuentoCDP) egresocdp;
                        if (egresoDescuentoCDP.getEgresoDescuento()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.FUERADELPRESUPUESTO) {
                            totalDescuentos += egresoDescuentoCDP.getEgresoDescuento().getTotalDescuento();
                        }
                    } else if (egresocdp instanceof EgresoInversionCDP) {
                        EgresoInversionCDP egresoInversionCDP = (EgresoInversionCDP) egresocdp;
                        if (egresoInversionCDP.getEgresoInversion()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.FUERADELPRESUPUESTO) {
                            totalInversiones += egresoInversionCDP.getEgresoInversion().getValor();
                        }
                    } else if (egresocdp instanceof EgresoRecurrenteAdmCDP) {
                        EgresoRecurrenteAdmCDP egresoRecurrenteAdmCDP = (EgresoRecurrenteAdmCDP) egresocdp;
                        if (egresoRecurrenteAdmCDP.getEgresoRecurrenteAdm()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.FUERADELPRESUPUESTO) {
                            totalRecurrentesAdm += egresoRecurrenteAdmCDP.getEgresoRecurrenteAdm().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoServDocenteCDP) {
                        EgresoServDocenteCDP egresoServDocenteCDP = (EgresoServDocenteCDP) egresocdp;
                        if (egresoServDocenteCDP.getEgresoServDocente()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.FUERADELPRESUPUESTO) {
                            totalServDocentes += egresoServDocenteCDP.getEgresoServDocente().getTotalPagoProfesor();
                        }
                    } else if (egresocdp instanceof EgresoServNoDocenteCDP) {
                        EgresoServNoDocenteCDP egresoServNoDocenteCDP = (EgresoServNoDocenteCDP) egresocdp;
                        if (egresoServNoDocenteCDP.getEgresoServNoDocente()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.FUERADELPRESUPUESTO) {
                            totalServNoDocentes += egresoServNoDocenteCDP.getEgresoServNoDocente().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoTransferenciaCDP) {
                        EgresoTransferenciaCDP egresoTransferenciaCDP = (EgresoTransferenciaCDP) egresocdp;
                        if (egresoTransferenciaCDP.getEgresoTransferencia()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.FUERADELPRESUPUESTO) {
                            totalTransferencias += egresoTransferenciaCDP.getEgresoTransferencia().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoViajeCDP) {
                        EgresoViajeCDP egresoViajeCD = (EgresoViajeCDP) egresocdp;
                        if (egresoViajeCD.getEgresoViaje()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.FUERADELPRESUPUESTO) {
                            totalViajes += egresoViajeCD.getEgresoViaje().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoOtroCDP) {
                        EgresoOtroCDP egresoOtroCDP = (EgresoOtroCDP) egresocdp;
                        if (egresoOtroCDP.getEgresoOtro()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.FUERADELPRESUPUESTO) {
                            totalOtros += egresoOtroCDP.getEgresoOtro().getValorTotal();
                        }
                    } else if (egresocdp instanceof EgresoOtroServDocenteCDP) {
                        EgresoOtroServDocenteCDP egresoOtroServDocenteCDP = (EgresoOtroServDocenteCDP) egresocdp;
                        if (egresoOtroServDocenteCDP.getEgresoOtroServDocente()
                                .getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.FUERADELPRESUPUESTO) {
                            totalOtrosServDocentes += egresoOtroServDocenteCDP.getEgresoOtroServDocente()
                                    .getValorTotal();
                        }
                    }
                }
            }

            return TotalEjecucionResponse.builder().totalDescuentos(totalDescuentos).totalGenerales(totalGenerales)
                    .totalInversiones(totalInversiones).totalOtros(totalOtros)
                    .totalOtrosServDocentes(totalOtrosServDocentes)
                    .totalRecurrentesAdm(totalRecurrentesAdm).totalServDocentes(totalServDocentes)
                    .totalServNoDocentes(totalServNoDocentes).totalTransferencias(totalTransferencias)
                    .totalViajes(totalViajes).build();
        } else {
            return null;
        }
    }

}

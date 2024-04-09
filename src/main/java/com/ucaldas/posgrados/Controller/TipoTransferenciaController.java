package com.ucaldas.posgrados.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.TipoTransferencia;
import com.ucaldas.posgrados.Repository.TipoTransferenciaRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/tipoTransferencia")
public class TipoTransferenciaController {

    @Autowired
    private TipoTransferenciaRepository tipoTransferenciaRepository;

    @PostMapping(path = "/crear")
    public @ResponseBody String crear(@RequestParam String nombreTipo) {

        TipoTransferencia tipoTransferencia = new TipoTransferencia();
        tipoTransferencia.setNombreTipo(nombreTipo);

        tipoTransferenciaRepository.save(tipoTransferencia);
        return "Tipo de transferencia guardado";

    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        tipoTransferenciaRepository.deleteById(id);
        return "Tipo de transferencia eliminado";

    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String nombreTipo) {

        TipoTransferencia tipoTransferencia = tipoTransferenciaRepository.findById(id).get();
        tipoTransferencia.setNombreTipo(nombreTipo);

        tipoTransferenciaRepository.save(tipoTransferencia);
        return "Tipo de transferencia actualizado";

    }

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<TipoTransferencia> listar() {

        return tipoTransferenciaRepository.findAllByOrderByNombreTipoAsc();

    }

}

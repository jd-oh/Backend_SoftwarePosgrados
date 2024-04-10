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

import com.ucaldas.posgrados.Entity.TipoCompensacion;
import com.ucaldas.posgrados.Repository.TipoCompensacionRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/tipoCompensacion")
public class TipoCompensacionController {

    @Autowired
    private TipoCompensacionRepository tipoCompensacionRepository;

    @PostMapping(path = "/crear")
    public @ResponseBody String crear(@RequestParam String nombreTipo) {

        TipoCompensacion tipoCompensacion = new TipoCompensacion();
        tipoCompensacion.setNombreTipo(nombreTipo);

        tipoCompensacionRepository.save(tipoCompensacion);
        return "OK";

    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        tipoCompensacionRepository.deleteById(id);
        return "OK";

    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String nombreTipo) {

        TipoCompensacion tipoCompensacion = tipoCompensacionRepository.findById(id).get();
        tipoCompensacion.setNombreTipo(nombreTipo);

        tipoCompensacionRepository.save(tipoCompensacion);
        return "OK";

    }

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<TipoCompensacion> listar() {

        return tipoCompensacionRepository.findAllByOrderByNombreTipoAsc();

    }

}

package com.ucaldas.posgrados.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
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
        return "Tipo de descuento guardado";

    }

    @PostMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        tipoCompensacionRepository.deleteById(id);
        return "Tipo de descuento eliminado";

    }

    @PostMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String nombreTipo) {

        TipoCompensacion tipoCompensacion = tipoCompensacionRepository.findById(id).get();
        tipoCompensacion.setNombreTipo(nombreTipo);

        tipoCompensacionRepository.save(tipoCompensacion);
        return "Tipo de descuento actualizado";

    }

    @PostMapping(path = "/listar")
    public @ResponseBody Iterable<TipoCompensacion> listar() {

        return tipoCompensacionRepository.findAllByOrderByNombreTipoAsc();

    }

}

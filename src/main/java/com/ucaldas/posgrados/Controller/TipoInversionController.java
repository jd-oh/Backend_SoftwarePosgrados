package com.ucaldas.posgrados.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.TipoInversion;
import com.ucaldas.posgrados.Repository.TipoInversionRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/tipoInversion")
public class TipoInversionController {

    @Autowired
    private TipoInversionRepository tipoInversionRepository;

    @PostMapping(path = "/crear")
    public @ResponseBody String crear(@RequestParam String nombreTipo) {

        TipoInversion tipoInversion = new TipoInversion();
        tipoInversion.setNombreTipo(nombreTipo);

        tipoInversionRepository.save(tipoInversion);
        return "Tipo de descuento guardado";

    }

    @PostMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        tipoInversionRepository.deleteById(id);
        return "Tipo de descuento eliminado";

    }

    @PostMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String nombreTipo) {

        TipoInversion tipoInversion = tipoInversionRepository.findById(id).get();
        tipoInversion.setNombreTipo(nombreTipo);

        tipoInversionRepository.save(tipoInversion);
        return "Tipo de descuento actualizado";

    }

    @PostMapping(path = "/listar")
    public @ResponseBody Iterable<TipoInversion> listar() {

        return tipoInversionRepository.findAllByOrderByNombreTipoAsc();

    }

}

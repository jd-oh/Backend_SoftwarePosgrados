package com.ucaldas.posgrados.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.TipoDescuento;
import com.ucaldas.posgrados.Repository.TipoDescuentoRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/tipoDescuento")
public class TipoDescuentoController {

    @Autowired
    private TipoDescuentoRepository tipoDescuentoRepository;

    @PostMapping(path = "/crear")
    public @ResponseBody String crear(@RequestParam String nombreTipo) {

        TipoDescuento tipoDescuento = new TipoDescuento();
        tipoDescuento.setNombreTipo(nombreTipo);

        tipoDescuentoRepository.save(tipoDescuento);
        return "Tipo de descuento guardado";

    }

    @PostMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        tipoDescuentoRepository.deleteById(id);
        return "Tipo de descuento eliminado";

    }

    @PostMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String nombreTipo) {

        TipoDescuento tipoDescuento = tipoDescuentoRepository.findById(id).get();
        tipoDescuento.setNombreTipo(nombreTipo);

        tipoDescuentoRepository.save(tipoDescuento);
        return "Tipo de descuento actualizado";

    }

    @PostMapping(path = "/listar")
    public @ResponseBody Iterable<TipoDescuento> listar() {

        return tipoDescuentoRepository.findAllByOrderByNombreTipoAsc();

    }

}

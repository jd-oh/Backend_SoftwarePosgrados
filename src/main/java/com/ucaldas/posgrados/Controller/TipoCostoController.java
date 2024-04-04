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

import com.ucaldas.posgrados.Entity.TipoCosto;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/tipoCosto")
public class TipoCostoController {

    @Autowired
    private TipoCostoRepository tipoCostoRepository;

    @PostMapping(path = "/crear")
    public @ResponseBody String crear(@RequestParam String nombreTipo) {

        TipoCosto tipoCosto = new TipoCosto();
        tipoCosto.setNombreTipo(nombreTipo);

        tipoCostoRepository.save(tipoCosto);
        return "Tipo de costo guardado";

    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        tipoCostoRepository.deleteById(id);
        return "Tipo de costo eliminado";

    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String nombreTipo) {

        TipoCosto tipoCosto = tipoCostoRepository.findById(id).get();
        tipoCosto.setNombreTipo(nombreTipo);

        tipoCostoRepository.save(tipoCosto);
        return "Tipo de costo actualizado";

    }

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<TipoCosto> listar() {

        return tipoCostoRepository.findAllByOrderByNombreTipoAsc();

    }

}

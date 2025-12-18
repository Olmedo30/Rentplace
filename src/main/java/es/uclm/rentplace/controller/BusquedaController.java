package es.uclm.rentplace.controller;

import es.uclm.rentplace.dto.BusquedaPropiedadDTO;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.service.BusquedaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
public class BusquedaController {

    @Autowired
    private BusquedaService busquedaService;

    @GetMapping("/buscar")
    public String mostrarPaginaBusqueda(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String tipoInmueble,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

    	// AÑADE ESTA LÍNEA PARA DEPURAR
        System.out.println("Parámetros recibidos - ciudad: '" + ciudad + "', tipoInmueble: '" + tipoInmueble + "'");
        
    	// Asegurar que siempre hay un objeto criterios
    	BusquedaPropiedadDTO criterios = new BusquedaPropiedadDTO();
    	criterios.setCiudad(ciudad != null ? ciudad : "");
    	criterios.setTipoInmueble(tipoInmueble != null ? tipoInmueble : "");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("precioNoche").ascending());
        
        try {
            Page<Propiedad> propiedades = busquedaService.buscarPropiedades(criterios, pageable);
            List<String> tiposInmueble = busquedaService.obtenerTiposInmuebleDisponibles();
            List<String> ciudades = busquedaService.obtenerCiudadesDisponibles();
            
            // SIEMPRE añadir todos los atributos al modelo
            model.addAttribute("propiedades", propiedades);
            model.addAttribute("criterios", criterios); // ← ¡ESTO ES CLAVE!
            model.addAttribute("tiposInmueble", tiposInmueble);
            model.addAttribute("ciudades", ciudades);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", propiedades.getTotalPages());
            
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error, crear objetos vacíos pero nunca null
            model.addAttribute("propiedades", Page.empty());
            model.addAttribute("criterios", new BusquedaPropiedadDTO()); // ← ¡NUNCA NULL!
            model.addAttribute("tiposInmueble", Collections.emptyList());
            model.addAttribute("ciudades", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("error", "Error al cargar las propiedades");
        }
        
        return "busqueda-propiedades";
    }
    
    @GetMapping("/fecha-entrada")
    public String buscarFechaEntrada() {
        return "redirect:/buscar";
    }

    @GetMapping("/fecha-salida")
    public String buscarFechaSalida() {
        return "redirect:/buscar";
    }
}
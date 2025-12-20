package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.ListaDeseos;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.usuarioDAO;
import es.uclm.rentplace.persistence.ListaDeseosDAO;
import es.uclm.rentplace.persistence.PropiedadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ListaDeseosService {
    
    @Autowired
    private ListaDeseosDAO listaDeseosDAO;
    
    @Autowired
    private PropiedadDAO propiedadDAO;
    
    @Autowired
    private usuarioDAO usuarioDAO;
    
    @Transactional
    public ListaDeseos crearListaDeseosParaUsuario(Usuario usuario) {
        ListaDeseos lista = new ListaDeseos(usuario);
        return listaDeseosDAO.save(lista);
    }
    
    public boolean estaEnListaDeDeseos(Long usuarioId, Long propiedadId) {
        ListaDeseos lista = listaDeseosDAO.findByUsuarioId(usuarioId).orElse(null);
        if (lista == null) {
            return false;
        }
        
        // Verificar si la propiedad está en la lista
        return lista.getPropiedades().stream()
                .anyMatch(prop -> prop.getId().equals(propiedadId));
    }
    
    @Transactional
    public boolean agregarPropiedadALista(Long usuarioId, Long propiedadId) {
        // 1. Verificar que la propiedad existe
        Propiedad propiedad = propiedadDAO.findById(propiedadId).orElse(null);
        if (propiedad == null) {
            return false;
        }

        // 2. Verificar que el usuario existe
        Usuario usuario = usuarioDAO.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return false;
        }

        // 3. Obtener o crear la lista de deseos
        ListaDeseos lista = listaDeseosDAO.findByUsuarioId(usuarioId)
            .orElseGet(() -> {
                ListaDeseos nuevaLista = new ListaDeseos(usuario); // ✅ usuario gestionado
                return listaDeseosDAO.save(nuevaLista);
            });

        // 4. Evitar duplicados
        if (lista.getPropiedades().stream().anyMatch(p -> p.getId().equals(propiedadId))) {
            return true;
        }

        // 5. Añadir y guardar
        lista.agregarPropiedad(propiedad);
        listaDeseosDAO.save(lista);
        return true;
    }
    
    @Transactional
    public boolean eliminarPropiedadDeLista(Long usuarioId, Long propiedadId) {
        Optional<ListaDeseos> listaOpt = listaDeseosDAO.findByUsuarioId(usuarioId);
        if (!listaOpt.isPresent()) {
            return false;
        }
        
        Propiedad propiedad = propiedadDAO.findById(propiedadId).orElse(null);
        if (propiedad == null) {
            return false;
        }
        
        ListaDeseos lista = listaOpt.get();
        lista.eliminarPropiedad(propiedad);
        listaDeseosDAO.save(lista);
        return true;
    }
    
    public ListaDeseos obtenerListaDeUsuario(Long usuarioId) {
        return listaDeseosDAO.findByUsuarioId(usuarioId).orElse(null);
    }
    
    public List<Propiedad> obtenerPropiedadesDeLista(Long usuarioId) {
        ListaDeseos lista = listaDeseosDAO.findByUsuarioId(usuarioId).orElse(null);
        return lista != null ? lista.getPropiedades() : List.of();
    }
}
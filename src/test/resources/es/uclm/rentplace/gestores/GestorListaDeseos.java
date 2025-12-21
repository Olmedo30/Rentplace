package es.uclm.rentplace.gestores;

import es.uclm.rentplace.entity.ListaDeseos;
import es.uclm.rentplace.entity.Propiedad;
import es.uclm.rentplace.entity.Usuario;
import es.uclm.rentplace.persistence.ListaDeseosDAO;
import es.uclm.rentplace.persistence.PropiedadDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GestorListaDeseos { 

    @Autowired
	public ListaDeseosDAO listaDeseosDAO;
    
    @Autowired
	public PropiedadDAO propiedadDAO;

    
     // Obtiene la lista de deseos de un usuario
     
    public ListaDeseos obtenerListaDeseosPorUsuario(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido.");
        }
        return listaDeseosDAO.findByUsuarioId(usuarioId).orElse(null);
    }

    
     // Verifica si un usuario tiene lista de deseos
     
    public boolean usuarioTieneListaDeseos(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido.");
        }
        return listaDeseosDAO.existsByUsuarioId(usuarioId);
    }

    
     //Crea una nueva lista de deseos para un usuario
     
    public ListaDeseos crearListaDeseos(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (usuario.getId() == null || usuario.getId() <= 0) {
            throw new IllegalArgumentException("El usuario debe tener un ID válido.");
        }
        
        // Verificar si ya tiene lista de deseos
        if (listaDeseosDAO.existsByUsuarioId(usuario.getId())) {
            throw new IllegalArgumentException("El usuario ya tiene una lista de deseos.");
        }
        
        ListaDeseos nuevaLista = new ListaDeseos(usuario);
        return listaDeseosDAO.save(nuevaLista);
    }

    
     // Agrega una propiedad a la lista de deseos
     
    public boolean agregarPropiedadALista(Long usuarioId, Long propiedadId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido.");
        }
        if (propiedadId == null || propiedadId <= 0) {
            throw new IllegalArgumentException("ID de propiedad inválido.");
        }
        
        // Obtener la lista de deseos
        ListaDeseos lista = listaDeseosDAO.findByUsuarioId(usuarioId).orElse(null);
        if (lista == null) {
            return false;
        }
        
        // Obtener la propiedad
        Propiedad propiedad = propiedadDAO.findById(propiedadId).orElse(null);
        if (propiedad == null) {
            return false;
        }
        
        // Verificar si ya está en la lista
        if (lista.getPropiedades().contains(propiedad)) {
            return false;
        }
        
        lista.agregarPropiedad(propiedad);
        listaDeseosDAO.save(lista);
        return true;
    }

    
     // Elimina una propiedad de la lista de deseos
     
    public boolean eliminarPropiedadDeLista(Long usuarioId, Long propiedadId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido.");
        }
        if (propiedadId == null || propiedadId <= 0) {
            throw new IllegalArgumentException("ID de propiedad inválido.");
        }
        
        // Obtener la lista de deseos
        ListaDeseos lista = listaDeseosDAO.findByUsuarioId(usuarioId).orElse(null);
        if (lista == null) {
            return false;
        }
        
        // Obtener la propiedad
        Propiedad propiedad = propiedadDAO.findById(propiedadId).orElse(null);
        if (propiedad == null) {
            return false;
        }
        
        // Verificar si está en la lista
        if (!lista.getPropiedades().contains(propiedad)) {
            return false;
        }
        
        lista.eliminarPropiedad(propiedad);
        listaDeseosDAO.save(lista);
        return true;
    }

    
     // Obtiene todas las propiedades de la lista de deseos de un usuario
     
    public List<Propiedad> obtenerPropiedadesDeLista(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido.");
        }
        
        ListaDeseos lista = listaDeseosDAO.findByUsuarioId(usuarioId).orElse(null);
        if (lista == null) {
            return List.of();
        }
        
        return lista.getPropiedades();
    }

    
     // Verifica si una propiedad está en la lista de deseos de un usuario
     
    public boolean propiedadEstaEnLista(Long usuarioId, Long propiedadId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido.");
        }
        if (propiedadId == null || propiedadId <= 0) {
            throw new IllegalArgumentException("ID de propiedad inválido.");
        }
        
        ListaDeseos lista = listaDeseosDAO.findByUsuarioId(usuarioId).orElse(null);
        if (lista == null) {
            return false;
        }
        
        return lista.getPropiedades().stream()
                .anyMatch(p -> p.getId().equals(propiedadId));
    }

    
     // Obtiene el número de propiedades en la lista de deseos
     
    public int contarPropiedadesEnLista(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido.");
        }
        
        ListaDeseos lista = listaDeseosDAO.findByUsuarioId(usuarioId).orElse(null);
        if (lista == null) {
            return 0;
        }
        
        return lista.getPropiedades().size();
    }

    
     //Limpia todas las propiedades de la lista de deseos
     
    public boolean limpiarListaDeseos(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido.");
        }
        
        ListaDeseos lista = listaDeseosDAO.findByUsuarioId(usuarioId).orElse(null);
        if (lista == null) {
            return false;
        }
        
        lista.getPropiedades().clear();
        listaDeseosDAO.save(lista);
        return true;
    }
}

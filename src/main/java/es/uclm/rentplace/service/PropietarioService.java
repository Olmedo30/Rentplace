package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Propietario;
import es.uclm.rentplace.persistence.PropietarioDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropietarioService {

    @Autowired
    private PropietarioDAO propietarioDAO;

    public Propietario crearPropietario(Propietario p) {
        return propietarioDAO.save(p);
    }

    public Optional<Propietario> findById(Long id){
        return propietarioDAO.findById(id);
    }

    public boolean existsByEmail(String email){
        return propietarioDAO.existsByEmail(email);
    }

    public List<Propietario> listarTodos(){
        return propietarioDAO.findAll();
    }
}


package es.uclm.rentplace.service;

import es.uclm.rentplace.entity.Reserva;
import es.uclm.rentplace.persistence.ReservaDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PagoService {

    @Value("${dominio.url}")
    private String domainUrl;

    @Autowired
    private ReservaDAO reservaDAO;

    /**
     * SIMULACIÓN: Simula la creación de una sesión de pago y devuelve la URL 
     * de redireccionamiento al endpoint de éxito.
     * @param reserva La reserva a pagar.
     * @return URL de redireccionamiento al éxito con un 'mock_session_id'.
     */
    public String createCheckoutSession(Reserva reserva) {
        
        // SIMULACIÓN: mock_session_id con el ID de la reserva
        String mockSessionId = "mock_sess_" + reserva.getId();
        
        // Redirige al endpoint de éxito en PagoController
        String successUrl = domainUrl + "/pago/exito?session_id=" + mockSessionId;

        System.out.println("SIMULACIÓN: Generada URL de éxito para Reserva ID: " + reserva.getId());
        return successUrl; 
    }

    /**
     * SIMULACIÓN: Asume que el pago fue exitoso y actualiza el estado de la reserva.
     * @param sessionId El ID de la sesión de pago (que contiene el ID de la reserva en la simulación).
     * @return ID de la Reserva si el pago fue 'confirmado' y se actualizó a PAGADA.
     */
    public Long confirmPaymentAndReserva(String sessionId) {
        
        // SIMULACIÓN: Extrae el ID de la mock_session_id
        if (sessionId == null || !sessionId.startsWith("mock_sess_")) {
            System.err.println("SIMULACIÓN FALLIDA: Session ID no válido.");
            return null;
        }
        
        Long reservaId;
        try {
            // Extraer el número después de "mock_sess_"
            reservaId = Long.parseLong(sessionId.substring("mock_sess_".length()));
        } catch (NumberFormatException e) {
             System.err.println("SIMULACIÓN FALLIDA: ID de reserva no numérico.");
            return null;
        }

        return reservaDAO.findById(reservaId).map(reserva -> {
            // SIMULACIÓN: Asume que el pago fue confirmado.
            if ("PENDIENTE".equals(reserva.getEstado())) {
                reserva.setEstado("PAGADA");
                reservaDAO.save(reserva);
                System.out.println("SIMULACIÓN EXITOSA: Reserva " + reservaId + " pagada y confirmada.");
            }
            return reservaId;
        }).orElse(null);
    }
}
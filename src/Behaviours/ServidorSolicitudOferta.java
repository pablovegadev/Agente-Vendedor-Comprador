package Behaviours;

import Agentes.AgenteVendedorLibros;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ServidorSolicitudOferta extends CyclicBehaviour {
    private AgenteVendedorLibros bsAgent;

    // Constructor del comportamiento
    public ServidorSolicitudOferta(AgenteVendedorLibros a) {
        bsAgent = a;
    }

    // Método principal del comportamiento
    public void action() {
        // Definir una plantilla de mensaje para capturar mensajes de tipo CFP (Call for Proposal)
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);

        // Recibir el mensaje que cumple con la plantilla
        ACLMessage msg = bsAgent.receive(mt);

        if (msg != null) {
            String title = msg.getContent();
            ACLMessage reply = msg.createReply(); // Crear una respuesta al mensaje recibido

            // Intentar obtener el precio del libro del catálogo del agente vendedor
            Integer price = (Integer) bsAgent.getCatalogue().get(title);

            if (price != null) {
                // Si el libro está disponible en el catálogo, proponer un precio al agente comprador
                reply.setPerformative(ACLMessage.PROPOSE);
                reply.setContent(String.valueOf(price.intValue()));
            } else {
                // Si el libro no está disponible en el catálogo, rechazar la solicitud
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("not-available");
            }

            // Enviar la respuesta al agente comprador
            bsAgent.send(reply);
        } else {
            block(); // Bloquear el comportamiento si no se recibe ningún mensaje
        }
    }
}

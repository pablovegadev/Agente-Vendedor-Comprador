package Behaviours;

import Agentes.AgenteCompradorLibros;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class EjecutadorSolicitud extends Behaviour {
    private AID bestSeller;                // El agente vendedor con la mejor oferta
    private int bestPrice;                // El precio más bajo ofrecido
    private int repliesCount = 0;         // Contador de respuestas recibidas
    private MessageTemplate mt;           // Plantilla de mensajes
    private int step = 0;                 // Paso actual en el comportamiento
    private AgenteCompradorLibros bbAgent; // El agente comprador asociado
    private String bookTitle;             // Título del libro a comprar

    // Constructor del comportamiento
    public EjecutadorSolicitud(AgenteCompradorLibros a) {
        bbAgent = a;
        bookTitle = a.getBookTitle();
    }

    // Método de ejecución del comportamiento
    public void action() {
        switch (step) {
            case 0:
                // Paso 0: Enviar una solicitud de oferta a los agentes vendedores
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for (int i = 0; i < bbAgent.getSellerAgents().length; i++) {
                    cfp.addReceiver(bbAgent.getSellerAgents()[i]);
                }

                cfp.setContent(bookTitle);
                cfp.setConversationId("book-trade");
                cfp.setReplyWith("cfp" + System.currentTimeMillis());
                myAgent.send(cfp);

                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                        MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                step = 1;
                break;

            case 1:
                // Paso 1: Recibir y evaluar las respuestas de los agentes vendedores
                ACLMessage reply = bbAgent.receive(mt);
                if (reply != null) {
                    if (reply.getPerformative() == ACLMessage.PROPOSE) {
                        int price = Integer.parseInt(reply.getContent());
                        if (bestSeller == null || price < bestPrice) {
                            bestPrice = price;
                            bestSeller = reply.getSender();
                        }
                    }
                    repliesCount++;
                    if (repliesCount >= bbAgent.getSellerAgents().length) {
                        step = 2;
                    }
                } else {
                    block();
                }
                break;

            case 2:
                // Paso 2: Enviar una orden de compra al agente vendedor con la mejor oferta
                ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                order.addReceiver(bestSeller);
                order.setContent(bookTitle);
                order.setConversationId("book-trade");
                order.setReplyWith("order" + System.currentTimeMillis());
                bbAgent.send(order);

                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                        MessageTemplate.MatchInReplyTo(order.getReplyWith()));

                step = 3;
                break;

            case 3:
                // Paso 3: Recibir confirmación de compra o mensaje de falla
                reply = myAgent.receive(mt);
                if (reply != null) {
                    if (reply.getPerformative() == ACLMessage.INFORM) {
                        System.out.println(bookTitle + " comprado exitosamente al agente " + reply.getSender().getName());
                        System.out.println("Precio = " + bestPrice);
                        myAgent.doDelete(); // Terminar el agente comprador
                    } else {
                        System.out.println("El intento falló: ¡el libro solicitado ya se vendió!");
                    }
                    step = 4;
                } else {
                    block();
                }
                break;
        }
    }

    // Método que indica si el comportamiento ha terminado
    public boolean done() {
        if (step == 2 && bestSeller == null) {
            System.out.println("El intento falló: " + bookTitle + " no está disponible para la venta");
        }
        return ((step == 2 && bestSeller == null) || step == 4);
    }
}

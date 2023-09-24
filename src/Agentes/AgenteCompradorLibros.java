package Agentes;

import Behaviours.EjecutadorSolicitud;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgenteCompradorLibros extends Agent {
    // Variables de clase
    private String bookTitle;          // Título del libro que el agente desea comprar
    private AID[] sellerAgents;        // Array de identificadores de agentes vendedores
    private int ticker_timer = 10000;  // Intervalo de tiempo para buscar agentes vendedores (10 segundos)
    private AgenteCompradorLibros this_agent = this;

    // Método de configuración del agente
    protected void setup() {
        System.out.println("El agente comprador " + getAID().getName() + " está listo");

        // Obtener argumentos pasados al agente
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            bookTitle = (String) args[0];
            System.out.println("Libro a comprar: " + bookTitle);

            // Comportamiento de temporización para buscar agentes vendedores
            addBehaviour(new TickerBehaviour(this, ticker_timer) {
                protected void onTick() {
                    System.out.println("Intentando comprar el libro: " + bookTitle);

                    // Crear una descripción de agente para buscar agentes vendedores
                    DFAgentDescription template = new DFAgentDescription();
                    ServiceDescription sd = new ServiceDescription();
                    sd.setType("book-selling");
                    template.addServices(sd);

                    try {
                        // Buscar agentes vendedores que proporcionen el servicio de venta de libros
                        DFAgentDescription[] result = DFService.search(myAgent, template);
                        System.out.println("Se encontraron los siguientes agentes vendedores:");
                        sellerAgents = new AID[result.length];
                        for (int i = 0; i < result.length; i++) {
                            sellerAgents[i] = result[i].getName();
                            System.out.println(sellerAgents[i].getName());
                        }

                    } catch (FIPAException fe) {
                        fe.printStackTrace();
                    }

                    // Agregar un comportamiento de ejecución de solicitud
                    myAgent.addBehaviour(new EjecutadorSolicitud(this_agent));
                }
            });
        } else {
            System.out.println("No se especificó un título de libro objetivo");
            doDelete(); // Terminar el agente si no se proporciona un título de libro
        }
    }

    // Método de finalización del agente
    protected void takeDown() {
        System.out.println("El agente comprador " + getAID().getName() + " está terminando");
    }

    // Getter para obtener la lista de identificadores de agentes vendedores
    public AID[] getSellerAgents() {
        return sellerAgents;
    }

    // Getter para obtener el título del libro objetivo
    public String getBookTitle() {
        return bookTitle;
    }
}

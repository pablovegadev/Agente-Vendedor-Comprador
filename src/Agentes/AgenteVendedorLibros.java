package Agentes;

import java.util.Hashtable;
import Behaviours.ServidorSolicitudOferta;
import Behaviours.ServidorOrdenCompra;
import gui.InterfazVendedorLibros;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class AgenteVendedorLibros extends Agent {
    // Variables de clase
    private Hashtable catalogue;           // Almacena el catálogo de libros disponibles
    private InterfazVendedorLibros gui;    // Interfaz gráfica para el agente vendedor

    // Método de configuración del agente
    protected void setup() {
        catalogue = new Hashtable();  // Inicializa el catálogo como una tabla hash vacía

        gui = new InterfazVendedorLibros(this);  // Crea una interfaz gráfica para el agente vendedor
        gui.showGui();  // Muestra la interfaz gráfica

        // Configura la descripción del agente para el registro en el servicio de directorio
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("book-selling");  // Establece el tipo de servicio como "book-selling"
        sd.setName("book-trading");  // Establece el nombre del servicio como "book-trading"
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);  // Registra el agente en el servicio de directorio
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Agrega comportamientos para servir solicitudes de oferta y órdenes de compra
        addBehaviour(new ServidorSolicitudOferta(this));
        addBehaviour(new ServidorOrdenCompra(this));
    }

    // Método de finalización del agente
    protected void takeDown() {
        try {
            DFService.deregister(this);  // Deregistra el agente del servicio de directorio
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        gui.dispose();  // Cierra la interfaz gráfica

        System.out.println("El agente vendedor " + getAID().getName() + " está terminando");
    }

    // Método para actualizar el catálogo de libros
    public void updateCatalogue(final String title, final int price) {
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                catalogue.put(title, price);  // Agrega el libro y su precio al catálogo
                System.out.println(title + " insertado con un precio de " + price);
            }
        });
    }

    // Método para obtener el catálogo de libros
    public Hashtable getCatalogue() {
        return catalogue;
    }
}

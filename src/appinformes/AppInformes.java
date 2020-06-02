package appinformes;

import static java.lang.System.exit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class AppInformes extends Application {

    public static Connection conexion = null;

    @Override
    public void start(Stage primaryStage) {
        //establecemos la conexión con la BD
        conectaBD();
        //Creamos la escena
        MenuBar menu = new MenuBar();
        Menu men1 = new Menu("Informes");
        Menu men2 = new Menu("Exit");
        MenuItem salir = new MenuItem("Salir");
        MenuItem opcion1 = new MenuItem("Listado Facturas");
        MenuItem opcion2 = new MenuItem("Ventas Totales");
        MenuItem opcion3 = new MenuItem("Facturas por Cliente");
        MenuItem opcion4 = new MenuItem("Subinforme Listado Facturas");

        men2.getItems().add(salir);
        men1.getItems().addAll(opcion1, opcion2, opcion3, opcion4);

        menu.getMenus().addAll(men1, men2);

        VBox root = new VBox();
        root.getChildren().add(menu);

        opcion1.setOnAction(e -> {
            generaInforme1();
            System.out.println("Generando Informe");
        });

        opcion2.setOnAction(e -> {
            generaInforme2();
            System.out.println("Generando Informe");
        });

        opcion3.setOnAction(e -> {
            Stage stage = new Stage();
            VBox stack = new VBox();
            TextField entrada = new TextField();
            Button confirmar = new Button("Generar");
            stack.getChildren().addAll(entrada, confirmar);
            stage.setTitle("Introducción de datos");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(stack));
            stage.show();
            confirmar.setOnAction((ActionEvent e1) -> {
                int parametro = Integer.parseInt(entrada.getText());
                generaInforme3(parametro);
                System.out.println("Generando Informe");
            });
        });

        opcion4.setOnAction(e -> {
            generaInforme4();
            System.out.println("Generando Informe");
        });

        salir.setOnAction(e -> {
            exit(1);
        });

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("APP Informes");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        try {
            DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9001/xdb1;shutdown=true");
        } catch (Exception ex) {
            System.out.println("No se pudo cerrar la conexion a la BD");
        }
    }

    public void conectaBD() {
        //Establecemos conexión con la BD
        String baseDatos = "jdbc:hsqldb:hsql://localhost:9001/xdb1";
        String usuario = "sa";
        String clave = "";
        try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            conexion = DriverManager.getConnection(baseDatos, usuario, clave);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Fallo al cargar JDBC");
            System.exit(1);
        } catch (SQLException sqle) {
            System.err.println("No se pudo conectar a BD");
            System.exit(1);
        } catch (java.lang.InstantiationException sqlex) {
            System.err.println("Imposible Conectar");
            System.exit(1);
        } catch (Exception ex) {
            System.err.println("Imposible Conectar");
            System.exit(1);
        }
    }

    public void generaInforme1() {
        try {
            JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("PedidosPorDocumento.jasper"));

            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, null, conexion);
            JasperViewer.viewReport(jp, false);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public void generaInforme2() {
        try {
            JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("Ventas_Totales.jasper"));

            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, null, conexion);
            JasperViewer.viewReport(jp, false);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public void generaInforme3(int parametro) {
        try {
            JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("PedidoPorCliente.jasper"));
            //Map de parámetros
            Map parametros = new HashMap();
            parametros.put("ID", parametro);

            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion);
            JasperViewer.viewReport(jp, false);
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    public void generaInforme4() {
        try {
            
            JasperReport jr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("Informe4.jasper"));
            JasperReport subjr = (JasperReport) JRLoader.loadObject(AppInformes.class.getResource("FacturasPorCliente.jasper"));
            //Map de parámetros
            Map parametros = new HashMap();
            parametros.put("subreportParameter", subjr);

            JasperPrint jp = (JasperPrint) JasperFillManager.fillReport(jr, parametros, conexion);
            JasperViewer.viewReport(jp, false);
            
        } catch (JRException ex) {
            System.out.println("Error al recuperar el jasper");
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

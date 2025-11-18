package vista;  // o el paquete que estés usando

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import modelo.ConexionBD;

public class Opciones extends JFrame {

    private String rol;

    // Componentes
    private JLabel lblTitulo;
    private JLabel lblSubtitulo;

    private JButton btnProductor;
    private JButton btnTecnico;
    private JButton btnFuncionario;
    private JButton btnLugarProduccion;
    private JButton btnLote;
    private JButton btnPredio;
    private JButton btnCultivo;
    private JButton btnInspeccion;
    private JButton btnObservaciones;
    private JButton btnPlaga;
    private JButton btnCerrarSesion;

    public Opciones(String rol) {
        this.rol = (rol == null) ? "" : rol.toUpperCase();
        initUI();
        configurarPorRol();
        setLocationRelativeTo(null); // centrar ventana
    }

    private void initUI() {
        setTitle("Menú de opciones");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 420);
        setResizable(false);

        // ==== PANEL PRINCIPAL ====
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(0xED, 0xDA, 0xC5)); // mismo color que usas
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        // ==== ENCABEZADO ====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        lblTitulo = new JLabel("Seleccione una opción");
        lblTitulo.setFont(new Font("Lucida Bright", Font.BOLD | Font.ITALIC, 22));

        lblSubtitulo = new JLabel("Has iniciado sesión como: " + rolBonito(rol));
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));

        header.add(lblTitulo, BorderLayout.NORTH);
        header.add(lblSubtitulo, BorderLayout.SOUTH);

        // ==== BOTONES (CENTRO) ====
        JPanel grid = new JPanel(new GridLayout(5, 2, 10, 10));
        grid.setOpaque(false);

        btnTecnico         = new JButton("Técnico");
        btnFuncionario     = new JButton("Funcionario ICA");
        btnLote            = new JButton("Lote");
        btnObservaciones   = new JButton("Observaciones");
        btnCultivo         = new JButton("Cultivo");
        btnProductor       = new JButton("Productor");
        btnPredio          = new JButton("Predio");
        btnPlaga           = new JButton("Plaga");
        btnInspeccion      = new JButton("Inspección fitosanitaria");
        btnLugarProduccion = new JButton("Lugar de producción");

        // Para que no quede tan cargado, metemos los más usados primero:
        grid.add(btnLote);
        grid.add(btnLugarProduccion);
        grid.add(btnInspeccion);
        grid.add(btnObservaciones);
        grid.add(btnCultivo);
        grid.add(btnPlaga);
        grid.add(btnPredio);
        grid.add(btnProductor);
        grid.add(btnTecnico);
        grid.add(btnFuncionario);
        // Técnico y Funcionario los manejamos más como administración,
        // pero puedes cambiarlos de sitio si quieres.

        // ==== PIE (botones especiales) ====
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        btnCerrarSesion = new JButton("Cerrar sesión");

        // lo alineamos a la derecha con un panel intermedio
        JPanel cerrarPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        cerrarPanel.setOpaque(false);
        cerrarPanel.add(btnCerrarSesion);

        footer.add(cerrarPanel, BorderLayout.CENTER);

        // ==== TOOLTIP BÁSICOS (ayuda al usuario) ====
        configurarTooltips();

        // ==== LISTENERS (aquí abrirías tus otras vistas) ====
        configurarAcciones();

        // ==== ARMAR TODO EN EL FRAME ====
        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(grid, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);
    }

    private void configurarPorRol() {
        // Por defecto: asumimos ADMIN → todo visible
        mostrarTodo();

        switch (rol) {
            case "ADMIN":
                lblSubtitulo.setText("Has iniciado sesión como: Administrador");
                // Admin ve todo
                break;

            case "TECNICO":
                lblSubtitulo.setText("Has iniciado sesión como: Técnico oficial");
                // Técnico:
                // consultar lotes, plagas, cultivos, predios,
                // CRUD inspección, generar reportes (desde inspecciones o menú aparte).

                btnProductor.setVisible(false);      // no administra productores
                btnTecnico.setVisible(false);        // no administra otros técnicos
                btnFuncionario.setVisible(false);    // no administra funcionarios
                // LugarProducción podrías dejarlo visible si lo usas en reportes,
                // aquí lo dejo solo lectura a través de reportes.
                btnLugarProduccion.setVisible(false);

                btnLote.setVisible(true);
                btnPredio.setVisible(true);
                btnCultivo.setVisible(true);
                btnInspeccion.setVisible(true);
                btnPlaga.setVisible(true);
                btnObservaciones.setVisible(false); // no maneja observaciones

                break;

            case "PRODUCTOR":
                lblSubtitulo.setText("Has iniciado sesión como: Productor");
                // Productor:
                // CRUD lote y registrar/consultar lugar de producción

                btnProductor.setVisible(false);    // no necesita administrar productores
                btnTecnico.setVisible(false);
                btnFuncionario.setVisible(false);
                btnObservaciones.setVisible(false);
                btnInspeccion.setVisible(false);
                btnCultivo.setVisible(false);
                btnPredio.setVisible(false);
                btnPlaga.setVisible(false);

                btnLote.setVisible(true);
                btnLugarProduccion.setVisible(true);
                break;

            case "FUNCIONARIO_ICA":
                lblSubtitulo.setText("Has iniciado sesión como: Funcionario ICA");
                // Funcionario ICA:
                // CRUD Observaciones, consultar inspecciones (+ contexto si quieres)

                btnProductor.setVisible(false);
                btnTecnico.setVisible(false);
                btnFuncionario.setVisible(false);
                btnLugarProduccion.setVisible(false);
                btnLote.setVisible(false);
                btnCultivo.setVisible(false);
                btnPredio.setVisible(false);
                btnPlaga.setVisible(false);

                btnInspeccion.setVisible(true);    // consulta
                btnObservaciones.setVisible(true); // CRUD
                break;

            default:
                lblSubtitulo.setText("Has iniciado sesión como: invitado");
                // Todo oculto por seguridad
                ocultarTodo();
        }

        // Si quieres, puedes cambiar el título de la ventana también:
        setTitle("Menú de opciones - " + rolBonito(rol));
    }

    private void mostrarTodo() {
        btnProductor.setVisible(true);
        btnTecnico.setVisible(true);
        btnFuncionario.setVisible(true);
        btnLugarProduccion.setVisible(true);
        btnLote.setVisible(true);
        btnPredio.setVisible(true);
        btnCultivo.setVisible(true);
        btnInspeccion.setVisible(true);
        btnObservaciones.setVisible(true);
        btnPlaga.setVisible(true);
    }

    private void ocultarTodo() {
        btnProductor.setVisible(false);
        btnTecnico.setVisible(false);
        btnFuncionario.setVisible(false);
        btnLugarProduccion.setVisible(false);
        btnLote.setVisible(false);
        btnPredio.setVisible(false);
        btnCultivo.setVisible(false);
        btnInspeccion.setVisible(false);
        btnObservaciones.setVisible(false);
        btnPlaga.setVisible(false);
    }

    private void configurarTooltips() {
        btnProductor.setToolTipText("Gestión de productores (solo admin).");
        btnTecnico.setToolTipText("Gestión de técnicos oficiales (solo admin).");
        btnFuncionario.setToolTipText("Gestión de funcionarios ICA (solo admin).");
        btnLugarProduccion.setToolTipText("Registrar y consultar lugares de producción.");
        btnLote.setToolTipText("Registrar y consultar lotes de cultivo.");
        btnPredio.setToolTipText("Consultar información de predios.");
        btnCultivo.setToolTipText("Gestión de cultivos.");
        btnInspeccion.setToolTipText("Registrar y consultar inspecciones fitosanitarias.");
        btnObservaciones.setToolTipText("Registrar y consultar observaciones sobre inspecciones.");
        btnPlaga.setToolTipText("Gestión de plagas registradas en el sistema.");
        btnCerrarSesion.setToolTipText("Cerrar sesión y volver a la pantalla de login.");
    }

    private void configurarAcciones() {
        // Aquí solo pongo ejemplos. Tú reemplazas los JOptionPane
        // con la apertura de tus vistas reales (Tabla, TablaLote, etc.).

        btnLote.addActionListener(e -> {
            TablaLote tablaLote = new TablaLote(rol); // ⬅ le paso el mismo rol
            tablaLote.setVisible(true);
            dispose();
        });

        btnLugarProduccion.addActionListener(e -> {
            TablaLugarPr tablaLugarPr = new TablaLugarPr(rol);
            tablaLugarPr.setVisible(true);
            dispose();
        });

        btnInspeccion.addActionListener(e -> {
            TablaInspeccion tablaInspeccion = new TablaInspeccion(rol);
            tablaInspeccion.setVisible(true);
            dispose();
        });

        btnObservaciones.addActionListener(e -> {
            TablaObservaciones tablaObservaciones = new TablaObservaciones(rol);
            tablaObservaciones.setVisible(true);
            dispose();
        });

        btnCultivo.addActionListener(e -> {
            TablaCultivo tablaCultivo = new TablaCultivo(rol);
            tablaCultivo.setVisible(true);
            dispose();
        });

        btnPredio.addActionListener(e -> {
            TablaPredio tablaPredio = new TablaPredio(rol);
            tablaPredio.setVisible(true);
            dispose();
        });

        btnPlaga.addActionListener(e -> {
            TablaPlaga tablaPlaga = new TablaPlaga(rol);
            tablaPlaga.setVisible(true);
            dispose();
        });

        btnProductor.addActionListener(e -> {
            Tabla tabla = new Tabla(rol);
            tabla.setVisible(true);
            dispose();
        });

        btnTecnico.addActionListener(e -> {
            TablaTecnico tablaTecnico = new TablaTecnico(rol);
            tablaTecnico.setVisible(true);
            dispose();
        });

        btnFuncionario.addActionListener(e -> {
            TablaFuncionarioICA tablaFuncionarioICA = new TablaFuncionarioICA(rol);
            tablaFuncionarioICA.setVisible(true);
            dispose();
        });

        btnCerrarSesion.addActionListener(e -> {
            int resp = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseas cerrar sesión?",
                    "Confirmar cierre de sesión",
                    JOptionPane.YES_NO_OPTION
            );
            if (resp == JOptionPane.YES_OPTION) {
                ConexionBD.reconfigurar("PROYECTOINTE", "proyectointe");
                new Login().setVisible(true); // cuando lo conectes
                dispose();
            }
        });
    }

    private String rolBonito(String rol) {
        if (rol == null) return "desconocido";
        switch (rol.toUpperCase()) {
            case "ADMIN":           return "Administrador";
            case "TECNICO":         return "Técnico oficial";
            case "PRODUCTOR":       return "Productor";
            case "FUNCIONARIO_ICA": return "Funcionario ICA";
            default:                return rol;
        }
    }

    // Solo para probar rápido esta vista:
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Cambia el rol aquí para probar diferentes vistas:
            // "ADMIN", "TECNICO", "PRODUCTOR", "FUNCIONARIO_ICA"
            new Opciones("ADMIN").setVisible(true);
        });
    }
}

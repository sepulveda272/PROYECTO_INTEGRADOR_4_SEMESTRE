/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import controlador.InspeccionFitosanitariaController;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import modelo.InspeccionFitosanitaria;
import javax.swing.*;
import javax.swing.table.*;
import java.util.List;
import modelo.ReporteInspeccionDTO;

// Para PDF (iText)
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


/**
 *
 * @author ESTUDIANTE
 */
class TablaInspeccion extends javax.swing.JFrame {
    private InspeccionFitosanitariaController inspeccionFitosanitariaController;
    
    private List<ReporteInspeccionDTO> listaReporte = new ArrayList<>();

    public TablaInspeccion() {
        initComponents();
        inspeccionFitosanitariaController = new InspeccionFitosanitariaController();
        setLocationRelativeTo(null);
        cargarInspecciones();
    }
    
    private void descargarInformeTxt() {
        if (listaReporte == null || listaReporte.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para descargar.");
            return;
        }

        String informe = generarTextoInforme();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar informe de inspecciones (TXT)");

        // Nombre por defecto con fecha
        String nombrePorDefecto = String.format(
                "informe_inspecciones_%s.txt",
                java.time.LocalDate.now()  // yyyy-MM-dd
        );
        fileChooser.setSelectedFile(new File(nombrePorDefecto));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = fileChooser.getSelectedFile();

        // Asegurar extensión .txt
        String nombre = archivo.getName();
        if (!nombre.toLowerCase().endsWith(".txt")) {
            archivo = new File(archivo.getParentFile(), nombre + ".txt");
        }

        // Confirmar si ya existe
        if (archivo.exists()) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "El archivo \"" + archivo.getName() + "\" ya existe.\n" +
                    "¿Deseas reemplazarlo?",
                    "Confirmar reemplazo",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }
        }

        // Guardar usando UTF-8
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(archivo), java.nio.charset.StandardCharsets.UTF_8))) {

            bw.write(informe);

            JOptionPane.showMessageDialog(
                    this,
                    "Informe guardado correctamente en:\n" + archivo.getAbsolutePath(),
                    "Informe guardado",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al guardar el informe: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    
    private void descargarInformePdf() {
        if (listaReporte.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para descargar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Informe (PDF)");
        fileChooser.setSelectedFile(new File("informe_inspecciones.pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        File archivo = fileChooser.getSelectedFile();

        try {
            // ====== Configuración básica del documento ======
            com.itextpdf.text.Document document = 
                    new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4.rotate()); // Horizontal
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(archivo));
            document.open();

            // ====== Fuentes ======
            com.itextpdf.text.Font tituloFont = com.itextpdf.text.FontFactory.getFont(
                    com.itextpdf.text.FontFactory.HELVETICA_BOLD, 16,
                    new com.itextpdf.text.BaseColor(40, 40, 40)
            );
            com.itextpdf.text.Font subTituloFont = com.itextpdf.text.FontFactory.getFont(
                    com.itextpdf.text.FontFactory.HELVETICA, 11,
                    new com.itextpdf.text.BaseColor(80, 80, 80)
            );
            com.itextpdf.text.Font headerFont = com.itextpdf.text.FontFactory.getFont(
                    com.itextpdf.text.FontFactory.HELVETICA_BOLD, 9,
                    com.itextpdf.text.BaseColor.WHITE
            );
            com.itextpdf.text.Font cellFont = com.itextpdf.text.FontFactory.getFont(
                    com.itextpdf.text.FontFactory.HELVETICA, 9,
                    new com.itextpdf.text.BaseColor(40, 40, 40)
            );

            // ====== Título principal ======
            com.itextpdf.text.Paragraph titulo = new com.itextpdf.text.Paragraph(
                    "INFORME DE INSPECCIONES FITOSANITARIAS", tituloFont);
            titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5f);
            document.add(titulo);

            // ====== Subtítulo: fecha y breve descripción ======
            String fecha = java.time.LocalDate.now().toString();
            int totalInspecciones = listaReporte.size();

            // Totales simples
            long totalRevisadas = 0;
            long totalAfectadas = 0;
            for (ReporteInspeccionDTO r : listaReporte) {
                totalRevisadas += r.getPlantasRevisadas();
                totalAfectadas += r.getPlantasAfectadas();
            }

            com.itextpdf.text.Paragraph subtitulo = new com.itextpdf.text.Paragraph(
                    "Fecha de generación: " + fecha +
                    "\nTotal de inspecciones: " + totalInspecciones +
                    " | Plantas revisadas: " + totalRevisadas +
                    " | Plantas afectadas: " + totalAfectadas,
                    subTituloFont
            );
            subtitulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(15f);
            document.add(subtitulo);

            // ====== Tabla PDF ======
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(8);
            table.setWidthPercentage(100); // ocupa el ancho de la página
            table.setSpacingBefore(10f);

            // Anchos relativos de columnas
            table.setWidths(new float[]{3f, 3f, 2.5f, 2.5f, 3f, 2f, 1.7f, 1.7f});

            // Color para encabezado y filas cebra
            com.itextpdf.text.BaseColor headerBg = new com.itextpdf.text.BaseColor(0, 121, 107);   // verde tipo “material”
            com.itextpdf.text.BaseColor rowAltBg = new com.itextpdf.text.BaseColor(245, 245, 245); // gris muy claro

            // Helper para crear celdas de encabezado
            java.util.function.Function<String, com.itextpdf.text.pdf.PdfPCell> headerCell = text -> {
                com.itextpdf.text.pdf.PdfPCell cell =
                        new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, headerFont));
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(headerBg);
                cell.setPadding(5f);
                return cell;
            };

            // Encabezados
            table.addCell(headerCell.apply("Productor"));
            table.addCell(headerCell.apply("Predio"));
            table.addCell(headerCell.apply("Cultivo"));
            table.addCell(headerCell.apply("Plaga"));
            table.addCell(headerCell.apply("Técnico"));
            table.addCell(headerCell.apply("F. Insp."));
            table.addCell(headerCell.apply("Revisadas"));
            table.addCell(headerCell.apply("Afectadas"));

            table.setHeaderRows(1); // la primera fila es encabezado

            // Helper para celdas de texto
            java.util.function.BiFunction<String, Boolean, com.itextpdf.text.pdf.PdfPCell> textCell =
                    (text, zebra) -> {
                        com.itextpdf.text.pdf.PdfPCell cell =
                                new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, cellFont));
                        cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
                        if (zebra) cell.setBackgroundColor(rowAltBg);
                        cell.setPadding(4f);
                        return cell;
                    };

            // Helper para celdas numéricas
            java.util.function.BiFunction<String, Boolean, com.itextpdf.text.pdf.PdfPCell> numberCell =
                    (text, zebra) -> {
                        com.itextpdf.text.pdf.PdfPCell cell =
                                new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(text, cellFont));
                        cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                        cell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
                        if (zebra) cell.setBackgroundColor(rowAltBg);
                        cell.setPadding(4f);
                        return cell;
                    };

            // Filas de datos con efecto “cebra”
            boolean zebra = false;
            for (ReporteInspeccionDTO r : listaReporte) {
                String fechaInsp = (r.getFechaInspeccion() != null)
                        ? r.getFechaInspeccion().toString()
                        : "";

                table.addCell(textCell.apply(nvl(r.getProductor()), zebra));
                table.addCell(textCell.apply(nvl(r.getPredio()), zebra));
                table.addCell(textCell.apply(nvl(r.getCultivo()), zebra));
                table.addCell(textCell.apply(nvl(r.getPlaga()), zebra));
                table.addCell(textCell.apply(nvl(r.getTecnico()), zebra));
                table.addCell(textCell.apply(fechaInsp, zebra));
                table.addCell(numberCell.apply(String.valueOf(r.getPlantasRevisadas()), zebra));
                table.addCell(numberCell.apply(String.valueOf(r.getPlantasAfectadas()), zebra));

                zebra = !zebra; // alterna el color para la siguiente fila
            }

            document.add(table);

            // ====== Pie del informe opcional ======
            com.itextpdf.text.Paragraph footer = new com.itextpdf.text.Paragraph(
                    "\nInforme generado automáticamente por el sistema de inspecciones fitosanitarias.",
                    subTituloFont
            );
            footer.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            footer.setSpacingBefore(10f);
            document.add(footer);

            document.close();

            JOptionPane.showMessageDialog(this, "PDF generado correctamente:\n" + archivo.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + e.getMessage());
        }
    }


    private String nvl(String s) {
        return s == null ? "" : s;
    }
    
    private void cargarDatosReporte() {
        try {
            // De momento: sin filtros → todo el historial
            // Ajusta la firma de este método según como lo crees en tu controller
            listaReporte = inspeccionFitosanitariaController.listarHistorialReporte(
                    null, // idProductor
                    null, // idPredio
                    null, // idCultivo
                    null, // idPlaga
                    null, // idTecnico
                    null, // fechaDesde
                    null  // fechaHasta
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar datos del informe: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            listaReporte = new ArrayList<>();
        }
    }
    
    private String generarTextoInforme() {
        if (listaReporte == null || listaReporte.isEmpty()) {
            return "No hay inspecciones para mostrar.\n";
        }

        StringBuilder informe = new StringBuilder();

        String margen = "  "; // margen izquierdo

        // ====== DEFINIR CABECERA Y ANCHO REAL ======
        String header = String.format(
                "%-18s | %-15s | %-18s | %-15s | %-15s | %-12s | %10s | %10s",
                "Productor", "Predio", "Cultivo", "Plaga", "Técnico",
                "F. Insp.", "Revisadas", "Afectadas"
        );

        int anchoUtil = header.length();          // <-- este es el ancho exacto
        String linea      = repetir('=', anchoUtil);
        String lineaMedia = repetir('-', anchoUtil);

        // ====== ENCABEZADO ======
        informe.append(margen).append(linea).append("\n");
        informe.append(margen)
               .append(centrarTexto("INFORME DE INSPECCIONES FITOSANITARIAS", anchoUtil))
               .append("\n");
        informe.append(margen).append(lineaMedia).append("\n");
        informe.append(margen)
               .append("Fecha de generación: ")
               .append(java.time.LocalDate.now())
               .append("\n");

        // ====== RESUMEN GENERAL ======
        int totalInspecciones = listaReporte.size();
        long totalRevisadas = 0;
        long totalAfectadas = 0;

        for (ReporteInspeccionDTO r : listaReporte) {
            totalRevisadas += r.getPlantasRevisadas();
            totalAfectadas += r.getPlantasAfectadas();
        }

        double porcentajeAfectadas = (totalRevisadas > 0)
                ? (totalAfectadas * 100.0 / totalRevisadas)
                : 0.0;

        informe.append(margen).append(String.format(
                "Total inspecciones: %d   |   Plantas revisadas: %d   |   Plantas afectadas: %d (%.1f%%)%n",
                totalInspecciones, totalRevisadas, totalAfectadas, porcentajeAfectadas
        ));
        informe.append(margen).append(lineaMedia).append("\n\n");

        // ====== CABECERA DE TABLA ======
        informe.append(margen).append(header).append("\n");
        informe.append(margen).append(lineaMedia).append("\n");

        // ====== FILAS ======
        for (ReporteInspeccionDTO r : listaReporte) {
            String fecha = (r.getFechaInspeccion() != null)
                    ? r.getFechaInspeccion().toString()
                    : "";

            String fila = String.format(
                    "%-18s | %-15s | %-18s | %-15s | %-15s | %-12s | %10d | %10d",
                    cortar(nvl(r.getProductor()), 18),
                    cortar(nvl(r.getPredio()), 15),
                    cortar(nvl(r.getCultivo()), 18),
                    cortar(nvl(r.getPlaga()), 15),
                    cortar(nvl(r.getTecnico()), 15),
                    fecha,
                    r.getPlantasRevisadas(),
                    r.getPlantasAfectadas()
            );

            informe.append(margen).append(fila).append("\n");
        }

        informe.append("\n");
        informe.append(margen).append(linea).append("\n");
        informe.append(margen)
               .append("Informe generado automáticamente por el sistema de inspecciones.\n");

        return informe.toString();
    }

    /** Repite un carácter n veces. */
    private String repetir(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    /** Centra un texto dentro de un ancho dado (para el título). */
    private String centrarTexto(String texto, int ancho) {
        if (texto == null) texto = "";
        if (texto.length() >= ancho) return texto;

        int espacios = (ancho - texto.length()) / 2;
        StringBuilder sb = new StringBuilder(ancho);
        for (int i = 0; i < espacios; i++) sb.append(' ');
        sb.append(texto);
        return sb.toString();
    }

    /** Corta el texto y agrega "..." si es muy largo. */
    private String cortar(String s, int max) {
        if (s == null) return "";
        if (s.length() <= max) return s;
        if (max <= 3) return s.substring(0, max); // caso extremo
        return s.substring(0, max - 3) + "...";
    }

    
     private void cargarInspecciones() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Fecha");
        modelo.addColumn("Revisadas");
        modelo.addColumn("Afectadas");
        modelo.addColumn("Nivel alerta");
        modelo.addColumn("N° Lote");
        modelo.addColumn("Técnico");

        List<InspeccionFitosanitaria> lista = inspeccionFitosanitariaController.listarConTecnicoEIncidencia();
        for (InspeccionFitosanitaria i : lista) {
            // Incidencia en UI (si tu POJO no la tiene):
            double inc = 0.0;
            if (i.getPlantas_revisadas() > 0) {
                inc = (i.getPlantas_afectadas() * 100.0) / i.getPlantas_revisadas();
            }
            Object[] fila = {
                i.getId_inspeccion(),
                i.getFecha_inspeccion(),         // "yyyy-MM-dd"
                i.getPlantas_revisadas(),
                i.getPlantas_afectadas(),
                i.getNivel_alerta(),
                i.getNumero_lote(),
                i.getNombre_tecnico()            // viene del JOIN del DAO
            };
            modelo.addRow(fila);
        }
        jTable1.setModel(modelo);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        botoncrear = new javax.swing.JButton();
        botoneditar = new javax.swing.JButton();
        botoneliminar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ver_informe = new javax.swing.JButton();
        descargar = new javax.swing.JButton();
        volver = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 238, 208));

        jPanel1.setBackground(new java.awt.Color(237, 218, 197));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 87, 830, 360));

        botoncrear.setText("Crear");
        botoncrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoncrearActionPerformed(evt);
            }
        });
        jPanel1.add(botoncrear, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 460, 150, 40));

        botoneditar.setText("Editar");
        botoneditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoneditarActionPerformed(evt);
            }
        });
        jPanel1.add(botoneditar, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 460, 150, 40));

        botoneliminar.setText("Eliminar");
        botoneliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botoneliminarActionPerformed(evt);
            }
        });
        jPanel1.add(botoneliminar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 460, 150, 40));

        jLabel1.setFont(new java.awt.Font("Cambria", 3, 48)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 153, 0));
        jLabel1.setText("Inspecciones fitosanitarias");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 10, 580, 60));

        ver_informe.setText("Ver informe");
        ver_informe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ver_informeActionPerformed(evt);
            }
        });
        jPanel1.add(ver_informe, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 460, 150, 40));

        descargar.setText("Descargar reporte");
        descargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descargarActionPerformed(evt);
            }
        });
        jPanel1.add(descargar, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 460, 150, 40));

        volver.setText("Volver a el menu");
        volver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volverActionPerformed(evt);
            }
        });
        jPanel1.add(volver, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 130, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botoncrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoncrearActionPerformed
        CrearInspeccion crear = new CrearInspeccion(this::cargarInspecciones);
        crear.setVisible(true);
    }//GEN-LAST:event_botoncrearActionPerformed

    private void botoneditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoneditarActionPerformed
        int viewRow = jTable1.getSelectedRow();
        if (viewRow == -1) { JOptionPane.showMessageDialog(this, "Seleccione una inspección."); return; }

        int row = jTable1.convertRowIndexToModel(viewRow);
        int id            = Integer.parseInt(jTable1.getModel().getValueAt(row, 0).toString());
        String fecha      = String.valueOf(jTable1.getModel().getValueAt(row, 1));
        int revisadas     = Integer.parseInt(jTable1.getModel().getValueAt(row, 2).toString());
        int afectadas     = Integer.parseInt(jTable1.getModel().getValueAt(row, 3).toString());
        String nivel      = String.valueOf(jTable1.getModel().getValueAt(row, 5)); // solo informativo
        int numeroLote    = Integer.parseInt(jTable1.getModel().getValueAt(row, 6).toString());
        String nombreTec  = String.valueOf(jTable1.getModel().getValueAt(row, 7));

         ActualizarInspeccion actualizar = new ActualizarInspeccion(
            id, revisadas, afectadas, fecha, numeroLote, /* idTecnico: lo resolvemos por combo */ 0,
            this::cargarInspecciones
        );
        actualizar.setVisible(true);
    }//GEN-LAST:event_botoneditarActionPerformed

    private void botoneliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botoneliminarActionPerformed
        int viewRow = jTable1.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una inspección.");
            return;
        }

        int row = jTable1.convertRowIndexToModel(viewRow);
        int id = Integer.parseInt(jTable1.getModel().getValueAt(row, 0).toString());

        int res = JOptionPane.showConfirmDialog(this, "¿Eliminar esta inspección?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (res == JOptionPane.YES_OPTION) {

            String error = inspeccionFitosanitariaController.eliminar(id);

            if (error == null) {
                JOptionPane.showMessageDialog(this, "✅ Inspección eliminada.");
                cargarInspecciones();
            } else {
                JOptionPane.showMessageDialog(this, "❌ No se pudo eliminar. Verifique dependencias (OBSERVACION).");
            }
        }
    }//GEN-LAST:event_botoneliminarActionPerformed

    private void ver_informeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ver_informeActionPerformed
        // TODO add your handling code here:
        // 1. Traer datos desde BD
        cargarDatosReporte();

        // 2. Validar
        if (listaReporte == null || listaReporte.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para generar el informe.");
            return;
        }

        String informe = generarTextoInforme();

        JTextArea area = new JTextArea(informe);
        area.setEditable(false);
        area.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12)); // <-- CLAVE
        area.setCaretPosition(0); // empieza arriba

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new java.awt.Dimension(900, 500));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                "Informe de inspecciones",
                JOptionPane.PLAIN_MESSAGE  // sin icono azul
        );
    }//GEN-LAST:event_ver_informeActionPerformed

    private void descargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descargarActionPerformed
        // TODO add your handling code here:
        cargarDatosReporte();
        if (listaReporte == null || listaReporte.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para descargar.");
            return;
        }

        Object[] opciones = {"TXT", "PDF"};
        int sel = JOptionPane.showOptionDialog(
                this,
                "¿En qué formato quieres descargar el informe?",
                "Formato de exportación",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        switch (sel) {
            case 0: // TXT
                descargarInformeTxt();   // el método de arriba
                break;
            case 1: // PDF
                descargarInformePdf();   // el que te propuse antes con iText
                break;
            default:
                // canceló
        }
    }//GEN-LAST:event_descargarActionPerformed

    private void volverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volverActionPerformed
        // TODO add your handling code here:
        Opciones opciones = new Opciones();
        opciones.setVisible(true);
        dispose();
    }//GEN-LAST:event_volverActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TablaInspeccion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TablaInspeccion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TablaInspeccion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TablaInspeccion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TablaInspeccion().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botoncrear;
    private javax.swing.JButton botoneditar;
    private javax.swing.JButton botoneliminar;
    private javax.swing.JButton descargar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton ver_informe;
    private javax.swing.JButton volver;
    // End of variables declaration//GEN-END:variables
}

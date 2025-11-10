/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package vista;

import controlador.LoteController;
import modelo.CultivoDAO;              // ajusta el paquete si difiere
import modelo.LugarProduccionDAO;     // ajusta el paquete si difiere
import modelo.Cultivo;
import modelo.LugarProduccion;

import javax.swing.*;
import java.util.List;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 *
 * @author omaci
 */
public class ActualizarLote extends javax.swing.JFrame {

    /**
     * Creates new form CrearCliente
     */
  
    private Runnable onLoteActualizado;
    private java.util.List<Integer> cultivoIds = new java.util.ArrayList<>();
    private java.util.List<Integer> lugarIds   = new java.util.ArrayList<>();
    
    private static final DateTimeFormatter FMT_YMD =
        DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private boolean validarFechaCampo(JTextField campo, String etiqueta) {
        String s = campo.getText().trim();
        if (s.isEmpty()) return true; // permitir vacío en fecha_eliminacion
        try {
            LocalDate.parse(s, FMT_YMD);
            return true;
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                "Formato inválido en " + etiqueta + ": \"" + s + "\".\n" +
                "Usa yyyy-MM-dd (ej. 2025-11-09).",
                "Fecha inválida",
                JOptionPane.WARNING_MESSAGE
            );
            campo.requestFocus();
            return false;
        }
    }
    
    private static final String PLACEHOLDER = "— Selecciona estado —";

    private void cargarEstadosFenologicos() {
        jComboBox1.removeAllItems();
        jComboBox1.addItem(PLACEHOLDER);              // índice 0 => placeholder
        jComboBox1.addItem("Siembra");
        jComboBox1.addItem("Germinación");
        jComboBox1.addItem("Crecimiento vegetativo");
        jComboBox1.addItem("Floración");
        jComboBox1.addItem("Fructificación");
        jComboBox1.addItem("Cosecha");
        jComboBox1.addItem("Reposo");
        jComboBox1.setSelectedIndex(0);
    }
  
    public ActualizarLote(int numeroLote, double areaTotal, double areaSiembra, String estadoFenologico,
                      String fechaSiembra, String fechaEliminacion, int idCultivo, int idLugar,
                      Runnable onLoteActualizado) {
        initComponents();
        setLocationRelativeTo(null);
        this.onLoteActualizado = onLoteActualizado;

        // Cargar combos y preseleccionar el id que llega
        cargarCombosSeleccionando(idCultivo, idLugar);
        
        cargarEstadosFenologicos();

        if (estadoFenologico != null) {
            boolean encontrado = false;
            for (int i = 0; i < jComboBox1.getItemCount(); i++) {
                if (estadoFenologico.equalsIgnoreCase(jComboBox1.getItemAt(i))) {
                    jComboBox1.setSelectedIndex(i);
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                // Si el valor no está en el catálogo (caso raro), lo insertamos temporalmente
                jComboBox1.addItem(estadoFenologico);
                jComboBox1.setSelectedItem(estadoFenologico);
            }
        } else {
            jComboBox1.setSelectedIndex(0);
        }
        
        txtNumeroLote.setText(String.valueOf(numeroLote));
        txtNumeroLote.setEditable(false);
        txtAreaTotal.setText(String.valueOf(areaTotal));
        txtAreaSiembra.setText(String.valueOf(areaSiembra));
        txtFechaSiembra.setText(fechaSiembra != null ? fechaSiembra : "");
        txtFechaEliminacion.setText(fechaEliminacion != null ? fechaEliminacion : "");
    }
    
    private void cargarCombosSeleccionando(int idCultivoSel, int idLugarSel) {
        // Limpiar
        cboCultivo.removeAllItems(); cboLugar.removeAllItems();
        cultivoIds.clear(); lugarIds.clear();

        // Cultivos
        CultivoDAO cdao = new CultivoDAO();
        java.util.List<Cultivo> cultivos = cdao.listarCultivos();
        int idxSelC = -1, idx = 0;
        for (Cultivo c : cultivos) {
            String label = c.getNombre_especie() + " (ID " + c.getId_cultivo() + ")";
            cboCultivo.addItem(label);
            cultivoIds.add(c.getId_cultivo());
            if (c.getId_cultivo() == idCultivoSel) idxSelC = idx;
            idx++;
        }
        if (idxSelC >= 0) cboCultivo.setSelectedIndex(idxSelC);

        // Lugares
        LugarProduccionDAO ldao = new LugarProduccionDAO();
        java.util.List<LugarProduccion> lugares = ldao.listarLugaresProduccion();
        int idxSelL = -1; idx = 0;
        for (LugarProduccion l : lugares) {
            String label = l.getDepartamento() + " (ID " + l.getId_lugar() + ")";
            cboLugar.addItem(label);
            lugarIds.add(l.getId_lugar());
            if (l.getId_lugar() == idLugarSel) idxSelL = idx;
            idx++;
        }
        if (idxSelL >= 0) cboLugar.setSelectedIndex(idxSelL);
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
        jButton1 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        txtNumeroLote = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtAreaTotal = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtAreaSiembra = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtFechaSiembra = new javax.swing.JTextField();
        txtFechaEliminacion = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        cboCultivo = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        cboLugar = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 238, 208));

        jPanel1.setBackground(new java.awt.Color(237, 218, 197));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setText("Actualizar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 460, 180, 60));

        jLabel12.setFont(new java.awt.Font("Cambria", 3, 48)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 153, 0));
        jLabel12.setText("Actualizar lote");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 0, 320, 60));

        txtNumeroLote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumeroLoteActionPerformed(evt);
            }
        });
        jPanel1.add(txtNumeroLote, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, 180, 40));

        jLabel8.setText("Numero de lote");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, -1, -1));

        jLabel5.setText("Area total");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 90, -1, -1));

        txtAreaTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAreaTotalActionPerformed(evt);
            }
        });
        jPanel1.add(txtAreaTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 110, 180, 40));

        jLabel7.setText("Area de siembra");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 170, -1, -1));

        txtAreaSiembra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAreaSiembraActionPerformed(evt);
            }
        });
        jPanel1.add(txtAreaSiembra, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 190, 180, 40));

        jLabel9.setText("Estado fenologico");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, -1, -1));

        jLabel10.setText("Fecha de siembra");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 260, -1, -1));

        txtFechaSiembra.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFechaSiembraActionPerformed(evt);
            }
        });
        jPanel1.add(txtFechaSiembra, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 280, 180, 40));

        txtFechaEliminacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFechaEliminacionActionPerformed(evt);
            }
        });
        jPanel1.add(txtFechaEliminacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 180, 40));

        jLabel11.setText("Fecha de eliminacion");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, -1, -1));

        jPanel1.add(cboCultivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 370, 180, 40));

        jLabel13.setText("Cultivo");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 350, -1, -1));

        jPanel1.add(cboLugar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 370, 180, 40));

        jLabel6.setText("Lugar de produccion");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 350, -1, -1));

        jPanel1.add(jComboBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 190, 180, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 472, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    try {
        int numeroLote   = Integer.parseInt(txtNumeroLote.getText().trim());
        double areaTotal = Double.parseDouble(txtAreaTotal.getText().trim());
        double areaSiemb = Double.parseDouble(txtAreaSiembra.getText().trim());
        String estadoFenologico = (String) jComboBox1.getSelectedItem();
        String fSiembra  = txtFechaSiembra.getText().trim();
        String fElim     = txtFechaEliminacion.getText().trim();

        int idxCultivo = cboCultivo.getSelectedIndex();
        int idxLugar   = cboLugar.getSelectedIndex();
        
        if (estadoFenologico == null || estadoFenologico.equals(PLACEHOLDER)) {
            JOptionPane.showMessageDialog(this, "Selecciona el estado fenológico.");
            jComboBox1.requestFocus();
            return;
        }
        
        if (!validarFechaCampo(txtFechaSiembra, "Fecha de siembra")) return;
        if (!validarFechaCampo(txtFechaEliminacion, "Fecha de eliminación")) return;
        
        if (areaTotal <= 0) {
            JOptionPane.showMessageDialog(this, "El área total debe ser mayor que 0.");
            return;
        }
        if (areaSiemb < 0) {
            JOptionPane.showMessageDialog(this, "El área de siembra no puede ser negativa.");
            return;
        }
        if (areaSiemb > areaTotal) {
            JOptionPane.showMessageDialog(this,
                "El área de siembra no puede ser mayor que el área total.\n" +
                "Revísalo: siembra = " + areaSiemb + " | total = " + areaTotal);
            return;
        }
        
        if (idxCultivo < 0 || idxLugar < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, "Seleccione cultivo y lugar.");
            return;
        }
        int idCultivo = cultivoIds.get(idxCultivo);
        int idLugar   = lugarIds.get(idxLugar);

        LoteController ctrl = new LoteController();
        boolean ok = ctrl.actualizarLote(
            numeroLote, areaTotal, areaSiemb, estadoFenologico, fSiembra,
            fElim.isEmpty() ? null : fElim,
            idCultivo, idLugar
        );

        if (ok) {
            javax.swing.JOptionPane.showMessageDialog(this, "✅ Lote actualizado correctamente.");
            if (onLoteActualizado != null) onLoteActualizado.run();
            dispose();
        } else {
            javax.swing.JOptionPane.showMessageDialog(this, "❌ Error al actualizar el lote.");
        }
    } catch (NumberFormatException nfe) {
        javax.swing.JOptionPane.showMessageDialog(this, "Áreas deben ser numéricas (usa punto decimal).");
    } catch (IllegalArgumentException iae) {
        javax.swing.JOptionPane.showMessageDialog(this, iae.getMessage());
    } catch (Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(this, "Ocurrió un error al actualizar el lote.");
        ex.printStackTrace();
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtNumeroLoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumeroLoteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNumeroLoteActionPerformed

    private void txtAreaTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAreaTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAreaTotalActionPerformed

    private void txtAreaSiembraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAreaSiembraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAreaSiembraActionPerformed

    private void txtFechaSiembraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFechaSiembraActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaSiembraActionPerformed

    private void txtFechaEliminacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFechaEliminacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFechaEliminacionActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ActualizarLote(0,0.0,0.0,"","","",0,0,null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cboCultivo;
    private javax.swing.JComboBox<String> cboLugar;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtAreaSiembra;
    private javax.swing.JTextField txtAreaTotal;
    private javax.swing.JTextField txtFechaEliminacion;
    private javax.swing.JTextField txtFechaSiembra;
    private javax.swing.JTextField txtNumeroLote;
    // End of variables declaration//GEN-END:variables
}

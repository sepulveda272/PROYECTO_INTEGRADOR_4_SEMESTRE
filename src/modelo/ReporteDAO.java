/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.ConexionBD;
/**
 *
 * @author ADMIN
 */
public class ReporteDAO {
    private final Connection conexion;

    public ReporteDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /**
     * Parámetros de filtro:
     * - idProductor, idPredio, idCultivo, idPlaga, idTecnico pueden ser null si no se quieren filtrar.
     * - fechaDesde / fechaHasta pueden ser null (no filtra fechas).
     */
    public List<ReporteInspeccionDTO> listarHistorial(
            Integer idProductor,
            Integer idPredio,
            Integer idCultivo,
            Integer idPlaga,
            Integer idTecnico,
            java.time.LocalDate fechaDesde,
            java.time.LocalDate fechaHasta
    ) throws SQLException {

        List<ReporteInspeccionDTO> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("   p.primer_nombre || ' ' || p.primer_apellido AS productor, ")
           .append("   pr.nombre_predio AS predio, ")
           .append("   c.nombre_especie || ' ' || c.variedad AS cultivo, ")
           .append("   pl.nombre_comun AS plaga, ")
           .append("   t.primer_nombre || ' ' || t.primer_apellido AS tecnico, ")
           .append("   i.fecha_inspeccion, ")
           .append("   i.plantas_revisadas, ")
           .append("   i.plantas_afectadas, ")
           .append("   i.nivel_alerta, ")
           .append("   lp.municipio, ")
           .append("   lp.vereda ")
           .append("FROM inspeccion_fitosanitaria i ")
           .append("JOIN lote l              ON l.numero_lote = i.numero_lote ")
           .append("JOIN lugar_produccion lp ON lp.id_lugar = l.id_lugar ")
           .append("JOIN productor p         ON p.id_productor = lp.id_productor ")
           .append("JOIN predio pr           ON pr.id_lugar = lp.id_lugar ")
           .append("JOIN cultivo c           ON c.id_cultivo = l.id_cultivo ")
           .append("LEFT JOIN afectado a     ON a.numero_lote = l.numero_lote ")
           .append("LEFT JOIN plagas pl      ON pl.id_plaga = a.id_plaga ")
           .append("JOIN tecnico_oficial t   ON t.numero_registro = i.id_tecnico ")
           .append("WHERE 1 = 1 ");

        // Filtros opcionales
        List<Object> params = new ArrayList<>();

        if (idProductor != null) {
            sql.append(" AND p.id_productor = ? ");
            params.add(idProductor);
        }
        if (idPredio != null) {
            sql.append(" AND pr.id_predio = ? ");
            params.add(idPredio);
        }
        if (idCultivo != null) {
            sql.append(" AND c.id_cultivo = ? ");
            params.add(idCultivo);
        }
        if (idPlaga != null) {
            sql.append(" AND pl.id_plaga = ? ");
            params.add(idPlaga);
        }
        if (idTecnico != null) {
            sql.append(" AND t.numero_registro = ? ");
            params.add(idTecnico);
        }
        if (fechaDesde != null) {
            sql.append(" AND i.fecha_inspeccion >= ? ");
            params.add(java.sql.Date.valueOf(fechaDesde));
        }
        if (fechaHasta != null) {
            sql.append(" AND i.fecha_inspeccion <= ? ");
            params.add(java.sql.Date.valueOf(fechaHasta));
        }

        sql.append(" ORDER BY i.fecha_inspeccion DESC");

        try (PreparedStatement ps = conexion.prepareStatement(sql.toString())) {

            // set de parámetros en orden
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof java.sql.Date) {
                    ps.setDate(i + 1, (java.sql.Date) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else {
                    ps.setObject(i + 1, param);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteInspeccionDTO dto = new ReporteInspeccionDTO();

                    dto.setProductor(rs.getString("productor"));
                    dto.setPredio(rs.getString("predio"));
                    dto.setCultivo(rs.getString("cultivo"));
                    dto.setPlaga(rs.getString("plaga"));
                    dto.setTecnico(rs.getString("tecnico"));
                    dto.setFechaInspeccion(
                            rs.getDate("fecha_inspeccion").toLocalDate()
                    );
                    dto.setPlantasRevisadas(rs.getInt("plantas_revisadas"));
                    dto.setPlantasAfectadas(rs.getInt("plantas_afectadas"));
                    dto.setNivelAlerta(rs.getString("nivel_alerta"));
                    dto.setMunicipio(rs.getString("municipio"));
                    dto.setVereda(rs.getString("vereda"));

                    lista.add(dto);
                }
            }
        }

        return lista;
    }
}

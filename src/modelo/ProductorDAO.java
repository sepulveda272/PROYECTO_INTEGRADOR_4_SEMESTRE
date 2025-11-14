/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
import modelo.ConexionBD;
/**
 *
 * @author ADMIN
 */
public class ProductorDAO {
    private final Connection conexion;

    public ProductorDAO() {
        this.conexion = ConexionBD.getInstancia().getConnection();
    }

    /* ===================== SECUENCIA ===================== */
    /** Obtiene el siguiente id_productor desde la secuencia Oracle. 
     *  OJO: estás usando seq_producto; si renombraste a seq_productor, cambia aquí.
     */
    //opcional esta chimbada xd//
    public int siguienteIdProductor() {
        final String sql = "{ ? = call fn_next_productor_id() }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.INTEGER);
            cs.execute();
            return cs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error obteniendo NEXTVAL (función): " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }


    /* ===================== AUTH / LOGIN (BCrypt) ===================== */
    /** Retorna ID_PRODUCTOR si credenciales OK y ACTIVO; si no, null. */
    public Integer validarProductor(String correo, String passwordPlano) {
        final String sql = "{ call sp_get_login_productor(?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setString(1, correo);
            cs.registerOutParameter(2, Types.INTEGER); // p_id
            cs.registerOutParameter(3, Types.VARCHAR); // p_hash
            cs.registerOutParameter(4, Types.VARCHAR); // p_estado
            cs.execute();

            Integer id = (Integer) cs.getObject(2);
            String hash = cs.getString(3);
            String estado = cs.getString(4);

            if (id != null && hash != null && "ACTIVO".equalsIgnoreCase(estado)
                    && BCrypt.checkpw(passwordPlano, hash)) {
                return id;
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Error al validar productor (SP): " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    /* ===================== CREATE ===================== */
    /** Inserta usando el id_productor recibido; hashea la contraseña. */
    public boolean insertarProductor(Productor p) {
        final String sql = "{ call sp_insertar_productor_con_id(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1,  p.getId_productor());
            cs.setLong(2, p.getNumero_identificacion());
            cs.setString(3, p.getTipo_identificacion());
            cs.setString(4, p.getPrimer_nombre());

            // Normaliza NOT NULL
            String segNom = isBlank(p.getSegundo_nombre()) ? "N/A" : p.getSegundo_nombre();
            String segApe = isBlank(p.getSegundo_apellido()) ? "N/A" : p.getSegundo_apellido();

            cs.setString(5, segNom);
            cs.setString(6, p.getPrimer_apellido());
            cs.setString(7, segApe);
            cs.setString(8, p.getDireccion());
            cs.setLong(9,  p.getCelular());
            cs.setString(10, p.getCorreo());

            String hash = BCrypt.hashpw(p.getPassword(), BCrypt.gensalt(12));
            cs.setString(11, hash);

            cs.setString(12, isBlank(p.getEstado()) ? "ACTIVO" : p.getEstado());

            cs.registerOutParameter(13, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(13) > 0;

        } catch (SQLException e) {
            String msg = (e.getMessage() == null ? "" : e.getMessage().toUpperCase());
            if (msg.contains("ORA-00001") || msg.contains("UQ_PROD_IDENT")) {
                System.err.println("❌ Ya existe un productor con ese numero_identificacion/correo.");
            } else {
                System.err.println("Error al insertar productor (SP con ID): " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }


    /** Inserta autogenerando ID con secuencia. Retorna el ID generado (>0) o -1 si falla. */
    /** Inserta usando el procedimiento almacenado. Retorna el ID generado (>0) o -1 si falla. */
    public int insertarProductorAuto(Productor p) {
        String sql = "{ call sp_insertar_productor(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";

        try (CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setLong(1, p.getNumero_identificacion());
            cs.setString(2, p.getTipo_identificacion());
            cs.setString(3, p.getPrimer_nombre());
            cs.setString(4, p.getSegundo_nombre());   // OJO: tu columna es NOT NULL
            cs.setString(5, p.getPrimer_apellido());
            cs.setString(6, p.getSegundo_apellido()); // NOT NULL también
            cs.setString(7, p.getDireccion());
            cs.setLong(8, p.getCelular());
            cs.setString(9, p.getCorreo());

            // Hash de contraseña en Java
            String hash = BCrypt.hashpw(p.getPassword(), BCrypt.gensalt(12));
            cs.setString(10, hash);

            // Normalmente "ACTIVO"
            cs.setString(11, p.getEstado());

            // OUT: ID generado por el SP
            cs.registerOutParameter(12, Types.INTEGER);

            cs.execute();

            int nuevoId = cs.getInt(12);
            p.setId_productor(nuevoId);
            return nuevoId;

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toUpperCase().contains("UQ_PROD_IDENT")) {
                System.err.println("❌ Ya existe un productor con ese numero_identificacion.");
            } else {
                System.err.println("Error al insertar productor (SP): " + e.getMessage());
            }
            e.printStackTrace();
            return -1;
        }
    }

    /* ===================== READ ===================== */
    /** Lista solo ACTIVO; no expone password. */
    public List<Productor> listarProductoresActivos() {
        List<Productor> lista = new ArrayList<>();
        final String sql = "{ call sp_listar_productores_activos(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.REF_CURSOR); // si falla, usa OracleTypes.CURSOR
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapRowSinPassword(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando productores activos (SP): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    public List<Productor> listarProductores() {
        List<Productor> lista = new ArrayList<>();
        final String sql = "{ call sp_listar_productores(?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.registerOutParameter(1, Types.REF_CURSOR); // si falla, usa OracleTypes.CURSOR
            cs.execute();
            try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                while (rs.next()) lista.add(mapRowSinPassword(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error listando productores (SP): " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }


    private Productor mapRowSinPassword(ResultSet rs) throws SQLException {
        Productor p = new Productor();
        p.setId_productor(rs.getInt("ID_PRODUCTOR"));
        p.setNumero_identificacion(rs.getLong("NUMERO_IDENTIFICACION"));
        p.setTipo_identificacion(rs.getString("TIPO_IDENTIFICACION"));
        p.setPrimer_nombre(rs.getString("PRIMER_NOMBRE"));
        p.setSegundo_nombre(rs.getString("SEGUNDO_NOMBRE"));
        p.setPrimer_apellido(rs.getString("PRIMER_APELLIDO"));
        p.setSegundo_apellido(rs.getString("SEGUNDO_APELLIDO"));
        p.setDireccion(rs.getString("DIRECCION"));
        p.setCelular(rs.getLong("CELULAR"));
        p.setCorreo(rs.getString("CORREO"));
        p.setEstado(rs.getString("ESTADO"));
        return p;
    }

    /* ===================== UPDATE ===================== */
    /** Actualiza datos generales; NO cambia la contraseña. */
    public boolean actualizarProductor(Productor p) {
        final String sql = "{ call sp_actualizar_productor(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1,  p.getId_productor());
            cs.setLong(2, p.getNumero_identificacion());
            cs.setString(3, p.getTipo_identificacion());
            cs.setString(4, p.getPrimer_nombre());
            cs.setString(5, isBlank(p.getSegundo_nombre()) ? "N/A" : p.getSegundo_nombre());
            cs.setString(6, p.getPrimer_apellido());
            cs.setString(7, isBlank(p.getSegundo_apellido()) ? "N/A" : p.getSegundo_apellido());
            cs.setString(8, p.getDireccion());
            cs.setLong(9, p.getCelular());
            cs.setString(10, p.getCorreo());
            cs.setString(11, p.getEstado());
            cs.registerOutParameter(12, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(12) > 0;
        } catch (SQLException e) {
            String msg = (e.getMessage() == null ? "" : e.getMessage().toUpperCase());
            if (msg.contains("UQ_PROD_IDENT") || msg.contains("ORA-00001")) {
                System.err.println("❌ Ya existe un productor con ese numero_identificacion/correo.");
            } else {
                System.err.println("Error al actualizar productor (SP): " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }


    /** Cambia contraseña: guarda hash BCrypt. */
    public boolean actualizarPassword(int idProductor, String nuevoPasswordPlano) {
        if (isBlank(nuevoPasswordPlano)) {
            System.err.println("❌ password no puede ser vacío.");
            return false;
        }
        final String sql = "{ call sp_actualizar_contraProductor(?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            String hash = BCrypt.hashpw(nuevoPasswordPlano, BCrypt.gensalt(12));
            cs.setInt(1, idProductor);
            cs.setString(2, hash);
            cs.registerOutParameter(3, Types.INTEGER); // p_filas
            cs.execute();
            return cs.getInt(3) > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar password (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    
    public boolean desactivarProductorSiNoReferenciado(int idProductor) {
        if (!existeProductor(idProductor)) {
            System.err.println("⚠️ No existe el productor con ID " + idProductor + ".");
            return false;
        }
        final String sql = "{ call sp_inactivar_productor(?, ?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idProductor);
            cs.registerOutParameter(2, Types.INTEGER);  // p_code
            cs.registerOutParameter(3, Types.VARCHAR);  // p_msg
            cs.execute();

            int code = cs.getInt(2);
            String msg = cs.getString(3);

            if (code == 0) return true;
            System.err.println("❌ " + msg);
            return false;
        } catch (SQLException e) {
            System.err.println("Error al inactivar productor (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    /* ===================== EXISTS ===================== */
    public boolean existeProductor(int idProductor) {
        final String sql = "{ call sp_existe_productor(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idProductor);
            cs.registerOutParameter(2, Types.INTEGER); // p_exists
            cs.execute();
            return cs.getInt(2) > 0;
        } catch (SQLException e) {
            System.err.println("Error en existeProductor (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeProductorActivo(int idProductor) {
        final String sql = "{ call sp_existe_productorAct(?, ?) }";
        try (CallableStatement cs = conexion.prepareCall(sql)) {
            cs.setInt(1, idProductor);
            cs.registerOutParameter(2, Types.INTEGER); // p_exists
            cs.execute();
            return cs.getInt(2) > 0;
        } catch (SQLException e) {
            System.err.println("Error en existeProductorActivo (SP): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    /* ===================== Helpers ===================== */
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}

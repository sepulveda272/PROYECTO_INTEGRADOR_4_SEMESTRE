package controlador;


import modelo.ProductorDAO;
import modelo.TecnicoOficialDAO;
import modelo.FuncionarioICADAO;
import modelo.AdminDAO;


public class Logincontroller {
    private ProductorDAO productorDAO;
    private TecnicoOficialDAO tecnicoOficialDAO;
    private FuncionarioICADAO funcionarioICADAO;
    private AdminDAO adminDAO;


    public Logincontroller() {
        
        this.productorDAO = new ProductorDAO();
        this.tecnicoOficialDAO = new TecnicoOficialDAO();
        this.funcionarioICADAO = new FuncionarioICADAO();
        this.adminDAO = new AdminDAO();
    
    }

    public Integer autenticarProductor(String correo, String password) {
        return productorDAO.validarProductor(correo, password);
    }
    public Integer autenticarTecnico(String correo, String password) {
        return tecnicoOficialDAO.validarTecnico(correo, password);
    }
    
    public Integer autenticarFuncionario(String correo, String password) {
        return funcionarioICADAO.validarFuncionario(correo, password);
    }
    
    public Integer autenticarAdmin(String correo, String password) {
        return adminDAO.validarAdmin(correo, password);
    }
}

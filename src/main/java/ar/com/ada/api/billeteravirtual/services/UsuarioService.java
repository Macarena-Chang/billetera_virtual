package ar.com.ada.api.billeteravirtual.services;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import ar.com.ada.api.billeteravirtual.entities.Billetera;
import ar.com.ada.api.billeteravirtual.entities.Cuenta;
import ar.com.ada.api.billeteravirtual.entities.Persona;
import ar.com.ada.api.billeteravirtual.entities.Usuario;
import ar.com.ada.api.billeteravirtual.repos.UsuarioRepository;
import ar.com.ada.api.billeteravirtual.security.Crypto;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuariorepo;

    @Autowired
    PersonaService personaService;

    @Autowired
    BilleteraService billeteraService;





    
    public Usuario crearUsuario(String nombre, int pais, int tipoDocumentoId, String documento, Date fechaNacimiento, String email, String password){
         
    /*1.Metodo: Crear Usuario
    1.1-->Crear una Persona(setearle un usuario)
    1.2-->crear un usuario
    1.3-->Crear una billetera(setearle una persona)
    1.4-->Crear una cuenta en pesos y otra en dolares*/
    
        Persona persona  = new Persona();
        persona.setNombre(nombre);
        persona.setPaisId(pais);
        persona.setTipoDocumentoId(tipoDocumentoId);
        persona.setDocumento(documento);
        persona.setFechaNacimiento(fechaNacimiento);
        Usuario usuario = new Usuario();

        usuario.setEmail(email);
        usuario.setPassword(Crypto.encrypt(password, email));
        usuario.setUsername(email);
      
        // la relacion bidireccional  esta escrita en persona.setUsuario
        persona.setUsuario(usuario);

        personaService.grabar(persona);
        
       // 1.3-->Crear una billetera(setearle una persona)
      
        Billetera billetera  = new Billetera();


        // 1.4-->Crear una cuenta en pesos y otra en dolares*v
        Cuenta cuentaARS = new Cuenta(); 
        cuentaARS.setSaldo(new BigDecimal(0));
        cuentaARS.setMoneda("ARS");

        billetera.agregarCuenta(cuentaARS);

        Cuenta cuentaUSD = new Cuenta();
        cuentaUSD.setSaldo(new BigDecimal(0));
        cuentaUSD.setMoneda("USD");

        billetera.agregarCuenta(cuentaUSD);
		 billetera.agregarCuenta(cuentaARS);

        //setearle la billetera a la perrsona
        persona.setBilletera(billetera);

        billeteraService.grabar(billetera);
        BigDecimal saldoRegalo500ARS = new BigDecimal(500);

        billeteraService.cargarSaldo(saldoRegalo500ARS,"ARS", billetera.getBilleteraId(), "Regalo", "Bienvenida por creacion de usuario" );
        
        
        return usuario;
    }


    public Usuario buscarPorUsername(String username) {
        return usuariorepo.findByUsername(username);
      }
    
      public void login(String username, String password) {
        /**
         * Metodo IniciarSesion recibe usuario y contraseña validar usuario y contraseña
         */
    
        Usuario u = buscarPorUsername(username);
    
        if (u == null || !u.getPassword().equals(Crypto.encrypt(password, u.getUsername()))) {
    
          throw new BadCredentialsException("Usuario o contraseña invalida");
        }
      }
   



    /* 2. Metodo: Iniciar Sesion 
    2.1-- recibe el username y la password
    2.2-- vamos a validar los datos
    2.3-- devolver un verdadero o falso
    */



}
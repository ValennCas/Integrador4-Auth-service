package Auth.services;

import Auth.dto.UsuarioDTO;
import Auth.model.Usuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface DefaultUserService extends UserDetailsService {
	Usuario save(UsuarioDTO usuario_dto);



	UserDetails loadUserByUsername(String email);


}

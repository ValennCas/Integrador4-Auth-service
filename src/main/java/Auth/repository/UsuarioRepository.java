package Auth.repository;


import Auth.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Query("SELECT u FROM Usuario u WHERE u.email =:email")
    Usuario findByEmail(String email);

    @Query("SELECT u FROM Usuario u WHERE u.password =:password")
    Usuario findByPassword(String password);
}
package Auth.dto;

import lombok.Data;


@Data
public class UsuarioDTO {

    private String password;
    private String email;
    private String rol;


    public UsuarioDTO(String email, String password){
        this.email = email;
        this.password = password;
    }

}
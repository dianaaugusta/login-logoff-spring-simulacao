package sptech.school.login02211035diana;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {
    List<Usuario> userList = new ArrayList<>();
    int contadorTentativas = 0;

    @PostMapping
    public Usuario postUser(@RequestBody Usuario userToPost){
        userList.add(userToPost);
        return userToPost;
    }

    @PostMapping("/autenticacao/{usuario}/{senha}")
    public Usuario authUser(@PathVariable String usuario, @PathVariable String senha){
        for(Usuario u: userList){
            if(u.getUsuario().equals(usuario) && u.getSenha().equals(senha)){
                u.setAutenticado(true);
                return u;
            }
        }
        return null;
    }

    @GetMapping
    public List<Usuario> getUser(){
        if(!userList.isEmpty()){
            return userList;
        }
        return null;
    }

    @DeleteMapping("/autenticacao/{usuario}")
    public String deleteUser(@PathVariable String usuario){
        for(Usuario u: userList){
            if(u.getUsuario().equals(usuario)){
                if(u.isAutenticado()){
                    u.setAutenticado(false);
                    return String.format("Logoff do usuário %s concluído", u.getNome());
                }
                return String.format("Usuário %s NÃO está autenticado", u.getNome());
            }
        }
        return String.format("Usuário %s não encontrado", usuario);
    }

    /* Método extra = Mudar a senha do usuário. O usuario necessitará estar autenticado e passar corretamente o seu
    * username e senha atual para que a mudança seja feita. Caso ultrapasse 5 tentativas na senha errada, simula um
    * sistema bancário ou de backoffice e bloqueia o usuário de qualquer ação envolvendo sua senha atual. Peguei a ideia
    * do backoffice de quando trabalhava na valemobi :3 */
    @PatchMapping("/autenticacao/{usuario}/{senhaAtual}/{senhaNova}")
    public String updateUserPassword(@PathVariable String usuario, @PathVariable String senhaAtual, @PathVariable String senhaNova){
        for(Usuario u: userList){
            if(u.getUsuario().equals(usuario)){
                if(contadorTentativas >= 5){
                    return "Usuário bloqueado. Fale com o suporte antes de qualquer ação";
                }

                if(u.getSenha().equals(senhaAtual)){
                    if(u.isAutenticado()){
                        u.setSenha(senhaNova);
                        return String.format("Senha do usuario %s alterada", u.getUsuario());
                    }
                    return String.format("%s, por razões de segurança, autentique-se antes de alterar a senha", u.getNome());
                }
                contadorTentativas++;
                return String.format("Tentativa de alterar senha detectada. Você tem mais %d tentativas antes do bloqueio.",
                        (5 - contadorTentativas));
            }
        }

        return String.format("Usuário %s não encontrado", usuario);
    }

}

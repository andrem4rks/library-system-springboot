package marks.learning.librarysystemspringboot.security;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import marks.learning.librarysystemspringboot.model.Papel;
import marks.learning.librarysystemspringboot.model.Usuario;
import marks.learning.librarysystemspringboot.repository.usuarioRepository;

@Service
@Transactional
public class DetalheUsuarioServico implements UserDetailsService {

    private usuarioRepository usuarioRepository;

    public DetalheUsuarioServico(usuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByLogin(username);

        if(usuario != null && usuario.isAtivo()){
            Set<GrantedAuthority> papeisDoUsuario = new HashSet<GrantedAuthority>();
            for(Papel papel : usuario.getPapeis()){
                GrantedAuthority pp = new SimpleGrantedAuthority(papel.getPapel());
                papeisDoUsuario.add(pp);
            }
            User user = new User(usuario.getLogin(), usuario.getPassword(), papeisDoUsuario);
            return (UserDetails) user;
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado!");
        }

        
        

    }



}

package marks.learning.librarysystemspringboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import marks.learning.librarysystemspringboot.model.Papel;
import marks.learning.librarysystemspringboot.model.Usuario;
import marks.learning.librarysystemspringboot.repository.papelRepository;
import marks.learning.librarysystemspringboot.repository.usuarioRepository;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private usuarioRepository usuarioRepository;

    @Autowired
    private papelRepository papelRepository;

    @Autowired
    private BCryptPasswordEncoder criptografia;

    private boolean temAutorizacao(Usuario usuario, String papel) {
        for (Papel pp : usuario.getPapeis()) {
            if (pp.getPapel().equals(papel)) {
                return true;
            }
        }
        return false;
    }

    @GetMapping("/index")
    public String index(@CurrentSecurityContext(expression = "authentication.name")String login) {
        Usuario usuario = usuarioRepository.findByLogin(login);

        String redirectURL = "";
        if (temAutorizacao(usuario, "ADMIN")) {
            redirectURL = "/auth/admin/admin-index";
        } else if (temAutorizacao(usuario, "USER")) {
            redirectURL = "/auth/user/user-index";
        } else if (temAutorizacao(usuario, "BIBLIOTECARIO")) {
            redirectURL = "/auth/biblio/biblio-index";
        }
        return redirectURL;
    }

    @GetMapping("/novo")
    public String adicionarUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "/publica-criar-usuario";
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@Valid Usuario usuario, BindingResult result,Model model, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "/publica-criar-usuario";
        }

        Usuario usrLogin = usuarioRepository.findByLogin(usuario.getLogin());
        Usuario usrCpf = usuarioRepository.findByCpf(usuario.getCpf());

        if(usrCpf != null) {
            model.addAttribute("cpfExiste", "CPF já existe cadastrado");
        }

        if(usrLogin != null) {
            model.addAttribute("loginExiste", "Login já existe cadastrado");
        }

        if(usrLogin != null || usrCpf != null) {
            return "/publica-criar-usuario";
        }

        //busca o objeto de papel pelo seu nome
        Papel papel = papelRepository.findByPapel("USER");
        List<Papel> papeis = new ArrayList<Papel>();
        papeis.add(papel);
        usuario.setPapeis(papeis); // associa o papel USER a todos os novos usuarios

        String senhaCript = criptografia.encode(usuario.getPassword());
        usuario.setPassword(senhaCript);
        
        usuarioRepository.save(usuario);
        attributes.addFlashAttribute("mensagem", "Usuário salvo com sucesso!");
        return "redirect:/usuario/novo";
    }

    @RequestMapping("/admin/listar")
    public String listarUsuario(Model model){
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "/auth/admin/admin-listar-usuario";
    }

    @GetMapping("/admin/apagar/{id}")
    public String deleteUser(@PathVariable("id") long id, Model model){
        if(!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Id inválido!"+id); 
        }  
        usuarioRepository.deleteById(id);    
        return "redirect:/usuario/admin/listar";
    } 

    @GetMapping("/admin/editar/{id}")
    public String editarUsuario(@PathVariable("id") long id, Model model) {
        Optional<Usuario> usuarioVelho = usuarioRepository.findById(id);
        if(!usuarioVelho.isPresent()) {
            throw new IllegalArgumentException("Usuario invalido!" + id);
        }
        Usuario usuario = usuarioVelho.get();
        model.addAttribute("usuario", usuario);
        model.addAttribute("listaPapeis", papelRepository.findAll());

        return "auth/user/user-alterar-usuario";
    }

    @PostMapping("/editar/{id}")
    public String salvarUsuario(@PathVariable("id") long id, @RequestParam(value = "pps",required = false) int[] pps, @Valid Usuario usuario,  BindingResult result, Model model) {
        String testCpf = usuario.getCpf();
        String testLogin = usuario.getLogin();

        Usuario usrCpf = usuarioRepository.findByCpf(usuario.getCpf());
        Usuario usrLogin = usuarioRepository.findByLogin(usuario.getLogin());

        boolean verif = false;


        if(!result.hasErrors()){
            if(usrCpf != null) {
                if(id != usuarioRepository.findByCpf(testCpf).getId()) {
                    model.addAttribute("cpfExiste", "CPF pertence a outro usuário!");
                    verif = true;
                }
            } 

            if(usrLogin != null) {
                if(id != usuarioRepository.findByLogin(testLogin).getId()) {                   
                    model.addAttribute("loginExiste", "Login pertence a outro usuário!");
                    verif = true;
                }
            }  
        } else {
            usuario.setId(id);
            return "/auth/user/user-alterar-usuario"; 
        }

        if(pps == null) {
            usuario.setId(id);
            model.addAttribute("papelInexistente", "Pelo menos um papel deve ser informado!");
            verif = true;
        } else {
            List<Papel>  papeisLocal = new ArrayList<Papel>();
            for(int i = 0; i < pps.length; i++) {
                long idPapel = pps[i];
                System.out.println(pps[i]);
                Optional<Papel> papelOptional = papelRepository.findById(idPapel);
                if(papelOptional.isPresent()) {
                    Papel paper = papelOptional.get();
                    papeisLocal.add(paper);
                }
            }
            Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
            if(usuarioOptional.isPresent()) {
                Usuario usr = usuarioOptional.get();
                usr.setPapeis(papeisLocal);
                usr.setAtivo(usuario.isAtivo());
                usuarioRepository.save(usr);
            }
        }
        
        if(verif) {
            model.addAttribute("listaPapeis", papelRepository.findAll());
            return "/auth/user/user-alterar-usuario";        
        }

        return "redirect:/usuario/admin/listar";

    }

}

package marks.learning.librarysystemspringboot.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import marks.learning.librarysystemspringboot.model.Usuario;
import marks.learning.librarysystemspringboot.repository.usuarioRepository;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private usuarioRepository usuarioRepository;

    @GetMapping("/novo")
    public String adicionarUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "/publica-criar-usuario";
    }

    @PostMapping("/salvar")
    public String salvarUsuario(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "/publica-criar-usuario";
        }
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
        return "auth/user/user-alterar-usuario";
    }

    @PostMapping("/editar/{id}")
    public String salvarUsuario(@PathVariable("id") long id, @Valid Usuario usuario, BindingResult result) {
        if(result.hasErrors()) {
            usuario.setId(id);
            return "/auth/user/user-alterar-usuario";
        }
        usuarioRepository.save(usuario);
        return "redirect:/usuario/admin/listar";
    }

}

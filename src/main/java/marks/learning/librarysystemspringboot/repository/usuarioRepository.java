package marks.learning.librarysystemspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import marks.learning.librarysystemspringboot.model.Usuario;

public interface usuarioRepository extends JpaRepository<Usuario ,Long> {
    
}

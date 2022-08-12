package marks.learning.librarysystemspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import marks.learning.librarysystemspringboot.model.Papel;

public interface papelRepository extends JpaRepository<Papel, Long> {
    Papel findByPapel(String papel);
}

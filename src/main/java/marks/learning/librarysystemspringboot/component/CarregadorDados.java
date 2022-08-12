package marks.learning.librarysystemspringboot.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

import marks.learning.librarysystemspringboot.model.Papel;
import marks.learning.librarysystemspringboot.repository.papelRepository;

@Component
public class CarregadorDados implements CommandLineRunner {

    @Autowired
    private papelRepository papelRepository;

    @Override
	public void run(String... args) throws Exception {
		
		String[] papeis = {"ADMIN", "USER", "BIBLIOTECARIO"};
		
		for (String papelString: papeis) {
			Papel papel = papelRepository.findByPapel(papelString);
			if (papel == null) {
				papel = new Papel(papelString);
				papelRepository.save(papel);
			}
		}				
	}

    
    
}

package com.generation.blogpessoal.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import com.generation.blogpessoal.model.Usuario;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioRepositoryTest {
    
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start(){

		//Apagando registros do banco de dados
		usuarioRepository.deleteAll();
		
		//Gravando quatro usuários no banco de dados
		usuarioRepository.save(new Usuario(
				0L, 
				"João da Silva",
				"joao@email.com.br",
				"13465278",
				"https://i.imgur.com/FETvs2O.jpg"
				));
		
		usuarioRepository.save(new Usuario(
				0L,
				"Manuela da Silva",
				"manuela@email.com.br",
				"13465278",
				"https://i.imgur.com/NtyGneo.jpg"
				));
		
		usuarioRepository.save(new Usuario(
				0L, 
				"Adriana da Silva", 
				"adriana@email.com.br", 
				"13465278", 
				"https://i.imgur.com/mB3VM2N.jpg"
				));

        usuarioRepository.save(new Usuario(
        		0L,
        		"Paulo Antunes",
        		"paulo@email.com.br",
        		"13465278",
        		"https://i.imgur.com/JR7kUFU.jpg"
        		));

	}

	@Test
	@DisplayName("Retorna 1 usuario")
	public void deveRetornarUmUsuario() {

		//Testando se o sistema retorna um usuário 
		Optional<Usuario> usuario = usuarioRepository.findByUsuario("joao@email.com.br");

		//verificando se o nome encontrado é igual ao nome buscado
		assertTrue(usuario.get().getUsuario().equals("joao@email.com.br"));
	}

	@Test
	@DisplayName("Retorna 3 usuarios")
	public void deveRetornarTresUsuarios() {

		//Como tem três usuários com silva cadastrado a busca será realizada com esse sobrenome
		List<Usuario> listaDeUsuarios = usuarioRepository.findAllByNomeContainingIgnoreCase("Silva");

		//Verificando se o retorno foi de 3 usuários
		assertEquals(3, listaDeUsuarios.size());

		//Verificando se os nomes passados são os nomes que contém silva
		assertTrue(listaDeUsuarios.get(0).getNome().equals("João da Silva"));
		assertTrue(listaDeUsuarios.get(1).getNome().equals("Manuela da Silva"));
		assertTrue(listaDeUsuarios.get(2).getNome().equals("Adriana da Silva"));
		
	}

	@AfterAll
	public void end() {
		usuarioRepository.deleteAll();
	}
	
}
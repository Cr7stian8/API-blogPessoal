package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioControllerTest {

	// Adição injeção de dependência
	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@BeforeAll
	void start() {
		// Limpando o banco de dados antes de começar
		usuarioRepository.deleteAll();
	}

	// Teste 01
	@Test
	@Order(1)
	@DisplayName("Cadastrar Um Usuário")
	public void deveCriarUmUsuario() {

		// Passando dados do usuário que vai ser cadastrado
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Paulo Antunes",
				"paulo_antunes@email.com.br", "13465278", "https://i.imgur.com/JR7kUFU.jpg"));

		// Cadastrando o usuário
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		// Verificando se o usuário foi criado
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());

		// Verificando se o nome e usuário do corpo da requisição é igual ao do corpo da
		// resposta
		assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
		assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
	}

	// Teste02
	@Test
	@Order(2)
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {

		// Cadastrando um usuário
		usuarioService.cadastrarUsuario(new Usuario(0L, "Maria da Silva", "maria_silva@email.com.br", "13465278",
				"https://i.imgur.com/T12NIp9.jpg"));

		// passando usuário duplicado para o corpo da requisição
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Maria da Silva",
				"maria_silva@email.com.br", "13465278", "https://i.imgur.com/T12NIp9.jpg"));

		// Tentando cadastrar usuário já cadastrado
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.exchange("/usuarios/cadastrar", HttpMethod.POST,
				corpoRequisicao, Usuario.class);

		// Testando se o banco de dados está retornando um status de erro e retornando
		// um status ok caso esteja
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}

	// Teste 03
	@Test
	@Order(3)
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {

		// Cadastrando usuário
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, "Juliana Andrews",
				"juliana_andrews@email.com.br", "juliana123", "https://i.imgur.com/yDRVeK7.jpg"));

		// Pegando Id do usuário cadastrado e atualizando os dados
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), "Juliana Andrews Ramos",
				"juliana_ramos@email.com.br", "juliana123", "https://i.imgur.com/yDRVeK7.jpg");

		// Inserindo o usuáio atualizado dentro da classe HttpEntity
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);

		// Utilizando método put para atualizar os dados
		ResponseEntity<Usuario> corpoResposta = testRestTemplate.withBasicAuth("root", "root")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);

		// Verificando se a atualização funcionou e retornando um status
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

		// Verificando se o nome e usuário do corpo da requisição é igual ao do corpo da
		// resposta
		assertEquals(corpoRequisicao.getBody().getNome(), corpoResposta.getBody().getNome());
		assertEquals(corpoRequisicao.getBody().getUsuario(), corpoResposta.getBody().getUsuario());
	}

	// Teste 04
	@Test
	@Order(4)
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {

		// Cadastrando dois usuários
		usuarioService.cadastrarUsuario(new Usuario(0L, "Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123",
				"https://i.imgur.com/5M2p5Wb.jpg"));

		usuarioService.cadastrarUsuario(new Usuario(0L, "Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123",
				"https://i.imgur.com/Sk5SjWE.jpg"));

		// Realizando método get para cadastrar usuário
		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("root", "root").exchange("/usuarios/all",
				HttpMethod.GET, null, String.class);

		// Retornando status ok caso tenha dado certo
		assertEquals(HttpStatus.OK, resposta.getStatusCode());

	}

	// Teste 05
	@Test
	@Order(5)
	@DisplayName("Listar Um Usuário Específico")
	public void deveListarApenasUmUsuario() {

		// Cadastrando um usuário
		Optional<Usuario> usuarioBusca = usuarioService.cadastrarUsuario(new Usuario(0L, "Laura Santolia",
				"laura_santolia@email.com.br", "laura12345", "https://i.imgur.com/EcJG8kB.jpg"));

		// Buscando pelo usuário cadastrado
		ResponseEntity<String> resposta = testRestTemplate.withBasicAuth("root", "root")
				.exchange("/usuarios/" + usuarioBusca.get().getId(), HttpMethod.GET, null, String.class);

		// Verificando se encontrou o usuário e retornando o status
		assertEquals(HttpStatus.OK, resposta.getStatusCode());

	}

	// Teste 06
	@Test
	@Order(6)
	@DisplayName("Login do Usuário")
	public void deveAutenticarUsuario() {

		// Cadastrando usuário
		usuarioService.cadastrarUsuario(new Usuario(0L, "Marisa Souza", "marisa_souza@email.com.br", "13465278",
				"https://i.imgur.com/T12NIp9.jpg"));

		// passando os dados que desejamos atualizar para o corpo da requisição
		HttpEntity<UsuarioLogin> corpoRequisicao = new HttpEntity<UsuarioLogin>(
				new UsuarioLogin(0L, "", "marisa_souza@email.com.br", "13465278", "", ""));

		// Usando o corpo da requisição para realizar o login do usuário
		ResponseEntity<UsuarioLogin> corpoResposta = testRestTemplate.exchange("/usuarios/logar", HttpMethod.POST,
				corpoRequisicao, UsuarioLogin.class);

		// Verificando se o status code ok está correto
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());

	}
}
package com.generation.blogpessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

//Indicando que a classe é do tipo service
@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	// Método responsável por cadastrar usuário
	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {
		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent())
			return Optional.empty();
		usuario.setSenha(criptografarSenha(usuario.getSenha()));
		return Optional.of(usuarioRepository.save(usuario));

	}

	// Método responsável por atualizar usuário
	public Optional<Usuario> atualizarUsuario(Usuario usuario) {

		if (usuarioRepository.findById(usuario.getId()).isPresent()) {
			Optional<Usuario> buscaUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());

			if ((buscaUsuario.isPresent()) && (buscaUsuario.get().getId() != usuario.getId()))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);

			usuario.setSenha(criptografarSenha(usuario.getSenha()));

			return Optional.ofNullable(usuarioRepository.save(usuario));

		}

		return Optional.empty();

	}

	// Método responsável por autenticar o usuário
	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

		Optional<Usuario> usuario = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

		if (usuario.isPresent()) {

			if (compararSenhas(usuarioLogin.get().getSenha(), usuario.get().getSenha())) {

				usuarioLogin.get().setId(usuario.get().getId());
				usuarioLogin.get().setNome(usuario.get().getNome());
				usuarioLogin.get().setFoto(usuario.get().getFoto());
				usuarioLogin.get()
						.setToken(gerarBasicToken(usuarioLogin.get().getUsuario(), usuarioLogin.get().getSenha()));
				usuarioLogin.get().setSenha(usuario.get().getSenha());

				return usuarioLogin;

			}
		}
		
		return Optional.empty();

	}

	//Método responsável por criptografar as senhas
	private String criptografarSenha(String senha) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		return encoder.encode(senha);

	}

	//Método responsável por comparar senhas
	private boolean compararSenhas(String senhaDigitada, String senhaBanco) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		return encoder.matches(senhaDigitada, senhaBanco);

	}

	//Método responsável por gerar token basico
	private String gerarBasicToken(String usuario, String senha) {

		String token = usuario + ":" + senha;
		byte[] tokenBase64 = Base64.encodeBase64(token.getBytes(Charset.forName("US-ASCII")));
		return "Basic " + new String(tokenBase64);

	}

}

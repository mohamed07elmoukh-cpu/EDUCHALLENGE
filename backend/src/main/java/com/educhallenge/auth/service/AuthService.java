package com.educhallenge.auth.service;

import com.educhallenge.auth.dto.LoginRequest;
import com.educhallenge.auth.dto.LoginResponse;
import com.educhallenge.auth.dto.RegisterRequest;
import com.educhallenge.auth.dto.RegisterResponse;
import com.educhallenge.backend.security.AuthenticatedUserDetails;
import com.educhallenge.backend.security.JwtService;
import com.educhallenge.users.dto.UserResponse;
import com.educhallenge.users.entity.User;
import com.educhallenge.users.repository.UserRepository;
import com.educhallenge.users.service.UserService;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthService {

	private final UserService userService;
	private final UserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(
			UserService userService,
			UserRepository userRepository,
			AuthenticationManager authenticationManager,
			JwtService jwtService
	) {
		this.userService = userService;
		this.userRepository = userRepository;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	public RegisterResponse register(RegisterRequest request) {
		UserResponse user = userService.registerUser(
				request.getUsername(),
				request.getEmail(),
				request.getPassword()
		);
		return new RegisterResponse("Inscription reussie", user);
	}

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
		String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);

		AuthenticatedUserDetails authenticatedUser;

		try {
			authenticatedUser = (AuthenticatedUserDetails) authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(normalizedEmail, request.getPassword())
			).getPrincipal();
		} catch (BadCredentialsException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		}

		User user = userRepository.findById(authenticatedUser.getUserId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));

		UserResponse responseUser = new UserResponse(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getRole(),
				user.getPointsTotal(),
				user.getLevel()
		);

		return new LoginResponse(
				"Connexion reussie",
				responseUser,
				jwtService.generateToken(authenticatedUser),
				"Bearer",
				jwtService.getExpirationSeconds()
		);
	}
}

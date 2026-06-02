package com.educhallenge.backend.security;

import com.educhallenge.users.entity.User;
import com.educhallenge.users.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

	private final UserRepository userRepository;

	public CurrentUserArgumentResolver(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentUser.class)
				&& User.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(
			MethodParameter parameter,
			ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory
	) {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

		if (request == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
		}

		Long userId = extractUserId(request);

		if (userId != null) {
			return userRepository.findById(userId)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
		}

		String email = request.getHeader("X-User-Email");

		if (StringUtils.hasText(email)) {
			return userRepository.findByEmailIgnoreCase(email.trim())
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
		}

		throw new ResponseStatusException(
				HttpStatus.UNAUTHORIZED,
				"Authentication required. Provide X-User-Id, X-User-Email, or an authenticated session."
		);
	}

	private Long extractUserId(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session != null) {
			Object sessionUserId = session.getAttribute("authenticatedUserId");
			Long parsedSessionUserId = parseUserId(sessionUserId);

			if (parsedSessionUserId != null) {
				return parsedSessionUserId;
			}
		}

		return parseUserId(request.getHeader("X-User-Id"));
	}

	private Long parseUserId(Object rawUserId) {
		if (rawUserId == null) {
			return null;
		}

		if (rawUserId instanceof Number number) {
			return number.longValue();
		}

		String value = rawUserId.toString().trim();

		if (!StringUtils.hasText(value)) {
			return null;
		}

		try {
			return Long.parseLong(value);
		} catch (NumberFormatException exception) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authenticated user identifier");
		}
	}
}

package com.github.vincemann.springlemon.auth.security;

import com.github.vincemann.springlemon.auth.domain.AbstractUser;
import com.github.vincemann.springlemon.auth.domain.LemonAuthenticatedPrincipal;
import com.github.vincemann.springlemon.auth.service.token.AuthorizationTokenService;
import com.github.vincemann.springlemon.auth.service.token.HttpTokenService;
import com.github.vincemann.springlemon.auth.service.token.JwsTokenService;
import com.github.vincemann.springlemon.auth.util.LecUtils;
import com.github.vincemann.springlemon.auth.util.LemonAuthenticated;
import com.github.vincemann.springrapid.core.security.RapidSecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AllArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter for token authentication
 */
@AllArgsConstructor
public class LemonAuthenticationFilter extends OncePerRequestFilter {
	
    private static final Log log = LogFactory.getLog(LemonAuthenticationFilter.class);
    
    private JwsTokenService jwsTokenService;
    private JwtClaimsPrincipalConverter jwtPrincipalConverter;


    private HttpTokenService httpTokenService;
    private AuthorizationTokenService<LemonAuthenticatedPrincipal> authorizationTokenService;
    private RapidSecurityContext<LemonAuthenticatedPrincipal>


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {


		log.debug("Inside LemonTokenAuthenticationFilter ...");

		String token = httpTokenService.extractToken(request);


		String header = request.getHeader(HttpHeaders.AUTHORIZATION);				
		
    	if (header != null && header.startsWith(LecUtils.TOKEN_PREFIX)) { // token present
			
			log.debug("Found a token");			
		    String token = header.substring(7);
		    
		    try {
				JWTClaimsSet claims = jwsTokenService.parseToken(token, JwsTokenService.AUTH_AUDIENCE);
				AbstractUser<?> user = jwtPrincipalConverter.toPrincipal(claims);
				LemonAuthenticated.login(user);
				log.debug("Token authentication successful");
				    		    	
		    } catch (Exception e) {
		    	
				log.debug("Token authentication failed - " + e.getMessage());
				
		    	response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
						"Authentication Failed: " + e.getMessage());
		    	
		    	return;
		    }
		    
		} else
			//no token found -> let downstream filters log user in or block this request
			log.debug("Token authentication skipped");
		
		filterChain.doFilter(request, response);
	}

//	////@LogInteraction(level = LogInteraction.Level.TRACE)
//	public Authentication createAuthToken(String token) {
//
//
////		LemonUserDto lemonUserDto = LemonUtils.getUserDto(claims);
////		if (lemonUserDto == null)
////			lemonUserDto = fetchUserDto(claims);
////
////        LemonAuthenticatedPrincipal principal = new LemonAuthenticatedPrincipal(lemonUserDto);
//        return new UsernamePasswordAuthenticationToken(authenticatedPrincipal, token, authenticatedPrincipal.getAuthorities());
//	}

//	/**
//	 * Default behaviour is to throw error. To be overridden in auth service.
//	 *
//	 * @return
//	 */
//	protected LemonUserDto fetchUserDto(JWTClaimsSet claims) {
//		throw new AuthenticationCredentialsNotFoundException(
//				LexUtils.getMessage("com.naturalprogrammer.spring.userClaimAbsent"));
//	}
}

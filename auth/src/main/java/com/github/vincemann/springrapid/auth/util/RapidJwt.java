package com.github.vincemann.springrapid.auth.util;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Maps;
import org.springframework.security.access.AccessDeniedException;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper-Methods for creating and validating Jwt's claims
 */
@Slf4j
public class RapidJwt {

	public static final String SUBJECT_CLAIM = "sub";
	public static final String AUDIENCE_CLAIM = "aud";
	public static final String AUTH_CLAIM = "auth";
	// do not change these to the standard exp and iat bc the jwt builder will convert to date obj again and round to seconds, which fucks up the tests
	public static final String EXPIRATION_CLAIM = "expired";
	public static final String ISSUED_AT_CLAIM = "issued-at";



	public static JWTClaimsSet create(String aud, String subject, long expirationMillis, Map<String,Object> otherClaims){
		JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

		builder
				//.issueTime(new Date())
//                .expirationTime(new Date()) -> rounds to millis bad for tests
				.claim(RapidJwt.EXPIRATION_CLAIM,System.currentTimeMillis() + expirationMillis)
				.audience(aud)
				.subject(subject)
				.claim(RapidJwt.ISSUED_AT_CLAIM,System.currentTimeMillis());
//                .issueTime(new Date()); -> rounds to millis bad for tests

		otherClaims.forEach(builder::claim);
		return builder.build();
	}

	public static JWTClaimsSet.Builder createRawBuilder(String aud, String subject, long expirationMillis, long issuedAt, Map<String,Object> otherClaims){
		JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

		builder
				//.issueTime(new Date())
//                .expirationTime(new Date()) -> rounds to millis bad for tests
				.claim(RapidJwt.EXPIRATION_CLAIM,expirationMillis)
				.audience(aud)
				.subject(subject)
				.claim(RapidJwt.ISSUED_AT_CLAIM,issuedAt);
//                .issueTime(new Date()); -> rounds to millis bad for tests
		if (otherClaims!=null)
			otherClaims.forEach(builder::claim);
		return builder;

	}

	public static JWTClaimsSet mod(JWTClaimsSet claimsSet, String aud, String subject, Long expirationMillis,Long issuedAt, Map<String,Object> otherClaims) throws ParseException {
		JWTClaimsSet.Builder builder = createRawBuilder(
				claimsSet.getAudience().get(0),
				claimsSet.getSubject(),
				claimsSet.getLongClaim(RapidJwt.EXPIRATION_CLAIM),
				claimsSet.getLongClaim(RapidJwt.ISSUED_AT_CLAIM),
				getOtherClaims(claimsSet)
		);
		if (expirationMillis!=null)
			builder.claim(RapidJwt.EXPIRATION_CLAIM,expirationMillis);
		if (issuedAt!=null)
			builder.claim(RapidJwt.ISSUED_AT_CLAIM,issuedAt);
		if (aud!=null)
			builder.audience(aud);
		if (subject != null)
			builder.claim(RapidJwt.SUBJECT_CLAIM,subject);
		if (otherClaims !=null){
			for (Map.Entry<String, Object> claim : otherClaims.entrySet()) {
				builder.claim(claim.getKey(),claim.getValue());
			}
		}
		return builder.build();
	}


	private static Map<String,Object> getOtherClaims(JWTClaimsSet claimsSet){
		Set<Map.Entry<String, Object>> otherClaimsSet = claimsSet.getClaims().entrySet().stream().filter(e -> {
			return !e.getKey().equals(RapidJwt.EXPIRATION_CLAIM) &&
					!e.getKey().equals(RapidJwt.ISSUED_AT_CLAIM) &&
					!e.getKey().equals("sub") &&
					!e.getKey().equals("aud");
		}).collect(Collectors.toSet());
		Map<String, Object> result = new HashMap<>();
		for (Map.Entry<String, Object> entry : otherClaimsSet) {
			result.put(entry.getKey(),entry.getValue());
		}
		return result;
	}

	public static JWTClaimsSet create(String aud, String subject, long expirationMillis){
		return create(aud,subject,expirationMillis,new HashMap<>());
	}

	public static void validateNotExpired(JWTClaimsSet claims) {
		long expirationTime = (long) claims.getClaim(RapidJwt.EXPIRATION_CLAIM);
		long currentTime = System.currentTimeMillis();
		log.debug("Check if token is expired...");
		log.debug("Expiration time = " + new Date(expirationTime)
				+ ". Current time = " + new Date(currentTime));
		log.debug("Expiration time = " + expirationTime
				+ ". Current time = " + currentTime);
		if (expirationTime< currentTime){
			throw new AccessDeniedException("Expired token");
		}
		log.debug("Token not expired.");
	}

	public static void validate(JWTClaimsSet claims, String expectedAud,long issuedAfter) {
		validateNotExpired(claims);
		validateAud(claims,expectedAud);
		validateIssuedAfter(claims,issuedAfter);
	}

	public static void validate(JWTClaimsSet claims, String expectedAud) {
		validateNotExpired(claims);
		validateAud(claims,expectedAud);
	}

	public static void validateAud(JWTClaimsSet claims, String expectedAud) {
		if (expectedAud==null || !claims.getAudience().contains(expectedAud)){
			throw new AccessDeniedException("Wrong token audience");
		}
	}

	public static void validateIssuedAfter(JWTClaimsSet claims, long issuedAfter)  {
		log.debug("Check if token is obsolete...");
		long issueTime = (long) claims.getClaim(ISSUED_AT_CLAIM);
		log.debug("Token issued at: " + new Date(issueTime) +  ", must be issued after: " + new Date(issuedAfter));
		log.debug("Token issued at: " + issueTime +  ", must be issued after: " + issuedAfter);
		if (issueTime < issuedAfter){
			throw new AccessDeniedException("Token has become obsolete");
		}
		log.debug("Token is not obsolete.");
	}
	

//	public static final String AUTHORIZATION_REQUEST_COOKIE_NAME = "lemon_oauth2_authorization_request";
//	public static final String LEMON_REDIRECT_URI_COOKIE_PARAM_NAME = "lemon_redirect_uri";



//	public static ApplicationContext applicationContext;
//
//	public ValidationUtils(ApplicationContext applicationContext) {
//
//		ValidationUtils.applicationContext = applicationContext;
//
//	}


//	public ValidationUtils() {
//
//	}

//	/**
//	 * Throws AccessDeniedException is not authorized
//	 *
//	 * @param authorized
//	 * @param messageKey
//	 */
//	public static void ensureAuthority(boolean authorized, String messageKey) {
//
//		if (!authorized)
//			throw new AccessDeniedException(Message.get(messageKey));
//	}




	


//	/**
//	 * Gets the reference to an application-context bean
//	 *
//	 * @param clazz	the type of the bean
//	 */
//	public static <T> T getBean(Class<T> clazz) {
//		return applicationContext.getBean(clazz);
//	}


//	/**
//	 * Generates a random unique string
//	 */
//	public static String uid() {
//
//		return UUID.randomUUID().toString();
//	}





}

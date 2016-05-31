package com.jpose.caloriestracker.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.GenericFilterBean;

import com.jpose.caloriestracker.entity.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

public class JwtFilter extends GenericFilterBean {
	
	private static final String SECRET_KEY = "ef0X4BYuHXTJBYSJ5b0ceOL7PZMugOymskdslkDS7m8POeWc5b7IlUdAgCWbGQw";
	
	private UserDetailsService userDetailsService;
	
	public JwtFilter(UserDetailsService userDetailsService) {
		super();
		this.userDetailsService = userDetailsService;
	}

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        if (requiresAuthentication(request)) {
        	final String authHeader = request.getHeader("Authorization");
        	if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        		throw new ServletException("Missing or invalid authorization.");
        	}
        	final String token = authHeader.substring(7); // The part after "Bearer "
        	try {
        		final Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        		request.setAttribute("claims", claims);
        		UserDetails userDetails = (UserDetails) userDetailsService.loadUserByUsername(claims.getSubject());
        		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
        	} catch (final SignatureException e) {
        		throw new ServletException("Invalid token.");
        	}
        }
        chain.doFilter(req, res);
    }
    
    private boolean requiresAuthentication(HttpServletRequest request) {
    	String url = request.getRequestURL().toString();
    	return !StringUtils.containsIgnoreCase(url, "api/authentication")
    		&& !StringUtils.containsIgnoreCase(url, "api/combos/userRole")
			&& !StringUtils.containsIgnoreCase(url, "api/user/register");
    }

}
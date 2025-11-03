package com.security.api.filter;
import com.security.api.service.JwtService;import com.security.api.service.UserDetailsServiceImpl;
import jakarta.servlet.*;import jakarta.servlet.http.*;import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;import org.springframework.web.filter.OncePerRequestFilter;import java.io.IOException;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {  private final JwtService jwt; private final UserDetailsServiceImpl uds;
  public JwtAuthFilter(JwtService j, UserDetailsServiceImpl u){this.jwt=j;this.uds=u;}
  @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain fc)throws ServletException,IOException{
    String header=req.getHeader("Authorization"); if(header==null||!header.startsWith("Bearer ")){fc.doFilter(req,res);return;}
    String token=header.substring(7); String username=jwt.extractUsername(token);
    if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
      var details=uds.loadUserByUsername(username); if(jwt.isTokenValid(token,details)){
        var auth=new UsernamePasswordAuthenticationToken(details,null,details.getAuthorities());
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req)); SecurityContextHolder.getContext().setAuthentication(auth);} }
    fc.doFilter(req,res);} }
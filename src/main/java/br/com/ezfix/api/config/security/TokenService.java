package br.com.ezfix.api.config.security;

import br.com.ezfix.api.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    @Value("${forum.jwt.expiration}")
    private String expiration;

    @Value("${forum.jwt.secret}")
    private String secret;

    public String gerarToken(Authentication authentication){
        Usuario usuario = (Usuario) authentication.getPrincipal();
        Date hoje = new Date();

        return Jwts.builder()
                .setIssuer("Api do forum da alura")
                .setSubject(usuario.getId().toString())
                .setIssuedAt(hoje)
                .setExpiration(new Date(hoje.getTime() + Long.parseLong(expiration)))
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();
    }

    public boolean isValido(String token){
        try {
            Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Long getIdUsuario(String token){
        Claims claims = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject());
    }
}

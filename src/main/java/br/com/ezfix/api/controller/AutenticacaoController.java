package br.com.ezfix.api.controller;

import br.com.ezfix.api.config.security.TokenService;
import br.com.ezfix.api.controller.form.AssistenciaForm;
import br.com.ezfix.api.controller.form.CadastroForm;
import br.com.ezfix.api.controller.form.LoginForm;
import br.com.ezfix.api.controller.form.SolicitanteForm;
import br.com.ezfix.api.controller.dto.TokenDto;
import br.com.ezfix.api.model.Perfil;
import br.com.ezfix.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AutenticacaoController{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SolicitanteRepository solicitanteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private PlanosRepository planosRepository;

    @Autowired
    private RepresentanteRepository representanteRepository;

    @Autowired
    private AssistenciaRepository assistenciaRepository;

    @Autowired
    private PerfisRepository perfisRepository;

    @PostMapping
    public ResponseEntity<TokenDto> autenticar(@RequestBody @Valid LoginForm loginForm){
        try {
            Authentication authentication = authenticationManager.authenticate(loginForm.converter());
            String token = tokenService.gerarToken(authentication);
            return ResponseEntity.ok(new TokenDto(token,"Bearer"));
        }catch (AuthenticationException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/novoSolicitante")
    public ResponseEntity<?> novoSolicitante(@RequestBody SolicitanteForm solicitanteForm){

        converterForm(solicitanteForm,perfisRepository.getById(1l));
        solicitanteForm.converterSolicitante();
        
        if(!usuarioRepository.existsById(solicitanteForm.getEmail())) {
            usuarioRepository.save(solicitanteForm.getUsuarios());
        }else {
            return ResponseEntity.status(400).body("Email já cadastrado por favor faça o login");
        }

        if(!enderecoRepository.existsById(solicitanteForm.getCep())){
            enderecoRepository.save(solicitanteForm.getEnderecos());
        }

        if(!solicitanteRepository.existsById(solicitanteForm.getCpf())){
            solicitanteRepository.save(solicitanteForm.getSolicitante());
        }else {
            return ResponseEntity.status(400).body("Cpf já cadastrado por favor faça o login");
        }

        return ResponseEntity.status(201).build();
    }

    @PostMapping("/novaAssistencia")
    public ResponseEntity<?> novaAssistencia(@RequestBody AssistenciaForm assistenciaForm){

        converterForm(assistenciaForm,perfisRepository.getById(2l));
        assistenciaForm.converterRepresentantes();
        assistenciaForm.converterAssistencias(planosRepository.getById(assistenciaForm.getPlano()));

        usuarioRepository.save(assistenciaForm.getUsuarios());
        enderecoRepository.save(assistenciaForm.getEnderecos());
        representanteRepository.save(assistenciaForm.getRepresentantes());
        assistenciaRepository.save(assistenciaForm.getAssistencias());

        return ResponseEntity.status(201).build();
    }

    private void converterForm(CadastroForm cadastroForm, Perfil perfil){
        cadastroForm.converterUsuarios(perfil);
        cadastroForm.converterEnderecos();
    }
}

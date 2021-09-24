package br.com.ezfix.api.controller;

import br.com.ezfix.api.controller.vo.SolicitanteVo;
import br.com.ezfix.api.repository.SolicitanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cliente")
public class ClienteController extends baseController {

    @Autowired
    private SolicitanteRepository clienteRepository;

    @GetMapping("/todos")
    public Page<SolicitanteVo> buscarTodos(@PageableDefault(page = 0,size = 10) Pageable paginacao) {
        return SolicitanteVo.converter(clienteRepository.findAll(paginacao));
    }
}

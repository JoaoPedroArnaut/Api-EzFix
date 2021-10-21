package br.com.ezfix.api.controller;

import br.com.ezfix.api.controller.dto.OrcamentoDto;
import br.com.ezfix.api.controller.form.ItemForm;
import br.com.ezfix.api.controller.form.OrcamentoForm;
import br.com.ezfix.api.model.ItemOrcamento;
import br.com.ezfix.api.model.Orcamento;
import br.com.ezfix.api.repository.*;
import br.com.ezfix.api.util.Csv;
import br.com.ezfix.api.util.ListaObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController extends BaseController{

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private ItemOrcamentoRepository itemOrcamentoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private AssistenciaRepository assistenciaRepository;

    @Autowired
    private SolicitanteRepository solicitanteRepository;

    @PostMapping("/{idSolicitante}/{idAssistencia}")
    private ResponseEntity novoOrcamento(
            @RequestBody List<ItemForm> itens,
            @PathVariable String idSolicitante,
            @PathVariable Long idAssistencia
    ){

        List<ItemOrcamento> itemOrcamentos = new ArrayList<>();

        for (ItemForm itemForm : itens){
            ItemOrcamento item = itemForm.converterItem(produtoRepository.findById(itemForm.getProduto()).get());
            itemOrcamentoRepository.save(item);
            itemOrcamentos.add(item);
        }

        orcamentoRepository.save(
                new Orcamento(
                0., "agurdando resposta tecnico",
                solicitanteRepository.findById(idSolicitante).get(),
                assistenciaRepository.findById(idAssistencia).get(),
                        itemOrcamentos
        ));

        return ResponseEntity.status(201).build();
    }



    @PostMapping("/{id}")
    public ResponseEntity adicionaItem(@PathVariable Long id,@RequestBody ItemForm itemForm){

        if (!orcamentoRepository.existsById(id)){
            return ResponseEntity.status(404).build();
        }

        ItemOrcamento novoItem = itemForm.converterItem(produtoRepository.findById(itemForm.getProduto()).get());

        Orcamento orcamento = orcamentoRepository.findById(id).get();
        orcamento.getItens().add(novoItem);

        itemOrcamentoRepository.save(novoItem);
        orcamentoRepository.save(orcamento);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity buscarTodos(@PageableDefault(page = 0,size = 10) Pageable paginacao){
        return ResponseEntity.ok().body(OrcamentoDto.converter(orcamentoRepository.findAll(paginacao)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity apagar(@PathVariable Long id){
        if(orcamentoRepository.existsById(id)){
            orcamentoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(404).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity atualizar(@PathVariable Long id, @RequestBody OrcamentoForm orcamentoForm) {
        if(orcamentoRepository.existsById(id)){
            Orcamento orcamento = orcamentoRepository.findById(id).get();
            orcamento.setStatusGeral(orcamentoForm.getStatus());
            orcamentoRepository.save(orcamento);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(404).build();
    }


    @GetMapping("/csv/{id}")
    public ResponseEntity gerarCsv(@PathVariable Long id){

        List<Orcamento> orcamentos = orcamentoRepository.findAllByAssistenciaId(id);

        ListaObj<Orcamento> listaObj = new ListaObj<>(orcamentos.size());

        for (Orcamento orcamento: orcamentos){
            listaObj.adiciona(orcamento);
        }

        Csv.gerarCsv(listaObj);

        return ResponseEntity.ok().build();
    }
}
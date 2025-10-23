package br.com.pec.api.san.controller;

import br.com.pec.api.san.dto.api.AnuenciaCreateRequest;
import br.com.pec.api.san.dto.api.AnuenciaResponseDTO;
import br.com.pec.api.san.service.AnuenciaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/anuencias")
public class AnuenciaController {

    private final AnuenciaService service;

    public AnuenciaController(AnuenciaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AnuenciaResponseDTO> criar(@Valid @RequestBody AnuenciaCreateRequest request) {
        var resp = service.criarEEnviarSolicitacao(request);
        return ResponseEntity.accepted().body(resp);
    }

    @GetMapping("/{nsu}")
    public ResponseEntity<AnuenciaResponseDTO> consultar(@PathVariable String nsu) {
        var resp = service.consultar(nsu);
        return resp == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(resp);
    }
}

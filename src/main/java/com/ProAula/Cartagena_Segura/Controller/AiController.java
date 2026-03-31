package com.ProAula.Cartagena_Segura.Controller;

import com.ProAula.Cartagena_Segura.Dto.AiDto;
import com.ProAula.Cartagena_Segura.Service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    /**
     * Chatbot conversacional para ciudadanos y agentes
     */
    @PostMapping("/chat")
    public ResponseEntity<AiDto.ChatResponse> chat(@RequestBody AiDto.Request request) {
        return ResponseEntity.ok(aiService.chat(request.getMessage()));
    }

    /**
     * Auto-clasificación de incidentes basada en descripción
     */
    @PostMapping("/classify")
    public ResponseEntity<AiDto.ClassifyResponse> classify(@RequestBody AiDto.Request request) {
        return ResponseEntity.ok(aiService.classify(request.getDescription()));
    }

    /**
     * Generar resumen narrativo del sistema (Solo ADMIN)
     */
    @GetMapping("/summary")
    public ResponseEntity<AiDto.SummaryResponse> getSummary() {
        return ResponseEntity.ok(aiService.generateSummary());
    }

    /**
     * Análisis estratégico de zonas de riesgo (Solo ADMIN)
     */
    @GetMapping("/zones/analysis")
    public ResponseEntity<AiDto.ZoneAnalysis> getZonesAnalysis() {
        return ResponseEntity.ok(aiService.analyzeZones());
    }
}

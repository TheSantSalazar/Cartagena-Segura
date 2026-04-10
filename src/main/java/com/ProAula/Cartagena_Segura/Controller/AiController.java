package com.ProAula.Cartagena_Segura.Controller;

import com.ProAula.Cartagena_Segura.Dto.AiDto;
import com.ProAula.Cartagena_Segura.Service.AiService;
import com.ProAula.Cartagena_Segura.Service.ChatbotAgent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final ChatbotAgent chatbotAgent;

    /**
     * Chatbot conversacional para ciudadanos y agentes (Context-Aware)
     */
    @PostMapping("/chat")
    public ResponseEntity<AiDto.ChatResponse> chat(@RequestBody AiDto.Request request, Authentication auth) {
        // Detectamos si el usuario tiene el rol ADMIN
        boolean isAdmin = auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return ResponseEntity.ok(chatbotAgent.processMessage(request.getMessage(), isAdmin));
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

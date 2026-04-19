package Com.Backend.CartagenaSegura.Controller;

import Com.Backend.CartagenaSegura.Dto.AiDto;
import Com.Backend.CartagenaSegura.Service.AiService;
import Com.Backend.CartagenaSegura.Service.ChatbotAgent;
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
     * Auto-clasificaciÃƒÂ³n de incidentes basada en descripciÃƒÂ³n
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
     * AnÃƒÂ¡lisis estratÃƒÂ©gico de zonas de riesgo (Solo ADMIN)
     */
    @GetMapping("/zones/analysis")
    public ResponseEntity<AiDto.ZoneAnalysis> getZonesAnalysis() {
        return ResponseEntity.ok(aiService.analyzeZones());
    }
}


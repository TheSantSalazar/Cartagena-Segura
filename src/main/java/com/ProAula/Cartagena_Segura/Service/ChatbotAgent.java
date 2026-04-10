package com.ProAula.Cartagena_Segura.Service;

import com.ProAula.Cartagena_Segura.Dto.AiDto;
import com.ProAula.Cartagena_Segura.Model.Incident;
import com.ProAula.Cartagena_Segura.Model.Zone;
import com.ProAula.Cartagena_Segura.Repository.IncidentRepository;
import com.ProAula.Cartagena_Segura.Repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Agente Inteligente para la gestión de conversaciones del dominio de seguridad.
 * Implementa RAG (Retrieval Augmented Generation) básico consultando la BD local.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotAgent {

    private final AiService aiService;
    private final IncidentRepository incidentRepository;
    private final ZoneRepository zoneRepository;

    private static final String SYSTEM_PROMPT = """
        Eres 'Cartagena Segura AI', el Agente Oficial de Seguridad de Cartagena.
        TU REGLA MÁXIMA: Tienes acceso directo y real a la base de datos. NUNCA digas que no tienes información en tiempo real.
        
        INSTRUCCIONES DE RESPUESTA:
        1. Usa EXCLUSIVAMENTE la información del 'CONTEXTO ACTUAL DEL SISTEMA' para responder sobre incidentes y zonas.
        2. Si el CONTEXTO está vacío o no tiene incidentes, di: "Actualmente no hay reportes recientes en la base de datos", pero no digas que no tienes acceso.
        3. Si te preguntan cosas generales de Cartagena que no están en el contexto, puedes responder con tu conocimiento base, pero siempre prioriza los datos del sistema.
        4. Responde siempre en español, de forma profesional y empática.
        """;

    /**
     * Procesa la consulta del usuario enriqueciendo el prompt con el estado actual de la BD.
     */
    public AiDto.ChatResponse processMessage(String userMessage, boolean isAdmin) {
        log.info("🤖 Agente procesando mensaje para {}: {}", isAdmin ? "ADMIN" : "USER", userMessage);
        
        // 1. Obtener contexto de la BD
        String context = gatherSystemContext();
        
        // 2. Ajustar el rol en el prompt de sistema
        String customSystemPrompt = SYSTEM_PROMPT;
        if (isAdmin) {
            customSystemPrompt += "\n5. El usuario actual es un ADMINISTRADOR del sistema. Salúdalo formalmente y usa un tono de reporte operativo.";
        } else {
            customSystemPrompt += "\n5. El usuario actual es un CIUDADANO. Salúdalo cálidamente y sé empático.";
        }
        
        // 3. Construir el cuerpo del mensaje de usuario
        String userContent = "SITUACIÓN ACTUAL DE LA BASE DE DATOS:\n" + context + 
                            "\n\nPREGUNTA DEL USUARIO: " + userMessage;
        
        // 4. Llamar al servicio pasando tanto el mensaje como la personalidad ajustada
        return aiService.chat(userContent, customSystemPrompt);
    }

    public AiDto.ChatResponse processMessage(String userMessage) {
        return processMessage(userMessage, false);
    }

    /**
     * Recopila información relevante de Incidentes y Zonas para alimentar al LLM.
     */
    private String gatherSystemContext() {
        // Últimos 10 incidentes
        List<Incident> recentIncidents = incidentRepository.findAll().stream()
                .limit(10)
                .collect(Collectors.toList());
                
        String incidentsTxt = recentIncidents.isEmpty() ? 
                "No hay incidentes reportados en el sistema." :
                recentIncidents.stream()
                .map(i -> "- " + i.getType() + " en " + i.getLocation() + " (Estado: " + i.getStatus() + ")")
                .collect(Collectors.joining("\n"));

        // Estado de las Zonas
        List<Zone> zones = zoneRepository.findAll();
        String zonesTxt = zones.isEmpty() ?
                "No hay información de zonas configurada." :
                zones.stream()
                .map(z -> "- Zona " + z.getName() + ": Nivel de Riesgo " + z.getRiskLevel())
                .collect(Collectors.joining("\n"));

        return "### DATOS DE LA BASE DE DATOS LOCAL:\n" +
               "--- INCIDENTES ---\n" + incidentsTxt + 
               "\n\n--- ZONAS DE RIESGO ---\n" + zonesTxt;
    }
}

package controllers;

import dtos.*;
import models.Message;
import models.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.AuthService;
import services.MailService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    private final MailService mailService;
    private final AuthService authService;
    private final DtoMapper dtoMapper;

    public MessageController(MailService mailService, AuthService authService, DtoMapper dtoMapper) {
        this.mailService = mailService;
        this.authService = authService;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping
    public ResponseEntity<?> getConversations(@RequestHeader("X-User-Id") Long userId) {
        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = authService.getUserById(userId);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<User> partners = mailService.getChatPartners(userId);
        List<UserDTO> dtos = partners.stream()
                .map(dtoMapper::toUserDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{partnerId}")
    public ResponseEntity<?> getChatHistory(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long partnerId) {

        List<Message> history = mailService.getChatHistory(userId, partnerId);
        List<MessageDTO> dtos = history.stream()
                .map(dtoMapper::toMessageDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ALIAS for /api/messages/partners
    @GetMapping("/partners")
    public ResponseEntity<?> getPartners(@RequestHeader("X-User-Id") Long userId) {
        return getConversations(userId);
    }

    // ALIAS for /api/messages/chat/{userId}
    @GetMapping("/chat/{partnerId}")
    public ResponseEntity<?> getChat(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long partnerId) {
        return getChatHistory(userId, partnerId);
    }

    @PostMapping
    public ResponseEntity<?> sendMessage(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody MessageRequest request) {

        User sender = authService.getUserById(userId);
        if (sender == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        mailService.sendMessage(sender, request.recipientEmail(), request.content());
        // Since sendMessage is void and assumes success (prints to console), we just
        // return 200.
        // It handles recipient not found by printing "Recipient not found".
        // To be safe, we can check if recipient exists first.
        User recipient = authService.getUserByEmail(request.recipientEmail());
        if (recipient == null) {
            return ResponseEntity.badRequest().body("Recipient not found");
        }

        return ResponseEntity.ok("Message sent");
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessageAlias(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody MessageRequest request) {
        return sendMessage(userId, request);
    }
}

package dcu.fossilfuel.user.controller;

import dcu.fossilfuel.user.controller.dto.RegisterRequest;
import dcu.fossilfuel.user.service.MailService;
import dcu.fossilfuel.user.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MailService registerMail;



    // 인증 코드 전송
    @PostMapping("/api/send-verification-code")
    public ResponseEntity<Map<String, Object>> sendVerificationCode(HttpSession session, @RequestBody Map<String, String> request) throws Exception {
        String email = request.get("email");
        String verificationCode = registerMail.sendSimpleMessage(email);
        session.setAttribute("verificationCode", verificationCode);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "인증 코드가 이메일로 발송되었습니다.");
        return ResponseEntity.ok(response);
    }

    // 인증 코드 확인
    @PostMapping("/api/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(HttpSession session, @RequestBody Map<String, String> request) {
        String code = request.get("code");
        String verificationCode = (String) session.getAttribute("verificationCode");
        Map<String, Object> response = new HashMap<>();

        if (verificationCode != null && verificationCode.equals(code)) {
            session.setAttribute("emailVerified", true);
            response.put("success", true);
            response.put("message", "이메일 인증 성공!");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "인증 코드가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // 이메일 중복 확인
    @PostMapping("/api/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailDuplicate(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Map<String, Object> response = new HashMap<>();

        if (memberService.isEmailDuplicate(email)) {
            response.put("success", false);
            response.put("message", "이미 가입된 이메일입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("success", true);
        response.put("message", "사용 가능한 이메일입니다.");
        return ResponseEntity.ok(response);
    }

    // 이메일 인증 여부 확인
    @GetMapping("/api/email-last-verified")
    public ResponseEntity<Map<String, Object>> checkEmailVerified(HttpSession session) {
        Boolean emailVerified = (Boolean) session.getAttribute("emailVerified");
        Map<String, Object> response = new HashMap<>();

        if (Boolean.TRUE.equals(emailVerified)) {
            response.put("success", true);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    // 회원가입
    @PostMapping("api/members/register")
    public ResponseEntity<Map<String, Object>> saveMember(@RequestBody RegisterRequest registerRequest) {
        memberService.saveMember(registerRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "회원가입 성공!");
        return ResponseEntity.ok(response);
    }


    // [id 찾기 ]
    // 닉네임을 입력받아, 기존 회원 디비와 대조
    @PostMapping("/api/auth/find-id")
    public ResponseEntity<Map<String, Object>> findId(@RequestBody RegisterRequest request) {
        String email = memberService.findEmailByNickname(request.getNickname());

        Map<String, Object> response = new HashMap<>();
        if (email == null) {
            response.put("success", false);
            response.put("message", "닉네임이 잘못되었거나 회원이 아닙니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        response.put("success", true);
        response.put("email", email);
        return ResponseEntity.ok(response);
    }




}


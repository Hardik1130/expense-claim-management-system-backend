package com.expensemanagement.service.impl;

import com.expensemanagement.dto.response.ReceiptResponse;
import com.expensemanagement.entity.*;
import com.expensemanagement.enums.*;
import com.expensemanagement.exception.*;
import com.expensemanagement.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
@Transactional
public class ReceiptServiceImpl implements com.expensemanagement.service.ReceiptService {
    private final ClaimRepository claims;
    private final ClaimReceiptRepository receipts;
    private final UserRepository users;
    private final ClaimAuditLogRepository audits;
    private final Path root;

    public ReceiptServiceImpl(ClaimRepository c, ClaimReceiptRepository r, UserRepository u, ClaimAuditLogRepository a, @Value("${app.receipts.directory:./uploads/receipts}") String dir) {
        claims = c;
        receipts = r;
        users = u;
        audits = a;
        root = Paths.get(dir).toAbsolutePath().normalize();
    }

    public ReceiptResponse upload(UUID uid, UUID claimId, MultipartFile file) {
        Claim c = claim(claimId);
        owner(c, uid);
        if (c.getStatus() != ClaimStatus.PENDING)
            throw new BadRequestException("Receipt can only be attached to a pending claim.");
        if (file == null || file.isEmpty()) throw new BadRequestException("Please select a receipt file.");
        if (file.getSize() > 5L * 1024 * 1024) throw new BadRequestException("Receipt size must not exceed 5 MB.");
        String type = file.getContentType();
        if (!List.of("image/jpeg", "image/png", "image/webp", "application/pdf").contains(type))
            throw new BadRequestException("Only JPG, PNG, WEBP and PDF files are allowed.");
        if (receipts.findByClaimId(claimId).isPresent())
            throw new BadRequestException("A receipt is already attached to this claim.");
        try {
            Files.createDirectories(root);
            String ext = type.equals("application/pdf") ? ".pdf" : type.equals("image/png") ? ".png" : type.equals("image/webp") ? ".webp" : ".jpg";
            String stored = UUID.randomUUID() + ext;
            Path target = root.resolve(stored).normalize();
            if (!target.startsWith(root)) throw new BadRequestException("Invalid file path.");
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            ClaimReceipt r = receipts.save(ClaimReceipt.builder().claim(c).originalFilename(Path.of(file.getOriginalFilename() == null ? "receipt" : file.getOriginalFilename()).getFileName().toString()).storedFilename(stored).contentType(type).fileSize(file.getSize()).storagePath(target.toString()).build());
            User u = user(uid);
            audits.save(ClaimAuditLog.builder().claim(c).performedBy(u).action(AuditAction.RECEIPT_UPLOADED).newValue(r.getOriginalFilename()).build());
            return map(r);
        } catch (Exception e) {
            if (e instanceof RuntimeException re) throw re;
            throw new BadRequestException("Unable to store receipt file.");
        }
    }

    @Transactional(readOnly = true)
    public List<ReceiptResponse> list(UUID uid, UUID claimId, boolean manager) {
        Claim c = claim(claimId);
        if (!manager) owner(c, uid);
        return receipts.findByClaimId(claimId).stream().map(this::map).toList();
    }

    @Transactional(readOnly = true)
    public ReceiptFile download(UUID uid, UUID receiptId, boolean manager) {
        ClaimReceipt r = receipts.findById(receiptId).orElseThrow(() -> new ResourceNotFoundException("Receipt not found."));
        if (!manager) owner(r.getClaim(), uid);
        try {
            Path p = Paths.get(r.getStoragePath()).normalize();
            if (!p.startsWith(root) || !Files.exists(p))
                throw new ResourceNotFoundException("Receipt file not found on storage.");
            return new ReceiptFile(new FileSystemResource(p), r.getOriginalFilename(), r.getContentType());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Unable to read receipt.");
        }
    }

    @Transactional
    public void delete(UUID uid, UUID claimId) {

        Claim c = claim(claimId);
        owner(c, uid);

        if (c.getStatus() != ClaimStatus.PENDING) {
            throw new BadRequestException(
                    "Receipt can only be deleted while claim is pending."
            );
        }

        ClaimReceipt r = receipts.findByClaimId(claimId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Receipt not found.")
                );

        // Save audit log
        audits.save(
                ClaimAuditLog.builder()
                        .claim(c)
                        .performedBy(user(uid))
                        .action(AuditAction.RECEIPT_DELETED)
                        .oldValue(r.getOriginalFilename())
                        .newValue(null)
                        .comment("Receipt deleted by employee")
                        .build()
        );

        // Delete receipt record
        receipts.delete(r);
        receipts.flush();

        // Delete physical file
        try {
            Files.deleteIfExists(Paths.get(r.getStoragePath()));
        } catch (IOException e) {
            throw new RuntimeException("Unable to delete receipt file.", e);
        }
    }

    private Claim claim(UUID id) {
        return claims.findById(id).orElseThrow(() -> new ResourceNotFoundException("Claim not found."));
    }

    private User user(UUID id) {
        return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private void owner(Claim c, UUID uid) {
        if (!c.getUser().getId().equals(uid))
            throw new ForbiddenOperationException("You cannot access another employee's receipt.");
    }

    private ReceiptResponse map(ClaimReceipt r) {
        return ReceiptResponse.builder().id(r.getId()).claimId(r.getClaim().getId()).originalFilename(r.getOriginalFilename()).contentType(r.getContentType()).fileSize(r.getFileSize()).uploadedAt(r.getUploadedAt()).build();
    }

    public record ReceiptFile(Resource resource, String filename, String contentType) {
    }
}
